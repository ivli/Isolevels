/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isolevels;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.Raster;

/**
 *
 * @author likhachev
 */
/*
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
        ret.init();
        
        if (null != aLevel)
            ret.update(aLevel);
                
        return ret;
    }
    
    private double da[][];
    private double x[];
    private double y[];
    
    double min = java.lang.Double.MAX_VALUE;
    double max = java.lang.Double.MIN_VALUE;
    
    private void init() {
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
*/

public class Isolevel extends Path2D.Double {    
    private final BufferedImage iSrc; 
    private final Rectangle    iRect;
    
    private java.lang.Double iLevel;
    
    private Contour iCon;
      
    private Isolevel(BufferedImage aSrc, Rectangle aRect) {
        iSrc = aSrc;
        iRect = aRect;
        iCon = new Contour((double sX, double sY, double eX, double eY, double aNotUsed) -> {moveTo(sX, sY); lineTo(eX, eY);});       
    }  
    
    public static Isolevel create(BufferedImage aSrc, Rectangle aRect, java.lang.Double aLevel) {
        Isolevel ret = new Isolevel(aSrc, aRect);
        
        if (null != aLevel)
            ret.update(aLevel);
                
        return ret;
    }
           
    double min = java.lang.Double.MAX_VALUE;
    double max = java.lang.Double.MIN_VALUE;
     
    private static final float[] LoG_5x5 = {-1f, -1f, -1f, -1f, -1f, 
                                            -1f, -1f, -1f, -1f, -1f, 
                                            -1f, -1f, 24f, -1f, -1f, 
                                            -1f, -1f, -1f, -1f, -1f, 
                                            -1f, -1f, -1f, -1f, -1f};
    
    private static final Kernel KERNEL = new Kernel(5, 5, LoG_5x5);
    
    public void update(double aLevel) {    
        reset();
        BufferedImage in = new BufferedImage(iRect.width, iRect.height, iSrc.getType());
        in.setData(iSrc.getRaster().createWritableChild(iRect.x, iRect.y, iRect.width, iRect.height, 0, 0, null));       
        BufferedImage out = new ConvolveOp(KERNEL).filter(in, null);         
        iCon.contour(out.getRaster().getPixels(0, 0, out.getWidth(), out.getHeight(), (int[]) null), out.getWidth(), out.getHeight());
    }
}
