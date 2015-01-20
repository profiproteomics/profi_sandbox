/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.graphics;

import java.awt.Color;
import java.util.HashMap;

/**
 * plot information: color, title,...
 * @author MB243701
 */
public class PlotInformation {
    private Color plotColor ;
    
    private String plotTitle;
    
    // key value information
    private HashMap<String, String> plotInfo;

    public PlotInformation() {
    }

    public Color getPlotColor() {
        return plotColor;
    }

    public void setPlotColor(Color plotColor) {
        this.plotColor = plotColor;
    }

    public String getPlotTitle() {
        return plotTitle;
    }

    public void setPlotTitle(String plotTitle) {
        this.plotTitle = plotTitle;
    }

    public HashMap<String, String> getPlotInfo() {
        return plotInfo;
    }

    public void setPlotInfo(HashMap<String, String> plotInfo) {
        this.plotInfo = plotInfo;
    }
    
    
}

