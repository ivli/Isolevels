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
 
    private static final int E = 0; 
    private static final int N = 1; 
    private static final int W = 2; 
    private static final int S = 3;           
       
    private static final int [][]MATRIX = {
            /*E*/ {S, E, N, W},
            /*N*/ {E, N, W, S},
            /*W*/ {N, W, S, E},
            /*S*/ {W, S, E, N},
            }; 
    
    public void contour(int []d, int width, int height) {
        Point2D M = new Moments(width, height, d).getCoG();
        
        //1. let's start from the center of gravity 
        int i = (int)Math.floor(M.getX());
        int j = (int)Math.floor(M.getY());
       
        int dir = W;
        //2. go west to find the starting point where we have to return sometime                  
        while (0 != d[j*width + i]) --i;                 
                              
        int x = i, y = j;       
        int x1 = x, y1 = y;
        boolean []va = {false, false, false, false};
        //3. walk clockwise along the border 
        do { 
            va[E] = 0 == d[(y)  * width + (x+1)]; //E
            va[W] = 0 == d[(y)  * width + (x-1)]; //W
            va[N] = 0 == d[(y-1)* width + (x)];   //N            
            va[S] = 0 == d[(y+1)* width + (x)];   //S
        
            for (int k=0; k<MATRIX[dir].length; ++k) {            
                if (va[(MATRIX[dir][k])]) {                                       
                    switch(dir = MATRIX[dir][k]) {
                        case E: ++x; break;
                        case W: --x; break;
                        case N: --y; break;                        
                        case S: ++y; break;
                    }
                    
                    System.out.printf("-->next step is %d - %d, %d \n", dir, x, y); 
                    break;
                }                
            }

            render.addSegment(x1, y1, x, y, 0);
            x1 = x; y1 = y;
            
        } while (!(x == i && y == j));
        
    }    
}

