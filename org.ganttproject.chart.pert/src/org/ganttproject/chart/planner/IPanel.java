package org.ganttproject.chart.planner;

import biz.ganttproject.core.option.FontOption;
import biz.ganttproject.core.option.GPOptionGroup;
import biz.ganttproject.core.option.IntegerOption;
import net.sourceforge.ganttproject.GanttExportSettings;
import net.sourceforge.ganttproject.IGanttProject;
import net.sourceforge.ganttproject.chart.Chart;
import net.sourceforge.ganttproject.chart.ChartSelection;
import net.sourceforge.ganttproject.chart.ChartSelectionListener;
import net.sourceforge.ganttproject.chart.export.ChartImageVisitor;
import org.eclipse.core.runtime.IStatus;

import java.awt.image.RenderedImage;
import java.util.Date;

public interface IPanel {
    IGanttProject getProject();

    void init(IGanttProject project, IntegerOption dpiOption, FontOption chartFontOption);

    void buildImage(GanttExportSettings settings, ChartImageVisitor imageVisitor);

    RenderedImage getRenderedImage(GanttExportSettings settings);

    Date getStartDate();

    void setStartDate(Date startDate);

    Date getEndDate();

    void setDimensions(int height, int width);

    String getName();

    /** Repaints the chart */
    void reset();

    GPOptionGroup[] getOptionGroups();

    Chart createCopy();

    ChartSelection getSelection();

    IStatus canPaste(ChartSelection selection);

    void paste(ChartSelection selection);

    void addSelectionListener(ChartSelectionListener listener);

    void removeSelectionListener(ChartSelectionListener listener);
}
