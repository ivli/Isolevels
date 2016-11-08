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
 */
public class Zcross {    
    private final Render render;
    
    Zcross(Render aR) {
        render = aR;    
    }
              
    enum DIR {N, W, S, E, NW, SW, SE, NE};
    
    public void zcross(int []d, int width, int height) {
        Point2D M = new Moments(width, height, d).getCoG(null);
        //start from the center of gravity 
        final int startX = (int)Math.floor(M.getX());
        final int startY = (int)Math.floor(M.getY());
       
        //1. go west and find the first zero point     
        int i = startX;
        int j = startY;
        
        DIR dir = DIR.W;
                
        for (; i > 0; --i) // outer loop        
            if (0 == d[startY*width + i]) {                 
            //2. zero is found, let's start counterclockwise   
            /******************        
             *   v1 v8 v7
             *   v2 v0 v6
             *   v3 v4 v5
            *******************/                   
            int x = ++i; 
            int y = j;
            
            int x1 = x, y1 = y; //save old center
             
            do {                                                               
                boolean v1 = 0 == d[(y-1)*width + (x-1)];
                boolean v2 = 0 == d[(y)  *width + (x-1)];
                boolean v3 = 0 == d[(y+1)*width + (x-1)];     
                
                boolean v4 = 0 == d[(y+1)*width + (x)];     
                boolean v0 = 0 == d[(y)  *width + (x)];     
                boolean v8 = 0 == d[(y-1)*width + (x)];
                
                boolean v5 = 0 == d[(y+1)*width + (x+1)];
                boolean v6 = 0 == d[(y)  *width + (x+1)];
                boolean v7 = 0 == d[(y-1)*width + (x+1)];     
                      
                int sum = (v0?1:0) + (v1?1:0)  + (v2?1:0)  + (v3?1:0)  + (v4?1:0)  + (v5?1:0)  + (v6?1:0)  + (v7?1:0)  + (v8?1:0);
                System.out.printf("-->iteration for: %d, %d; sum = %d\n", x, y, sum); 
                // v0 is the anchor it must olways be false (so this is a boundary lime) 
                // run counterclockwise    
                if (sum == 3) {
                    if (v1&&v2&&v3) { // v4 
                        ++y;                                                            
                    } else if(v5&&v6&&v7) { // v8    
                        --y;                       
                    } else if(v3&&v4&&v5) { // go v6 
                        ++x;                        
                    }  else if(v1&&v8&&v7) { //go v2    
                        --x;                        
                    } 
                } else if (sum > 3) {
                    if (v1&&v2&&v3&&(v4||v5||v7||v8)) { // v6   
                         ++x;                              
                    } else if (v5&&v6&&v7&&(v1||v8||v3||v4)) { // v2   
                         --x;                             
                    } else if (v3&&v4&&v5&&(v1||v2||v6||v7)) { // v8   
                         --y;                              
                    } else if (v1&&v8&&v7&&(v2||v3||v6||v5)) { // v4  
                         ++y;                              
                    } else {
                        break;
                    }
                }
                render.drawContour(x1, y1, x, y, 0);
                
            } while (!(x == startX && y == startY));
        }
    }    
}






