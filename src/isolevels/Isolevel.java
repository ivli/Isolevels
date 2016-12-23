/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isolevels;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 *
 * @author likhachev
 */

public class Isolevel {    
    private final BufferedImage iSrc; 
    private final Shape iShape;    
            
    public static Isolevel create(BufferedImage aSrc, Shape aRect) { 
         return new Isolevel(aSrc, aRect);          
    }
    
    public Shape update(double aLevel, boolean aMethod) {        
        return (aMethod ? contour(aLevel) : isolevels(aLevel))
               .createTransformedShape(
                AffineTransform.getTranslateInstance(iShape.getBounds().getX(), iShape.getBounds().getY())
                );                                                       
    }
             
    protected Isolevel(BufferedImage aSrc, Shape aROI) {
        iSrc = aSrc;
        iShape = null != aROI ? aROI : new Rectangle(0, 0, aSrc.getWidth(), aSrc.getHeight());          
    }  
    
    ///Contour
    protected Path2D contour(double aLevel) {      
        Path2D path = new Path2D.Double();
        final Rectangle bounds = iShape.getBounds();        
        BufferedImage in = new BufferedImage(bounds.width, bounds.height, iSrc.getType());
        in.setData(iSrc.getRaster().createWritableChild(bounds.x, bounds.y, bounds.width, bounds.height, 0, 0, null));       

        BufferedImage out = new ConvolveOp(new Kernel(5, 5, Kernels.LOG_5x5)).filter(in, null);   

        new Contour((double sX, double sY, double eX, double eY) -> {
                                                                     path.moveTo(sX, sY); 
                                                                     path.lineTo(eX, eY);
                                                                    })
                .contour(out.getRaster().getPixels(0, 0, out.getWidth(), out.getHeight(), (int[]) null), out.getWidth(), out.getHeight());        
        return path;
    }  
    ///
    protected Path2D isolevels(double aLevel) {   
        
        final Rectangle bounds = iShape.getBounds();
        int [][]da = new int[bounds.width][bounds.height];
        int   []x = new int[bounds.width];
        int   []y = new int[bounds.height];
        Path2D path = new Path2D.Double();
        int [] temp = iSrc.getRaster().getPixels(bounds.x, bounds.y, bounds.width, bounds.height, (int[]) null);

        for (int j = 0; j < bounds.height; ++j) {                           
            y[j] = j;
            for (int i = 0; i < bounds.width; ++i) {                                
                da[i][j] = temp[j*bounds.width + i];

                x[i] = i; // Hahaha                                       
            }
        }  
        
        new Conrec((double sX, double sY, double eX, double eY) -> {               
                                                                    path.moveTo(sX, sY); 
                                                                    path.lineTo(eX, eY);
                                                                   })
                .contour(da, 0, bounds.width-1, 0, bounds.height-1, x, y, 1, new double[]{aLevel});

        Point2D cog = new Moments(iSrc, bounds).getCoG(); 
        return path;
    }    
}