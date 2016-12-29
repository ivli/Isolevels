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
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleImmutableEntry;
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
    
    public Shape update(double aLevel, boolean aMethod, double [] aD) {        
        return (aMethod ? contour(aLevel) : isolevels(aLevel, aD))
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
                
        ArrayList<Entry<Integer,Integer>> points = new ArrayList<>();
        
        new Contour((int sX, int sY) -> {            
            points.add(new SimpleImmutableEntry<>(sX, sY));  
        })                                             
        .contour(out.getRaster().getPixels(0, 0, out.getWidth(), out.getHeight(), (int[]) null), out.getWidth(), out.getHeight());   
        
        if (points.size() > 0) {
            path.moveTo(points.get(0).getKey(), points.get(0).getValue());

            for (int i = 1, size = points.size(); i < size; ++i) 
                path.lineTo(points.get(i).getKey(), points.get(i).getValue());

            path.closePath();
        }
       
        return path;
    }  
   
    protected Path2D isolevels(double aLevel, double[] aPoints) {     
        Path2D path = new Path2D.Double();
        final Rectangle bounds = iShape.getBounds();               
        final int []tmp = iSrc.getRaster().getPixels(bounds.x, bounds.y, bounds.width, bounds.height, (int[]) null);
                        
        new Conrec((double sX, double sY, double eX, double eY) -> {path.moveTo(sX, sY); path.lineTo(eX, eY);})                                                       
            .contour(tmp, 0, bounds.width, 0, bounds.height, (int)aLevel);

        double[] cog = Moments.create(bounds.width, bounds.height, tmp).calculate(0, 0, bounds.width, bounds.height).getCoG(); 
                        
        PathIterator it = path.getPathIterator(null);
        
        double min_dist = Math.sqrt(bounds.width * bounds.height);
        double [] min_coords = {0., 0.};
        
        while(!it.isDone()) {   
            double[] coords = {.0,.0,.0,.0,.0,.0};          
            switch(it.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:{                   
                    double dist = Math.sqrt((cog[0] - coords[0])*(cog[0] - coords[0]) + (cog[1] - coords[1])*(cog[1] - coords[1])); //euclidian distance                  
                    if (dist < min_dist) {
                        min_dist = dist;
                        min_coords[0] = coords[0]; 
                        min_coords[1] = coords[1]; 
                    }                
                } break;                
                default:
                    break;
            }            
            it.next();            
        }
        
        System.out.printf("CoG is: (%f:%f)\n", cog[0], cog[1]);
        System.out.printf("closest point is: |%f:%f| =%f\n", min_coords[0], min_coords[1], min_dist);        
        
        it = path.getPathIterator(null);
        //int prevs = -1;
        //double[] prevp = {.0, .0};
        
        while(!it.isDone()) {   
            double[] coords = {.0,.0,.0,.0,.0,.0};          
            switch(it.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO: {                           
                    System.out.printf("%f\t%f\t", coords[0], coords[1]);
                } break;       
                case PathIterator.SEG_LINETO: {                                          
                    System.out.printf("%f\t%f\n", coords[0], coords[1]);                                     
                } break;                
                default:
                    break;
            }            
            it.next();
        }
   
        if (null != aPoints) {
            aPoints[0] = min_coords[0] + bounds.x;
            aPoints[1] = min_coords[1] + bounds.y;                  
        }
        
        return path;
    }    
}