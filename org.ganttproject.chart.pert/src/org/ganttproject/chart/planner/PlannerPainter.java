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

        private static final int OFFSET = 50;

        private static final int ARC_SIZE = 50;

        public void paint(Graphics g, GanttStatistics myGanttStatistics) {
            updateFontSize(25, myPanel);
            setOffset(OFFSET, OFFSET);
            setRect(2.0 / 6.0, 5.0 / 6.0);
            setOffset(resizeX(OFFSET, myPanel), resizeY(OFFSET, myPanel) + rectHeight / 6);

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
            g.fillRoundRect(offsetX, offsetY,
                    rectWidth - resizeX(OFFSET, myPanel) * 2, rectHeight - resizeY(OFFSET, myPanel) * 2,
                    ARC_SIZE, ARC_SIZE);
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
            g.drawString("Current time spent: " + Math.max(0, myGanttStatistics.getCurrentSpentTime()), rectWidth / 8 + offsetX, offsetY + rectHeight * 2 / 7);
            g.drawString("Total estimated time: " + myGanttStatistics.getTotalEstimatedTime(), rectWidth / 8 + offsetX, offsetY + rectHeight * 3 / 7);
            g.drawString("Total finished tasks: " + myGanttStatistics.getFinishedTasks(), rectWidth / 8 + offsetX, offsetY + rectHeight * 4 / 7);
            g.drawString("Overall progress: " + myGanttStatistics.getOverallProgress() + "%", rectWidth / 8 + offsetX, offsetY + rectHeight * 5 / 7);
        }
    }

    /**
     * DrawGraph class - Paints the entire bar graph
     */
    protected class DrawGraph {

        private static final int SPACING_FACTOR = 5;     // Spacing between bars in the graph

        private GanttStatistics myGanttStatistics;

        private long daysAfterEstimation;

        /**
         * Paints the entire graphics
         *
         * @param g Graphics swing object
         * @param myGanttStatistics gantt statistics class. Gives important statistics
         */
        public void paint(Graphics g, GanttStatistics myGanttStatistics) {
            initVariables(myGanttStatistics);

            int spacing = rectHeight / SPACING_FACTOR;
            updateFontSize(25, myPanel);
            setOffset(rectWidth + resizeX(50, myPanel) * 2, resizeY(50, myPanel) + rectHeight / 6);
            setRect(3.5 / 6.0, 5.0 / 6.0);

            paintBackground(g);
            paintFinished(g, spacing);
            paintSpentTime(g, spacing);
            paintOverall(g, spacing);
        }

        /**
         * Inits variables related to gantt statistics
         *      Since the load is asynchronous, initializing gantt statistics in the constructor would return null
         */
        private void initVariables(GanttStatistics myGanttStatistics) {
            this.myGanttStatistics = myGanttStatistics;
            this.daysAfterEstimation = this.myGanttStatistics.getCurrentSpentTime() - this.myGanttStatistics.getTotalEstimatedTime();
        }

        /**
         * Paints the main square
         *
         * @param g Graphics swing object
         */
        private void paintBackground(Graphics g){
            g.setColor(Color.WHITE);
            g.fillRoundRect(offsetX, offsetY, rectWidth - resizeX(50, myPanel), rectHeight - resizeY(50, myPanel) * 2, 50, 50);
        }

        /**
         * Paints the finished tasks bar
         *
         * @param g Graphics swing object
         * @param spacing spacing between graph lines
         */
        private void paintFinished(Graphics g, int spacing) {
            double finishedTaskPercentage = this.myGanttStatistics.getTotalTasks() > 0 ?
                    (double) this.myGanttStatistics.getFinishedTasks() / this.myGanttStatistics.getTotalTasks() : 0;

            int yPos = drawGraphLine(g, resizeX(150, myPanel), spacing, finishedTaskPercentage, myGanttStatistics.getTotalTasks(), Color.RED);
            g.setColor(Color.DARK_GRAY);
            g.drawString("Finished Tasks", getTextPosition(yPos).x, getTextPosition(yPos).y);
        }

        /**
         * Logic for the spent time in the graph
         *      Checks if the bar is to be drawn, or if it is needed to advise the user of
         *      the lateness or if the project has been completed
         *
         * @param g Graphics swing object
         * @param spacing spacing between graph lines
         */
        private void paintSpentTime(Graphics g, int spacing){
            if(checkProjectIsFinished()){
                paintProjectFinishedText(g, spacing);
            }
            else if(checkProjectIsLate()) {
                paintProjectLateText(g, spacing);
            }
            else {
                paintSpentTimeLine(g, spacing);
            }
        }

        /**
         * Paints the overall bar
         *
         * @param g Graphics swing object
         * @param spacing spacing between graph lines
         */
        private void paintOverall(Graphics g, int spacing){
            int yPos = drawGraphLine(g, 0, spacing, myGanttStatistics.getOverallProgress() / 100, 100, Color.GREEN);
            g.setColor(Color.DARK_GRAY);
            g.drawString("Overall Progress", getTextPosition(yPos).x, getTextPosition(yPos).y);
        }

        /**
         * Paints the current spent time bar
         *
         * @param g Graphics swing object
         * @param spacing spacing between graph lines
         */
        private void paintSpentTimeLine(Graphics g, int spacing){
            double spentTimePercentage = this.myGanttStatistics.getCurrentSpentTime() > 0 ?
                    (double) this.myGanttStatistics.getCurrentSpentTime() / this.myGanttStatistics.getTotalEstimatedTime() : 0;

            int yPos = drawGraphLine(g, 0, spacing, spentTimePercentage, (int) this.myGanttStatistics.getTotalEstimatedTime(), Color.RED);
            g.setColor(Color.DARK_GRAY);
            g.drawString("Spent Time", getTextPosition(yPos).x, getTextPosition(yPos).y);
        }

        /**
         * Draws the text for advising that the project is late, including the number of days
         *
         * @param g Graphics sing object
         * @param spacing spacing between graph lines
         */
        private void paintProjectFinishedText(Graphics g, int spacing){
            g.setColor(Color.GREEN);
            g.drawString(getProjectFinishedText(), getTextPosition(150).x, (2 * spacing) + getTextPosition(150).y);
        }

        /**
         * Draws the text for advising that the project is late, including the number of days
         *
         * @param g Graphics sing object
         */
        private void paintProjectLateText(Graphics g, int spacing){
            String text = "Project is late " + daysAfterEstimation + " days!";

            g.setColor(Color.RED);
            g.drawString(text, getTextPosition(150).x, (2 * spacing) + getTextPosition(150).y);
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
        private int drawGraphLine(Graphics g, int oX, int oY, double p, int max, Color c) {
            int mainBarX =(int)(myPanel.getWidth() * 0.4);
            int mainBarY =(int)(myPanel.getHeight() * 0.021);
            int progressionBarX = (int)(myPanel.getWidth() * 0.4 * p);
            int progressionBarY = (int)(myPanel.getHeight() * 0.021);
            int offset = 10;


            setOffset(offsetX + oX, offsetY + oY);
            g.setColor(Color.LIGHT_GRAY);
            g.fillRoundRect(offsetX, offsetY, mainBarX, mainBarY, 5, 5);
            g.drawString("0", offsetX - resizeX(15, myPanel), offsetY + mainBarY + resizeY(30, myPanel));
            g.drawString("" + max, offsetX + mainBarX, offsetY + mainBarY + resizeY(30, myPanel));
            g.setColor(c);
            g.fillRoundRect(offsetX + (offset/2), offsetY + (offset/2),
                    progressionBarX - offset,progressionBarY - offset, 5, 5);

            return mainBarY;
        }

        /**
         * Gets the alert text for the project completion
         *
         * @return project completion alert text
         */
        private String getProjectFinishedText(){
            String text;

            if(checkProjectIsEarly()){
                text = "Project has been completed! Days early: " + Math.abs(daysAfterEstimation);
            }
            else{
                text = "Project has been completed! Days since completion: " + daysAfterEstimation;
            }

            return text;
        }

        /** Returns the position for all the drawn tet
         *
         * @return text position
         */
        private Point getTextPosition(int yPos){
            int textOffsetX = 150;
            int textOffsetY = 25;
            int x = offsetX - resizeX(textOffsetX, myPanel) + rectWidth / 7;
            int y = offsetY - resizeY(textOffsetY, myPanel) + yPos / 7;

            return new Point(x, y);
        }

        /**
         * Checks if the project is late or not
         *
         * @return true - late, false - not late
         */
        private boolean checkProjectIsLate() {
            return myGanttStatistics.getCurrentSpentTime() > myGanttStatistics.getTotalEstimatedTime();
        }

        /**
         * Checks if the project is late or not
         *
         * @return true - late, false - not late
         */
        private boolean checkProjectIsEarly() {
            return myGanttStatistics.getCurrentSpentTime() < myGanttStatistics.getTotalEstimatedTime();
        }

        /**
         * Checks if the project is finished
         *
         * @return true - finished, false - not finished
         */
        private boolean checkProjectIsFinished() {
            return myGanttStatistics.getOverallProgress() == 100;
        }
    }

    private static final int LOGO_SIZE_Y = 10;

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
        int sizeY = (int) ((1.0 / LOGO_SIZE_Y) * myPanel.getHeight());     // Resizes the image
        if(sizeY == 0){
            return;
        }

        Image img = logo.getScaledInstance(myPanel.getWidth(), sizeY, Image.SCALE_DEFAULT);
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
