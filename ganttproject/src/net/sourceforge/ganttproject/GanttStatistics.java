package net.sourceforge.ganttproject;

import biz.ganttproject.core.calendar.WeekendCalendarImpl;
import biz.ganttproject.core.time.GanttCalendar;
import net.sourceforge.ganttproject.io.BurndownDataIO;
import net.sourceforge.ganttproject.task.BurndownPastTask;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 * <p>
 * GanttStatistics class - Calculates aditional information/statistics about the project
 */
public class GanttStatistics {

    private static final Date date = new Date();

    private final TaskManager myTaskManager;

    private List<Integer> burndownChartData;

    private WeekendCalendarImpl calendar;

    public GanttStatistics(TaskManager taskManager) {
        this.calendar = new WeekendCalendarImpl();
        this.myTaskManager = taskManager;
    }

    /**
     * Gets the total number of tasks in the project
     *
     * @return number of tasks
     */
    public int getTotalTasks() {
        return this.myTaskManager.getTaskCount();
    }

    /**
     * Gets the total time spent, in days, in the project
     *
     * @return spent time
     */
    public long getCurrentSpentTime() {
        Date startDate = this.myTaskManager.getProjectStart();
        return getDifferenceDays(startDate, date);
    }

    /**
     * Calculates the difference between two dates in days
     *
     * @param d1 starting day
     * @param d2 end day
     * @return days between d1 and d2
     */
    public long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    /**
     * Gets the total estimated time, in days for the project's completion
     *
     * @return estimated time (in days)
     */
    public long getTotalEstimatedTime() {
        return this.myTaskManager.getProjectLength().getLength();
    }

    /**
     * Gets the sum of all the tasks' durations
     *
     * @return duration (in days)
     */
    public int getSumOfTaskDurations() {
        int totalTime = 0;
        int count = 0;
        int index = 0;

        while (count < this.getTotalTasks()) {
            if (this.myTaskManager.getTask(index) != null) {
                count++;
                totalTime += myTaskManager.getTask(index).getDuration().getLength();
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
     * Calculates the number of tasks whose progress is 100% (finished)
     *
     * @return Number of finished tasks
     */
    private int calculateFinishedTasks() {
        int finishedTasks = 0;
        int count = 0;
        int index = 0;

        while (count < this.myTaskManager.getTaskCount()) {
            if (this.myTaskManager.getTask(index) != null) {
                count++;
                if (this.myTaskManager.getTask(index).getCompletionPercentage() == 100)
                    finishedTasks++;
            }
            index++;
        }
        return finishedTasks;
    }

    /**
     * Overall progress is measured in percentage
     *
     * @return progress
     */
    public float getOverallProgress() {
        return myTaskManager.getProjectCompletion();
    }

    /**
     * TODO - Put this in another class
     * Gets the all the info for a burndown chart
     *
     * @return burndown chart data
     */
    public List<Integer> getBurndownInfo() {
        initBurndownData();
        return burndownChartData;
    }


    /**
     * TODO - Put this in another class
     */
    private void initBurndownData() {
        this.burndownChartData = new ArrayList<>();
        resetDataStructure(burndownChartData); // days in project fill with zeros

        int taskCount = 0;
        int index = 0;

        while (taskCount < this.myTaskManager.getTaskCount()) {
            if (this.myTaskManager.getTask(index) != null) {
                taskCount++;
                if (this.myTaskManager.getTask(index).getCompletionPercentage() == 100) {
                    int dayInProject = calculateDiffDate(index);
                    int sum = burndownChartData.get(dayInProject);
                    sum += myTaskManager.getTask(index).getDuration().getLength(); // task duration without weekends
                    burndownChartData.remove(dayInProject); // MAGIA
                    burndownChartData.add(dayInProject, sum);
                }
            }
            index++;
        }
    }

    private int calculateDiffDate(int index) {
        GanttCalendar dateToConvert = myTaskManager.getTask(index).getEnd();

        int year = dateToConvert.getYear() - 1900;
        int month = dateToConvert.getMonth();
        int day = dateToConvert.getDay();

        Date endDate = new Date(year, month, day);

        return (int) getDifferenceDays(myTaskManager.getProjectStart(), endDate);
    }

    public void resetDataStructure(List<Integer> list) {
        for (int i = 0; i < getTotalEstimatedTime() + 2; i++)
            list.add(i, 0);
    }

    public TaskManager getMyTaskManager() {
        return myTaskManager;
    }

    public boolean todayIsWeekend(Task task, int offSetDayInProject) {
        GanttCalendar dateToConvert = task.getStart();

        int year = dateToConvert.getYear() - 1900;
        int month = dateToConvert.getMonth();
        int day = dateToConvert.getDay() + offSetDayInProject;

        Date today = new Date(year, month, day);
        return calendar.isWeekend(today);
    }
}
