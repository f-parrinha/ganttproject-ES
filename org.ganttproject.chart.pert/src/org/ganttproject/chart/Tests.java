package org.ganttproject.chart;

import biz.ganttproject.core.chart.canvas.FontChooser;
import biz.ganttproject.core.time.GanttCalendar;
import junit.framework.TestCase;
import net.sourceforge.ganttproject.GanttStatistics;

import java.awt.*;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Tests extends TestCase {
    public void testGetDifferentDays() {
        GanttStatistics gstatistics = new GanttStatistics(null);
        //
        int year = 2022;
        int month = 10;
        int day = 11;
        Date startDate = new Date(year, month, day);
        //
        year = 2022;
        month = 10;
        day = 21;
        Date endDate = new Date(year, month, day);

        assertEquals(10, gstatistics.getDifferenceDays(startDate, endDate));
        //
        year = 2022;
        month = 4;
        day = 30;
        startDate = new Date(year, month, day);
        //
        year = 2022;
        month = 5;
        day = 1;
        endDate = new Date(year, month, day);
        assertEquals(1, gstatistics.getDifferenceDays(startDate, endDate));
    }
}
