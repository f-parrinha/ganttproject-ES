/*
Copyright 2003-2012 GanttProject Team

This file is part of GanttProject, an opensource project management tool.

GanttProject is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

GanttProject is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with GanttProject.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ganttproject.chart.planner;

import biz.ganttproject.core.option.*;
import com.google.common.base.Preconditions;
import net.sourceforge.ganttproject.GanttExportSettings;
import net.sourceforge.ganttproject.IGanttProject;
import net.sourceforge.ganttproject.PlannerStatistics;
import net.sourceforge.ganttproject.chart.Chart;
import net.sourceforge.ganttproject.chart.ChartSelection;
import net.sourceforge.ganttproject.chart.ChartSelectionListener;
import net.sourceforge.ganttproject.chart.export.ChartImageVisitor;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

import static net.sourceforge.ganttproject.gui.UIFacade.DEFAULT_DPI;


/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 *
 * TODO: Maybe in the future it will implement IPanel interface and not Chart interface
 *    to remove unused methods (can be hard to do)
 *      - to do this, we need to also add the possibility for the plugin to search for planners
 *
 * Panel Class - Abstract class that creates the basic functionality of a simple panel
 */
public abstract class Panel extends JPanel implements Chart {

  protected PlannerStatistics statistics;
  protected TaskManager task;

  /**
   * Initializes panel when the plugin is loaded
   *
   * @param project GanttProject
   * @param dpiOption Size of all the bars in the view
   * @param chartFontOption The font option chosen in the project options
   */
  @Override
  public void init(IGanttProject project, IntegerOption dpiOption, FontOption chartFontOption) {
    task = project.getTaskManager();
    statistics = new PlannerStatistics(task);
  }

  /**
   * Returns the name of the panel
   *
   * @return name
   */
  @Override
  public abstract String getName();

  /** Unused method */
  @Override
  public ChartSelection getSelection() {
    return ChartSelection.EMPTY;
  }

  /** Unused method */
  @Override
  public IStatus canPaste(ChartSelection selection) {
    return Status.CANCEL_STATUS;
  }

  /** Unused method */
  @Override
  public void addSelectionListener(ChartSelectionListener listener) {
    // No listeners are implemented
  }

  /** Unused method */
  @Override
  public void removeSelectionListener(ChartSelectionListener listener) {
    // No listeners are implemented
  }

  /** Unused method */
  @Override
  public void reset(){

  }

  /** This method is not supported by this chart */
  @Override
  public IGanttProject getProject() {
    throw new UnsupportedOperationException();
  }

  /** This method is not supported by this chart */
  @Override
  public void setDimensions(int height, int width) {
    throw new UnsupportedOperationException();
  }

  /** This method is not supported by this chart */
  @Override
  public void setStartDate(Date startDate) {
    throw new UnsupportedOperationException();
  }

  /** This method is not supported by this chart */
  @Override
  public void buildImage(GanttExportSettings settings, ChartImageVisitor imageVisitor) {
    throw new UnsupportedOperationException();
  }

  /** This method in not supported by this Chart. */
  @Override
  public Date getStartDate() {
    throw new UnsupportedOperationException();
  }

  /** This method in not supported by this Chart. */
  @Override
  public Date getEndDate() {
    throw new UnsupportedOperationException();
  }

  /** This method in not supported by this Chart. */
  @Override
  public GPOptionGroup[] getOptionGroups() {
    throw new UnsupportedOperationException();
  }

  /** This method in not supported by this Chart. */
  @Override
  public Chart createCopy() {
    throw new UnsupportedOperationException();
  }

  /** This method in not supported by this Chart. */
  @Override
  public void paste(ChartSelection selection) {
    throw new UnsupportedOperationException();
  }
}
