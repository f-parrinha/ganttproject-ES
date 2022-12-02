package org.ganttproject.chart;

import org.ganttproject.chart.planner.PlannerPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * @author Francisco Parrinha
 * @author Martin Magdalinchev
 * @author Bernardo Atalaia
 * @author Carlos Soares
 * @author Pedro Inácio
 * <p>
 * PlannerPainter asbtract class - Adds the extra functionalities used by 'panel painters'
 */
public abstract class PanelStyler {

    protected static final String FONT_STYLE = "Helvetica";

    protected int fontSize;

    protected Dimension screenSize;

    public PanelStyler(){
        screenSize = Toolkit. getDefaultToolkit(). getScreenSize();
    }

    /**
     * Gets the current screen width
     *
     * @return screen width
     */
    protected int getScreenSizeX(){
        return screenSize.width;
    }

    /**
     * Gets the current screen height
     *
     * @return screen height
     */
    protected int getScreenSizeY(){
        return screenSize.height;
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
        fontSize = size * myPanel.getWidth()/screenSize.width;
    }

    /**
     * Loads an image from the resources' folder, with a given directory
     *
     * @param directory directory in resources folder
     */
    protected Image loadImageFromDir(String directory) throws IOException {
        Image image;
        URL url = PlannerPanel.class.getResource(directory);
        assert url != null;
        image = ImageIO.read(url);

        return image;
    }
}

