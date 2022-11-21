package net.sourceforge.ganttproject;

import net.sourceforge.ganttproject.task.TaskManager;

import java.util.Date;

public class PlannerStatistics {

    /**
     * TO BE TESTED!
     */

    //private TaskManager taskManager;

    private int totalTasks;

    private int finishedTasks;

    private long totalEstimatedTime;

    private long currentSpentTime;

    public PlannerStatistics(TaskManager taskManager) {
        Date date = new Date();
        int startYear = taskManager.getProjectStart().getYear();
        int startMonth = taskManager.getProjectStart().getMonth();
        int endYear = taskManager.getProjectEnd().getYear();
        int endMonth = taskManager.getProjectEnd().getMonth();

        totalTasks = taskManager.getTaskCount();
        currentSpentTime = countDaysSinceYear(startYear, date.getYear()) +
                countDaysSinceMonth(startMonth, date.getMonth(), date.getYear());
        totalEstimatedTime = countDaysSinceYear(startYear, endYear) +
                countDaysSinceMonth(startMonth, endMonth, date.getYear());
        finishedTasks = calculateFinishedTasks(taskManager);
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public long getCurrentSpentTime() {
        return currentSpentTime;
    }

    public long getTotalEstimatedTime() {
        return totalEstimatedTime;
    }

    public int getFinishedTasks() {
        return finishedTasks;
    }

    /**
     * Overall progress is measured in percentage
     * <p>
     * NOTE: After this class is tested, check if taskManager.getProjectionCompletion
     * does the same.
     *
     * @return Progress
     */
    public float getOverallProgress() {
        return 100 * ((float) finishedTasks / (float) totalTasks);
    }


    /**
     * Counts the number of days that have passed since a certain year
     *
     * @param startYear - The first year
     * @return Days that have passed
     */
    private long countDaysSinceYear(int startYear, int currentYear) {
        int total = 0;

        for (int i = startYear; i < currentYear; i++) {
            total += (i % 4 == 0 ? 366 : 365);  // Checks if it is a leap year
        }

        return total;
    }

    /**
     * Counts the number of days that have passed since a certain month
     *
     * @param startMonth   - The first month
     * @param currentMonth - The current month.
     * @param currentYear  - The current year. Useful to check if current year is leap year
     * @return Days that have passed
     */
    private long countDaysSinceMonth(int startMonth, int currentMonth, int currentYear) {
        long total = 0;
        int[] daysInMonth = currentYear % 4 == 0 ? new int[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31} :
                new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};  // Checks leap year

        for (int i = startMonth; i < currentMonth; i++) {
            total += daysInMonth[i - 1];
        }

        return total;
    }

    /**
     * TODO
     * <p>
     * Calculates the number of tasks whose progress is 100½ (finished)
     *
     * @param taskManager
     * @return Number of finished tasks
     */
    private int calculateFinishedTasks(TaskManager taskManager) {
        return 0;
    }
}
