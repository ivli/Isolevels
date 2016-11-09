/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isolevels;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author likhachev
 */
public abstract class ContourBase extends Path2D.Double {
    public abstract void update(double aLevel);
    
   
}
