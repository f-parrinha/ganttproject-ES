package org.ganttproject.chart.burndownchart;

import biz.ganttproject.core.time.GanttCalendar;
import net.sourceforge.ganttproject.GanttStatistics;
import net.sourceforge.ganttproject.io.BurndownDataIO;
import net.sourceforge.ganttproject.task.Task;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RemainingEffortGraph extends Graph {

    public RemainingEffortGraph(GanttStatistics statistics, JPanel panel, int padding, int labelPadding, int pointWidth) {
        super(statistics, panel, padding, labelPadding, pointWidth);
        initGraphInfo();

    }

    @Override
    public List<Point> buildGraphPoints(double xScale, double yScale, int maxScore, int tasksTotalDuration) {
        this.graphPoints = new ArrayList<>();

        int originX = (padding + labelPadding);
        int originY = (int) ((maxScore - tasksTotalDuration) * yScale + padding);

        Point pointReference = new Point(originX, originY);

        graphPoints.add(pointReference);

        int yReference = 0;

        for (int i = 1; i < graphInfo.size() - 1; i++) {

            int x1 = (int) (i * xScale + padding + labelPadding);
            int y1 = (int) ((maxScore - tasksTotalDuration + yReference + graphInfo.get(i)) * yScale + padding);
            Point p = new Point(x1, y1);
            graphPoints.add(p);
            yReference += graphInfo.get(i);
            System.out.println("yReference" + yReference);
        }

        return graphPoints;
    }

    @Override
    public void drawActualFlowLine(Graphics2D g2) {
        Stroke oldStroke = g2.getStroke();

        g2.setColor(GraphPanel.COLOR.REMAINING_EFFORT_LINE.color);
        g2.setStroke(GRAPH_STROKE);
        System.out.println("SIZE " + graphPoints.size());
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = resizeX(graphPoints.get(i).x, myPanel);
            int y1 = resizeY(graphPoints.get(i).y, myPanel);
            int x2 = resizeX(graphPoints.get(i + 1).x, myPanel);
            int y2 = resizeY(graphPoints.get(i + 1).y, myPanel);
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(GraphPanel.COLOR.POINT_COLOR.color);
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = resizeX(graphPoints.get(i).x - pointWidth / 2, myPanel);
            int y = resizeY(graphPoints.get(i).y - pointWidth / 2, myPanel);
            int ovalW = resizeX(pointWidth, myPanel);
            int ovalH = resizeY(pointWidth, myPanel);
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }

    @Override
    public void initGraphInfo() {

        this.graphInfo = new ArrayList<>();
        myGanttStatistics.resetDataStructure(graphInfo); // days in project fill with zeros

        Task[] myTasks = myGanttStatistics.getMyTaskManager().getTasks();

        for (Task task : myTasks) {
            double percentage = task.getCompletionPercentage() / 100.0;
            int completedDuration = (int) (task.getDuration().getLength() * percentage);
            int dayOffSetInProject = calculateOffSetInProject(task.getStart());
            updateRemainingEffortData(task, dayOffSetInProject, completedDuration);

        }
    }

    /**
     * Updates the graph info based of the information of each task
     *
     * @param task
     * @param taskDayOffSetInProject
     * @param completedDuration
     */
    private void updateRemainingEffortData(Task task, int taskDayOffSetInProject, int completedDuration) {

        for (int i = taskDayOffSetInProject, j = 0, aux = 0; j < completedDuration + aux; i++) {
            if (i < getTodayOffset()) {
                if (myGanttStatistics.todayIsWeekend(task, j)) {
                    markWeekend(i + 1);
                    j++;
                    aux++;
                } else {
                    markWorkDoneToday(graphInfo, i + 1, 1);
                    j++;
                }
            } else {
                if (!myGanttStatistics.todayIsWeekend(task, j)) {
                    markWorkDoneToday(graphInfo, getTodayOffset() + 1, 1);
                    j++;
                } else {
                    j++;
                    aux++;
                }
            }
        }
    }


    public void setGraphPointsFromFiles(String folderPath, int totalEffort) throws IOException {
        BurndownDataIO data = new BurndownDataIO();
        data.changeSprintFolder(folderPath);
        double[] dataFromFiles = data.getPastRemainingEffort(graphPoints.size(), totalEffort);
        for (int currFileDay = 0; currFileDay < dataFromFiles.length; currFileDay++)
            if (dataFromFiles[currFileDay] != -1) graphInfo.set(currFileDay, (int) dataFromFiles[currFileDay]);
    }

    /**
     * Updates the graph info with the specified value
     *
     * @param list
     * @param index
     * @param value
     */
    private void markWorkDoneToday(List<Integer> list, int index, int value) {
        int sum = list.get(index);
        sum += value;
        list.remove(index);
        list.add(index, sum);
    }

    /**
     * Marks the weekends in the graphInfo
     *
     * @param index
     */
    private void markWeekend(int index) {
        if (!(graphInfo.get(index) < 0)) {
            graphInfo.remove(index);
            graphInfo.add(index, 0);
        }
    }

    /**
     * Calculates the offset of a given date in the project
     *
     * @param date
     * @return
     */
    private int calculateOffSetInProject(GanttCalendar date) {
        GanttCalendar dateToConvert = date;

        int year = dateToConvert.getYear() - 1900;
        int month = dateToConvert.getMonth();
        int day = dateToConvert.getDay();

        Date startDate = new Date(year, month, day);

        return (int) myGanttStatistics.getDifferenceDays(myGanttStatistics.getMyTaskManager().getProjectStart(), startDate);
    }

    /**
     * Returns the current day offset in the project calendar
     *
     * @return
     */
    private int getTodayOffset() {
        Date date = new Date();
        return (int) myGanttStatistics.getDifferenceDays(myGanttStatistics.getMyTaskManager().getProjectStart(), date);
    }
}
