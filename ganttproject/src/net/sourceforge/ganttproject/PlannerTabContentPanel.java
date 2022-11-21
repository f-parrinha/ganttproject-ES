package net.sourceforge.ganttproject;

import net.sourceforge.ganttproject.chart.Chart;
import net.sourceforge.ganttproject.chart.overview.GPToolbar;
import net.sourceforge.ganttproject.chart.overview.ToolbarBuilder;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.gui.view.GPView;

import javax.swing.*;
import java.awt.*;

/**
 * TODO
 *
 * Change this class to be like the Planner tab
 *
 */
public class PlannerTabContentPanel extends ChartTabContentPanel implements GPView {
    private TreeTableContainer myTreeFacade;
    private Component myPlanner;
    private JComponent myTabContentPanel;

    // For the updated class
    private PlannerStatistics statistics;
    //private TaskManager taskManager = new TaskManagerImpl();

    PlannerTabContentPanel(IGanttProject project, UIFacade workbenchFacade, TreeTableContainer plannerTree,
                                 Component planner) {
        super(project, workbenchFacade, workbenchFacade.getResourceChart());
        myTreeFacade = plannerTree;
        myPlanner = planner;
        addTableResizeListeners(plannerTree.getTreeComponent(), myTreeFacade.getTreeTable().getScrollPane().getViewport());

        //statistics = new PlannerStatistics(taskManager);
    }

    JComponent getComponent() {
        if (myTabContentPanel == null) {
            myTabContentPanel = createContentComponent();
        }
        return myTabContentPanel;
    }

    @Override
    protected Component createButtonPanel() {
        ToolbarBuilder builder = new ToolbarBuilder()
                .withHeight(24)
                .withSquareButtons()
                .withDpiOption(getUiFacade().getDpiOption())
                .withLafOption(getUiFacade().getLafOption(), null);
        myTreeFacade.addToolbarActions(builder);
        final GPToolbar toolbar = builder.build();
        return toolbar.getToolbar();
    }

    @Override
    protected Component getChartComponent() {
        return myPlanner;
    }

    @Override
    protected Component getTreeComponent() {
        return myTreeFacade.getTreeComponent();
    }

    @Override
    public Chart getChart() {
        return getUiFacade().getResourceChart();
    }

    @Override
    public Component getViewComponent() {
        return getComponent();
    }
}
