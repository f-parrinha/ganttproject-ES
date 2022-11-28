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
import net.sourceforge.ganttproject.IGanttProject;
import net.sourceforge.ganttproject.chart.Chart;
import net.sourceforge.ganttproject.PlannerStatistics;
import net.sourceforge.ganttproject.chart.export.ChartImageVisitor;
import net.sourceforge.ganttproject.language.GanttLanguage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.Date;

/**
 * PERT chart implementation where nodes are tasks and links succession
 * relations.
 *
 * @author bbaranne
 * @author Julien Seiler
 *
 */
public class PlannerPanel extends Panel {

  /** Useful for exportation */
  private int myMaxX = 1;
  private int myMaxY = 1;

  private final static GanttLanguage language = GanttLanguage.getInstance();

  /** Cannot remove this. WHY? */
  private final JScrollPane myScrollPane;

  public PlannerPanel() {
    setBackground(new Color(233, 233, 233));

    myScrollPane = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
  }

  /** Use this to initialize Planner variables, like statistics */
  @Override
  protected void buildPlanner() {
    setBackground(new Color (233, 233, 233));
  }

  @Override
  public void buildImage(GanttExportSettings settings, ChartImageVisitor imageVisitor) {
    // TODO Auto-generated method stub

  }

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

  @Override
  public void reset() {
  }

  /** Use this to paint its content */
  @Override
  public void paint(Graphics g) {
    super.paint(g);

    this.buildPlanner();

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
   * Max and min coordinates in the graphics that paints the graphical nodes and
   * arrows.
   */
  private int getMaxX() {
    return myMaxX;
  }


  private int getMaxY() {
    return myMaxY;
  }

  @Override
  public IGanttProject getProject() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setDimensions(int height, int width) {
    // TODO Auto-generated method stub
  }

  @Override
  public void setStartDate(Date startDate) {
    // TODO Auto-generated method stub
  }
}