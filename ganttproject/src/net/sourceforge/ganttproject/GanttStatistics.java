package net.sourceforge.ganttproject;

import biz.ganttproject.core.calendar.WeekendCalendarImpl;
import biz.ganttproject.core.time.GanttCalendar;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskManager;

import java.time.LocalDate;
import java.time.ZoneId;
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
     * Calculates the number of tasks whose progress is 100% (finished)
     *
     * @return Number of finished tasks
     */
    private int calculateFinishedTasks() {
        int finishedTasks = 0;
        int count = 0;
        int index = 0;

        while(count < this.myTaskManager.getTaskCount()) {
            if(this.myTaskManager.getTask(index) != null){
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

    public List<Integer> getRemEffortInfo() {
        initRemEffortData();
        return remainingEffortData;
    }

    /** TODO - Put this in another class */
    private void initBurndownData() {

        this.burndownChartData = new ArrayList<>();
        resetDataStructure(burndownChartData); // days in project fill with zeros
        int taskCount = 0;
        int index = 0;

        while(taskCount < this.myTaskManager.getTaskCount()) {
            if(this.myTaskManager.getTask(index) != null){
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

    private void initRemEffortData() {

        this.remainingEffortData = new ArrayList<>();
        resetDataStructure(remainingEffortData); // days in project fill with zeros

        Task[] myTasks = this.myTaskManager.getTasks();

        for (Task task : myTasks){
            double percentage = task.getCompletionPercentage() / 100.0;
            /* completedDuration -> Acrescentar os fins de semana */
            int completedDuration = (int)(task.getDuration().getLength() * percentage) + calculateNumOfWeekend(task.getTaskID()); // task completed duration without weekends
            int dayOffSetInProject = calculateOffSetInProject(task);
            updateRemainingEffortData(task, dayOffSetInProject, completedDuration);
        }
    }


    private int calculateNumOfWeekend(int index) {
        GanttCalendar startDateToConvert = myTaskManager.getTask(index).getEnd();
        GanttCalendar endDateToConvert = myTaskManager.getTask(index).getStart();

        int startYear = startDateToConvert.getYear() - 1900;
        int startMonth =startDateToConvert.getMonth();
        int startDay = startDateToConvert.getDay();

        Date startDate = new Date(startYear, startMonth, startDay);
        //
        int endYear = endDateToConvert.getYear() - 1900;
        int endMonth =endDateToConvert.getMonth();
        int endDay = endDateToConvert.getDay();

        Date endDate = new Date(endYear, endMonth, endDay);


        Date currDate = startDate;
        int currWeekends = 0;
        while (currDate.before(endDate)){
            currDate.setTime(currDate.getTime() + 86400000);//adiciona o numero de milisegundos de um dia
            if(calendar.isWeekend(currDate))currWeekends++;
        }
        return currWeekends;
       // Date currDate = new Date();
        //System.out.println(myTaskManager.getCalendar().getActivities(currDate, startDate));
        //return 0;

    }

    private void updateRemainingEffortData(Task task, int taskDayOffSetInProject, int completedDuration){

        int todayOffSetInProject = (int) getCurrentSpentTime();

        int iterationLoop = Math.min(todayOffSetInProject + 1, (taskDayOffSetInProject + completedDuration));
        int rest = Math.max((taskDayOffSetInProject + completedDuration) - (todayOffSetInProject + 1), 0); // Amount of work done that is scheduled for days after today

        //int toprint = (taskDayOffSetInProject + completedDuration);
        //System.out.println("dayOffSetInProject "+taskDayOffSetInProject);
        //System.out.println("completedDuration "+completedDuration);
        //System.out.println("dayOffSetInProject + completedDuration " + toprint);
        //System.out.println("todayOffSetInProject" +todayOffSetInProject);
        //System.out.println("iterationLoop "+iterationLoop);
        //System.out.println("rest "+rest);

        for(int i = taskDayOffSetInProject; i < iterationLoop; i++) {
            if (todayIsWeekend(task, i))
                markWeekend(i);
            else {
                int val = i == iterationLoop - 1 ? 1 + rest : 1;
                markWorkDoneToday(remainingEffortData, i, val);
            }
        }
    }

    private void markWorkDoneToday(List<Integer> list, int index, int value){
        int sum = list.get(index);
        sum += value;
        list.remove(index);
        list.add(index, sum);
    }

    private void markWeekend(int index){
        if (!(remainingEffortData.get(index) < 0)){
            remainingEffortData.remove(index);
            remainingEffortData.add(index, 0);
        }
    }

    private boolean todayIsWeekend(Task task, int offSetDayInProject) {
        GanttCalendar dateToConvert = task.getStart();

        int year = dateToConvert.getYear() - 1900;
        int month =dateToConvert.getMonth();
        int day = dateToConvert.getDay() + offSetDayInProject;

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




    private int calculateOffSetInProject(Task task) {
        GanttCalendar dateToConvert = task.getStart();

        int year = dateToConvert.getYear() - 1900;
        int month =dateToConvert.getMonth();
        int day = dateToConvert.getDay();

        Date startDate = new Date(year, month, day);

        return (int) getDifferenceDays(myTaskManager.getProjectStart(), startDate);
    }

    private void resetDataStructure(List<Integer> list){
        for(int i = 0; i < getTotalEstimatedTime() + 1; i++)
            list.add(i,0);
    }

}
