/*
GanttProject is an opensource project management tool.
Copyright (C) 2005-2011 Bernoit Baranne, Julien Seiler, GanttProject Team

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.ganttproject.chart.planner;

import net.sourceforge.ganttproject.GanttExportSettings;
import net.sourceforge.ganttproject.chart.Chart;
import net.sourceforge.ganttproject.language.GanttLanguage;
import org.ganttproject.chart.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;


/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 * <p>
 * PlannerPanel Class - Adds the planner panel to the project and its functionality
 */
public class PlannerPanel extends Panel {

    private final static GanttLanguage language = GanttLanguage.getInstance();

    private final JPanel myPanel;

    private final PlannerPainter myPainter;

    public PlannerPanel() throws IOException {
        myPanel = this;
        myPainter = new PlannerPainter(myPanel);

        setBackground(new Color(233, 233, 233));
    }

    @Override
    public String getName() {
        return language.getText("plannerLongName");
    }

    /**
     * Exportation. Returns a rendered image of the current panel.
     *
     * @param settings Export settings from the project
     * @return final image
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

    /**
     * Paints the panel's content
     *
     * @param g Graphics object. Used to paint
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        myPainter.paintLogo(g);
        myPainter.paintStatistics(g, myGanttStatistics);
        myPainter.paintGraphic(g, myGanttStatistics);
    }
}