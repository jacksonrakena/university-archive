// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 1
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * Shred stores information about a shred.
 * All shreds are 40x40 png images.
 */

public class Shred{

    // Fields to store
    //   the name of the image
    //   the id of the image
    public static final double SIZE = 40;
    private Color[][] pixels;
    private String filename;
    private int id;            // ID of the shred

    // Constructor
    /** Construct a new Shred object.
     *  Parameters are the name of the directory and the id of the image
     */
    public Shred(Path dir, int id){
        filename = dir.resolve(id+".png").toString();
        pixels=DeShredder.loadImage(filename);
        this.id = id;
    }

    public Color[][] getPixels() {
        return this.pixels;
    }

    public String getFilename() {
        return filename;
    }

    /**
     * Draw the shred (no border) at the specified coordinates
     */
    public void draw(double left, double top){
        UI.drawImage(filename, left, top, SIZE, SIZE);
    }

    /**
     * Draw the shred with a border at the specified coordinates
     */
    public void drawWithBorder(double left, double top){
        UI.drawImage(filename, left, top, SIZE, SIZE);
        UI.drawRect(left, top, SIZE, SIZE);
    }

    public void drawHighlighted(double left, double top) {
        UI.drawImage(filename, left, top, SIZE, SIZE);
        UI.setColor(Color.yellow.darker());
        UI.setLineWidth(3);
        UI.drawRect(left, top, SIZE, SIZE);
        UI.resetLineWidth();
        UI.setColor(Color.black);
    }

    public String toString(){
        return "ID:"+id;
    }

}
