/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isolevels;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;

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
   
    int minThreshold; 
    int maxThreshold;
    
    int width;
    int height;
    int pixels[];
    
    Moments(BufferedImage aR) {        
        width = aR.getWidth();
        height = aR.getHeight();
        pixels  = new int[width*height];
        PixelGrabber pg = new PixelGrabber(aR, 0, 0, width, height, pixels, 0, width);
        try {
            pg.grabPixels();
        } catch (InterruptedException ex) {
            System.exit(-1);
        }
    }
    
    void calc(int rx, int ry, int rw, int rh) {
        if (rx < 0 || ry < 0 || (rx + rw) > width || (ry + rh) > height)
            throw new IllegalArgumentException("Wrong ROI");
        
        calculateMoments(rx, ry, rw, rh);
    }
    
    void calc() {        
        calculateMoments(0,  0,  width, height);
    }
        
    private void calculateMoments(int rx, int ry, int rw, int rh) {                          
        double v, v2, sum1=0.0, sum2=0.0, sum3=0.0, sum4=0.0, xsum=0.0, ysum=0.0;
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
