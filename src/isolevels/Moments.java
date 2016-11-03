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
    int width;
    int height;
 
    double mean;    
    double xCenterOfMass;
    double yCenterOfMass;
    double skewness;
    double kurtosis;
    
    //int ry, rh;
    //int rx, rw;
    int pixelCount;
     
    int minThreshold; 
    int maxThreshold;
    
    //Raster iR;
    int pixels[];
    
    Moments(BufferedImage aR) {        
        width = aR.getWidth();
        height = aR.getHeight();
        pixels  = new int[width*height];
        PixelGrabber pg = new PixelGrabber(aR, 0, 0, width, height, pixels, 0, 0);
        pg.startGrabbing();
    }
    
    void calc(int rx, int ry, int rw, int rh) {
        if (rx < 0 || ry < 0 || (rx + rw) > width || (ry + rh) > height || rx > rw || ry > rh)
            throw new IllegalArgumentException("Wrong ROI");
        
        calculateMoments( rx,  ry, rw, rh);
    }
    
     void calc() {        
        calculateMoments(0,  0,  width, height);
    }
    
    
    private void calculateMoments(int rx, int ry, int rw, int rh) {           
            int i, iv;
            double v, v2, sum1=0.0, sum2=0.0, sum3=0.0, sum4=0.0, xsum=0.0, ysum=0.0;
         
            for (int y=ry; y<(ry+rh); y++) {
                i = y*width + rx;                    
                for (int x=rx; x<(rx+rw); x++) {				
                    iv = pixels[i] & 0xffff;
                    v = iv;
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
            
	    double mean2 = mean*mean;
	    double variance = sum2/pixelCount - mean2;
	    double sDeviation = Math.sqrt(variance);
	    skewness = ((sum3 - 3.0*mean*sum2)/pixelCount + 2.0*mean*mean2)/(variance*sDeviation);
	    kurtosis = (((sum4 - 4.0*mean*sum3 + 6.0*mean2*sum2)/pixelCount - 3.0*mean2*mean2)/(variance*variance)-3.0);
		
            xCenterOfMass = xsum/sum1+0.5;
            yCenterOfMass = ysum/sum1+0.5;		
	}
    
}
