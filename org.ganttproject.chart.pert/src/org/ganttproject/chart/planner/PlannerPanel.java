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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.net.URL;


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

  private Image logo;

  private int rectWidth;

  private int rectHeight;

  private int offsetX;

  private int offsetY;

  private Dimension maxSize;

  private final static GanttLanguage language = GanttLanguage.getInstance();

  /** Cannot remove this. WHY? */
  private final JPanel myPanel;

  public PlannerPanel() throws IOException {
    setBackground(new Color(233, 233, 233));
    maxSize = Toolkit. getDefaultToolkit(). getScreenSize();
    myPanel = this;
    URL url = PlannerPanel.class.getResource("/icons/big.png");
    logo = ImageIO.read(url);
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
    paintLogo(g, logo);
    setOffset(50, 50);
    paintStatistics(g);
    setOffset(1000, 1000);
  }

  /**
   * Sets a new offset to the object to be drawn
   *
   * @param x - Offset X
   * @param y - Offset Y
   */
  private void setOffset(int x, int y) {

    offsetX = x*myPanel.getWidth()/maxSize.width;
    offsetY = y*myPanel.getHeight()/maxSize.height;
  }

  /**
   *  Paints the logo no top side
   *
   * @param g - Graphics swing object
   */
  private void paintLogo(Graphics g, Image logo){
    
    g.drawImage(logo, 0, 0, null);
  }

  /**
   * Paints the statistics area on the left side
   *
   * @param g - Graphics swing object
   */
  private void paintStatistics(Graphics g) {

    int rectWidth = myPanel.getWidth()*2/5 - 50*myPanel.getWidth()/maxSize.width;
    int rectHeight = myPanel.getHeight() - 30*myPanel.getHeight()/maxSize.height;

    int fontSize = 25*myPanel.getWidth()/maxSize.width;

    g.setColor(Color.WHITE);
    g.fillRoundRect(offsetX,offsetY, rectWidth - offsetX, rectHeight - offsetY, 50, 50);

    g.setColor(Color.DARK_GRAY);

    g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
    g.drawString("Total number of tasks: " + statistics.getTotalTasks(), rectWidth/8 + offsetX, offsetY + rectHeight/6);

    /** Statistics is crashing here */
    //g.drawString("Current time spent: " + statistics.getCurrentSpentTime(), rectWidth/8 + offsetX, offsetY + rectHeight*2/6);
    //g.drawString("Total estimated time: " + statistics.getTotalEstimatedTime(), rectWidth/8 + offsetX, offsetY + rectHeight*3/6);
    //g.drawString("Total finished tasks: " + statistics.getFinishedTasks(), rectWidth/8 + offsetX, offsetY + rectHeight*4/6);
    //g.drawString("Overall progress: " + statistics.getOverallProgress() + "%", rectWidth/8 + offsetX, offsetY + rectHeight*5/6);

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