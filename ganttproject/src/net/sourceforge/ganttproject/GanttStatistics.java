package net.sourceforge.ganttproject;

import net.sourceforge.ganttproject.task.TaskManager;

import java.util.Date;
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


    public GanttStatistics(TaskManager taskManager) {
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

    public TaskManager getMyTaskManager() {
        return myTaskManager;
    }

}
