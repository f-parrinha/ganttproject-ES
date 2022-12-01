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

    private static final Date date = new Date();
    private TaskManager taskManager;
    private List<Integer> burndownChartData;

    public PlannerStatistics(TaskManager taskManager) {
        this.taskManager = taskManager;
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
    public long getDifferenceDays(Date d1, Date d2) {
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

    public List<Integer> getBurndownInfo() {
        initBurndownData();
        return burndownChartData;
    }

    /**
     * TODO
     * <p>
     * Calculates the number of tasks whose progress is 100% (finished)
     *
     * @return Number of finished tasks
     */
    private int calculateFinishedTasks() {
        int finTasks = 0;
        int count = 0;
        int index = 0;

        while(count < this.taskManager.getTaskCount()) {
            if(this.taskManager.getTask(index) != null){
                count++;
                if (this.taskManager.getTask(index).getCompletionPercentage() == 100)
                    finTasks++;
            }
            index++;
        }
        return finTasks;
    }

    private void initBurndownData() {

        this.burndownChartData = new ArrayList<>();

        for(int i = 0; i < getTotalEstimatedTime() + 1; i++)
            burndownChartData.add(i,0);
        int count = 0;
        int index = 0;

        while(count < this.taskManager.getTaskCount()) {
            if(this.taskManager.getTask(index) != null){
                count++;
                if (this.taskManager.getTask(index).getCompletionPercentage() == 100) {
                    int dayInProject = calculateDiffDate(index);
                    int sum = burndownChartData.get(dayInProject);
                    sum += taskManager.getTask(index).getDuration().getLength(); // task duration without weekends
                    burndownChartData.remove(dayInProject); // MAGIA
                    burndownChartData.add(dayInProject, sum);
                }
            }
            index++;
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


}
