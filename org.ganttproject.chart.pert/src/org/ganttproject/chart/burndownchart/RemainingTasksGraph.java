package org.ganttproject.chart.burndownchart;

import net.sourceforge.ganttproject.GanttStatistics;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class RemainingTasksGraph extends Graph {

    public RemainingTasksGraph(GanttStatistics statistics, JPanel panel, int padding, int labelPadding, int pointWidth){
        super(statistics, panel, padding, labelPadding, pointWidth);
        initGraphInfo();
    }

    @Override
    public void initGraphInfo() {
        graphInfo = new ArrayList<>();
        resetDataStructure(graphInfo); // days in project fill with zeros

        int taskCount = 0;
        int index = 0;

        while (taskCount < myGanttStatistics.getMyTaskManager().getTaskCount()) {
            if (myGanttStatistics.getMyTaskManager().getTask(index) != null) {
                taskCount++;
                if (myGanttStatistics.getMyTaskManager().getTask(index).getCompletionPercentage() == 100) {
                    int dayInProject = calculateDiffDate(index);
                    int sum = graphInfo.get(dayInProject);
                    sum += myGanttStatistics.getMyTaskManager().getTask(index).getDuration().getLength(); // task duration without weekends
                    graphInfo.remove(dayInProject); // MAGIA
                    graphInfo.add(dayInProject, sum);
                }
            }
            index++;
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

}
