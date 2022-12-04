package net.sourceforge.ganttproject.io;

import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 * <p>
 * <p>
 * BurndownDataIO Class - Responsible for the loadings and savings of the state of the burndown chart
 * from/into a text file. The information from the text file is used by RemainEffortGraph and
 * RemainTasksGraph in a different way
 */
public class BurndownDataIO {
    private String folderPath;

    /**
     * Changes the path to a folder where the state of the project will be saved
     *
     * @param folderPath new folder path
     */
    public void changeSprintFolder(String folderPath) {
        this.folderPath = folderPath;
    }


    /**
     * Saves the state of the project of a given day into a previous chosen folder
     *
     * @param tasks task manager
     * @param day   day of the project to be saved
     * @throws IOException exception
     */
    public void saveDay(TaskManager tasks, int day) throws IOException {
        FileWriter fileWriter = new FileWriter(folderPath + "/" + String.valueOf(day));
        PrintWriter printWriter = new PrintWriter(fileWriter);

        Task[] allTasks = tasks.getTasks();
        printWriter.println(allTasks.length);

        for (int currTask = 0; currTask < allTasks.length; currTask++) {
            printWriter.println(allTasks[currTask].getCompletionPercentage());
            printWriter.println(allTasks[currTask].getDuration().getLength());
        }

        printWriter.close();
    }

    /**
     * Calculates the overall progress at a specific date from its file
     *
     * @param day day to be considered
     * @return overall progress
     * @throws IOException exception
     */
    private int[] loadDay(int day) throws IOException {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(folderPath + "/" + String.valueOf(day)));

            int numOfTasksToBeLoaded = Integer.parseInt(reader.readLine());
            int[] pastTasks = new int[numOfTasksToBeLoaded];

            for (int currTask = 0; currTask < numOfTasksToBeLoaded; currTask++) {
                double currTaskPercentage = Integer.parseInt(reader.readLine()) / 100d;
                int currTaskDuration = Integer.parseInt(reader.readLine());
                pastTasks[currTask] = (int) (currTaskDuration * currTaskPercentage);
            }
            reader.close();
            return pastTasks;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IOException();
    }

    /**
     * Calculates the finished tasks at a specific day from its saved file
     *
     * @param day day to be calculated
     * @return number of finished tasks
     * @throws IOException exception
     */
    private int loadFinishedTasksOfDay(int day) throws IOException {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(folderPath + "/" + String.valueOf(day)));

            int numOfTasksToBeLoaded = Integer.parseInt(reader.readLine());
            int pastTasks = 0;

            for (int currTask = 0; currTask < numOfTasksToBeLoaded; currTask++) {
                int currTaskPercentage = Integer.parseInt(reader.readLine());
                int currTaskDuration = Integer.parseInt(reader.readLine());
                if (currTaskPercentage == 100)
                    pastTasks += currTaskDuration;
            }
            reader.close();
            return pastTasks;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IOException();
    }

    /**
     * Verify if there is a saved file with information about the project state of a given day
     *
     * @param day day to be verified
     * @return <code>true</code> if the file exists <code>false</code> if it does not
     */
    private boolean isThereAFileForThatDay(int day) {
        File f = new File(folderPath + "/" + String.valueOf(day));
        return f.exists();
    }

    /**
     * Calculates the number of days of work finished, at a specific day, in the remaining effort graph
     *
     * @param day day to be considered
     * @return days of finished work
     * @throws IOException exception
     */
    private int loadProgressAtDay(int day) throws IOException {
        int[] pastTasks = loadDay(day);
        //
        int doneTasks = 0;
        for (int currTask = 0; currTask < pastTasks.length; currTask++)
            doneTasks += pastTasks[currTask];
        return doneTasks;
    }

    /**
     * Extract the information to be used for the graphInfo list of the remaining effort graph
     *
     * @param numOfDays size of the project
     * @throws IOException exception
     * @ graphInfo list
     */
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

    /**
     * Builds the graphInfo list from the files information for the remaining tasks graph
     *
     * @param numOfDays size of the project
     * @return graphInfo list
     * @throws IOException exception
     */
    public List<Integer> getPastRemainingTasks(int numOfDays) throws IOException {
        List<Integer> definedPoints = new ArrayList<>();
        for (int currDay = 0; currDay < numOfDays; currDay++) {
            if (isThereAFileForThatDay(currDay)) {
                int progressAtDay = loadFinishedTasksOfDay(currDay);
                definedPoints.add(currDay, progressAtDay);
            } else definedPoints.add(currDay, 0);
        }
        return definedPoints;
    }

}
