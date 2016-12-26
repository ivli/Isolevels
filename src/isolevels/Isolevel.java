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
import java.awt.geom.PathIterator;
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
   
    protected Path2D contour(double aLevel) {      
        Path2D path = new Path2D.Double();
        final Rectangle bounds = iShape.getBounds();        
        BufferedImage in = new BufferedImage(bounds.width, bounds.height, iSrc.getType());
        in.setData(iSrc.getRaster().createWritableChild(bounds.x, bounds.y, bounds.width, bounds.height, 0, 0, null));       

        BufferedImage out = new ConvolveOp(new Kernel(5, 5, Kernels.LOG_5x5)).filter(in, null);   

        new Contour((double sX, double sY, double eX, double eY) -> {path.moveTo(sX, sY); path.lineTo(eX, eY);})                                               
            .contour(out.getRaster().getPixels(0, 0, out.getWidth(), out.getHeight(), (int[]) null), out.getWidth(), out.getHeight());   
        
        return path;
    }  
   
    protected Path2D isolevels(double aLevel) {     
        Path2D path = new Path2D.Double();
        final Rectangle bounds = iShape.getBounds();               
        final int [] tmp = iSrc.getRaster().getPixels(bounds.x, bounds.y, bounds.width, bounds.height, (int[]) null);
        
        new Conrec((double sX, double sY, double eX, double eY) -> {path.moveTo(sX, sY); path.lineTo(eX, eY);})                                                       
            .contour(tmp, 0, bounds.width, 0, bounds.height, (int)aLevel);

        double[] cog = Moments.create(bounds.width, bounds.height, tmp).calculate(0, 0, bounds.width, bounds.height).getCoG(); 
        
        //while ()
        
        
        PathIterator it = path.getPathIterator(null);
        
        double[] coords={.0,.0,.0,.0,.0,.0};
        
        while(!it.isDone()) {            
            switch(it.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:{
                    Point2D p1 = new Point2D.Double(coords[0], coords[1]);
                    Point2D p2 = new Point2D.Double(coords[2], coords[3]);
                            } break;
                case PathIterator.SEG_LINETO:{
                    Point2D p1 = new Point2D.Double(coords[0], coords[1]);
                    Point2D p2 = new Point2D.Double(coords[2], coords[3]);
                }; break;
                case PathIterator.SEG_QUADTO:
                case PathIterator.SEG_CUBICTO:
                case PathIterator.SEG_CLOSE:   
                default:
                    break;
            }            
            it.next();
        }
        
        
        return path;
    }    
}