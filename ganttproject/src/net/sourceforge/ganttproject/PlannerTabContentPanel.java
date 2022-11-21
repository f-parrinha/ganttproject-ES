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
public class PlannerTabContentPanel extends TabContentPanel implements GPView {

    private TreeTableContainer myTreeFacade;
    private Component myPlannerChart;
    private JComponent myTabContentPanel;

    public PlannerTabContentPanel(IGanttProject project, UIFacade workbenchFacade, TreeTableContainer resourceTree,
                                  Component plannerChart){
        super(project, workbenchFacade, workbenchFacade.getResourceChart());

        myTreeFacade = resourceTree;
        myPlannerChart = plannerChart;

        System.out.println("Hello World!");
    }


    JComponent getComponent() {
        if (myTabContentPanel == null) {
            myTabContentPanel = createContentComponent();
        }
        return myTabContentPanel;
    }

    @Override
    protected Component getChartComponent() {
        return myPlannerChart;
    }

    @Override
    protected Component getTreeComponent() {
        return myTreeFacade.getTreeComponent();
    }

    @Override
    protected Component createButtonPanel() {
        ToolbarBuilder builder = new ToolbarBuilder()
                .withHeight(100) // Undo to 24
                .withSquareButtons()
                .withDpiOption(getUiFacade().getDpiOption())
                .withLafOption(getUiFacade().getLafOption(), null);
        myTreeFacade.addToolbarActions(builder);
        final GPToolbar toolbar = builder.build();
        return toolbar.getToolbar();
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
