package org.ganttproject.chart;

import javax.swing.*;
import java.awt.*;

public abstract class PanelStyler {

    protected static final String fontStyle = "Helvetica";

    protected Dimension screenSize;

    protected int fontSize;

    public PanelStyler(){
        screenSize = Toolkit. getDefaultToolkit(). getScreenSize();
    }

    /**
     * Scales the given x coordinate to match the current panel's and screen's width coordinates
     *
     * @param i x value
     * @return scaled x value
     */
    protected int resizeX(int i, JPanel myPanel) {
        return i*myPanel.getWidth()/screenSize.width;
    }

    /**
     * Scales the given y coordinate to match the current panel's and screen's width coordinates
     *
     * @param i y value
     * @return scaled y value
     */
    protected int resizeY(int i,  JPanel myPanel) {
        return i*myPanel.getHeight()/screenSize.height;
    }

    /**
     * Updates the font size, regarding panel's and screen's width
     *
     * @param size font size
     */
    protected void updateFontSize(int size, JPanel myPanel){
        fontSize = size*myPanel.getWidth()/screenSize.width;
    }
}
