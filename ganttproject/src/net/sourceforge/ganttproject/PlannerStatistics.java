package net.sourceforge.ganttproject;

import biz.ganttproject.core.time.GanttCalendar;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

import java.sql.SQLOutput;
import java.time.temporal.ChronoUnit;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
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

    private List<Integer> burndownChartData;

    public PlannerStatistics(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.burndownChartData = new ArrayList<>();
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
        Date startDate = this.taskManager.getProjectStart();

        return getDifferenceDays(startDate, date);
    }

    /**
     *
     * @param d1
     * @param d2
     * @return
     */
    private long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
    /**
     * Gets the total estimated time, in days for the project's completion
     *
     * @return estimated time
     */
    public long getTotalEstimatedTime() {
        return this.taskManager.getProjectLength().getLength();
    }

    public int getSumOfTaskDurations() {
        int totalTime = 0;
        int count = 0;
        int index = 0;

        while(count < this.getTotalTasks()) {
            if(this.taskManager.getTask(index) != null){
                count++;
                totalTime += taskManager.getTask(index).getDuration().getLength();
            }
            index++;
        }
        return totalTime;
    }
    /**
     * Gets the total amount of completed/finished tasks
     *
     * @return finished tasks
     */
    public int getFinishedTasks() {
        return calculateFinishedTasks();
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
        return taskManager.getProjectCompletion();
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
    private int calculateFinishedTasks() {
        initBurndown();
        int finTasks = 0;
        int count = 0;
        int index = 0;

        while(count < this.taskManager.getTaskCount()) {
            if(this.taskManager.getTask(index) != null){
                count++;
                if (this.taskManager.getTask(index).getCompletionPercentage() == 100) {
                    finTasks++;
                    int sum = burndownChartData.get(calculateDiffDate(index));
                    sum += taskManager.getTask(index).getDuration().getLength();
                    burndownChartData.add(calculateDiffDate(index), sum);
                    System.out.println(sum);
                }
            }
            index++;
        }
        return finTasks;
    }

    private void initBurndown() {
        for(int i = 0; i < getTotalEstimatedTime(); i++) {
            burndownChartData.add(i,0);
        }
    }

    private int calculateDiffDate(int index) {
        GanttCalendar dateToConvert = taskManager.getTask(index).getEnd();

        int year = dateToConvert.getYear() - 1900;
        int month =dateToConvert.getMonth();
        int day = dateToConvert.getDay();

        Date endDate = new Date(year, month, day);

        return (int) getDifferenceDays(taskManager.getProjectStart(), endDate);
    }
    /**
     * List of tasks done at certain date
     *
     * @return a treeMap with the KEY as the tasks the end date and a number of tasks completed on that day as the VALUE
     */
//    private Map<GanttCalendar, Integer> getCompletedCalendar() {
//        //Generates a treeMap with the key as the end date and a number of tasks completed on that day as the value
//        Map<GanttCalendar, Integer> tasksDoneAtDate = new TreeMap<GanttCalendar, Integer>();
//        for (int currTask = 0; currTask < this.getTotalTasks(); currTask++) {
//            if (tasks[currTask].getCompletionPercentage() == 100) {
//                GanttCalendar currEndDate = tasks[currTask].getEnd();
//                if (!tasksDoneAtDate.containsKey(currEndDate))
//                    tasksDoneAtDate.put(currEndDate, 1);
//                else {
//                    tasksDoneAtDate.put(currEndDate, tasksDoneAtDate.get(currEndDate) + 1);
//                }
//            }
//        }
//        return tasksDoneAtDate;
//    }

//    public Map<GanttCalendar, Integer> burndownData() {
//        //Em teoria ordena no putAll, verificar se o ganttCalendar implementa bem o Comparable
//        Map<GanttCalendar, Integer> totalTasksAtDay = new TreeMap<GanttCalendar, Integer>();
//        totalTasksAtDay.putAll(getCompletedCalendar());
//        //
//        Integer pastEntryValue = 0;
//        // Goes through list adding the past value with the current making this list containing the total
//        // tasks done until that day since the beginning (assuming the list is ordered by day (the key))
//        for (Map.Entry<GanttCalendar, Integer> entry : totalTasksAtDay.entrySet()) {
//            Integer currEntryValue = entry.getValue();
//            entry.setValue(pastEntryValue + currEntryValue);
//            pastEntryValue = currEntryValue;
//        }
//        return totalTasksAtDay;
//    }

}
