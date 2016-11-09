/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isolevels;


import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

/**
 *
 * @author likhachev
 */
public class Moments {    
    double M;    
    double XM;
    double YM;
    double skewness;
    double kurtosis;
    double min;
    double max;
    
    private int width;
    private int height;
    private int pixels[];
   
    
    public Moments(BufferedImage aI, Rectangle aR) {        
        width = aI.getWidth();
        height = aI.getHeight();
        pixels = aI.getRaster().getPixels(0, 0, width, height, (int[])null);      
        calculate(aR);
    }
    
    public Moments(int aWidth, int aHeight, int[] aPixels) {
        width = aWidth;
        height = aHeight;
        pixels = aPixels;
        calculateMoments(0, 0, width, height);
    }
    
    public Point2D getCoG() {        
        return new Point2D.Double(XM, YM);
    }
    
    public double getMin() {return min;}
    public double getMax() {return max;}
    public double getMed() {return (max - min) / 2.;}
    
    void calculate(Rectangle aR) {
        if (null == aR)
            calculateMoments(0, 0, width, height);
        else {
            if (aR.x < 0 || aR.y < 0 || (aR.x + aR.width) > width || (aR.y + aR.height) > height)
                throw new IllegalArgumentException("Wrong ROI");

            calculateMoments(aR.x, aR.y, aR.width, aR.height);
        }
    }
       
    private void calculateMoments(int rx, int ry, int rw, int rh) {                          
        double v, v2, sum1=0.0, sum2=0.0, sum3=0.0, sum4=0.0, xsum=0.0, ysum=0.0;
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        for (int y=ry; y<(ry+rh); y++) {
                int i = y*width + rx;                    
                for (int x=rx; x<(rx+rw); x++) {				
                    v = pixels[i] & 0xffff;                    
                    v2 = v*v;
                    sum1 += v;
                    sum2 += v2;
                    sum3 += v*v2;
                    sum4 += v2*v2;
                    xsum += x*v;
                    ysum += y*v;
                    min = Math.min(v, min);
                    max = Math.max(v, max);
                    i++;
                }
        }
        
        double pixelCount = rw*rh;
        M = sum1/pixelCount;
        double mean2 = M*M;
        double variance = sum2/pixelCount - mean2;
        double sDeviation = Math.sqrt(variance);
        skewness = ((sum3 - 3.0*M*sum2)/pixelCount + 2.0*M*mean2)/(variance*sDeviation);
        kurtosis = (((sum4 - 4.0*M*sum3 + 6.0*mean2*sum2)/pixelCount - 3.0*mean2*mean2)/(variance*variance)-3.0);

        XM = xsum/sum1+0.5;
        YM = ysum/sum1+0.5;	
        System.out.printf("--> (%d,%d,%d,%d) = %f, %f", rx, ry, rw, rh, XM, YM);
    }
}
