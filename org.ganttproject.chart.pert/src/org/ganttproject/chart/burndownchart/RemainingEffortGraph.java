package org.ganttproject.chart.burndownchart;

import biz.ganttproject.core.time.GanttCalendar;
import net.sourceforge.ganttproject.GanttStatistics;
import net.sourceforge.ganttproject.task.Task;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 * <p>
 *
 * RemainingEffortGraph Class - Adds the remaining effort graph to the Burndown Chart
 */
public class RemainingEffortGraph extends Graph {

    public RemainingEffortGraph(GanttStatistics statistics, JPanel panel, int padding, int labelPadding, int pointWidth){
        super(statistics, panel, padding, labelPadding, pointWidth);
        initGraphInfo();

    }

    @Override
    public void initGraphInfo() {

        this.graphInfo = new ArrayList<>();
        resetDataStructure(graphInfo); // days in project fill with zeros

        Task[] myTasks = myGanttStatistics.getMyTaskManager().getTasks();

        for (Task task : myTasks){
            double percentage = task.getCompletionPercentage() / 100.0;
            int completedDuration = (int)(task.getDuration().getLength() * percentage);
            int dayOffSetInProject = calculateOffSetInProject(task.getStart());
            updateRemainingEffortData(task, dayOffSetInProject, completedDuration);

        }
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

        }
        return graphPoints;
    }

    @Override
    public void drawActualFlowLine(Graphics2D g2){
        Stroke oldStroke = g2.getStroke();

        g2.setColor(GraphPanel.COLOR.REMAINING_EFFORT_LINE.color);
        g2.setStroke(GRAPH_STROKE);
        drawLines(g2);

        g2.setStroke(oldStroke);
        g2.setColor(GraphPanel.COLOR.POINT_COLOR.color);
        drawPoints(g2);
    }

    /**
     * Updates the graph info based on the information of each task
     * @param task
     * @param taskDayOffSetInProject
     * @param completedDuration
     */
    private void updateRemainingEffortData(Task task, int taskDayOffSetInProject, int completedDuration){

        for(int i = taskDayOffSetInProject, taskDayCounter = 0, weekendCounter = 0;
            taskDayCounter < completedDuration + weekendCounter; i++, taskDayCounter++) {

            if(i < getTodayOffset() && todayIsWeekend(task, taskDayCounter)) {
                markWeekend(i + 1);
                weekendCounter++;
            } else if(i < getTodayOffset()) {
                markWorkDoneToday(graphInfo, i+1, 1);
            } else if(i >= getTodayOffset() && !todayIsWeekend(task,taskDayCounter)) {
                markWorkDoneToday(graphInfo, getTodayOffset()+1, 1);
            } else
                weekendCounter++;
        }
    }

    /**
     * Updates the graph info with the specified value
     * @param list
     * @param index
     * @param value
     */
    private void markWorkDoneToday(List<Integer> list, int index, int value){
        int sum = list.get(index);
        sum += value;
        list.remove(index);
        list.add(index, sum);
    }

    /**
     * Marks the weekends in the graphInfo
     * @param index
     */
    private void markWeekend(int index){
        if (!(graphInfo.get(index) < 0)){
            graphInfo.remove(index);
            graphInfo.add(index, 0);
        }
    }

    /**
     * Calculates the offset of a given date in the project
     * @param date
     * @return
     */
    private int  calculateOffSetInProject(GanttCalendar date) {
        GanttCalendar dateToConvert = date;

        int year = dateToConvert.getYear() - 1900;
        int month =dateToConvert.getMonth();
        int day = dateToConvert.getDay();

        Date startDate = new Date(year, month, day);

        return (int) myGanttStatistics.getDifferenceDays(myGanttStatistics.getMyTaskManager().getProjectStart(), startDate);
    }

    /**
     * Returns the current day offset in the project calendar
     * @return
     */
    private int getTodayOffset() {
        Date date = new Date();
        return (int) myGanttStatistics.getDifferenceDays(myGanttStatistics.getMyTaskManager().getProjectStart(), date);
    }

}
