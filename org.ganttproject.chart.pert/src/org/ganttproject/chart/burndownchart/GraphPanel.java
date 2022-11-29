package org.ganttproject.chart.burndownchart;

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

    private int width = 1600;

    private int heigth = 800;
    private int padding = 45;
    private int labelPadding = 25;
    private Color actualLineColor = new Color(44, 102, 230, 180);

    private Color IdealLineColor = new Color(230, 10, 44, 180);
    private Color pointColor = new Color(100, 100, 100, 180);
    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int pointWidth = 4;
    private int numberYDivisions = 15;
    private List<Double> scores;

    private List<Double> orderedScores;

    public GraphPanel(List<Double> scores) {
        this.scores = scores;
        this.orderedScores = new LinkedList<>();
        orderedScores.addAll(scores);
        Collections.sort(orderedScores);
        Collections.reverse(orderedScores);

    }

    private int getGraphWidth(){
        return 1600 - 250;
    }


    private void drawIdealFlowLine(Graphics2D g2 , double xScale, double yScale, double maxScore){

        List<Point> graphPoints = new ArrayList<>();
        for (int i = 0; i < orderedScores.size(); i++) {
            int x1 = (int) (i * xScale + padding + labelPadding);
            int y1 = (int) ((maxScore - orderedScores.get(i)) * yScale + padding);
            graphPoints.add(new Point(x1, y1));
        }

        Stroke oldStroke = g2.getStroke();
        g2.setColor(IdealLineColor);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(pointColor);
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = graphPoints.get(i).x - pointWidth / 2;
            int y = graphPoints.get(i).y - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }

    private void drawGraphInfo(Graphics2D g2){
        // draw white background in side right panel
        int infoRectYOrigin = ((heigth / 2) - 100 - padding);
        g2.setColor(Color.WHITE);
        g2.fillRect(getGraphWidth(), infoRectYOrigin, 225, heigth * 1/4);
        g2.setStroke(new BasicStroke(5f));
        g2.setColor(actualLineColor);
        g2.drawLine(getGraphWidth() + padding, infoRectYOrigin + padding, getGraphWidth() + 225 - padding, infoRectYOrigin + padding);
        g2.setColor(IdealLineColor);
        g2.drawLine(getGraphWidth() + padding, infoRectYOrigin +  (heigth * 1/4) - padding - labelPadding, getGraphWidth() + 225 - padding, infoRectYOrigin +  (heigth * 1/4) - padding - labelPadding);
        g2.setColor(Color.BLACK);
        g2.drawString("Actual Tasks Remaining", getGraphWidth() + padding, infoRectYOrigin + padding + labelPadding);
        g2.drawString(" Ideal Tasks Remaining", getGraphWidth() + padding, infoRectYOrigin +  (heigth * 1/4) - padding);
        g2.drawString("Iteration Timeline (days)", ((getGraphWidth() + padding)/2) - "Iteration Timeline (days)".toCharArray().length, heigth - padding/2);
        AffineTransform defaultAt = g2.getTransform();

        // rotates the coordinate by 90 degree counterclockwise
        AffineTransform at = new AffineTransform();
        at.rotate(- Math.PI / 2);
        g2.setTransform(at);
        g2.drawString("Sum of Task Estimates (days)", -((heigth + padding)/2) - "Sum of Task Estimates (days)".toCharArray().length,  labelPadding);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double maxScore = getMaxScore();
        double minScore = getMinScore();

        double xScale = ((double) getGraphWidth() - (2 * padding) - labelPadding) / (scores.size() - 1);
        double yScale = ((double) heigth - (2 * padding) - labelPadding) / (maxScore - minScore);

        List<Point> graphPoints = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            int x1 = (int) (i * xScale + padding + labelPadding);
            int y1 = (int) ((maxScore - scores.get(i)) * yScale + padding);
            graphPoints.add(new Point(x1, y1));
        }

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getGraphWidth() - (2 * padding) - labelPadding, heigth - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);

        // create hatch marks and grid lines for y axis.
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = 800 - ((i * (800 - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            int y1 = y0;
            if (scores.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getGraphWidth() - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = ((int) ((minScore + (maxScore - minScore) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // and for x axis
        for (int i = 0; i < scores.size(); i++) {
            if (scores.size() > 1) {
                int x0 = i * (getGraphWidth() - padding * 2 - labelPadding) / (scores.size() - 1) + padding + labelPadding;
                int x1 = x0;
                int y0 = 800 - padding - labelPadding;
                int y1 = y0 - pointWidth;
                if ((i % ((int) ((scores.size() / 20.0)) + 1)) == 0) {
                    g2.setColor(gridColor);
                    g2.drawLine(x0, 800 - padding - labelPadding - 1 - pointWidth, x1, padding);
                    g2.setColor(Color.BLACK);
                    String xLabel = i + "";
                    FontMetrics metrics = g2.getFontMetrics();
                    int labelWidth = metrics.stringWidth(xLabel);
                    g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                }
                g2.drawLine(x0, y0, x1, y1);
            }
        }

        // create x and y axes
        g2.drawLine(padding + labelPadding, heigth - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, heigth - padding - labelPadding, getGraphWidth() - padding, heigth - padding - labelPadding);

        Stroke oldStroke = g2.getStroke();
        g2.setColor(actualLineColor);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(pointColor);
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = graphPoints.get(i).x - pointWidth / 2;
            int y = graphPoints.get(i).y - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;
            g2.fillOval(x, y, ovalW, ovalH);
        }
        drawIdealFlowLine(g2, xScale, yScale, maxScore);
        drawGraphInfo(g2);
    }

    private double getMinScore() {
        double minScore = Double.MAX_VALUE;
        for (Double score : scores) {
            minScore = Math.min(minScore, score);
        }
        return minScore;
    }

    private double getMaxScore() {
        double maxScore = Double.MIN_VALUE;
        for (Double score : scores) {
            maxScore = Math.max(maxScore, score);
        }
        return maxScore;
    }
/*
    public void setScores(List<Double> scores) {
        this.scores = scores;
        invalidate();
        this.repaint();
    }

    public List<Double> getScores() {
        return scores;
    }

    private static void createAndShowGui() {
        List<Double> scores = new ArrayList<>();
        Random random = new Random();
        int maxDataPoints = 40;
        int maxScore = 10;
        for (int i = 0; i < maxDataPoints; i++) {
            scores.add((double) random.nextDouble() * maxScore);
//            scores.add((double) i);
        }
        GraphPanel mainPanel = new GraphPanel(scores);
        mainPanel.setPreferredSize(new Dimension(1600, 800));
        JFrame frame = new JFrame("Burndown Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui();
            }
        });
    }*/
}
