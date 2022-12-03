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


/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 * <p>
 * <p>
 * RemainingEffortGraph Class - Adds the remaining effort graph to the Burndown Chart
 */
public class RemainingEffortGraph extends Graph {

    public RemainingEffortGraph(GanttStatistics statistics, JPanel panel, int padding, int labelPadding, int pointWidth) {
        super(statistics, panel, padding, labelPadding, pointWidth);
        initGraphInfo();
    }

    @Override
    public void initGraphInfo() {
        this.graphInfo = new ArrayList<>();
        resetDataStructure(graphInfo); // days in project fill with zeros

        Task[] myTasks = myGanttStatistics.getMyTaskManager().getTasks();

        for (Task task : myTasks) {
            double percentage = task.getCompletionPercentage() / 100.0;
            int completedDuration = (int) (task.getDuration().getLength() * percentage);
            int dayOffSetInProject = calculateOffSetInProject(task.getStart());
            // + 1 because the point with offset 0 is the starting point
            markWorkDoneToday(graphInfo, dayOffSetInProject + 1, completedDuration);
        }

        try {
            setGraphPointsFromFiles("/home/pedro/Desktop/teste", myGanttStatistics.getSumOfTaskDurations());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Point> buildGraphPoints(double xScale, double yScale, int maxScore, int tasksTotalDuration) {
        this.graphPoints = new ArrayList<>();

        int originX = (padding + labelPadding);
        int originY = (int) ((maxScore - tasksTotalDuration) * yScale + padding);

        Point pointReference = new Point(originX, originY);

        graphPoints.add(pointReference);

        for (int i = 1; i < graphInfo.size() - 1; i++) {
            int x1 = (int) (i * xScale + padding + labelPadding);
            int y1 = (int) ((maxScore - tasksTotalDuration + graphInfo.get(i)) * yScale + padding);
            Point p = new Point(x1, y1);
            graphPoints.add(p);
        }
        return graphPoints;
    }


    @Override
    public void drawActualFlowLine(Graphics2D g2) {
        Stroke oldStroke = g2.getStroke();

        g2.setColor(GraphPanel.COLOR.REMAINING_EFFORT_LINE.color);
        g2.setStroke(GRAPH_STROKE);
        drawLines(g2);

        g2.setStroke(oldStroke);
        g2.setColor(GraphPanel.COLOR.POINT_COLOR.color);
        drawPoints(g2);
    }



    public void setGraphPointsFromFiles(String folderPath, int totalEffort) throws IOException {
        BurndownDataIO data = new BurndownDataIO();
        data.changeSprintFolder(folderPath);
        double[] dataFromFiles = data.getPastRemainingEffort(graphInfo.size());
        for (int currFileDay = 0; currFileDay < dataFromFiles.length; currFileDay++)
            if (dataFromFiles[currFileDay] != -1) graphInfo.set(currFileDay, (int) dataFromFiles[currFileDay]);
    }

    /**
     * Updates the graph info with the specified value
     *
     * @param list
     * @param startIndex
     * @param value
     */
    private void markWorkDoneToday(List<Integer> list, int startIndex, int value) {
        for (int index = startIndex; index < list.size(); index++) {
            int sum = list.get(index);
            sum += value;
            list.remove(index);
            list.add(index, sum);
        }
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
