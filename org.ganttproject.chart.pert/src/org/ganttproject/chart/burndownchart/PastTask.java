package org.ganttproject.chart.burndownchart;

import java.util.Date;

public class PastTask {
    private Date startDate, endDate;
    private int percentage, ID;

    public PastTask(Date startDate, Date endDate, int percentage, int ID){
        this.startDate = startDate;
        this.endDate = endDate;
        this.percentage = percentage;
        this.ID = ID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getPercentage() {
        return percentage;
    }

    public int getID() {
        return ID;
    }
}
