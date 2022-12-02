package org.ganttproject.chart.planner;

import net.sourceforge.ganttproject.GanttStatistics;
import org.ganttproject.chart.PanelStyler;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 * <p>
 * PlannerPainter class - Used to paint the 'Planner' panel. Gives the design
 */
public class PlannerPainter extends PanelStyler {

    /**
     * DrawStatistics class - Paints the entire statistics area
     */
    protected class DrawStatistics {
        public void paint(Graphics g, GanttStatistics myGanttStatistics) {
            updateFontSize(25, myPanel);
            setOffset(50, 50);
            setRect(2.0 / 6.0, 5.0 / 6.0);
            setOffset(resizeX(50, myPanel), resizeY(50, myPanel) + rectHeight / 6);

            paintBackground(g);
            paintInfo(g, myGanttStatistics);
        }

        /**
         * Paints the main square
         *
         * @param g Graphics swing object
         */
        private void paintBackground(Graphics g){
            g.setColor(Color.WHITE);
            g.fillRoundRect(offsetX, offsetY, rectWidth - resizeX(50, myPanel) * 2, rectHeight - resizeY(50, myPanel) * 2, 50, 50);
        }

        /**
         * Paints the info about the project. The statistics in text
         *
         * @param g Graphics swing object
         * @param myGanttStatistics gantt statistics class. Gives important statistics
         */
        private void paintInfo(Graphics g, GanttStatistics myGanttStatistics){
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font(FONT_STYLE, Font.PLAIN, fontSize));
            g.drawString("Total number of tasks: " + myGanttStatistics.getTotalTasks(), rectWidth / 8 + resizeX(50, myPanel), offsetY + rectHeight / 7);
            g.drawString("Current time spent: " + myGanttStatistics.getCurrentSpentTime(), rectWidth / 8 + offsetX, offsetY + rectHeight * 2 / 7);
            g.drawString("Total estimated time: " + myGanttStatistics.getTotalEstimatedTime(), rectWidth / 8 + offsetX, offsetY + rectHeight * 3 / 7);
            g.drawString("Total finished tasks: " + myGanttStatistics.getFinishedTasks(), rectWidth / 8 + offsetX, offsetY + rectHeight * 4 / 7);
            g.drawString("Overall progress: " + myGanttStatistics.getOverallProgress() + "%", rectWidth / 8 + offsetX, offsetY + rectHeight * 5 / 7);
        }
    }

    /**
     * DrawGraph class - Paints the entire bar graph
     */
    protected class DrawGraph {
        private static final int SPACING_FACTOR = 5;     // Spacing between bars i the graph

        /**
         * Paints the entire graphics
         *
         * @param g Graphics swing object
         * @param myGanttStatistics gantt statistics class. Gives important statistics
         */
        public void paint(Graphics g, GanttStatistics myGanttStatistics) {
            int spacing = rectHeight / SPACING_FACTOR;
            updateFontSize(25, myPanel);
            setOffset(rectWidth + resizeX(50, myPanel) * 2, resizeY(50, myPanel) + rectHeight / 6);
            setRect(3.5 / 6.0, 5.0 / 6.0);

            paintMainSquare(g);
            paintFinished(g, myGanttStatistics, spacing);
            paintCurrentSpentTime(g, myGanttStatistics, spacing);
            paintOverall(g, myGanttStatistics, spacing);
        }

        /**
         * Paints the main square
         *
         * @param g Graphics swing object
         */
        private void paintMainSquare(Graphics g){
            g.setColor(Color.WHITE);
            g.fillRoundRect(offsetX, offsetY, rectWidth - resizeX(50, myPanel), rectHeight - resizeY(50, myPanel) * 2, 50, 50);
        }

        /**
         * Paints the finished tasks bar
         *
         * @param g Graphics swing object
         * @param myGanttStatistics gantt statistics class. Gives important statistics
         */
        private void paintFinished(Graphics g, GanttStatistics myGanttStatistics, int spacing) {
            double finishedTaskPercentage = myGanttStatistics.getTotalTasks() > 0 ? (double) myGanttStatistics.getFinishedTasks() / myGanttStatistics.getTotalTasks() : 0;
            drawGraphLine(g, resizeX(150, myPanel), spacing, finishedTaskPercentage, myGanttStatistics.getTotalTasks(), Color.RED);
            g.setColor(Color.DARK_GRAY);
            g.drawString("Finished tasks", offsetX + rectWidth / 7, offsetY - 10 + rectHeight / 7);
        }

        /**
         * Paints the current spent time bar
         *
         * @param g Graphics swing object
         * @param myGanttStatistics gantt statistics class. Gives important statistics
         */
        private void paintCurrentSpentTime(Graphics g, GanttStatistics myGanttStatistics, int spacing){
            double spentTimePercentage = myGanttStatistics.getCurrentSpentTime() > 0 ? (double) myGanttStatistics.getCurrentSpentTime() / myGanttStatistics.getTotalEstimatedTime() : 0;
            drawGraphLine(g, 0, spacing, spentTimePercentage, (int) myGanttStatistics.getTotalEstimatedTime(), Color.RED);
            g.setColor(Color.DARK_GRAY);
            g.drawString("Spent Time", offsetX + rectWidth / 7, offsetY - 10 + rectHeight / 7);
        }

        /**
         * Paints the overall bar
         *
         * @param g Graphics swing object
         * @param myGanttStatistics gantt statistics class. Gives important statistics
         */
        private void paintOverall(Graphics g, GanttStatistics myGanttStatistics, int spacing){
            drawGraphLine(g, 0, spacing, myGanttStatistics.getOverallProgress() / 100, 100, Color.GREEN);
            g.setColor(Color.DARK_GRAY);
            g.drawString("Overall Progress", offsetX + rectWidth / 7, offsetY - 10 + rectHeight / 7);
        }

        /**
         * Draws one line in the bar graph
         *
         * @param g   Graphics swing object
         * @param oX  offset in the x axis
         * @param oY  offset in the y axis
         * @param p   current percentage
         * @param max maximum value in the bar graph
         * @param c   color of the line
         */
        private void drawGraphLine(Graphics g, int oX, int oY, double p, int max, Color c) {
            setOffset(offsetX + oX, offsetY + oY);
            setRect(0.4, 0.021);
            g.setColor(Color.LIGHT_GRAY);
            g.fillRoundRect(offsetX, offsetY, rectWidth, rectHeight, 5, 5);
            g.drawString("0", offsetX - resizeX(15, myPanel), offsetY + rectHeight + resizeY(30, myPanel));
            g.drawString("" + max, offsetX + rectWidth, offsetY + rectHeight + resizeY(30, myPanel));
            setRect(0.4 * p, 0.02);
            g.setColor(c);
            g.fillRoundRect(offsetX, offsetY, rectWidth, rectHeight, 5, 5);
        }
    }

    private final JPanel myPanel;

    private final DrawStatistics myDrawStatistics;

    private final DrawGraph myDrawGraph;

    private final Image logo;

    private int rectWidth;

    private int rectHeight;

    private int offsetX;

    private int offsetY;

    public PlannerPainter(JPanel myPanel) throws IOException {
        this.myDrawStatistics = new DrawStatistics();
        this.myDrawGraph = new DrawGraph();
        this.myPanel = myPanel;

        logo = loadImageFromDir("/icons/big.png");
    }

    /**
     * Gets the draw statistics object. Used to paint the statistics' text on the left
     *
     * @return draw statistics object
     */
    public DrawStatistics getMyDrawStatistics() {
        return myDrawStatistics;
    }

    /**
     * Gets the draw graph object. Used to paint the graph on the right side
     *
     * @return draw graph object
     */
    public DrawGraph getMyDrawGraph(){
        return myDrawGraph;
    }

    /**
     * Paints the logo no top side
     *
     * @param g Graphics swing object
     */
    public void paintLogo(Graphics g) {
        int sizeY = (int) ((1.0 / 10.0) * myPanel.getHeight());     // Resizes the image
        if(sizeY == 0){
            return;
        }

        Image img = logo.getScaledInstance(myPanel.getWidth(), sizeY, Image.SCALE_DEFAULT);

        // Draws the image
        g.drawImage(img, 0, 0, null);
    }

    /**
     * Resizes a rectangle
     *
     * @param x value on the x axis
     * @param y value on the y axis
     */
    private void setRect(double x, double y) {
        rectWidth = (int) (myPanel.getWidth() * x);
        rectHeight = (int) (myPanel.getHeight() * y);
    }

    /**
     * Sets a new offset to the object to be drawn
     *
     * @param x Offset X
     * @param y Offset Y
     */
    private void setOffset(int x, int y) {
        offsetX = x;
        offsetY = y;
    }
}
