package org.ganttproject.chart.burndownchart;

import net.sourceforge.ganttproject.GanttStatistics;
import net.sourceforge.ganttproject.io.BurndownDataIO;
import net.sourceforge.ganttproject.task.Task;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
* @author Francisco Parrinha
* @author Martin Magdalinchev
* @author Bernardo Atalaia
* @author Carlos Soares
* @author Pedro Inácio
* <p>
 * <p>
 * RemainingTasksGraph Class - Adds the remaining tasks graph to the Burndown Chart
 */
public class RemainingTasksGraph extends Graph {

    public RemainingTasksGraph(GanttStatistics statistics, JPanel panel, int padding, int labelPadding, int pointWidth, boolean linearMode){
        super(statistics, panel, padding, labelPadding, pointWidth, linearMode);
        initGraphInfo();
    }

    @Override
    public void initGraphInfo() {
        graphInfo = new ArrayList<>();
        resetDataStructure(graphInfo); // days in project filled with '0'

        if(linearMode) {
            Task[] myTasks = myGanttStatistics.getMyTaskManager().getTasks();
            for (Task task : myTasks)
                loadGraphInfo(task);
        } else
            try {
                setGraphPointsFromFiles(sprintPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    @Override
    public List<Point> buildGraphPoints(double xScale, double yScale, int maxScore, int tasksTotalDuration) {
        this.graphPoints = new ArrayList<>();

        Point pointReference = createPoint(0, 0, xScale, yScale, maxScore, tasksTotalDuration);
        graphPoints.add(pointReference);
        int yReference = 0;

        for (int i = 0; i < graphInfo.size(); i++) {
            if (graphInfo.get(i) > 0){
                Point p = createPoint(i, yReference, xScale, yScale, maxScore, tasksTotalDuration);
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

    @Override
    public void loadGraphInfo(Task task) {
        if (task.getCompletionPercentage() == 100) {
            int dayInProject = calculateOffSetInProject(task.getEnd());
            int duration = graphInfo.get(dayInProject);
            duration += task.getDuration().getLength(); // task duration without weekends
            graphInfo.set(dayInProject, duration);
        }
    }

    @Override
    public void setGraphPointsFromFiles(String folderPath) throws IOException {
        BurndownDataIO data = new BurndownDataIO();
        data.changeSprintFolder(folderPath);
        List<Integer> dataFromFiles = data.getPastRemainingTasks(graphInfo.size());
        graphInfo = dataFromFiles;
    }
}
