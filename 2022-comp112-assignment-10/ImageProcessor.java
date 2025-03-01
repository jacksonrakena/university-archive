// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 10
 * Name:
 * Username:
 * ID:
 */

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ecs100.UI;
import ecs100.UIFileChooser;

/**
 * ImageProcessor allows the user to display and edit a
 * grayscale image in a number of ways.
 * The program represents the image as a 2D array of integers,
 * which must all be between 0 (black) and 255 (white).
 * The class includes three methods (already written for you) that will
 * - read a png or jpeg image file and store a 2D array of grayscale
 * values into the image field.
 * - render (display) the 2D array of grayscale values in the image
 * field on the graphics pane
 * - write the 2D array of grayscale values in the image field to
 * a png file.
 * 
 * You are to complete the methods to modify an image:
 * - lighten the image
 * - decrease the contrast (fade the image)
 * - flip the image horizontally
 * - shift the image vertically
 * - rotate the image 180 degrees and 90 degrees
 * - expands the top left quarter of the image
 * - merge another image with the current image
 *
 */
public class ImageProcessor {
    // the current image
    private int[][] image = null;

    // current selected point
    private int selectedRow = 0;
    private int selectedCol = 0;

    private final int pixelSize = 1; // the size of the pixels as drawn on screen

    /**
     * CORE:
     * Make all pixels in the image lighter by 20 graylevels.
     * but make sure that you never go above 255
     */
    public void lightenImage() {
        for (int x = 0; x < image.length; x++) {
            for (int y = 0; y < image[x].length; y++) {
                image[x][y] = Math.min(image[x][y] + 20, 255);
            }
        }

        this.redisplayImage();
    }

    /**
     * CORE:
     * Fade the image -
     * make all lighter pixels in the image (above 128) slightly darker (by 20%)
     * and make all darker pixels lighter (by 20%)
     * For example, a pixel value of 158 is 30 levels above 128. It should be
     * darkened by 20% of 30 (= 6) to 152.
     * A pixel value of 48 is 80 levels below 128. It should be lightened by 20% of
     * 80 (= 16) to 64.
     * But make sure that you never go above 255 or below 0.
     */
    public void fadeImage() {
        for (int x = 0; x < image.length; x++) {
            for (int y = 0; y < image[x].length; y++) {
                int original = image[x][y];

                int higherDifference = original - 128;
                int lowerDifference = 128 - original;
                double mod = 0;
                if (higherDifference > 0) {
                    mod = -1 * (0.2 * higherDifference);
                } else if (lowerDifference > 0) {
                    mod = 0.2 * lowerDifference;
                }
                image[x][y] = (int) Math.max(0, Math.min(original + mod, 255));
            }
        }

        this.redisplayImage();
    }

    /**
     * CORE:
     * Flip the image horizontally
     * exchange the values on the left half of the image
     * with the corresponding values on the right half
     */
    public void flipImageHorizontally() {
        for (int row = 0; row < image.length; row++) {
            for (int col = 0; col < Math.floor(image[row].length / 2.0); col++) {
                int oppositeCol = (image[row].length) - col - 1;
                int ocv = image[row][oppositeCol];
                int cur = image[row][col];
                image[row][oppositeCol] = cur;
                image[row][col] = ocv;
            }
        }

        this.redisplayImage();
    }

    /**
     * CORE:
     * Shift the image vertically
     * move each row of the image one step down,
     * moving the bottom row up to the top
     */
    public void shiftImageVertically() {
        int[] bottomRow = image[image.length - 1];
        int[][] newImage = new int[image.length][];
        for (int row = 0; row < image.length; row++) {
            if (row != image.length - 1) {
                newImage[row + 1] = image[row];
            }
        }
        newImage[0] = bottomRow;
        this.image = newImage;
        this.redisplayImage();
    }

    /**
     * COMPLETION:
     * Rotate the image 180 degrees
     * Each cell is swapped with the corresponding cell
     * on the other side of the center of the images.
     * It is easier to make a new array, the same size as image, then
     * copy each pixel in image to the right place in the new array
     * and then assign the new array to the image field.
     */
    public void rotateImage180() {
        for (int row = 0; row < image.length / 2; row++) {
            for (int col = 0; col < image[row].length; col++) {
                int oppositeRow = (image.length) - row - 1;
                int ocv = image[oppositeRow][col];
                int cur = image[row][col];
                image[oppositeRow][col] = cur;
                image[row][col] = ocv;
            }
        }

        this.redisplayImage();
    }

    /**
     * COMPLETION:
     * Rotate the image 90 degrees anticlockwise
     * Note, the resulting image will have different dimensions:
     * the width of the new image will be the height of the old image.
     * the height of the new image will be the width of the old image.
     * You will need to make a new image array of the new dimensions,
     * fill it with the correct pixel values from the original array,
     * and then set the image field to contain the new image.
     */
    public void rotateImage90() {
        int[][] rotate = new int[image[0].length][image.length];

        for (int row = 0; row < image.length; row++) {
            for (int col = 0; col < image[0].length; col++) {
                rotate[col][image.length - 1 - row] = image[row][col];
            }
        }

        image = rotate;

        this.redisplayImage();
    }

    /**
     * CHALLENGE:
     * Expand the top left quarter of the image to fill the whole image
     * each pixel in the top left quarter will be copied to four pixels
     * in the new image.
     * Be careful not to try to access elements past the edge of the array!
     * Hint: It is actually easier to work backwards from the bottom right corner.
     */
    public void expandImage() {
        int[][] newImage = new int[image.length][];
        for (int row = 0; row < image.length; row++) {
            for (int col = 0; col < image[row].length; col++) {
                if (newImage[row] == null)
                    newImage[row] = new int[image[row].length];
                newImage[row][col] = image[(int) (row / 2.0)][(int) (col / 2.0)];
            }
        }
        this.image = newImage;

        this.redisplayImage();
    }

    /**
     * CHALLENGE:
     * Merge two images
     * Ask the user to select another image file, and load it into another array.
     * Work out the rows and columns shared by the images
     * For each pixel value in the shared region, replace the current pixel value
     * by the average of the pixel value in current image and the corresponding
     * pixel value in the other image.
     */
    public void mergeImage() {
        int[][] other = this.loadAnImage(UIFileChooser.open());
        int rows = Math.min(this.image.length, other.length); // rows and cols
        int cols = Math.min(this.image[0].length, other[0].length); // common to both

        int[][] newImage = new int[rows][cols];
        for (int row = 0; row < newImage.length; row++) {
            for (int col = 0; col < newImage[row].length; col++) {
                newImage[row][col] = (int) ((image[row][col] + other[row][col]) / 2.0);
            }
        }

        this.image = newImage;
        this.redisplayImage();
    }

    // =========================================================================
    // Methods below here are already written for you -
    // for redisplaying the image array on the graphics pane,
    // for loading an image file into the image array,
    // for saving the image array into a file,
    // for setting the mouse position.

    /**
     * field and helper methods to precompute and store all the possible gray
     * colours,
     * so the redisplay method does not have to constantly construct new color
     * objects
     */
    private Color[] grayColors = computeGrayColors();

    /**
     * Display the image on the screen with each pixel as a square of size
     * pixelSize.
     * To speed it up, all the possible colours from 0 - 255 have been
     * precalculated.
     */
    public void redisplayImage() {
        UI.clearGraphics();
        UI.setImmediateRepaint(false);
        for (int row = 0; row < this.image.length; row++) {
            int y = row * this.pixelSize;
            for (int col = 0; col < this.image[0].length; col++) {
                int x = col * this.pixelSize;
                UI.setColor(this.grayColor(this.image[row][col]));
                UI.fillRect(x, y, this.pixelSize, this.pixelSize);
            }
        }
        UI.setColor(Color.red);
        UI.drawRect(this.selectedCol * this.pixelSize, this.selectedRow * this.pixelSize,
                this.pixelSize, this.pixelSize);
        UI.repaintGraphics();
    }

    /**
     * Get and return an image as a two-dimensional gray-scale image (from 0-255).
     * This method will cause the image to be returned as a gray-scale image,
     * regardless of the original colouration.
     */
    public int[][] loadAnImage(String imageName) {
        int[][] ans = null;
        if (imageName == null)
            return null;
        try {
            BufferedImage img = ImageIO.read(new File(imageName));
            UI.printMessage("loaded image height(rows)= " + img.getHeight() +
                    "  width(cols)= " + img.getWidth());
            ans = new int[img.getHeight()][img.getWidth()];
            for (int row = 0; row < img.getHeight(); row++) {
                for (int col = 0; col < img.getWidth(); col++) {
                    Color c = new Color(img.getRGB(col, row), true);
                    // Use a common algorithm to move to grayscale
                    ans[row][col] = (int) Math.round((0.3 * c.getRed()) + (0.59 * c.getGreen())
                            + (0.11 * c.getBlue()));
                }
            }
        } catch (IOException e) {
            UI.println("Image reading failed: " + e);
        }
        return ans;
    }

    /** Ask user for an image file, and load it into the current image */
    public void loadImage() {
        this.image = this.loadAnImage(UIFileChooser.open());
        this.redisplayImage();
    }

    /** Write the current grayscale image to the specified filename */
    public void saveImage() {
        // For speed, we'll assume every row of the image is the same length!
        int height = this.image.length;
        int width = this.image[0].length;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int grayscaleValue = this.image[row][col];
                Color c = new Color(grayscaleValue, grayscaleValue, grayscaleValue);
                img.setRGB(col, row, c.getRGB());
            }
        }
        try {
            String fname = UIFileChooser.save("save to png image file");
            if (fname == null)
                return;
            File imageFile = new File(fname);
            ImageIO.write(img, "png", new File(fname));
        } catch (IOException e) {
            UI.println("Image reading failed: " + e);
        }
    }

    private Color[] computeGrayColors() {
        Color[] ans = new Color[256];
        for (int i = 0; i < 256; i++) {
            ans[i] = new Color(i, i, i);
        }
        return ans;
    }

    private Color grayColor(int gray) {
        if (gray < 0) {
            return Color.blue;
        } else if (gray > 255) {
            return Color.red;
        } else {
            return this.grayColors[gray];
        }
    }

    public void doMouse(String a, double x, double y) {
        if (a.equals("released")) {
            this.setPos(x, y);
        }
    }

    /** Set the selected Row and Col to the pixel on the mouse position x, y */
    public void setPos(double x, double y) {
        int row = (int) (y / this.pixelSize);
        int col = (int) (x / this.pixelSize);
        if (this.image != null && row < this.image.length && col < this.image[0].length) {
            this.selectedRow = row;
            this.selectedCol = col;
            this.redisplayImage();
        }
    }

    public void setupGUI() {
        UI.initialise();
        UI.setMouseListener(this::doMouse);
        UI.addButton("Load", this::loadImage);
        UI.addButton("Save", this::saveImage);
        UI.addButton("Lighten", this::lightenImage);
        UI.addButton("Fade", this::fadeImage);
        UI.addButton("Flip Horiz", this::flipImageHorizontally);
        UI.addButton("Shift Vert", this::shiftImageVertically);
        UI.addButton("Rotate 180", this::rotateImage180);
        UI.addButton("Rotate 90", this::rotateImage90);
        UI.addButton("Expand", this::expandImage);
        UI.addButton("Merge", this::mergeImage);
        UI.addButton("Quit", UI::quit);

        // this is just to remind you to start the program using main!
        if (image != null) {
            this.redisplayImage();
        } else {
            UI.setFontSize(36);
            UI.drawString("Start the program from main", 2, 36);
            UI.drawString("in order to load an image", 2, 80);
            UI.sleep(2000);
            UI.quit();
        }
    }

    /**
     * main method: load an image and set up the user interface
     */
    public static void main(String[] arguments) {
        ImageProcessor obj = new ImageProcessor();
        obj.image = obj.loadAnImage("img-rose.jpg");
        obj.setupGUI();
    }
}
