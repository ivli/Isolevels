/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isolevels;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.image.Raster;

/**
 *
 * @author likhachev
 */
public class Isolevel extends Path2D.Double {
    private final Raster iSrc; 
    private final Rectangle iRect;
    
    private java.lang.Double iLevel;
    private Conrec iCon;
    
    private Isolevel(Raster aSrc, Rectangle aRect) {
        iSrc = aSrc;
        iRect = aRect;
        iCon = new Conrec((double sX, double sY, double eX, double eY, double aNotUsed) -> {moveTo(sX, sY); lineTo(eX, eY);});
       
     }  
    
    public static Isolevel create(Raster aSrc, Rectangle aRect, java.lang.Double aLevel) {
        Isolevel ret = new Isolevel(aSrc, aRect);
        ret.prepare();
        
        if (null != aLevel)
            ret.update(aLevel);
                
        return ret;
    }
    
    private double da[][];
    private double x[];
    private double y[];
    
    double min = java.lang.Double.MAX_VALUE;
    double max = java.lang.Double.MIN_VALUE;
    
    private void prepare() {
        da = new double[iSrc.getWidth()][iSrc.getHeight()];
        x = new double[iSrc.getWidth()];
        y = new double[iSrc.getHeight()];

       

        int [] temp = iSrc.getPixels(0, 0, iSrc.getWidth(), iSrc.getHeight(), (int[]) null);

        for (int j = 0; j < iSrc.getHeight(); ++j) {                           
            y[j] = j;
            for (int i = 0; i < iSrc.getWidth(); ++i) {                                
                da[i][j] = temp[j*iSrc.getWidth() + i];
                min = Math.min(da[i][j], min);                                    
                max = Math.max(da[i][j], max);

                x[i] = i; // Hahaha                                
            }
        }
    }
    
    public void update(double aLevel) {    
        reset();
        iCon.contour(da, 0, iSrc.getWidth()-1, 0, iSrc.getHeight()-1, x, y, 1, new double[]{iLevel = aLevel});
    }
}
