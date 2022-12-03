package org.ganttproject.chart.burndownchart;

import net.sourceforge.ganttproject.GanttStatistics;
import org.ganttproject.chart.PanelStyler;

import javax.swing.*;
import java.awt.*;
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

    protected Graph(GanttStatistics statistics, JPanel panel, int padding, int labelPadding, int pointWidth) {
        this.myGanttStatistics = statistics;
        this.myPanel = panel;
        this.padding = padding;
        this.labelPadding = labelPadding;
        this.pointWidth = pointWidth;

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
     *
     * @return
     */
    public int getSize() {
        return graphPoints.size();
    }
}
