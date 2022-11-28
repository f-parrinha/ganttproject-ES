package org.ganttproject.chart.planner;

import net.sourceforge.ganttproject.GanttExportSettings;
import net.sourceforge.ganttproject.IGanttProject;
import net.sourceforge.ganttproject.chart.export.ChartImageVisitor;
import net.sourceforge.ganttproject.language.GanttLanguage;
import net.sourceforge.ganttproject.task.TaskManager;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.util.Date;




/**
 * THIS ONE WILL BE THE PLANNER CLASS
 *
 * USE THE OTHERS TO GET INSPIRATION
 */
public class PlannerPanel extends PertChart {

    private TaskManager myTaskManager;
    private GanttLanguage language;


    @Override
    public IGanttProject getProject() {
        return null;
    }

    @Override
    public void buildImage(GanttExportSettings ganttExportSettings, ChartImageVisitor chartImageVisitor) {

    }

    @Override
    public RenderedImage getRenderedImage(GanttExportSettings ganttExportSettings) {
        return null;
    }

    @Override
    public void setStartDate(Date date) {

    }

    @Override
    public void setDimensions(int i, int i1) {

    }

    @Override
    /**
     * Returns the tab's name
     */
    public String getName() {
        return language.getText("plannerLongName");
    }

    @Override
    public void paint(Graphics g) {
        this.buildPlanner();
        super.paint(g);
    }

    @Override
    public void reset() {

    }

    @Override
    protected void buildPlanner() {
        System.out.println("Build");
    }

    @Override
    public Object getAdapter(Class aClass) {
        return null;
    }
}
