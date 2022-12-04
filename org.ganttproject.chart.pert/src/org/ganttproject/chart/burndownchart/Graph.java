package org.ganttproject.chart.burndownchart;

import biz.ganttproject.core.calendar.WeekendCalendarImpl;
import biz.ganttproject.core.time.GanttCalendar;
import net.sourceforge.ganttproject.GanttStatistics;
import net.sourceforge.ganttproject.task.Task;
import org.ganttproject.chart.PanelStyler;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;

public abstract class Graph extends PanelStyler {

    protected static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    protected GanttStatistics myGanttStatistics;

    protected final JPanel myPanel;
    protected final int padding;
    protected final int labelPadding;
    protected final int pointWidth;

    protected List<Integer> graphInfo;
    protected List<Point> graphPoints;

    protected WeekendCalendarImpl calendar;

    protected Graph(GanttStatistics statistics, JPanel panel, int padding, int labelPadding, int pointWidth) {
        this.myGanttStatistics = statistics;
        this.myPanel = panel;
        this.padding = padding;
        this.labelPadding = labelPadding;
        this.pointWidth = pointWidth;
        this.calendar = new WeekendCalendarImpl();
    }

    /**
     * Inits the 'graphInfo' array list
     */
    public abstract void initGraphInfo();

    /**
     *
     * @param xScale
     * @param yScale
     * @param maxScore
     * @param tasksTotalDuration
     * @return
     */
    public abstract List<Point> buildGraphPoints(double xScale, double yScale, int maxScore, int tasksTotalDuration);

    /**
     *
     * @param g2
     */
    public abstract void drawActualFlowLine(Graphics2D g2);


    /**
     * Draws all the connective lines between two consecutive points
     * from the graphPoints array
     * @param g2
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
     * @param g2
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
     *
     * @param list
     */
    public void resetDataStructure(List<Integer> list) {
        for (int i = 0; i < myGanttStatistics.getTotalEstimatedTime() + 2; i++) {
            list.add(i, 0);

        }
    }

    /**
     *
     * @param index
     * @return
     */
    public int calculateDiffDate(int index) {
        GanttCalendar dateToConvert = myGanttStatistics.getMyTaskManager().getTask(index).getEnd();

        int year = dateToConvert.getYear() - 1900;
        int month = dateToConvert.getMonth();
        int day = dateToConvert.getDay();

        Date endDate = new Date(year, month, day);

        return (int) myGanttStatistics.getDifferenceDays(myGanttStatistics.getMyTaskManager().getProjectStart(), endDate);
    }

    /**
     *
     * @param task
     * @param offSetDayInProject
     * @return
     */
    public boolean todayIsWeekend(Task task, int offSetDayInProject) {
        GanttCalendar dateToConvert = task.getStart();

        int year = dateToConvert.getYear() - 1900;
        int month = dateToConvert.getMonth();
        int day = dateToConvert.getDay() + offSetDayInProject;

        Date today = new Date(year, month, day);
        return calendar.isWeekend(today);
    }
}
