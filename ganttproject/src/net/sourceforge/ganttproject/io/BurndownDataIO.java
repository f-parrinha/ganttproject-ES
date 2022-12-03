package net.sourceforge.ganttproject.io;

import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskManager;

import java.io.*;

public class BurndownDataIO {
    private String folderPath;

    public void changeSprintFolder(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getSprintFolder() {
        return folderPath;
    }

    public void saveDay(TaskManager tasks, int day) throws IOException {
        FileWriter fileWriter = new FileWriter(folderPath + "/" + String.valueOf(day));
        PrintWriter printWriter = new PrintWriter(fileWriter);

        Task[] allTasks = tasks.getTasks();
        printWriter.println(allTasks.length);

        for (int currTask = 0; currTask < allTasks.length; currTask++) {
            printWriter.println(allTasks[currTask].getCompletionPercentage());
            printWriter.println(allTasks[currTask].getDuration().getLength()); //FALTA POR O LENGH EM DIAS!!
        }

        printWriter.close();
    }

    //returns a list of past tasks
    private int[] loadDay(int day) throws IOException {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(folderPath + "/" + String.valueOf(day)));

            int numOfTasksToBeLoaded = Integer.parseInt(reader.readLine());
            int[] pastTasks = new int[numOfTasksToBeLoaded];

            System.out.println(numOfTasksToBeLoaded);

            for (int currTask = 0; currTask < numOfTasksToBeLoaded; currTask++) {
                int currTaskPercentage = Integer.parseInt(reader.readLine()) / 100;
                int currTaskDuration = Integer.parseInt(reader.readLine());
                //
                pastTasks[currTask] = currTaskDuration * currTaskPercentage;

            }
            reader.close();
            return pastTasks;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IOException();
    }

    private boolean isThereAFileForThatDay(int day) {
        File f = new File(folderPath + "/" + String.valueOf(day));
        return f.exists();
    }

    private double loadProgressAtDay(int day) throws IOException {
        int[] pastTasks = loadDay(day);
        //
        double doneTasks = 0;
        for (int currTask = 0; currTask < pastTasks.length; currTask++)
            doneTasks += pastTasks[currTask];
        return doneTasks;
    }

    public double[] getPastRemainingEffort(int numOfDays, int totalEffort) throws IOException {
        double[] definedPoints = new double[numOfDays];
        System.out.println("totalEffort: " + totalEffort);
        for (int currDay = 0; currDay < numOfDays; currDay++) {
            if (isThereAFileForThatDay(currDay)) {
                double progressAtDay = loadProgressAtDay(currDay);
                definedPoints[currDay] = progressAtDay;
            }else definedPoints[currDay] = -1;
        }
        return definedPoints;
    }
}
