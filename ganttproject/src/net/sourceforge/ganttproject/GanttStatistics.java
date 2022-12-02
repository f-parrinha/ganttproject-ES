package net.sourceforge.ganttproject;

import biz.ganttproject.core.calendar.GPCalendarCalc;
import biz.ganttproject.core.calendar.WeekendCalendarImpl;
import biz.ganttproject.core.time.GanttCalendar;
import net.sourceforge.ganttproject.task.TaskManager;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 *
 * GanttStatistics class - Calculates aditional information/statistics about the project
 */
public class GanttStatistics {

    private static final Date date = new Date();

    private final TaskManager myTaskManager;

    private List<Integer> burndownChartData;
    private List<Integer> remainingEffortData;
    private WeekendCalendarImpl calendar;
    public GanttStatistics(TaskManager taskManager) {
        this.calendar = new WeekendCalendarImpl();
        this.myTaskManager = taskManager;
    }

    /**
     * Gets the total number of tasks in the project
     * @return number of tasks
     */
    public int getTotalTasks() {
        return this.myTaskManager.getTaskCount();
    }

    /**
     * Gets the total time spent, in days, in the project
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

        while(count < this.getTotalTasks()) {
            if(this.myTaskManager.getTask(index) != null){
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

    public List<Integer> getRemEffortInfo() {
        initRemEffortData();
        return remainingEffortData;
    }
    /**
     * Calculates the number of tasks whose progress is 100% (finished)
     *
     * @return Number of finished tasks
     */
    private int calculateFinishedTasks() {
        int finTasks = 0;
        int count = 0;
        int index = 0;

        while(count < this.myTaskManager.getTaskCount()) {
            if(this.myTaskManager.getTask(index) != null){
                count++;
                if (this.myTaskManager.getTask(index).getCompletionPercentage() == 100)
                    finTasks++;
            }
            index++;
        }
        return finTasks;
    }

    /** TODO - Put this in another class */
    private void initBurndownData() {

        this.burndownChartData = new ArrayList<>();

        for(int i = 0; i < getTotalEstimatedTime() + 1; i++)
            burndownChartData.add(i,0);
        int count = 0;
        int index = 0;

        while(count < this.myTaskManager.getTaskCount()) {
            if(this.myTaskManager.getTask(index) != null){
                count++;
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

    private void initRemEffortData() {

        this.remainingEffortData = new ArrayList<>();

        for(int i = 0; i < getTotalEstimatedTime() + 1; i++)
           remainingEffortData.add(i,0);
        int count = 0;
        int index = 0;

        while(count < this.myTaskManager.getTaskCount()) {
            if(this.myTaskManager.getTask(index) != null){
                count++;

                double percentage = this.myTaskManager.getTask(index).getCompletionPercentage() / 100.0;
                int duration = (int)(this.myTaskManager.getTask(index).getLength() * percentage);
                int dayInProject = calculateRemEffData(index);

                for(int i = dayInProject; i < duration + dayInProject; i++ ) {
                    System.out.println(isWeekend(index, i));
                    System.out.println("duration "+duration);
                    System.out.println("dayInProject "+dayInProject);
                    System.out.println("duration + dayInProject "+ duration + dayInProject);
                    if (!isWeekend(index, i)){
                        int sum = remainingEffortData.get(i);
                        sum += 1;
                        remainingEffortData.remove(i);
                        remainingEffortData.add(i, sum);
                    } else {
                        if (!(remainingEffortData.get(i) < 0)){
                            remainingEffortData.remove(i);
                            remainingEffortData.add(i, -1);
                        }

                    }

                }
            }
            index++;
        }
    }

    private boolean isWeekend(int taskIndex, int offset) {
        GanttCalendar dateToConvert = myTaskManager.getTask(taskIndex).getStart();

        int year = dateToConvert.getYear() - 1900;
        int month =dateToConvert.getMonth();
        int day = dateToConvert.getDay() + offset;

        Date today = new Date(year, month, day);
        return calendar.isWeekend(today);
    }

    private int calculateDiffDate(int index) {
        GanttCalendar dateToConvert = myTaskManager.getTask(index).getEnd();

        int year = dateToConvert.getYear() - 1900;
        int month =dateToConvert.getMonth();
        int day = dateToConvert.getDay();

        Date endDate = new Date(year, month, day);

        return (int) getDifferenceDays(myTaskManager.getProjectStart(), endDate);
    }


    private int calculateRemEffData(int index) {
        GanttCalendar dateToConvert = myTaskManager.getTask(index).getStart();

        int year = dateToConvert.getYear() - 1900;
        int month =dateToConvert.getMonth();
        int day = dateToConvert.getDay();

        Date startDate = new Date(year, month, day);

        return (int) getDifferenceDays(myTaskManager.getProjectStart(), startDate);
    }

}
