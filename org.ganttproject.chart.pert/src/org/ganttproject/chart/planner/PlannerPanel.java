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

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;


/**
 * @author Francisco Parrinha
 * @author Martin Magalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 *
 * PlannerPanel Class - Adds the planner panel to the project and its functionality
 */
public class PlannerPanel extends Panel {

  /** TODO: Useful for exportation */
  private int myMaxX = 1;
  private int myMaxY = 1;

  private final static GanttLanguage language = GanttLanguage.getInstance();

  private final JScrollPane myScrollPane;

  public PlannerPanel() {
    myScrollPane = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
  }

  /** Use this to initialize Planner variables, like statistics */
  @Override
  protected void startPanel() {
    setBackground(new Color (233, 233, 233));
  }

  /** TODO
   * Exportation. Returns a rendered image of the current panel.
   *
   * @param settings - Export settings from the project
   * @return final image
   */
  @Override
  public RenderedImage getRenderedImage(GanttExportSettings settings) {
    BufferedImage image = new BufferedImage(getMaxX(), getMaxY(), BufferedImage.TYPE_INT_RGB);
    Graphics g = image.getGraphics();
    g.fillRect(0, 0, getMaxX(), getMaxY());
    paint(g);
    return image;
  }

  @Override
  public String getName() {
    return language.getText("plannerLongName");
  }

  /** TODO */
  @Override
  public void reset() {
    System.out.println("Closing tab!");
  }

  /**
   * Paints the panel's content
   *
   * @param g - Graphics object. Used to paint
   */
  @Override
  public void paint(Graphics g) {
    super.paint(g);

    this.startPanel();

    int rectWidth = 400;
    int rectHeight = 400;

    int offsetX = 100;
    int offsetY = 50;

    int fontSize = 20;

    g.setColor(Color.WHITE);
    g.fillRoundRect(offsetX,offsetY, rectWidth,rectHeight, 50, 50);

    g.setColor(Color.DARK_GRAY);

    g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
    g.drawString("Total Tasks: " + statistics.getTotalTasks(), (rectWidth + offsetX - fontSize)/ 2, offsetY * 2);
  }

  /**
   * Adapter pattern. Returns either the Chart's class or the JFrame's container
   *
   * @param adapter - adapter. Which class to get, container's or chart's
   * @return Class or Container
   */
  @Override
  public Object getAdapter(Class adapter) {
    if (adapter.equals(Chart.class)) {
      return this;
    }

    if (adapter.equals(Container.class)) {
      return myScrollPane;
    }
    return null;
  }

  /**
   * TODO: Still have to figure this one out. Useful in exporation
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