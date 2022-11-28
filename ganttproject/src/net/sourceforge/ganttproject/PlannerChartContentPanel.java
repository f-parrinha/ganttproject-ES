package net.sourceforge.ganttproject;

import net.sourceforge.ganttproject.chart.Chart;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.gui.view.GPView;
import net.sourceforge.ganttproject.task.TaskManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author Francisco Parrinha
 *
 * TODO:
 *      1. Draw the correct areas
 *      2. Get event listeners from planner statistics
 *      3. Add the correct statistics
 *      4. Draw the graph (still too broad)
 */
public class PlannerChartContentPanel implements GPView {

    private PlannerStatistics plannerStatistics;

    private final TreeTableContainer myTreeFacade;

    private final UIFacade myUiFacade;

    private JComponent myTabContentPanel;

    public PlannerChartContentPanel(UIFacade workbenchFacade, TreeTableContainer resourceTree, TaskManager taskManager) {
        myUiFacade = workbenchFacade;
        myTreeFacade = resourceTree;
        plannerStatistics = new PlannerStatistics(taskManager);
    }

    /**
     * Gets the Planner Chart
     *
     * @return Chart
     */
    public Chart getChart() {
        return getUiFacade().getResourceChart();
    }

    /**
     * Gets tab's contetn
     *
     * @return View Content
     */
    public Component getViewComponent() {
        if (myTabContentPanel == null) {
            myTabContentPanel = createContentComponent();
        }

        return myTabContentPanel;
    }

    /**
     * Sets the tab active (or not)
     *
     * @param active - true or false
     */
    public void setActive(boolean active) {
        if (active) {
            getTreeComponent().requestFocus();
        }
    }

    /**
     * TODO - Study Tree Component
     * Still do not know what this does
     *
     * @return
     */
    protected Component getTreeComponent() {
        return myTreeFacade.getTreeComponent();
    }

    /**
     * TODO - Study UI Facade
     * Returns the UI Facade
     *
     * @return Planner UI Facade
     */
    protected UIFacade getUiFacade() {
        return myUiFacade;
    }

    /**
     * Draws all the content in the tab
     *
     * @return final component with statistics area and graph area
     */
    private JComponent createContentComponent() {
        JPanel mainComponent = new JPanel(new BorderLayout());

        mainComponent.add(createStatisticsArea());
        mainComponent.setBackground(Color.CYAN);    // NOTE: This is not working

        return mainComponent;
    }

    /**
     * Creates the area in the tab with the statistics
     *
     * @return area with statistics
     */
    private JComponent createStatisticsArea() {
        JPanel statisticsArea = new JPanel(new BorderLayout(10, 10));

        // Adds text
        JTextArea textArea = new JTextArea();

        /*textArea.setSize(100,100);
        textArea.setBackground(Color.WHITE);
        textArea.setText("!! TEST !! Total  Tasks: " + plannerStatistics.getTotalTasks());*/

        // Final product of the statistics area
        statisticsArea.add(textArea, BorderLayout.CENTER);
        statisticsArea.setBounds(10, 10, 300, 100); // This is not working
        statisticsArea.setBackground(Color.BLUE);   // This is not working

        /**
         * TESTS
         *
         * NOTE: All prints show the correct info, but it is not drawing on the given position
         *
         */
        System.out.println(statisticsArea.getSize());
        System.out.println(statisticsArea.getLocation());
        System.out.println(statisticsArea.getBounds());

        return statisticsArea;
    }

    /**
     * TODO
     * @return
     */
    private JComponent createGraphArea() {
        return null;
    }
}
