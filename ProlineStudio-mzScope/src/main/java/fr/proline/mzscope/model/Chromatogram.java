/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.proline.mzscope.model;

/**
 *
 * @author CB205360
 */
public class Chromatogram {
   
   public IRawFile rawFile;
   public String title;
   public double minMz;
   public double maxMz;
   public double[] time;
   public double[] intensities;
   
}