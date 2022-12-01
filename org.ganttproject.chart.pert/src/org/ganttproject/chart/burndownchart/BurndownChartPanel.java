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
 * @author Martin Magdalinchev
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
        myGraph = new GraphPanel();
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
        myGraph.init(statistics);
        setBackground(Color.WHITE);
        Graphics2D g2 = (Graphics2D) g;
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

}

