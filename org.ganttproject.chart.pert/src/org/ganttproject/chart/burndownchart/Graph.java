package org.ganttproject.chart.burndownchart;

import biz.ganttproject.core.calendar.WeekendCalendarImpl;
import biz.ganttproject.core.time.GanttCalendar;
import net.sourceforge.ganttproject.GanttStatistics;
import net.sourceforge.ganttproject.task.Task;
import org.ganttproject.chart.PanelStyler;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 * <p>
 * <p>
 * Graph Class - Abstract class with variables and functionalities that are helpful
 * for the creation of statistical graphs
 */
public abstract class Graph extends  PanelStyler {

    protected static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    protected GanttStatistics myGanttStatistics;

    protected final JPanel myPanel;
    protected final int padding;
    protected final int labelPadding;
    protected final int pointWidth;
    protected boolean linearMode;

    protected List<Integer> graphInfo;
    protected List<Point> graphPoints;

    protected WeekendCalendarImpl calendar;

    protected Graph(GanttStatistics statistics, JPanel panel, int padding, int labelPadding, int pointWidth, boolean linearMode) {
        this.myGanttStatistics = statistics;
        this.myPanel = panel;
        this.padding = padding;
        this.labelPadding = labelPadding;
        this.pointWidth = pointWidth;
        this.calendar = new WeekendCalendarImpl();
        this.linearMode = linearMode;
    }

    /**
     * Inits the 'graphInfo' list based on the current state of the project
     * or from a file ('Ideal' mode Vs 'History' mode). Decision made by a boolean
     * variable.
     */
    public abstract void initGraphInfo();

    /**
     *Inits the 'graphPoints' list from the 'graphInfo' list, considering
     * the mode of the program ('Ideal' Vs 'History')
     * @param xScale scale for x coordinate
     * @param yScale scale for y coordinate
     * @param maxScore max y-axis value
     * @param tasksTotalDuration sum of the tasks´ duration of the project
     * @return a set of points to be drawn
     */
    public abstract List<Point> buildGraphPoints(double xScale, double yScale, int maxScore, int tasksTotalDuration);

    /**
     * Draws the points and the lines for the specific graph
     * @param g2 Graphics2D
     */
    public abstract void drawActualFlowLine(Graphics2D g2);

    /**
     * Loads the graph info from the current state of the project ('Ideal' mode)
     * @param task
     */
    public abstract void loadGraphInfo(Task task);

    /**
     * Loads the graph info from a saved file ('History' mode)
     * @param folderPath path of the file folder
     * @throws IOException exception
     */
    public abstract void setGraphPointsFromFiles(String folderPath) throws IOException;

    /**
     * Draws all the connective lines between two consecutive points
     * from the graphPoints array
     * @param g2 Graphics2D
     */
    public void drawLines(Graphics2D g2) {
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = resizeX(graphPoints.get(i).x, myPanel);
            int y1 = resizeY(graphPoints.get(i).y, myPanel);
            int x2 = resizeX(graphPoints.get(i + 1).x, myPanel);
            int y2 = resizeY(graphPoints.get(i + 1).y, myPanel);
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * Draws all the points from the graphPoints array
     * @param g2 Graphics2D
     */
    public void drawPoints(Graphics2D g2) {
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = resizeX(graphPoints.get(i).x - pointWidth / 2, myPanel);
            int y = resizeY(graphPoints.get(i).y - pointWidth / 2, myPanel);
            int ovalW = resizeX(pointWidth, myPanel);
            int ovalH = resizeY(pointWidth, myPanel);
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }

    /**
     * Resets a given list (each index filled with 0)
     * @param list structure to reset
     */
    public void resetDataStructure(List<Integer> list) {
        for (int i = 0; i < myGanttStatistics.getTotalEstimatedTime() + 2; i++) {
            list.add(i, 0);
        }
    }

    /**
     *  Creates a point in the graph
     * @param x coordinate
     * @param yReference previous y reference to consider
     * @param xScale scale for x coordinate
     * @param yScale scale for y coordinate
     * @param maxScore max y-axis value
     * @param tasksTotalDuration sum of the tasks´ duration of the project
     * @return a point in the graph
     */
    public Point createPoint(int x, int yReference, double xScale, double yScale, int maxScore, int tasksTotalDuration) {
        int x1 = (int) (x * xScale + padding + labelPadding);
        int y1 = (int) ((maxScore - tasksTotalDuration + yReference + graphInfo.get(x)) * yScale + padding);

        return new Point(x1, y1);
    }

    /**
     *  Calculates the offset in the project of a given date
     * @param date date to calculate
     * @return offset in the project
     */
    public int calculateOffSetInProject(GanttCalendar date){
        GanttCalendar dateToConvert = date;

        int year = dateToConvert.getYear() - 1900;
        int month = dateToConvert.getMonth();
        int day = dateToConvert.getDay();

        Date startDate = new Date(year, month, day);

        return (int) myGanttStatistics.getDifferenceDays(myGanttStatistics.getMyTaskManager().getProjectStart(), startDate);
    }

}
