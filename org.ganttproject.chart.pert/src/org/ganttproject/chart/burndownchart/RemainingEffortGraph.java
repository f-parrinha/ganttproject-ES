package org.ganttproject.chart.burndownchart;

import net.sourceforge.ganttproject.GanttStatistics;
import org.ganttproject.chart.PanelStyler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;



public class RemainingEffortGraph extends PanelStyler {

    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);

    private final JPanel myPanel;
    private final int padding;
    private final int labelPadding;
    private final int pointWidth;
    private GanttStatistics statistics;
    private List<Integer> graphInfo;
    private List<Point> graphPoints;

    public RemainingEffortGraph(GanttStatistics statistics, JPanel panel, int padding, int labelPadding, int pointWidth){
        this.myPanel = panel;
        this.graphInfo = graphInfo;
        this.statistics = statistics;
        this.graphInfo = statistics.getRemEffortInfo();
        this.padding = padding;
        this.labelPadding = labelPadding;
        this.pointWidth = pointWidth;

    }

    public List<Point> buildGraphPoints(double xScale, double yScale, int maxScore, int tasksTotalDuration) {
        this.graphPoints = new ArrayList<>();

        int originX = (padding + labelPadding);
        int originY = (int) ((maxScore - tasksTotalDuration) * yScale + padding);

        Point pointReference = new Point(originX, originY);

        graphPoints.add(pointReference);

        int yReference = 0;

        for (int i = 1; i < graphInfo.size(); i++) {

            int x1 = (int) (i * xScale + padding + labelPadding);
            int y1 = (int) ((maxScore - tasksTotalDuration + yReference + graphInfo.get(i-1)) * yScale + padding);
            Point p = new Point(x1, y1);
            graphPoints.add(p);
            yReference += graphInfo.get(i-1);
            System.out.println("yReference" + yReference);

        }

        return graphPoints;
    }

    public void drawActualFlowLine(Graphics2D g2){
        Stroke oldStroke = g2.getStroke();

        g2.setColor(GraphPanel.COLOR.REMAINING_EFFORT_LINE.color);
        g2.setStroke(GRAPH_STROKE);
        System.out.println("SIZE "+graphPoints.size());
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

    public int getSize() {
        return graphPoints.size();
    }
}
