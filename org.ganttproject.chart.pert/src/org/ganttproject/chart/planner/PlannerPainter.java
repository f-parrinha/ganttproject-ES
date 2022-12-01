package org.ganttproject.chart.planner;

import org.ganttproject.chart.GanttStatistics;
import org.ganttproject.chart.PanelStyler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class PlannerPainter extends PanelStyler {

    private final JPanel myPanel;

    private int rectWidth;

    private int rectHeight;

    private int offsetX;

    private int offsetY;

    private Image logo;

    public PlannerPainter(JPanel myPanel) throws IOException {
        this.myPanel = myPanel;

        loadImageFromDir("/icons/big.png");
    }

    /**
     * Paints the entire statistics area
     *
     * @param g Graphics swing object
     */
    public void paintStatistics(Graphics g, GanttStatistics myGanttStatistics) {
        updateFontSize(25, myPanel);
        setOffset(50, 50);
        setRect(2.0 / 6.0, 5.0 / 6.0);
        setOffset(resizeX(50, myPanel), resizeY(50, myPanel) + rectHeight / 6);

        // Main square
        g.setColor(Color.WHITE);
        g.fillRoundRect(offsetX, offsetY, rectWidth - resizeX(50, myPanel) * 2, rectHeight - resizeY(50, myPanel) * 2, 50, 50);

        // Statistics
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font(fontStyle, Font.PLAIN, fontSize));
        g.drawString("Total number of tasks: " + myGanttStatistics.getTotalTasks(), rectWidth / 8 + resizeX(50, myPanel), offsetY + rectHeight / 7);
        g.drawString("Current time spent: " + myGanttStatistics.getCurrentSpentTime(), rectWidth / 8 + offsetX, offsetY + rectHeight * 2 / 7);
        g.drawString("Total estimated time: " + myGanttStatistics.getTotalEstimatedTime(), rectWidth / 8 + offsetX, offsetY + rectHeight * 3 / 7);
        g.drawString("Total finished tasks: " + myGanttStatistics.getFinishedTasks(), rectWidth / 8 + offsetX, offsetY + rectHeight * 4 / 7);
        g.drawString("Overall progress: " + myGanttStatistics.getOverallProgress() + "%", rectWidth / 8 + offsetX, offsetY + rectHeight * 5 / 7);
    }

    /**
     * Paints the entire bar graph
     *
     * @param g Graphics swing object
     */
    public void paintGraphic(Graphics g, GanttStatistics myGanttStatistics) {
        int spacing = rectHeight / 5;     // Spacing between bars
        updateFontSize(25, myPanel);
        setOffset(rectWidth + resizeX(50, myPanel) * 2, resizeY(50, myPanel) + rectHeight / 6);
        setRect(3.5 / 6.0, 5.0 / 6.0);

        // Main square
        g.setColor(Color.WHITE);
        g.fillRoundRect(offsetX, offsetY, rectWidth - resizeX(50, myPanel), rectHeight - resizeY(50, myPanel) * 2, 50, 50);

        // Finished tasks
        double finishedTaskPercentage = myGanttStatistics.getTotalTasks() > 0 ? (double) myGanttStatistics.getFinishedTasks() / myGanttStatistics.getTotalTasks() : 0;
        drawGraphLine(g, resizeX(150, myPanel), spacing, finishedTaskPercentage, myGanttStatistics.getTotalTasks(), Color.RED);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Finished tasks", offsetX + rectWidth / 7, offsetY - 10 + rectHeight / 7);

        // Current spent time
        double spentTimePercentage = myGanttStatistics.getCurrentSpentTime() > 0 ? (double) myGanttStatistics.getCurrentSpentTime() / myGanttStatistics.getTotalEstimatedTime() : 0;
        drawGraphLine(g, 0, spacing, spentTimePercentage, (int) myGanttStatistics.getTotalEstimatedTime(), Color.RED);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Spent Time", offsetX + rectWidth / 7, offsetY - 10 + rectHeight / 7);

        // Overall progress
        drawGraphLine(g, 0, spacing, myGanttStatistics.getOverallProgress() / 100, 100, Color.GREEN);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Overall Progress", offsetX + rectWidth / 7, offsetY - 10 + rectHeight / 7);
    }

    /**
     * Paints the logo no top side
     *
     * @param g Graphics swing object
     */
    public void paintLogo(Graphics g) {
        // Resizes the image
        int sizeY = (int) ((1.0 / 10.0) * myPanel.getHeight());
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

    /**
     * Loads an image from the resources' folder, with a given directory
     *
     * @param directory directory in resources folder
     */
    private void loadImageFromDir(String directory) throws IOException {
        URL url = PlannerPanel.class.getResource(directory);
        assert url != null;
        logo = ImageIO.read(url);
    }
}
