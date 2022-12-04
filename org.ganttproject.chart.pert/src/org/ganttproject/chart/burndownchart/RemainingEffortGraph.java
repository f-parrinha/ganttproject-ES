package org.ganttproject.chart.burndownchart;

import biz.ganttproject.core.time.GanttCalendar;
import net.sourceforge.ganttproject.GanttStatistics;
import net.sourceforge.ganttproject.io.BurndownDataIO;
import net.sourceforge.ganttproject.task.Task;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
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

    /**
     * A list that keeps track of the points that should be drawn when in "history" mode
     * '0' -> draw point    '-1' -> do not draw the point
     */
    private List<Integer> flag = new ArrayList<>();

    public RemainingEffortGraph(GanttStatistics statistics, JPanel panel, int padding, int labelPadding, int pointWidth, boolean linearMode) {
        super(statistics, panel, padding, labelPadding, pointWidth, linearMode);
        initGraphInfo();
    }

    @Override
    public void initGraphInfo() {
        this.graphInfo = new ArrayList<>();
        resetDataStructure(graphInfo);                          // days in project filled with '0'
        resetFlags();                                           // flag for each filled with '-1'

        if(linearMode){
            Task[] myTasks = myGanttStatistics.getMyTaskManager().getTasks();
            for (Task task : myTasks)
                loadGraphInfo(task);
        } else {
            try {
                setGraphPointsFromFiles(sprintPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<Point> buildGraphPoints(double xScale, double yScale, int maxScore, int tasksTotalDuration) {
        this.graphPoints = new ArrayList<>();

        Point firstPoint = createPoint(0, 0, xScale, yScale, maxScore, tasksTotalDuration);
        graphPoints.add(firstPoint);

        for (int i = 1; i < graphInfo.size() - 2; i++) {
            if ( flag.get(i) == 0 || linearMode) { // if we are in 'history' mode, cheks the flag for that day
                Point middlePoint = createPoint(i, 0, xScale, yScale, maxScore, tasksTotalDuration);
                graphPoints.add(middlePoint);
            }
        }

        Point lastPoint = createPoint(graphInfo.size()-2, 0, xScale, yScale, maxScore, tasksTotalDuration);
        graphPoints.add(lastPoint);

        return graphPoints;
    }


    /**
     * Draws the line with the current work done, the green one
     *
     * @param g2
     */
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

    @Override
    public void loadGraphInfo(Task task) {

        double percentage = task.getCompletionPercentage() / 100.0;
        int completedDuration = (int) (task.getDuration().getLength() * percentage);
        int dayOffSetInProject = calculateOffSetInProject(task.getStart());

        int weekends = 0;
        for (int currDay = 0; currDay < completedDuration + weekends; currDay++) {
            GanttCalendar startDate = task.getStart().clone();
            startDate.add(Calendar.DATE, currDay);

            if (calendar.isWeekend(startDate.getTime()))
                weekends++;
            else
                markWorkDoneToday(graphInfo, dayOffSetInProject + 1 + currDay, 1);// + 1 because the point with offset 0 is the starting point
        }
    }

    @Override
    public void setGraphPointsFromFiles(String folderPath) throws IOException {
        BurndownDataIO data = new BurndownDataIO();
        data.changeSprintFolder(folderPath);
        int[] dataFromFiles = data.getPastRemainingEffort(graphInfo.size());
        for (int currFileDay = 0; currFileDay < dataFromFiles.length; currFileDay++)
            if (dataFromFiles[currFileDay] != -1) setWorkDone(graphInfo, currFileDay, dataFromFiles[currFileDay]);
    }

    /**
     * Updates the graph info by overwriting with the specified value, from the given position until the end of the list.
     * The flag for the 'startIndex' date is updated to 0 -> a point should be drawn in that position.
     * @param list list to be updated
     * @param startIndex starting index of updating
     * @param value value to be added
     */
    private void setWorkDone(List<Integer> list, int startIndex, int value) {
        for (int index = startIndex; index < list.size(); index++) {
            list.set(index, value);
        }
        flag.set(startIndex, 0); //updates the flag -> a point should be drawn in that day
    }

    /**
     * Updates the graph info by adding the specified value, from the given position until the end of the list.
     * Used when in 'Ideal' mode.
     * @param list list to be updated
     * @param startIndex starting index of updating
     * @param value value to be added
     */
    private void markWorkDoneToday(List<Integer> list, int startIndex, int value) {
        for (int index = startIndex; index < list.size(); index++) {
            int duration = list.get(index);
            duration += value;
            list.set(index, duration);
        }
    }

    /**
     * Initializes the flag´s list with '-1' (no points to be drawn)
     */
    private void resetFlags() {
        for(int i = 0; i < graphInfo.size(); i++)
            flag.add(i, -1);
    }

}
