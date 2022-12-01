package org.ganttproject.chart.burndownchart;

import net.sourceforge.ganttproject.PlannerStatistics;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.*;

/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 */
public class GraphPanel extends JPanel {

    private static final Color ACTUAL_LINE_COLOR = new Color(44, 102, 230, 180);
    private static final Color IDEAL_LINE_COLOR = new Color(230, 10, 44, 180);
    private static final Color POINT_COLOR = new Color(100, 100, 100, 180);
    private static final Color GRID_COLOR = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int width = 1600;
    private int heigth = 800;
    private int padding = 45;
    private int labelPadding = 25;
    private int pointWidth = 4;
    private int numberYDivisions = 15;
    private int estimatedTime;
    private int tasksTotalDuration;
    private int maxScore;
    private int minScore;
    private double xScale;
    private double yScale;
    private List<Integer> finishedTasksInfo;
    private PlannerStatistics statistics;


    public GraphPanel() {
        finishedTasksInfo = new ArrayList<>();
    }

    public void init(PlannerStatistics statistics) {
        this.statistics = statistics;
        this.finishedTasksInfo = statistics.getBurndownInfo();
        this.tasksTotalDuration = initY();
        this.estimatedTime = initX();
        this.maxScore = getMaxScore();
        this.minScore = 0;
        this.xScale = ((double) getGraphWidth() - (2 * padding) - labelPadding) / (this.estimatedTime);
        this.yScale = ((double) heigth - (2 * padding) - labelPadding) / (this.maxScore - this.minScore);
    }
    private int initX() {
        return (int) statistics.getTotalEstimatedTime();
    }

    private int initY() {
        return statistics.getSumOfTaskDurations();
    }

    private int getGraphWidth(){
        return width - 250;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getGraphWidth() - (2 * padding) - labelPadding, heigth - 2 * padding - labelPadding);

        // create x and y axes
        g2.setColor(Color.BLACK);
        g2.drawLine(padding + labelPadding, heigth - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, heigth - padding - labelPadding, getGraphWidth() - padding, heigth - padding - labelPadding);

        if (estimatedTime > 0 && tasksTotalDuration > 0){
            // draw ideal burndown line flow, create hatch marks and grid lines for X and Y axis.
            draw_X_Marks(g2);
            draw_Y_Marks(g2);
            drawIdealFlowLine(g2);
            List<Point> graphPoints = buildAllPoints();
            if (graphPoints.size() > 1)
                drawActualFlowLine(g2, graphPoints);
        }
        drawGraphInfo(g2);
    }

    private void drawActualFlowLine(Graphics2D g2, List<Point> graphPoints){
        Stroke oldStroke = g2.getStroke();
        g2.setColor(ACTUAL_LINE_COLOR);
        g2.setStroke(GRAPH_STROKE);
        System.out.println("SIZE "+graphPoints.size());
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(POINT_COLOR);
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = graphPoints.get(i).x - pointWidth / 2;
            int y = graphPoints.get(i).y - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }
    // tirar do draw
    private List<Point> buildAllPoints(){

        List<Point> graphPoints = new ArrayList<>();

        int originX = (padding + labelPadding);
        int originY = (int) ((maxScore - tasksTotalDuration) * yScale + padding);
        graphPoints.add(new Point(originX, originY));

        for (int i = 0; i < finishedTasksInfo.size(); i++) {
            if (finishedTasksInfo.get(i) > 0){
                int x1 = (int) (i * xScale + padding + labelPadding);
                int y1 = (int) ((maxScore - tasksTotalDuration + finishedTasksInfo.get(i)) * yScale + padding);
                if (graphPoints.add(new Point(x1, y1))) System.out.println("INSERT ITER "+ i + "POINT: X -> " + (int) (i * xScale + padding + labelPadding) + "POINT: Y -> " + (int) ((maxScore - tasksTotalDuration + finishedTasksInfo.get(i)) * yScale + padding));
            }
        }
        return graphPoints;
    }

    // create hatch marks, grid lines and identifiers for X axis.
    private void draw_X_Marks(Graphics2D g2){

        for (int i = 0; i < estimatedTime+1; i++) {/*+1*/
            Point p0 = new Point(i * (getGraphWidth() - padding * 2 - labelPadding) / estimatedTime + padding + labelPadding,800 - padding - labelPadding);
            Point p1 = new Point(i * (getGraphWidth() - padding * 2 - labelPadding) / estimatedTime + padding + labelPadding,800 - padding - labelPadding - pointWidth);

            if (i > 0 && (i % ((int) ((estimatedTime / 20.0)) + 1)) == 0){
                draw_X_Grid(g2, p0, p1);
                draw_X_Identifiers(g2, i, p0);
            }
            g2.drawLine(p0.x, p0.y, p1.x, p1.y);
        }
    }

    private void draw_X_Grid(Graphics2D g2, Point p0, Point p1){
        g2.setColor(GRID_COLOR);
        g2.drawLine(p0.x, 800 - padding - labelPadding - 1 - pointWidth, p1.x, padding);
    }

    private void draw_X_Identifiers(Graphics2D g2, int index, Point p0){
        g2.setColor(Color.BLACK);
        String xLabel = index + "";
        FontMetrics metrics = g2.getFontMetrics();
        int labelWidth = metrics.stringWidth(xLabel);
        g2.drawString(xLabel, p0.x - labelWidth / 2, p0.y + metrics.getHeight() + 3);
    }

    // create hatch marks, grid lines and identifiers for Y axis.
    private void draw_Y_Marks(Graphics2D g2){

        for (int i = 0; i < numberYDivisions + 1; i++) {
            Point p0 = new Point(padding + labelPadding,800 - ((i * (800 - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding));
            Point p1 = new Point(pointWidth + padding + labelPadding, p0.y);

            draw_Y_Grid(g2, p0, p1);
            draw_Y_Identifiers(g2, i, p0);

            g2.drawLine(p0.x, p0.y, p1.x, p1.y);
        }
    }

    private void draw_Y_Grid(Graphics2D g2, Point p0, Point p1){
        g2.setColor(GRID_COLOR);
        g2.drawLine(padding + labelPadding + 1 + pointWidth, p0.y, getGraphWidth() - padding, p1.y);
    }

    private void draw_Y_Identifiers(Graphics2D g2, int index, Point p0){
        g2.setColor(Color.BLACK);
        String yLabel = (int) ((minScore + (maxScore - minScore) * (index * 1.0 / numberYDivisions)) ) + "";
        FontMetrics metrics = g2.getFontMetrics();
        int labelWidth = metrics.stringWidth(yLabel);
        g2.drawString(yLabel, p0.x - labelWidth - 5, p0.y + (metrics.getHeight() / 2) - 3);
    }

    private void drawIdealFlowLine(Graphics2D g2) {
        g2.setColor(IDEAL_LINE_COLOR);
        g2.setStroke(GRAPH_STROKE);
        g2.drawLine((padding + labelPadding), (int)((maxScore - tasksTotalDuration) * yScale + padding) , (int)(estimatedTime * xScale + padding + labelPadding), (int) (maxScore * yScale + padding));
    }

    private void drawGraphInfo(Graphics2D g2){
        // draw white background in side right panel
        int infoRectYOrigin = ((heigth / 2) - 100 - padding);
        g2.setColor(Color.WHITE);
        g2.fillRect(getGraphWidth(), infoRectYOrigin, 225, heigth * 1/4);
        g2.setStroke(new BasicStroke(5f));
        g2.setColor(ACTUAL_LINE_COLOR);
        g2.drawLine(getGraphWidth() + padding, infoRectYOrigin + padding, getGraphWidth() + 225 - padding, infoRectYOrigin + padding);
        g2.setColor(IDEAL_LINE_COLOR);
        g2.drawLine(getGraphWidth() + padding, infoRectYOrigin +  (heigth * 1/4) - padding - labelPadding, getGraphWidth() + 225 - padding, infoRectYOrigin +  (heigth * 1/4) - padding - labelPadding);
        g2.setColor(Color.BLACK);
        g2.drawString("Actual Tasks Remaining", getGraphWidth() + padding, infoRectYOrigin + padding + labelPadding);
        g2.drawString(" Ideal Tasks Remaining", getGraphWidth() + padding, infoRectYOrigin +  (heigth * 1/4) - padding);
        g2.drawString("Iteration Timeline (days)", ((getGraphWidth() + padding)/2) - "Iteration Timeline (days)".toCharArray().length, heigth - padding/2);

        // rotates the coordinate by 90 degree counterclockwise
        AffineTransform at = new AffineTransform();
        at.rotate(- Math.PI / 2);
        g2.setTransform(at);
        g2.drawString("Sum of Task Estimates (days)", -((heigth + padding)/2) - "Sum of Task Estimates (days)".toCharArray().length,  labelPadding);
    }

    private int getMaxScore() {
       if(tasksTotalDuration % 15 == 0) {
           return tasksTotalDuration;
       } else {
           return (15 - (tasksTotalDuration % 15)) + tasksTotalDuration;
       }
    }
}
