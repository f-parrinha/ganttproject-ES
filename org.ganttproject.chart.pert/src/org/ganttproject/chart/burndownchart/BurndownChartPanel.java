package org.ganttproject.chart.burndownchart;

import net.sourceforge.ganttproject.GanttExportSettings;
import net.sourceforge.ganttproject.chart.Chart;
import net.sourceforge.ganttproject.language.GanttLanguage;
import org.ganttproject.chart.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 *
 * BurndownChartPanel class - Adds the burndown chart panel to the project and its functionalities
 */

public class BurndownChartPanel extends Panel {

    private final static GanttLanguage language = GanttLanguage.getInstance();

    private final JPanel myPanel;

    private final GraphPanel myGraph;

    public BurndownChartPanel() {
        myPanel = this;
        myGraph = new GraphPanel(myPanel);
    }

    /**
     * Create a buffered image
     * Instantiates a graphic object
     * Call the function paint to draw component
     * Returns a rendered image of the current panel.
     * @param settings GanttExportSettings
     */
    @Override
    public RenderedImage getRenderedImage(GanttExportSettings settings) {
        BufferedImage image = new BufferedImage(myPanel.getWidth(), myPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, myPanel.getWidth(), myPanel.getHeight());
        paint(g);
        return image;
    }

    /**
     * Paints the panel's content
     *
     * @param g Graphics object. Used to paint
     */
    @Override
    public void paint(Graphics g) {

        myGraph.init(myGanttStatistics);
        setBackground(Color.WHITE);
        Graphics2D g2 = (Graphics2D) g;
        super.paint(g);
        myGraph.paintGraphic(g2);
    }

    @Override
    public void reset() {
        System.out.println("Closing tab!");
    }

    @Override
    public String getName() {
        return language.getText("burndownLongName");
    }

    /**
     * Adapter pattern. Returns either the Chart's class or the JFrame's container
     *
     * @param adapter adapter. Which class to get, container's or chart's
     * @return Class or Container
     */
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

