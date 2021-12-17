package com.ivli.isolevels;


/**
 *
 * @author likhachev
 * based on an article http://cyberleninka.ru/article/n/algoritm-postroeniya-kontura-ploskoy-figury
 * 
 */
public class Contour {  
    
    @FunctionalInterface
    public interface Render {
        void push(int x, int y);
    }
    
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
        double[] M = Moments.create(width, height, d).calculate(0, 0, width, height).getCoG();
        
        //1. let's start from the center of gravity 
        int i = (int)Math.floor(M[0]);
        int j = (int)Math.floor(M[1]);
       
        int dir = W;
        //2. go west to find the starting point where we'll return sometime                  
        while (0 != d[j*width + i] && i >=0) --i;  
                                                         
        int x = i, y = j;       
                
        //3. walk counter clockwise along the border 
        do { 
            final boolean []va = {
              /*E*/  0 == d[(y)  * width + (x+1)], 
              /*N*/  0 == d[(y-1)* width + (x)],    
              /*W*/  0 == d[(y)  * width + (x-1)],                       
              /*S*/  0 == d[(y+1)* width + (x)]
            };   
        
            for (int k=0, len=MATRIX[dir].length; k<len; ++k) {            
                if (va[(MATRIX[dir][k])]) {                                       
                    switch(dir = MATRIX[dir][k]) {
                        case E: ++x; break;
                        case N: --y; break;
                        case W: --x; break;                                                
                        case S: ++y; break;
                    }        
                    break;
                }                
            }

            render.push(x, y);
        
        } while (!(x == i && y == j));
        
    }    
}

