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
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 *
 * PlannerPanel Class - Adds the planner panel to the project and its functionality
 */
public class PlannerPanel extends Panel {

  private final static GanttLanguage language = GanttLanguage.getInstance();

  private final JPanel myPanel;

  private final String fontStyle;

  private final Dimension maxSize;

  private int rectWidth;

  private int rectHeight;

  private int offsetX;

  private int offsetY;

  private int fontSize;

  private Image logo;

  public PlannerPanel() throws IOException {
    maxSize = Toolkit. getDefaultToolkit(). getScreenSize();
    myPanel = this;
    fontStyle = "Helvetica";

    setBackground(new Color(233, 233, 233));
    loadImageFromDir("/icons/big.png");
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

    paintLogo(g, logo);
    setOffset(50, 50);
    updateFontSize(25);

    paintStatistics(g);
    paintGraphic(g);
  }

  /**
   * Paints the entire statistics area
   *
   * @param g Graphics swing object
   */
  private void paintStatistics(Graphics g) {
    setRect(2.0/6.0, 5.0/6.0);
    setOffset(resizeX(50), resizeY(50) + rectHeight/6);

    // Main square
    g.setColor(Color.WHITE);
    g.fillRoundRect(offsetX,offsetY, rectWidth - resizeX(50)*2, rectHeight - resizeY(50)*2, 50, 50);

    // Statistics
    g.setColor(Color.DARK_GRAY);
    g.setFont(new Font(fontStyle, Font.PLAIN, fontSize));
    g.drawString("Total number of tasks: " + statistics.getTotalTasks(), rectWidth/8 + resizeX(50),offsetY + rectHeight/7);
    g.drawString("Current time spent: " + statistics.getCurrentSpentTime(), rectWidth/8 + offsetX, offsetY + rectHeight*2/7);
    g.drawString("Total estimated time: " + statistics.getTotalEstimatedTime(), rectWidth/8 + offsetX, offsetY + rectHeight*3/7);
    g.drawString("Total finished tasks: " + statistics.getFinishedTasks(), rectWidth/8 + offsetX, offsetY + rectHeight*4/7);
    g.drawString("Overall progress: " + statistics.getOverallProgress() + "%", rectWidth/8 + offsetX, offsetY + rectHeight*5/7);
  }

  /**
   * Paints the entire bar graph
   *
   * @param g Graphics swing object
   */
  private void paintGraphic(Graphics g) {
    int spacing = rectHeight/5;
    setOffset(rectWidth + resizeX(50)*2, resizeY(50)+ rectHeight/6);
    setRect(3.5/6.0, 5.0/6.0);

    // Main square
    g.setColor(Color.WHITE);
    g.fillRoundRect(offsetX,offsetY, rectWidth - resizeX(50), rectHeight - resizeY(50)*2, 50, 50);

    // Finished tasks
    double finishedTaskPercentage = statistics.getTotalTasks() > 0 ? (double) statistics.getFinishedTasks()/statistics.getTotalTasks() : 0;
    drawGraphLine(g, resizeX(150), spacing, finishedTaskPercentage,statistics.getTotalTasks(), Color.RED);
    g.setColor(Color.DARK_GRAY);
    g.drawString("Finished tasks", offsetX + rectWidth/7,offsetY - 10 + rectHeight/7);

    // Current spent time
    double spentTimePercentage = statistics.getCurrentSpentTime() > 0 ? (double) statistics.getCurrentSpentTime()/statistics.getTotalEstimatedTime() : 0;
    drawGraphLine(g, 0, spacing, spentTimePercentage, (int) statistics.getTotalEstimatedTime(), Color.RED);
    g.setColor(Color.DARK_GRAY);
    g.drawString("Spent Time", offsetX + rectWidth/7,offsetY - 10 + rectHeight/7);

    // Overall progress
    drawGraphLine(g, 0, spacing, statistics.getOverallProgress()/100, 100, Color.GREEN);
    g.setColor(Color.DARK_GRAY);
    g.drawString("Overall Progress", offsetX + rectWidth/7,offsetY - 10 + rectHeight/7);
  }

  /**
   * Sets a new offset to the object to be drawn
   *
   * @param x Offset X
   * @param y Offset Y
   */
  private void setOffset(int x, int y) {
    offsetX = x;
    offsetY = y;
  }

  /**
   * Resizes a rectangle
   *
   * @param x value on the x axis
   * @param y value on the y axis
   */
  private void setRect(double x, double y) {
    rectWidth = (int) (myPanel.getWidth()*x);
    rectHeight = (int) (myPanel.getHeight()*y);
  }

  /**
   * Paints the logo no top side
   *
   * @param g Graphics swing object
   */
  private void paintLogo(Graphics g, Image logo){
    // Resizes the image
    int sizeY = (int)((1.0/10.0) * myPanel.getHeight());
    Image img = logo.getScaledInstance(myPanel.getWidth(), sizeY, Image.SCALE_DEFAULT);

    // Draws the image
    g.drawImage(img, 0, 0, null);
  }

  /**
   * Scales the given x value to match the current panel's and screen's width
   *
   * @param i x value
   * @return scaled x value
   */
  private int resizeX(int i) {
    return i*myPanel.getWidth()/maxSize.width;
  }

  /**
   * Scales the given y value to match the current panel's and screen's width
   *
   * @param i y value
   * @return scaled y value
   */
  private int resizeY(int i) {
    return i*myPanel.getHeight()/maxSize.height;
  }

  /**
   * Draws one line in the bar graph
   *
   * @param g Graphics swing object
   * @param oX offset in the x axis
   * @param oY offset in the y axis
   * @param p current percentage
   * @param max maximum value in the bar graph
   * @param c color of the line
   */
  private void drawGraphLine(Graphics g, int oX, int oY, double p, int max, Color c) {
    setOffset(offsetX + oX, offsetY + oY);
    setRect(0.4, 0.021);
    g.setColor(Color.LIGHT_GRAY);
    g.fillRoundRect(offsetX, offsetY, rectWidth, rectHeight, 5, 5);
    g.drawString("0", offsetX - resizeX(15), offsetY + rectHeight + resizeY(30));
    g.drawString("" + max, offsetX + rectWidth, offsetY + rectHeight + resizeY(30));
    setRect(0.4 * p, 0.02);
    g.setColor(c);
    g.fillRoundRect(offsetX, offsetY, rectWidth, rectHeight, 5, 5);
  }

  /**
   * Loads an image from the resources' folder, with a given directory
   *
   * @param directory directory in resources folder
   * @throws IOException
   */
  private void loadImageFromDir(String directory) throws IOException {
    URL url = PlannerPanel.class.getResource(directory);
    logo = ImageIO.read(url);
  }

  /**
   * Updates the font size, regarding panel's and screen's width
   *
   * @param size font size
   */
  private void updateFontSize(int size){
    fontSize = size*myPanel.getWidth()/maxSize.width;
  }
}