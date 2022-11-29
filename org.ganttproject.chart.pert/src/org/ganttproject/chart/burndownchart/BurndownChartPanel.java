package org.ganttproject.chart.burndownchart;

import net.sourceforge.ganttproject.GanttExportSettings;
import net.sourceforge.ganttproject.chart.Chart;
import net.sourceforge.ganttproject.language.GanttLanguage;
import org.ganttproject.chart.planner.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Francisco Parrinha
 * @author Martin Magalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 *
 * BurndownChartPanel Class - Adds the burndown chart panel to the project and its functionalities
 */

public class BurndownChartPanel extends Panel {

    /** TODO: Useful for exportation */
    private int myMaxX = 1;
    private int myMaxY = 1;

    private final static GanttLanguage language = GanttLanguage.getInstance();

    /** Cannot remove this. WHY? */
    private final JPanel myPanel;
    private final GraphPanel myGraph;

    public BurndownChartPanel() {
        myPanel = this;
        myGraph = new GraphPanel(getData());
    }

    @Override
    public RenderedImage getRenderedImage(GanttExportSettings settings) {
        BufferedImage image = new BufferedImage(1600, 800, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, 1600, 800);
        paint(g);
        return image;
    }

    public void paint(Graphics g) {
        setBackground(Color.WHITE);
        Graphics2D g2 = (Graphics2D) g;
        //g.setColor(Color.WHITE);
        super.paint(g);
        myGraph.paintComponent(g2);
    }

    @Override
    public void reset() {
        System.out.println("Closing tab!");
    }

    @Override
    public String getName() {
        return language.getText("burndownLongName");
    }

    @Override
    protected void startPanel() {
        //setBackground(new Color (233, 233, 233));
    }

    @Override
    public Object getAdapter(Class adapter) {
        if (adapter.equals(Chart.class)) {
            return this;
        }

        if (adapter.equals(Container.class)) {
            return myPanel;
        }
        return null;
    }

    /**
     * Max and min coordinates in the graphics that paints the graphical nodes and
     * arrows.
     */
    private int getMaxX() {
        return myMaxX;
    }

    /**
     * TODO: Still have to figure this one out. Useful in exporation
     */
    private int getMaxY() {
        return myMaxY;
    }

    private List<Double> getData(){
        java.util.List<Double> scores = new ArrayList<>();
        Random random = new Random();
        int maxDataPoints = 40;
        int maxScore = 10;
        for (int i = 0; i < maxDataPoints; i++) {
            scores.add(random.nextDouble() * maxScore);
//            scores.add((double) i);
        }
        return scores;
    }
}

