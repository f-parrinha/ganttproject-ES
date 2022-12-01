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

    private final static GanttLanguage language = GanttLanguage.getInstance();

    private final JPanel myPanel;

    private final GraphPanel myGraph;

    public BurndownChartPanel() {
        myPanel = this;
        myGraph = new GraphPanel();
    }

    @Override
    public RenderedImage getRenderedImage(GanttExportSettings settings) {
        BufferedImage image = new BufferedImage(myPanel.getWidth(), myPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, myPanel.getWidth(), myPanel.getHeight());
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
}

