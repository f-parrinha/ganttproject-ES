package org.ganttproject.chart.burndownchart;

import net.sourceforge.ganttproject.GanttStatistics;
import org.ganttproject.chart.PanelStyler;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.*;

/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 */
public class GraphPanel extends PanelStyler {
    protected enum COLOR {
        ACTUAL_LINE(new Color(44, 102, 230, 180)),
        REMAINING_EFFORT_LINE(new Color(78, 243, 51, 255)),
        IDEAL_LINE_COLOR(new Color(230, 10, 44, 180)),
        POINT_COLOR(new Color(100, 100, 100, 180)),
        GRID_COLOR(new Color(200, 200, 200, 200));

        private final Color color;

        COLOR(Color color){
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

    private int estimatedTime;
    private int tasksTotalDuration;
    private int maxScore;
    private double xScale;
    private double yScale;
    private List<Integer> finishedTasksInfo;
    private GanttStatistics statistics;
    private List<Point> graphPoints;

    private List<Integer> remainingEffortInfo;
    private List<Point> effortPoints;
    private static final Date date = new Date();

    public GraphPanel(JPanel myPanel) {
        this.finishedTasksInfo = new ArrayList<>();
        this.remainingEffortInfo = new ArrayList<>();
        this.minScore = 0;
        this.graphInfOffSet = 250;
        this.padding = 45;
        this.labelPadding = 25;
        this.pointWidth = 4;
        this.numberYDivisions = 15;
        this.myPanel = myPanel;
    }

    public void init(GanttStatistics statistics) {
        this.statistics = statistics;
        this.finishedTasksInfo = statistics.getBurndownInfo();
        this.remainingEffortInfo = statistics.getRemEffortInfo();
        this.tasksTotalDuration = initY();
        this.estimatedTime = initX();
        this.maxScore = getMaxScore();
        this.xScale = ((double) getGraphWidth() - (2 * padding) - labelPadding) / (this.estimatedTime);
        this.yScale = ((double) getScreenSizeY() - (2 * padding) - labelPadding) / (this.maxScore - this.minScore);
        this.graphPoints = buildAllPoints();
        this.effortPoints = buildEffortPoints();
    }

    private int initX() {
        return (int) statistics.getTotalEstimatedTime();
    }

    private int initY() {
        return statistics.getSumOfTaskDurations();
    }

    private int getGraphWidth(){
        return getScreenSizeX() - graphInfOffSet;
    }

    /**
     * Paints the entire graphic resizing all of its coordinates to match the panel's and screen's getScreenSizeX()
     *
     * @param g Graphics swing object
     */
    public void paintGraphic(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getGraphWidth() - (2 * padding) - labelPadding, getScreenSizeY() - 2 * padding - labelPadding);

        // create x and y axes
        g2.setColor(Color.BLACK);
        g2.drawLine(resizeX(padding + labelPadding, myPanel),   resizeY(getScreenSizeY() - padding - labelPadding, myPanel),
                    resizeX(padding + labelPadding, myPanel),   resizeY(padding, myPanel));

        g2.drawLine(resizeX(padding + labelPadding, myPanel),   resizeY(getScreenSizeY() - padding - labelPadding, myPanel),
                    resizeX(getGraphWidth() - padding, myPanel),resizeY(getScreenSizeY() - padding - labelPadding, myPanel));

        if (estimatedTime > 0 && tasksTotalDuration > 0){
            // draw ideal burndown line flow, create hatch marks and grid lines for X and Y axis.
            draw_X_Marks(g2);
            draw_Y_Marks(g2);
            drawIdealFlowLine(g2);
            if (graphPoints.size() > 1)
                drawActualFlowLine(g2, graphPoints);
            if(effortPoints.size() > 1)
                drawRemainingEffortLine(g2, effortPoints);
        }

        // Paints graph's info - estimated line and current progression line
        drawGraphInfo(g2);
    }

    private void drawRemainingEffortLine(Graphics2D g2, List<Point> graphPoints){
        Stroke oldStroke = g2.getStroke();
        g2.setColor(COLOR.REMAINING_EFFORT_LINE.color);
        g2.setStroke(GRAPH_STROKE);
        System.out.println("SIZE "+graphPoints.size());
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(resizeX(x1, myPanel), resizeY(y1, myPanel), resizeX(x2, myPanel), resizeY(y2, myPanel));
        }

        g2.setStroke(oldStroke);
        g2.setColor(COLOR.POINT_COLOR.color);
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = graphPoints.get(i).x - pointWidth / 2;
            int y = graphPoints.get(i).y - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;
            g2.fillOval(resizeX(x, myPanel), resizeY(y, myPanel), resizeX(ovalW, myPanel), resizeY(ovalH, myPanel));
        }
    }

    private void drawActualFlowLine(Graphics2D g2, List<Point> graphPoints){
        Stroke oldStroke = g2.getStroke();

        g2.setColor(COLOR.ACTUAL_LINE.color);
        g2.setStroke(GRAPH_STROKE);
        System.out.println("SIZE "+graphPoints.size());
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = resizeX(graphPoints.get(i).x, myPanel);
            int y1 = resizeY(graphPoints.get(i).y, myPanel);
            int x2 = resizeX(graphPoints.get(i + 1).x, myPanel);
            int y2 = resizeY(graphPoints.get(i + 1).y, myPanel);
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(COLOR.POINT_COLOR.color);
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = resizeX(graphPoints.get(i).x - pointWidth / 2, myPanel);
            int y = resizeY(graphPoints.get(i).y - pointWidth / 2, myPanel);
            int ovalW = resizeX(pointWidth, myPanel);
            int ovalH = resizeY(pointWidth, myPanel);
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }

    // create hatch marks, grid lines and identifiers for X axis.
    private void draw_X_Marks(Graphics2D g2){

        for (int i = 0; i < estimatedTime+1; i++) {/*+1*/
            Point p0 = new Point(i * (getGraphWidth() - padding * 2 - labelPadding) / estimatedTime + padding + labelPadding,getScreenSizeY() - padding - labelPadding);
            Point p1 = new Point(i * (getGraphWidth() - padding * 2 - labelPadding) / estimatedTime + padding + labelPadding,getScreenSizeY() - padding - labelPadding - pointWidth);

            if (i > 0 && (i % ((int) ((estimatedTime / 20.0)) + 1)) == 0){
                draw_X_Grid(g2, p0, p1);
                draw_X_Identifiers(g2, i, p0);
            }
            g2.drawLine(resizeX(p0.x, myPanel), resizeY(p0.y, myPanel), resizeX(p1.x, myPanel), resizeY(p1.y, myPanel));
        }
    }

    private void draw_X_Grid(Graphics2D g2, Point p0, Point p1){
        g2.setColor(COLOR.GRID_COLOR.color);
        g2.drawLine(resizeX(p0.x,myPanel), resizeY(getScreenSizeY() - padding - labelPadding - 1 - pointWidth, myPanel), resizeX(p1.x, myPanel), resizeY(padding, myPanel));
    }

    private void draw_X_Identifiers(Graphics2D g2, int index, Point p0){
        g2.setColor(Color.BLACK);
        String xLabel = index + "";
        FontMetrics metrics = g2.getFontMetrics();
        int labelWidth = metrics.stringWidth(xLabel);
        g2.drawString(xLabel, resizeX(p0.x - labelWidth / 2, myPanel), resizeY(p0.y + metrics.getHeight() + 3, myPanel));
    }

    // create hatch marks, grid lines and identifiers for Y axis.
    private void draw_Y_Marks(Graphics2D g2){
        for (int i = 0; i < numberYDivisions + 1; i++) {
            Point p0 = new Point(padding + labelPadding,getScreenSizeY() - ((i * (getScreenSizeY() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding));
            Point p1 = new Point(pointWidth + padding + labelPadding, p0.y);

            draw_Y_Grid(g2, p0, p1);
            draw_Y_Identifiers(g2, i, p0);

            g2.drawLine(resizeX(p0.x, myPanel), resizeY(p0.y, myPanel), resizeX(p1.x, myPanel), resizeY(p1.y, myPanel));
        }
    }

    private void draw_Y_Grid(Graphics2D g2, Point p0, Point p1){
        g2.setColor(COLOR.GRID_COLOR.color);
        g2.drawLine(resizeX(padding + labelPadding + 1 + pointWidth, myPanel),  resizeY(p0.y, myPanel),
                    resizeX(getGraphWidth() - padding, myPanel),                resizeY(p1.y, myPanel));
    }

    private void draw_Y_Identifiers(Graphics2D g2, int index, Point p0){
        g2.setColor(Color.BLACK);
        String yLabel = (int) ((minScore + (maxScore - minScore) * (index * 1.0 / numberYDivisions)) ) + "";
        FontMetrics metrics = g2.getFontMetrics();
        int labelWidth = metrics.stringWidth(yLabel);
        g2.drawString(yLabel, resizeX(p0.x - labelWidth - 5, myPanel), resizeY(p0.y + (metrics.getHeight() / 2) - 3, myPanel));
    }

    private void drawIdealFlowLine(Graphics2D g2) {
        g2.setColor(COLOR.IDEAL_LINE_COLOR.color);
        g2.setStroke(GRAPH_STROKE);
        g2.drawLine(resizeX(padding + labelPadding, myPanel), resizeY((int)((maxScore - tasksTotalDuration) * yScale + padding), myPanel),
                resizeX((int)(estimatedTime * xScale + padding + labelPadding), myPanel), resizeY((int) (maxScore * yScale + padding), myPanel));
    }

    private void drawGraphInfo(Graphics2D g2){
        // draw white background in side right panel
        int infoRectYOrigin = ((getScreenSizeY() / 2) - 100 - padding);
        g2.setColor(Color.WHITE);
        g2.fillRect(resizeX(getGraphWidth(), myPanel), resizeY(infoRectYOrigin, myPanel), resizeX(225, myPanel), resizeY(getScreenSizeY() * 1/4, myPanel));
        g2.setStroke(new BasicStroke(5f));
        g2.setColor(COLOR.ACTUAL_LINE.color);
        g2.drawLine(resizeX(getGraphWidth() + padding, myPanel),       resizeY(infoRectYOrigin + padding, myPanel),
                    resizeX(getGraphWidth() + 225 - padding, myPanel), resizeY(infoRectYOrigin + padding, myPanel));
        g2.setColor(COLOR.IDEAL_LINE_COLOR.color);
        g2.drawLine(resizeX(getGraphWidth() + padding, myPanel),       resizeY(infoRectYOrigin +  (getScreenSizeY() * 1/4) - padding - labelPadding, myPanel),
                    resizeX(getGraphWidth() + 225 - padding, myPanel), resizeY(infoRectYOrigin +  (getScreenSizeY() * 1/4) - padding - labelPadding, myPanel));
        g2.setColor(Color.BLACK);
        g2.drawString("Actual Tasks Remaining", resizeX(getGraphWidth() + padding, myPanel),
                resizeY(infoRectYOrigin + padding + labelPadding, myPanel));
        g2.drawString(" Ideal Tasks Remaining", resizeX(getGraphWidth() + padding, myPanel),
                resizeY(infoRectYOrigin +  (getScreenSizeY() * 1/4) - padding, myPanel));
        g2.drawString("Iteration Timeline (days)", resizeX(((getGraphWidth() + padding)/2) - "Iteration Timeline (days)".toCharArray().length, myPanel),
                resizeY((getScreenSizeY() - padding/2), myPanel));

        // rotates the coordinate by 90 degree counterclockwise
        AffineTransform at = new AffineTransform();
        at.rotate(- Math.PI / 2);
        g2.setTransform(at);
        g2.drawString("Sum of Task Estimates (days)", - resizeY(((getScreenSizeY() + padding)/2) - "Sum of Task Estimates (days)".toCharArray().length, myPanel),
                resizeX(labelPadding, myPanel));
    }

    private int getMaxScore() {
       if(tasksTotalDuration % 15 == 0) {
           return tasksTotalDuration;
       } else {
           return (15 - (tasksTotalDuration % 15)) + tasksTotalDuration;
       }
    }
    private Date today;

    private List<Point> buildEffortPoints() {
        this.effortPoints = new ArrayList<>();

        int originX = (padding + labelPadding);
        int originY = (int) ((maxScore - tasksTotalDuration) * yScale + padding);

        Point pointReference = new Point(originX, originY);

        effortPoints.add(pointReference);

        int yReference = 0;

        for (int i = 1; i < remainingEffortInfo.size(); i++) {
                if (remainingEffortInfo.get(i-1) >= 0) {
                    int x1 = (int) (i * xScale + padding + labelPadding);
                    int y1 = (int) ((maxScore - tasksTotalDuration + yReference + remainingEffortInfo.get(i - 1)) * yScale + padding);
                    Point p = new Point(x1, y1);
                    effortPoints.add(p);
                    yReference += remainingEffortInfo.get(i - 1);
                    //System.out.println("yReference" + yReference);
                } else {
                    Point p0 = effortPoints.get(effortPoints.size() - 1);
                    Point p1 = new Point((int) (i * xScale + padding + labelPadding) + p0.x, p0.y);
                    effortPoints.add(p1);
                }

        }

        return effortPoints;
    }


    private List<Point> buildAllPoints(){

        this.graphPoints = new ArrayList<>();

        int originX = (padding + labelPadding);
        int originY = (int) ((maxScore - tasksTotalDuration) * yScale + padding);

        Point pointReference = new Point(originX, originY);

        graphPoints.add(pointReference);

        int yReference = 0;

        for (int i = 0; i < finishedTasksInfo.size(); i++) {

            if (finishedTasksInfo.get(i) > 0){
                int x1 = (int) (i * xScale + padding + labelPadding);
                int y1 = (int) ((maxScore - tasksTotalDuration + yReference + finishedTasksInfo.get(i)) * yScale + padding);
                Point p = new Point(x1, y1);
                graphPoints.add(p);
                yReference += finishedTasksInfo.get(i);
                //System.out.println("yReference" + yReference);
            }
        }
        return graphPoints;
    }
}
