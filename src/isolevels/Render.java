/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isolevels;

@FunctionalInterface
public interface Render { 
    public void addSegment(double startX, double startY, double endX, double endY); 
}

