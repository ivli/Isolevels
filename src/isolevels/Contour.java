/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isolevels;

import java.awt.geom.Point2D;

/**
 *
 * @author likhachev
 * based on an article http://cyberleninka.ru/article/n/algoritm-postroeniya-kontura-ploskoy-figury
 * 
 */
public class Contour {    
    final Render render;
    
    Contour(Render aR) {
        render = aR;    
    }
              
    enum DIR {E, N, W, S};
    
    public void contour(int []d, int width, int height) {
        Point2D M = new Moments(width, height, d).getCoG(null);
        //start from the center of gravity 
        final int startX = (int)Math.floor(M.getX());
        final int startY = (int)Math.floor(M.getY());
       
        //1. coordinates of the starting point where we have to return sometime    
        int i = startX;
        int j = startY;
        
        DIR dir = DIR.W;
                          
        while (0 != d[j*width + i]) --i;                 
                              
        int x = i, y = j;       
        int x1 = x, y1 = y; 

        do {             
            boolean [] va = {0 == d[(y)  *width + (x+1)], //E
                             0 == d[(y-1)*width + (x)],   //N
                             0 == d[(y)  *width + (x-1)], //W
                             0 == d[(y+1)*width + (x)]};  //S
            
            final DIR [][]matrix = {
            /*E*/ {DIR.S, DIR.E, DIR.N, DIR.W},
            /*N*/ {DIR.E, DIR.N, DIR.W, DIR.S},
            /*W*/ {DIR.N, DIR.W, DIR.S, DIR.E},
            /*S*/ {DIR.W, DIR.S, DIR.E, DIR.N},
            }; 

            for (int k=0; k<matrix[dir.ordinal()].length; ++k) {            
                if (va[(matrix[dir.ordinal()][k]).ordinal()]) {                                       
                    switch(dir = matrix[dir.ordinal()][k]) {
                        case E: ++x; break;
                        case W: --x; break;
                        case N: --y; break;                        
                        case S: ++y; break;
                    }
                    
                    System.out.printf("-->next step is %s - %d, %d \n", dir.toString(), x, y); 
                    break;
                }                
            }

            render.drawContour(x1, y1, x, y, 0);
            x1 = x; y1 = y;
            
        } while (!(x == i && y == j));
        
    }    
}

