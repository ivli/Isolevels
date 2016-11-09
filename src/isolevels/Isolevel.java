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

/**
 *
 * @author likhachev
 */

public abstract class Isolevel extends Path2D.Double {
    private final BufferedImage iSrc; 
    private final Rectangle    iRect;    
    private java.lang.Double  iLevel;   
    
    public Isolevel(BufferedImage aSrc, Rectangle aRect) {
        iSrc = aSrc;
        iRect = null != aRect ? aRect : new Rectangle(0,0, aSrc.getWidth(), aSrc.getHeight());          
    }  
    
    public abstract void update(double aLevel);
    
    public static Isolevel create(boolean aT, BufferedImage aSrc, Rectangle aRect) {
        if(aT)
            return new IsolevelA(aSrc, aRect);
        else
            return new IsolevelB(aSrc, aRect);
    }
    
    protected Rectangle getRect() {return iRect;}
    protected BufferedImage getImage(){return iSrc;}
    
    private static class IsolevelA extends Isolevel {    

        public IsolevelA(BufferedImage aSrc, Rectangle aRect) {
            super(aSrc, aRect);        
        }  

        public void update(double aLevel) {    
            reset();
            double da[][];
            double x[];
            double y[];
            da = new double[getRect().width][getRect().height];
            x = new double[getRect().width];
            y = new double[getRect().height];

            int [] temp = getImage().getRaster().getPixels(getRect().x, getRect().y, getRect().width, getRect().height, (int[]) null);

            for (int j = 0; j < getRect().height; ++j) {                           
                y[j] = j;
                for (int i = 0; i < getRect().width; ++i) {                                
                    da[i][j] = temp[j*getRect().width + i];

                    x[i] = i; // Hahaha                                       
                }
            }  

            new Conrec((double sX, double sY, double eX, double eY, double aNotUsed) -> {moveTo(sX, sY); lineTo(eX, eY);})
                    .contour(da, 0, getRect().width-1, 0, getRect().height-1, x, y, 1, new double[]{aLevel});
        }
    }

    private static class IsolevelB extends Isolevel {    
        
        public IsolevelB(BufferedImage aSrc, Rectangle aRect) {
            super(aSrc, aRect);       
        }  

        private static final float[] LoG_5x5 = {-1f, -1f, -1f, -1f, -1f, 
                                                -1f, -1f, -1f, -1f, -1f, 
                                                -1f, -1f, 24f, -1f, -1f, 
                                                -1f, -1f, -1f, -1f, -1f, 
                                                -1f, -1f, -1f, -1f, -1f};

        private static final Kernel KERNEL = new Kernel(5, 5, LoG_5x5);

        public void update(double aLevel) {    
            reset();
            BufferedImage in = new BufferedImage(getRect().width, getRect().height, getImage().getType());
            in.setData(getImage().getRaster().createWritableChild(getRect().x, getRect().y, getRect().width, getRect().height, 0, 0, null));       
            BufferedImage out = new ConvolveOp(KERNEL).filter(in, null);   

            new Contour((double sX, double sY, double eX, double eY, double aNotUsed) -> {moveTo(sX, sY); lineTo(eX, eY);})
                    .contour(out.getRaster().getPixels(0, 0, out.getWidth(), out.getHeight(), (int[]) null), out.getWidth(), out.getHeight());
        }
    }
}