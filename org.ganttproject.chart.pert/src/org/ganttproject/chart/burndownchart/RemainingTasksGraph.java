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



public class RemainingTasksGraph extends Graph {

    private boolean linearMode;

    public RemainingTasksGraph(GanttStatistics statistics, JPanel panel, int padding, int labelPadding, int pointWidth, boolean linearMode){
        super(statistics, panel, padding, labelPadding, pointWidth);
        this.linearMode = linearMode;
        initGraphInfo();
    }

    @Override
    public void initGraphInfo() {
        graphInfo = new ArrayList<>();
        resetDataStructure(graphInfo); // days in project fill with zeros

        if(linearMode){
            Task[] myTasks = myGanttStatistics.getMyTaskManager().getTasks();
            for (Task task : myTasks) {
                if (task.getCompletionPercentage() == 100) {
                    int dayInProject = calculateOffSetInProject(task.getEnd());
                    int sum = graphInfo.get(dayInProject);
                    sum += task.getDuration().getLength(); // task duration without weekends
                    graphInfo.remove(dayInProject); // MAGIA
                    graphInfo.add(dayInProject, sum);
                }
            }
        } else {
            try {
                setGraphPointsFromFiles(MUDAR, myGanttStatistics.getSumOfTaskDurations());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

        for (int i = 0; i < graphInfo.size(); i++) {

            if (graphInfo.get(i) > 0){
                int x1 = (int) (i * xScale + padding + labelPadding);
                int y1 = (int) ((maxScore - tasksTotalDuration + yReference + graphInfo.get(i)) * yScale + padding);
                Point p = new Point(x1, y1);
                graphPoints.add(p);
                if(linearMode)
                    yReference += graphInfo.get(i);
            }
        }

        return graphPoints;
    }

    @Override
    public void drawActualFlowLine(Graphics2D g2){
        Stroke oldStroke = g2.getStroke();

        g2.setColor(GraphPanel.COLOR.ACTUAL_LINE.color);
        g2.setStroke(GRAPH_STROKE);
        drawLines(g2);

        g2.setStroke(oldStroke);
        g2.setColor(GraphPanel.COLOR.POINT_COLOR.color);
        drawPoints(g2);
    }
    private int calculateOffSetInProject(GanttCalendar date) {
        GanttCalendar dateToConvert = date;

        int year = dateToConvert.getYear() - 1900;
        int month = dateToConvert.getMonth();
        int day = dateToConvert.getDay();

        Date startDate = new Date(year, month, day);

        return (int) myGanttStatistics.getDifferenceDays(myGanttStatistics.getMyTaskManager().getProjectStart(), startDate);
    }

    public void setGraphPointsFromFiles(String folderPath, int totalEffort) throws IOException {
        BurndownDataIO data = new BurndownDataIO();
        data.changeSprintFolder(folderPath);
        int[] dataFromFiles = data.getPastRemainingTasks(graphInfo.size());
        for (int currFileDay = 0; currFileDay < dataFromFiles.length; currFileDay++)
            graphInfo.set(currFileDay, dataFromFiles[currFileDay]);

        System.out.println(graphInfo);
    }
}
