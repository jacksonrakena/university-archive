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
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.stream.Collectors;

/**
 * DeShredder allows a user to sort fragments of a shredded document ("shreds") into strips, and
 * then sort the strips into the original document.
 * The program shows
 *   - a list of all the shreds along the top of the window, 
 *   - the working strip (which the user is constructing) just below it.
 *   - the list of completed strips below the working strip.
 * The "rotate" button moves the first shred on the list to the end of the list to let the
 *  user see the shreds that have disappeared over the edge of the window.
 * The "shuffle" button reorders the shreds in the list randomly
 * The user can use the mouse to drag shreds between the list at the top and the working strip,
 *  and move shreds around in the working strip to get them in order.
 * When the user has the working strip complete, they can move
 *  the working strip down into the list of completed strips, and reorder the completed strips
 *
 */

enum PadMode {
    Left,
    Right,
    Split,
    None
}
public class DeShredder {

    // Fields to store the lists of Shreds and strips.  These should never be null.
    private List<Shred> allShreds = new ArrayList<Shred>();    //  List of all shreds
    private List<Shred> workingStrip = new ArrayList<Shred>(); // Current strip of shreds
    private List<List<Shred>> completedStrips = new ArrayList<List<Shred>>();

    /**
     * A shred of empty (white) pixels, used for padding.
     */
    private static final Color[][] blankShred = new Color[(int) Shred.SIZE][(int) Shred.SIZE];

    public DeShredder() {
        // Initialise the empty shred.
        Color[] white = new Color[(int) Shred.SIZE];
        Arrays.fill(white, Color.white);
        Arrays.fill(blankShred, white);
    }

    // Constants for the display and the mouse
    public static final double LEFT = 20;       // left side of the display
    public static final double TOP_ALL = 20;    // top of list of all shreds 
    public static final double GAP = 5;         // gap between strips
    public static final double SIZE = Shred.SIZE; // size of the shreds

    public static final double TOP_WORKING = TOP_ALL+SIZE+GAP;
    public static final double TOP_STRIPS = TOP_WORKING+(SIZE+GAP);

    //Fields for recording where the mouse was pressed  (which list/strip and position in list)
    // note, the position may be past the end of the list!
    private List<Shred> fromStrip;   // The strip (List of Shreds) that the user pressed on
    private int fromPosition = -1;   // index of shred in the strip

    /**
     * Initialises the UI window, and sets up the buttons. 
     */
    public void setupGUI() {
        UI.addButton("Load library",   this::loadLibrary);
        UI.addButton("Rotate",         this::rotateList);
        UI.addButton("Shuffle",        this::shuffleList);
        UI.addButton("Complete Strip", this::completeStrip);
        UI.addButton("Save (pad right)", () -> { this.saveAllCompletedStrips(PadMode.Right);});
        UI.addButton("Save (pad left)", () -> {this.saveAllCompletedStrips(PadMode.Left);});
        UI.addButton("Save (split pad)", () -> {this.saveAllCompletedStrips(PadMode.Split);});
        UI.addButton("Save (no padding)", () -> {this.saveAllCompletedStrips(PadMode.None);});
        UI.addButton("Quit",           UI::quit);

        UI.setMouseListener(this::doMouse);
        UI.setWindowSize(1000,800);
        UI.setDivider(0);
    }

    /**
     * Asks user for a library of shreds, loads it, and redisplays.
     * Uses UIFileChooser to let user select library
     * and finds out how many images are in the library
     * Calls load(...) to construct the List of all the Shreds
     */
    public void loadLibrary(){
        Path filePath = Path.of(UIFileChooser.open("Choose first shred in directory"));
        Path directory = filePath.getParent(); //subPath(0, filePath.getNameCount()-1);
        int count=1;
        while(Files.exists(directory.resolve(count+".png"))){ count++; }
        //loop stops when count.png doesn't exist
        count = count-1;
        load(directory, count);   // YOU HAVE TO COMPLETE THE load METHOD
        display();
    }

    /**
     * Empties out all the current lists (the list of all shreds,
     *  the working strip, and the completed strips).
     * Loads the library of shreds into the allShreds list.
     * Parameters are the directory containing the shred images and the number of shreds.
     * Each new Shred needs the directory and the number/id of the shred.
     */
    public void load(Path dir, int count) {
        allShreds.clear();
        workingStrip.clear();
        completedStrips.clear();

        for (int i = 1; i < count; i++) {
            allShreds.add(new Shred(dir, i));
        }
    }

    /**
     * Rotate the list of all shreds by one step to the left
     * and redisplay;
     * Should not have an error if the list is empty
     * (Called by the "Rotate" button)
     */
    public void rotateList(){
        List<Shred> temp = new ArrayList<>(allShreds.size());
        for (int i = 0; i < allShreds.size(); i++) {
            Shred shred = allShreds.get(i == allShreds.size()-1 ? 0 : i+1);
            temp.add(shred);
        }
        allShreds = temp;
        display();
    }

    /**
     * Shuffle the list of all shreds into a random order
     * and redisplay;
     */
    public void shuffleList(){
        Collections.shuffle(allShreds);
        display();
    }

    /**
     * Move the current working strip to the end of the list of completed strips.
     * (Called by the "Complete Strip" button)
     */
    public void completeStrip(){
        if (workingStrip.isEmpty()) return;
        completedStrips.add(workingStrip);
        workingStrip = new ArrayList<>();
        display();
    }

    /**
     * Simple Mouse actions to move shreds and strips
     *  User can
     *  - move a Shred from allShreds to a position in the working strip
     *  - move a Shred from the working strip back into allShreds
     *  - move a Shred around within the working strip.
     *  - move a completed Strip around within the list of completed strips
     *  - move a completed Strip back to become the working strip
     *    (but only if the working strip is currently empty)
     * Moving a shred to a position past the end of a List should put it at the end.
     * You should create additional methods to do the different actions - do not attempt
     *  to put all the code inside the doMouse method - you will lose style points for this.
     * Attempting an invalid action should have no effect.
     * Note: doMouse uses getStrip and getColumn, which are written for you (at the end).
     * You should not change them.
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("pressed")){
            fromStrip = getStrip(y);      // the List of shreds to move from (possibly null)
            fromPosition = getColumn(x);  // the index of the shred to move (may be off the end)
        }
        if (action.equals("released")){
            List<Shred> toStrip = getStrip(y); // the List of shreds to move to (possibly null)
            int toPosition = getColumn(x);     // the index to move the shred to (may be off the end)

            // If dragging a shred from all shreds
            if (fromStrip == allShreds) {
                performIndexSafeMove(fromPosition, toPosition, fromStrip, toStrip);
            }
            // If dragging a completed strip to somewhere else
            else if (completedStrips.contains(fromStrip)) {
                // Move a completed strip back into the working strip
                if (toStrip == workingStrip && workingStrip.isEmpty()) {
                    dematerializeList(completedStrips, fromStrip, workingStrip);
                }
                // Delete a completed strip
                else if (toStrip == allShreds) {
                    dematerializeList(completedStrips, fromStrip, allShreds);
                }
                // Move a completed strip to a new position within the completed set
                else if (completedStrips.contains(toStrip)) {
                    performIndexSafeMove(completedStrips.indexOf(fromStrip), completedStrips.indexOf(toStrip), completedStrips, completedStrips);
                }
            }
            // If dragging the working strip back into all shreds (resetting the working strip)
            else if (toStrip == allShreds && fromStrip == workingStrip) {
                performIndexSafeMove(fromPosition, toPosition, workingStrip, allShreds);
            }
            // Move a shred around inside the working strip
            else if(toStrip == workingStrip && fromStrip == workingStrip) {
                performIndexSafeMove(fromPosition, toPosition, workingStrip, workingStrip);
            }
            display();
        }
    }

    /**
     * Dematerialises a list (the origin), which involves:
     * 1) removing it from the provided list of lists (the context)
     * 2) copying all of its contents into the destination list (the destination)
     * 3) clearing it
     */
    public <T> void dematerializeList(List<List<T>> context, List<T> origin, List<T> destination) {
        context.remove(origin);
        destination.addAll(origin);
        origin.clear();
    }

    /**
     * Moves an object in a List<T> from one position to another position in a different list,
     * with checked indexes, ensuring an index-out-of-range error cannot possibly occur.
     */
    public <T> void performIndexSafeMove(int fromPosition, int newPosition, List<T> origin, List<T> destination) {
        if (origin == null || destination == null) return;
        if (fromPosition >= origin.size()) return;
        int initialDestinationSize = destination.size();
        T target = origin.get(fromPosition);
        origin.remove(target);

        // Check the indexes:
        // If the new position is above the bounds of the context, set it to the last element in the array
        // If the new position is less than 0, set it to zero.
        destination.add(newPosition < initialDestinationSize ? (Math.max(newPosition, 0)) : (destination.size() > 0 ? destination.size() - 1 : 0), target);
    }

    public void saveAllCompletedStrips(PadMode padMode) {
        if (completedStrips.isEmpty()) return;

        // Find the length of the longest strip.
        int longestLength = completedStrips.stream().mapToInt(List::size).max().orElseThrow();
        // Color[h][w]
        Color[][] image = new Color[(int) Math.ceil((completedStrips.size())*Shred.SIZE)][];

        // Loop over all the completed strips.
        for (int stripIndex = 0; stripIndex < completedStrips.size(); stripIndex++) {

            // Initialise the array for this strip,
            // by looping over the size of the shred,
            // and initialise that area
            for (int size0 = 0; size0 < Shred.SIZE; size0++) {
                image[(int)Math.ceil((stripIndex*SIZE)+size0)] = new Color[(int) Math.ceil(
                        (padMode == PadMode.None ? completedStrips.get(stripIndex).size() : longestLength)
                                * Shred.SIZE)];
            }

            // Convert the strip into a list of image arrays
            List<Color[][]> strip = completedStrips.get(stripIndex).stream().map(e -> loadImage(e.getFilename())).collect(Collectors.toList());

            // If padding, calculate and insert the necessary padding shreds
            if (strip.size() < longestLength && padMode != PadMode.None) {
                // Gap between this strip and the longest strip
                int differential = longestLength - strip.size();

                // If the user requested to split the padding on the left and right:
                if (padMode == PadMode.Split) {

                    // If the difference is even, add half of it to the left,
                    // otherwise add half of it, rounded down
                    for (int i = 0; i < (differential % 2 == 0 ? differential/2 : (differential-1)/2); i++) {
                        strip.add(0, blankShred);
                    }

                    // If the difference is even, add half of it to the right,
                    // otherwise add half of it, rounded up
                    for (int i = 0; i < (differential % 2 == 0 ? differential/2 : (differential+1)/2); i++) {
                        strip.add(blankShred);
                    }
                }
                // Otherwise just add it to either side depending on what they chose
                else if (padMode == PadMode.Left || padMode == PadMode.Right) {
                    for (int i = 0; i < differential; i++) {
                        strip.add(padMode == PadMode.Left ? 0 : strip.size(), blankShred);
                    }
                }
            }

            // Loop over this strip's shreds, and insert it into the final image
            for (int shredIndex = 0; shredIndex < strip.size(); shredIndex++) {
                Color[][] shredImage = strip.get(shredIndex);

                // For each pixel in the current shred:
                for (int row = 0; row < shredImage.length; row++) {
                    for (int col = 0; col < shredImage[row].length; col++) {

                        // Copy the pixel to the resultant image,
                        // offset by the current strip multiplied by the size of each shred (for the Y axis)
                        // and offset by the current shred within the strip multiplied by the size of each shred (for X)
                        if (image[(int) ((stripIndex*Shred.SIZE)+row)] == null) {
                            image[(int) ((stripIndex*Shred.SIZE)+row)] = new Color[(int) Math.ceil(
                                    (padMode == PadMode.None ? completedStrips.get(stripIndex).size() : longestLength)
                                            * Shred.SIZE)];
                        }
                        image[(int) ((stripIndex*Shred.SIZE)+row)][(int) ((shredIndex*Shred.SIZE)+col)]=shredImage[row][col];
                    }
                }

            }
        }
        saveImage(image, UIFileChooser.save("Where would you like to save your completed strips?") + ".png");
    }

    // Additional methods to perform the different actions, called by doMouse

    /*# YOUR CODE HERE */

    //=============================================================================
    // Completed for you. Do not change.
    // loadImage and saveImage may be useful for the challenge.

    /**
     * Displays the remaining Shreds, the working strip, and all completed strips
     */
    public void display(){
        UI.clearGraphics();

        // list of all the remaining shreds that haven't been added to a strip
        double x=LEFT;
        if (!workingStrip.isEmpty()) {
            Shred lastShred = workingStrip.get(workingStrip.size()-1);
            Color[][] lastShredImage = lastShred.getPixels();
            for (Shred shred : allShreds){
                int matchingPixels=0;
                double totalPixels = lastShredImage[lastShredImage.length-1].length;
                for (int y = 0; y < lastShredImage[lastShredImage.length-1].length; y++) {
                    Color shredPixel = shred.getPixels()[y][0];
                    if (lastShredImage[y][lastShredImage.length-1].getRGB() == shred.getPixels()[y][0].getRGB()) {
                        matchingPixels++;
                    }
                }
                double ratio = matchingPixels/totalPixels;
                if (ratio>0.89) {
                    shred.drawHighlighted(x, TOP_ALL);
                } else {
                    shred.drawWithBorder(x, TOP_ALL);
                }
                x+=SIZE;
            }
        } else {
            for (Shred shred : allShreds){

                shred.drawWithBorder(x, TOP_ALL);
                x+=SIZE;
            }
        }

        //working strip (the one the user is workingly working on)
        x=LEFT;
        for (Shred shred : workingStrip){
            shred.draw(x, TOP_WORKING);
            x+=SIZE;
        }
        UI.setColor(Color.red);
        UI.drawRect(LEFT-1, TOP_WORKING-1, SIZE*workingStrip.size()+2, SIZE+2);
        UI.setColor(Color.black);

        //completed strips
        double y = TOP_STRIPS;
        for (List<Shred> strip : completedStrips){
            x = LEFT;
            for (Shred shred : strip){
                shred.draw(x, y);
                x+=SIZE;
            }
            UI.drawRect(LEFT-1, y-1, SIZE*strip.size()+2, SIZE+2);
            y+=SIZE+GAP;
        }
    }

    /**
     * Returns which column the mouse position is on.
     * This will be the index in the list of the shred that the mouse is on, 
     * (or the index of the shred that the mouse would be on if the list were long enough)
     */
    public int getColumn(double x){
        return (int) ((x-LEFT)/(SIZE));
    }

    /**
     * Returns the strip that the mouse position is on.
     * This may be the list of all remaining shreds, the working strip, or
     *  one of the completed strips.
     * If it is not on any strip, then it returns null.
     */
    public List<Shred> getStrip(double y){
        int row = (int) ((y-TOP_ALL)/(SIZE+GAP));
        if (row<=0){
            return allShreds;
        }
        else if (row==1){
            return workingStrip;
        }
        else if (row-2<completedStrips.size()){
            return completedStrips.get(row-2);
        }
        else {
            return null;
        }
    }

    public static void main(String[] args) {
        DeShredder ds =new DeShredder();
        ds.setupGUI();

    }


    /**
     * Load an image from a file and return as a two-dimensional array of Color.
     * From COMP 102 assignment 8&9.
     * Maybe useful for the challenge. Not required for the core or completion.
     */
    public static Color[][] loadImage(String imageFileName) {
        if (imageFileName==null || !Files.exists(Path.of(imageFileName))){
            return null;
        }
        try {
            BufferedImage img = ImageIO.read(Files.newInputStream(Path.of(imageFileName)));
            int rows = img.getHeight();
            int cols = img.getWidth();
            Color[][] ans = new Color[rows][cols];
            for (int row = 0; row < rows; row++){
                for (int col = 0; col < cols; col++){                 
                    Color c = new Color(img.getRGB(col, row));
                    ans[row][col] = c;
                }
            }
            return ans;
        } catch(IOException e){UI.println("Reading Image from "+imageFileName+" failed: "+e);}
        return null;
    }

    /**
     * Save a 2D array of Color as an image file
     * From COMP 102 assignment 8&9.
     * Maybe useful for the challenge. Not required for the core or completion.
     */
    public static void saveImage(Color[][] imageArray, String imageFileName) {
        int rows = imageArray.length;
        int cols = imageArray[0].length;
        BufferedImage img = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < imageArray[row].length; col++) {
                Color c =imageArray[row][col];
                img.setRGB(col, row, c.getRGB());
            }
        }
        try {
            if (imageFileName==null) { return;}
            ImageIO.write(img, "png", Files.newOutputStream(Path.of(imageFileName)));
        } catch(IOException e){UI.println("Image reading failed: "+e);}

    }

}
