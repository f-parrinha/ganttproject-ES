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
                double currTaskPercentage = Integer.parseInt(reader.readLine()) / 100d;
                int currTaskDuration = Integer.parseInt(reader.readLine());
                //
                pastTasks[currTask] = (int) (currTaskDuration * currTaskPercentage);

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

    private int loadProgressAtDay(int day) throws IOException {
        int[] pastTasks = loadDay(day);
        //
        int doneTasks = 0;
        for (int currTask = 0; currTask < pastTasks.length; currTask++)
            doneTasks += pastTasks[currTask];
        return doneTasks;
    }

    public int[] getPastRemainingEffort(int numOfDays) throws IOException {
        int[] definedPoints = new int[numOfDays];

        for (int currDay = 0; currDay < numOfDays; currDay++) {
            if (isThereAFileForThatDay(currDay)) {
                int progressAtDay = loadProgressAtDay(currDay);
                definedPoints[currDay] = progressAtDay;
            } else definedPoints[currDay] = -1;
        }
        return definedPoints;
    }
}
