package org.ganttproject.chart.burndownchart;

import net.sourceforge.ganttproject.GanttStatistics;
import net.sourceforge.ganttproject.io.BurndownDataIO;
import org.ganttproject.chart.PanelStyler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 *
 * GraphPanel class - paints the main panel and graph
 *                  - it uses the correspoding curve objects to paint the right curve, such as remaining effort curve
 */
public class GraphPanel extends PanelStyler {
    protected enum COLOR {
        ACTUAL_LINE(new Color(44, 102, 230, 180)),
        REMAINING_EFFORT_LINE(new Color(78, 243, 51, 255)),
        IDEAL_LINE_COLOR(new Color(230, 10, 44, 180)),
        POINT_COLOR(new Color(100, 100, 100, 180)),
        GRID_COLOR(new Color(200, 200, 200, 200));

        public final Color color;

        COLOR(Color color) {
            this.color = color;
        }
    }

    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);

    private final int graphInfOffSet;
    private final int padding;
    private final int labelPadding;
    private final int pointWidth;
    private final int numberYDivisions;
    private final int minScore;
    private final JPanel myPanel;
    private JComboBox daySelector;
    private ArrayList<String> comboboxItems;
    private int estimatedTime;
    private int tasksTotalDuration;
    private int maxScore;
    private double xScale;
    private double yScale;

    private static boolean linearMode = true;

    private GanttStatistics statistics;

    private RemainingTasksGraph remainingTasksGraph;

    private RemainingEffortGraph remainingEffortGraph;

    public GraphPanel(JPanel myPanel) {
        this.minScore = 0;
        this.graphInfOffSet = 250;
        this.padding = 45;
        this.labelPadding = 25;
        this.pointWidth = 4;
        this.numberYDivisions = 15;
        this.myPanel = myPanel;

        /* init is called each paint and we don't want to recreate
         * the select sprint folder button mid selection */

        defineUI();
    }

    /**
     * Initializes graph values, such as the scale and some statistics
     *  - this method is called in every paint
     *
     * @param statistics GanttStatistics object
     */
    public void init(GanttStatistics statistics) {
        this.statistics = statistics;

        this.tasksTotalDuration = initY();
        this.estimatedTime = initX();
        this.maxScore = getMaxScore();

        this.xScale = ((double) getGraphWidth() - (2 * padding) - labelPadding) / (this.estimatedTime);
        this.yScale = ((double) getScreenSizeY() - (2 * padding) - labelPadding) / (this.maxScore - this.minScore);
        initRemainingEffortGraph();
        initRemainingTasksGraph();

        defineCombobox();
    }

    /**
     * Defines the UI panel on the top of the frame
     *  - Adds all the points
     */
    private void defineUI() {
        addResetButton();
        addPathButton();
        //
        addModeButton();
        addSaveButton();
        addCombobox();
    }

    /**
     * Defines the combo box containing the days to choose.
     *      The selected day will be used to paint the current burndown chart
     */
    private void defineCombobox() {
        if (comboboxItems == null ||
                comboboxItems.size() != statistics.getTotalEstimatedTime()) {
            comboboxItems = new ArrayList<String>();
            for (int currDay = 1; currDay < statistics.getTotalEstimatedTime() + 1; currDay++)
                comboboxItems.add(String.valueOf(currDay));

            myPanel.remove(daySelector);
            daySelector = new JComboBox(comboboxItems.toArray());
            myPanel.add(daySelector);
            myPanel.validate();
        }
    }

    /**
     * Initializes the remaining effort curve in the graph
     */
    private void initRemainingEffortGraph() {
        this.remainingEffortGraph = new RemainingEffortGraph(statistics, myPanel, padding, labelPadding, pointWidth, linearMode);
        remainingEffortGraph.buildGraphPoints(xScale, yScale, maxScore, tasksTotalDuration);
    }

    /**
     * Initializes the remaining tasks curve in the graph
     */
    private void initRemainingTasksGraph() {
        this.remainingTasksGraph = new RemainingTasksGraph(statistics, myPanel, padding, labelPadding, pointWidth, linearMode);
        remainingTasksGraph.buildGraphPoints(xScale, yScale, maxScore, tasksTotalDuration);
    }

    /**
     * Initializes the x axis, the total estimated time is the max value of this axis
     *
     * @return total estimated time, the max value
     */
    private int initX() {
        return (int) statistics.getTotalEstimatedTime();
    }

    /**
     * Initializes the y axis, the sum of all the tasks' duration is the max value of this axis
     *
     * @return sum of all the tasks' duration, the max value
     */
    private int initY() {
        return statistics.getSumOfTaskDurations();
    }

    /**
     * Gets the width of the graph by subtracting the screen size by the offset in the screen
     *
     * @return graph width
     */
    private int getGraphWidth() {
        return getScreenSizeX() - graphInfOffSet;
    }

    /**
     * Paints the entire graphic resizing all of its coordinates to match the panel's and screen's getScreenSizeX()
     *
     * @param g Graphics swing object
     */
    public void paintGraphic(Graphics g) {
        // Setup
        updateFontSize(15, myPanel);
        g.setFont(new Font(FONT_STYLE, Font.PLAIN, fontSize));
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getGraphWidth() - (2 * padding) - labelPadding, getScreenSizeY() - 2 * padding - labelPadding);

        // create x and y axes
        g2.setColor(Color.BLACK);
        g2.drawLine(resizeX(padding + labelPadding, myPanel), resizeY(getScreenSizeY() - padding - labelPadding, myPanel),
                resizeX(padding + labelPadding, myPanel), resizeY(padding, myPanel));

        g2.drawLine(resizeX(padding + labelPadding, myPanel), resizeY(getScreenSizeY() - padding - labelPadding, myPanel),
                resizeX(getGraphWidth() - padding, myPanel), resizeY(getScreenSizeY() - padding - labelPadding, myPanel));

        if (estimatedTime > 0 && tasksTotalDuration > 0) {
            // draw ideal burndown line flow, create hatch marks and grid lines for X and Y axis.
            draw_X_Marks(g2);
            draw_Y_Marks(g2);
            drawIdealFlowLine(g2);
            remainingTasksGraph.drawActualFlowLine(g2);
            remainingEffortGraph.drawActualFlowLine(g2);
        }

        // Paints graph's info
        drawGraphInfo(g2);
    }

    /**
     * Paints the marks along the x axis
     *
     * @param g2 graphics 2D object
     */
    // create hatch marks, grid lines and identifiers for X axis.
    private void draw_X_Marks(Graphics2D g2) {

        for (int i = 0; i < estimatedTime + 1; i++) {/*+1*/
            Point p0 = new Point(i * (getGraphWidth() - padding * 2 - labelPadding) / estimatedTime + padding + labelPadding, getScreenSizeY() - padding - labelPadding);
            Point p1 = new Point(i * (getGraphWidth() - padding * 2 - labelPadding) / estimatedTime + padding + labelPadding, getScreenSizeY() - padding - labelPadding - pointWidth);

            if (i > 0 && (i % ((int) ((estimatedTime / 20.0)) + 1)) == 0) {
                draw_X_Grid(g2, p0, p1);
                draw_X_Identifiers(g2, i, p0);
            }
            g2.drawLine(resizeX(p0.x, myPanel), resizeY(p0.y, myPanel), resizeX(p1.x, myPanel), resizeY(p1.y, myPanel));
        }
    }

    /**
     * Draws the grid along the x axis (vertical lines)
     *
     * @param g2 graphics 2D object
     * @param p0 starting point coordinate
     * @param p1 end point coordinate
     */
    private void draw_X_Grid(Graphics2D g2, Point p0, Point p1) {
        g2.setColor(COLOR.GRID_COLOR.color);
        g2.drawLine(resizeX(p0.x, myPanel), resizeY(getScreenSizeY() - padding - labelPadding - 1 - pointWidth, myPanel), resizeX(p1.x, myPanel), resizeY(padding, myPanel));
    }

    /**
     * Draws the markers' identifiers. The numbers along the x axis
     *
     * @param g2 graphics 2D object
     * @param index day
     * @param p0 point coordinate
     */
    private void draw_X_Identifiers(Graphics2D g2, int index, Point p0) {
        g2.setColor(Color.BLACK);
        String xLabel = index + "";
        FontMetrics metrics = g2.getFontMetrics();
        int labelWidth = metrics.stringWidth(xLabel);
        g2.drawString(xLabel, resizeX(p0.x - labelWidth / 2, myPanel), resizeY(p0.y + metrics.getHeight() + 3, myPanel));
    }

    /**
     * Create hatch marks, grid lines and identifiers for the y axis
     *
     * @param g2 graphics 2D object
     */
    private void draw_Y_Marks(Graphics2D g2) {
        for (int i = 0; i < numberYDivisions + 1; i++) {
            Point p0 = new Point(padding + labelPadding, getScreenSizeY() - ((i * (getScreenSizeY() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding));
            Point p1 = new Point(pointWidth + padding + labelPadding, p0.y);

            draw_Y_Grid(g2, p0, p1);
            draw_Y_Identifiers(g2, i, p0);

            g2.drawLine(resizeX(p0.x, myPanel), resizeY(p0.y, myPanel), resizeX(p1.x, myPanel), resizeY(p1.y, myPanel));
        }
    }

    /**
     * Draws the y grid. The horizontal lines
     *
     * @param g2 graphics 2D object
     * @param p0 starting point coordinate
     * @param p1 end point coordinate
     */
    private void draw_Y_Grid(Graphics2D g2, Point p0, Point p1) {
        g2.setColor(COLOR.GRID_COLOR.color);
        g2.drawLine(resizeX(padding + labelPadding + 1 + pointWidth, myPanel), resizeY(p0.y, myPanel),
                resizeX(getGraphWidth() - padding, myPanel), resizeY(p1.y, myPanel));
    }

    /**
     * Draws the identifiers on the y axis. The numbers.
     * @param g2 graphics 2D object
     * @param index day
     * @param p0 point coordinate
     */
    private void draw_Y_Identifiers(Graphics2D g2, int index, Point p0) {
        g2.setColor(Color.BLACK);
        String yLabel = (int) ((minScore + (maxScore - minScore) * (index * 1.0 / numberYDivisions))) + "";
        FontMetrics metrics = g2.getFontMetrics();
        int labelWidth = metrics.stringWidth(yLabel);
        g2.drawString(yLabel, resizeX(p0.x - labelWidth - 5, myPanel), resizeY(p0.y + (metrics.getHeight() / 2) - 3, myPanel));
    }

    /**
     * Draws the ideal estimate, the red curve on the graph. In fact, it is a straight line
     *
     * @param g2 graphics 2D object
     */
    private void drawIdealFlowLine(Graphics2D g2) {
        g2.setColor(COLOR.IDEAL_LINE_COLOR.color);
        g2.setStroke(GRAPH_STROKE);
        g2.drawLine(resizeX(padding + labelPadding, myPanel), resizeY((int) ((maxScore - tasksTotalDuration) * yScale + padding), myPanel),
                resizeX((int) (estimatedTime * xScale + padding + labelPadding), myPanel), resizeY((int) (maxScore * yScale + padding), myPanel));
    }

    private void drawGraphInfo(Graphics2D g2) {
        int infoRectYOrigin = ((getScreenSizeY() / 2) - 100 - padding);
        // draw white background in side right panel
        drawInfoBackGround(g2, infoRectYOrigin);
        // draw strings for graph info
        drawStrings(g2, infoRectYOrigin);
    }

    private void drawInfoBackGround(Graphics2D g2, int infoRectYOrigin){
        g2.setColor(Color.WHITE);
        g2.fillRect(resizeX(getGraphWidth(), myPanel), resizeY(infoRectYOrigin, myPanel), resizeX(225, myPanel), resizeY(getScreenSizeY() * 1 / 4, myPanel));
        g2.setStroke(new BasicStroke(5f));

        g2.setColor(COLOR.ACTUAL_LINE.color);
        g2.drawLine(resizeX(getGraphWidth() + padding, myPanel), resizeY(infoRectYOrigin + padding, myPanel), resizeX(getGraphWidth() + 225 - padding, myPanel), resizeY(infoRectYOrigin + padding, myPanel));

        g2.setColor(COLOR.IDEAL_LINE_COLOR.color);
        g2.drawLine(resizeX(getGraphWidth() + padding, myPanel), resizeY(infoRectYOrigin + (getScreenSizeY() * 1 / 4) - padding - labelPadding, myPanel), resizeX(getGraphWidth() + 225 - padding, myPanel), resizeY(infoRectYOrigin + (getScreenSizeY() * 1 / 4) - padding - labelPadding, myPanel));

        g2.setColor(COLOR.REMAINING_EFFORT_LINE.color);
        g2.drawLine(resizeX(getGraphWidth() + padding, myPanel), resizeY(infoRectYOrigin + (getScreenSizeY() * 1 / 6) - padding - labelPadding, myPanel), resizeX(getGraphWidth() + 225 - padding, myPanel), resizeY(infoRectYOrigin + (getScreenSizeY() * 1 / 6) - padding - labelPadding, myPanel));

    }

    private void drawStrings(Graphics2D g2, int infoRectYOrigin){
        g2.setColor(Color.BLACK);

        g2.drawString("Actual Tasks Remaining", resizeX(getGraphWidth() + padding, myPanel), resizeY(infoRectYOrigin + padding + labelPadding, myPanel));

        g2.drawString("Remaining effort", resizeX(getGraphWidth() + padding, myPanel), resizeY(infoRectYOrigin + (getScreenSizeY() * 1 / 6) - padding, myPanel));

        g2.drawString(" Ideal Tasks Remaining", resizeX(getGraphWidth() + padding, myPanel), resizeY(infoRectYOrigin + (getScreenSizeY() * 1 / 4) - padding, myPanel));

        g2.drawString("Iteration Timeline (days)", resizeX(((getGraphWidth() + padding) / 2) - "Iteration Timeline (days)".toCharArray().length, myPanel), resizeY((getScreenSizeY() - padding / 2), myPanel));

        // rotates the coordinate by 90 degree counterclockwise
        AffineTransform at = new AffineTransform();
        at.rotate(-Math.PI / 2);
        g2.setTransform(at);
        g2.drawString("Sum of Task Estimates (days)", -resizeY(((getScreenSizeY() + padding) / 2) - "Sum of Task Estimates (days)".toCharArray().length, myPanel), resizeX(labelPadding, myPanel));
    }

    private int getMaxScore() {
        if (tasksTotalDuration % 15 == 0) {
            return tasksTotalDuration;
        } else {
            return (15 - (tasksTotalDuration % 15)) + tasksTotalDuration;
        }
    }

    private void addResetButton() {
        final JButton reset = new JButton();
        reset.setText("Reset ");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!sprintPath.equals("")) {
                    File dir = new File(sprintPath);
                    for (File file : dir.listFiles())
                        if (!file.isDirectory())
                            file.delete();
                }
            }
        });
        myPanel.add(reset);
    }

    private void addPathButton() {
        final JButton definePath = new JButton();
        definePath.setText("Select Sprint Folder");
        definePath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser f = new JFileChooser();
                    f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    f.showSaveDialog(null);
                    sprintPath = f.getSelectedFile().getAbsolutePath();
                } catch (Exception ex) {
                }
            }
        });
        myPanel.add(definePath);
    }

    private void addSaveButton() {
        final JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BurndownDataIO bd = new BurndownDataIO();
                bd.changeSprintFolder(sprintPath);
                try {
                    bd.saveDay(statistics.getMyTaskManager(),
                            Integer.parseInt(String.valueOf(daySelector.getSelectedItem())));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        myPanel.add(saveButton);
    }

    private void addModeButton() {
        final JButton changeModeButton = new JButton("Ideal");
        changeModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                linearMode = !linearMode;
                String currentMode = "";
                if (linearMode)
                    currentMode = "Ideal";
                else
                    currentMode = "History";
                //
                changeModeButton.setText(currentMode);
            }
        });
        myPanel.add(changeModeButton);
    }

    private void addCombobox() {
        daySelector = new JComboBox();
        myPanel.add(daySelector);
    }
}
