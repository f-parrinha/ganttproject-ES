package net.sourceforge.ganttproject;

import biz.ganttproject.core.time.GanttCalendar;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskManager;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Francisco Parrinha
 * <p>
 * TODO:
 *      1. Add event listeners to capture the right statistics
 *          - Maybe make observer class (?)
 */
public class PlannerStatistics {

    /**
     * TO BE TESTED!
     */

    private static final Date date = new Date();

    private TaskManager taskManager;

    private Task[] tasks;

    public PlannerStatistics(TaskManager taskManager) {
        this.taskManager = taskManager;

        tasks = taskManager.getTasks();
    }

    /**
     * Gets the total number of tasks in the project
     * @return number of tasks
     */
    public int getTotalTasks() {
        return this.taskManager.getTaskCount();
    }

    /**
     * Gets the total time spent, in days, in the project
     * @return spent time
     */
    public long getCurrentSpentTime() {
        int startYear = taskManager.getProjectStart().getYear();
        int startMonth = taskManager.getProjectStart().getMonth();

        return countDaysSinceYear(startYear, date.getYear()) +
                countDaysSinceMonth(startMonth, date.getMonth(), date.getYear());
    }

    /**
     * Gets the total estimated time, in days for the project's completion
     *
     * @return estimated time
     */
    public long getTotalEstimatedTime() {
        int startYear = taskManager.getProjectStart().getYear();
        int startMonth = taskManager.getProjectStart().getMonth();
        int endYear = taskManager.getProjectEnd().getYear();
        int endMonth = taskManager.getProjectEnd().getMonth();

        return countDaysSinceYear(startYear, endYear) + countDaysSinceMonth(startMonth, endMonth, date.getYear());
    }

    /**
     * Gets the total amount of completed/finished tasks
     *
     * @return finished tasks
     */
    public int getFinishedTasks() {
        return calculateFinishedTasks(taskManager);
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
        return 100 * ((float) this.getFinishedTasks() / (float) this.getTotalTasks());
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

        for (int i = startMonth; i < currentMonth; i++) {
            int[] daysInMonth = currentYear % 4 == 0 ? new int[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31} :
                    new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};  // Checks leap year

            total += daysInMonth[i - 1];
        }

        return total;
    }

    /**
     * TODO
     * <p>
     * Calculates the number of tasks whose progress is 100% (finished)
     *
     * @param taskManager
     * @return Number of finished tasks
     */
    private int calculateFinishedTasks(TaskManager taskManager) {
        int finTasks = 0;
        for (int currTask = 0; currTask < this.getTotalTasks(); currTask++)
            if (tasks[currTask].getCompletionPercentage() == 100)
                finTasks++;

        return finTasks;
    }

    /**
     * List of tasks done at certain date
     *
     * @return a treeMap with the KEY as the tasks the end date and a number of tasks completed on that day as the VALUE
     */
    private Map<GanttCalendar, Integer> getCompletedCalendar() {
        //Generates a treeMap with the key as the end date and a number of tasks completed on that day as the value
        Map<GanttCalendar, Integer> tasksDoneAtDate = new TreeMap<GanttCalendar, Integer>();
        for (int currTask = 0; currTask < this.getTotalTasks(); currTask++) {
            if (tasks[currTask].getCompletionPercentage() == 100) {
                GanttCalendar currEndDate = tasks[currTask].getEnd();
                if (!tasksDoneAtDate.containsKey(currEndDate))
                    tasksDoneAtDate.put(currEndDate, 1);
                else {
                    tasksDoneAtDate.put(currEndDate, tasksDoneAtDate.get(currEndDate) + 1);
                }
            }
        }
        return tasksDoneAtDate;
    }

    public Map<GanttCalendar, Integer> burndownData() {
        //Em teoria ordena no putAll, verificar se o ganttCalendar implementa bem o Comparable
        Map<GanttCalendar, Integer> totalTasksAtDay = new TreeMap<GanttCalendar, Integer>();
        totalTasksAtDay.putAll(getCompletedCalendar());
        //
        Integer pastEntryValue = 0;
        // Goes through list adding the past value with the current making this list containing the total
        // tasks done until that day since the beginning (assuming the list is ordered by day (the key))
        for (Map.Entry<GanttCalendar, Integer> entry : totalTasksAtDay.entrySet()) {
            Integer currEntryValue = entry.getValue();
            entry.setValue(pastEntryValue + currEntryValue);
            pastEntryValue = currEntryValue;
        }
        return totalTasksAtDay;
    }
}
