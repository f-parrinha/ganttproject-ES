package org.ganttproject.chart.burndownchart;

import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskManager;

import java.io.*;
import java.util.Date;

public class BurndownDataIO {

    public BurndownDataIO() {
    }

    public void save(TaskManager tasks, Date date) throws IOException {
        String fileName = "~/Desktop/teste.burndown"; //MUDAR ISTO HARDCORE

        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        //
        int year = date.getYear();
        int month = date.getYear();
        int day = date.getYear();
        //
        printWriter.println(year);
        printWriter.println(month);
        printWriter.println(day);

        Task[] allTasks = tasks.getTasks();
        printWriter.println(allTasks.length);

        for (int currTask = 0; currTask < allTasks.length; currTask++) {
            printWriter.println(allTasks[currTask].getStart().getYear());
            printWriter.println(allTasks[currTask].getStart().getMonth());
            printWriter.println(allTasks[currTask].getStart().getDay());
            //
            printWriter.println(allTasks[currTask].getEnd().getYear());
            printWriter.println(allTasks[currTask].getEnd().getMonth());
            printWriter.println(allTasks[currTask].getEnd().getDay());
            //
            printWriter.println(allTasks[currTask].getCompletionPercentage());
            printWriter.println(allTasks[currTask].getTaskID());
        }
        printWriter.close();
    }

    public void load() throws IOException {
        String fileName = "~/Desktop/teste.burndown"; //MUDAR ISTO HARDCORE

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            int year = Integer.parseInt(reader.readLine());
            int month = Integer.parseInt(reader.readLine());
            int day = Integer.parseInt(reader.readLine());

            int numOfTasksToBeLoaded = Integer.parseInt(reader.readLine());
            PastTask[] pastTasks = new PastTask[numOfTasksToBeLoaded];

            for (int currTask = 0; currTask < numOfTasksToBeLoaded; currTask++) {
                int currStartYear = Integer.parseInt(reader.readLine());
                int currStartMonth = Integer.parseInt(reader.readLine());
                int currStartDay = Integer.parseInt(reader.readLine());
                Date currTaskStartDate = new Date(currStartYear, currStartMonth, currStartDay);
                //
                int currEndYear = Integer.parseInt(reader.readLine());
                int currEndMonth = Integer.parseInt(reader.readLine());
                int currEndDay = Integer.parseInt(reader.readLine());
                Date currTaskEndDate = new Date(currEndYear, currEndMonth, currEndDay);
                //
                int currTaskPercentage = Integer.parseInt(reader.readLine()) / 100;
                int currTaskID = Integer.parseInt(reader.readLine());
                //
                pastTasks[currTask] = new PastTask(currTaskStartDate, currTaskEndDate, currTaskPercentage, currTaskID);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
