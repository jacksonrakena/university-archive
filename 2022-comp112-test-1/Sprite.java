// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP102 - 2022T1, Online test
 * Name:
 * Username:
 * ID:
 */

import java.awt.Color;
import java.awt.Point;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import ecs100.*;

/**
 * A cute character that performs actions on the screen.
 *
 * <br/>
 * <strong>*** IMPORTANT ***</strong><br/>
 * You don't need to understand or change the code in this class.
 * The documentation tells you everything you need to know.
 * Wait until after the test to look at the source code!
 *
 * @author Morgan
 */
public class Sprite
{
    //Instance variables
    private String name;

    // We need to keep track of the position on the screen
    // This is the *bottom-center* of the sprite
    private double x = 100;
    private double y = 300.00;
    // We also track the position immediately before this one
    // so we know what area to erase when we're re-drawing
    private double oldX = 100.00;
    private double oldY = 300.00;
    
    // Are we facing right?
    // This is used to flip the sprite if we're facing left
    private boolean right = true;

    /* These are arrays to store the sprite data
     * This is a memory-intensive way to do things but
     * it stops us having to load the file from disk every frame
     */
    private BufferedImage[] idle;
    private BufferedImage[] run;
    private BufferedImage[] jump;
    private BufferedImage[] attack;
    private BufferedImage[] draw;
    private BufferedImage[] sheathe;
    
    //References to the current and previous frame
    private BufferedImage currentImage;
    private BufferedImage oldImage;

    /**
     * Creates a new Sprite object of the specified name. e . g. "Alice".
     * 
     * The sprite is drawn at the specified x position.
     * If facingRight is true , the sprite is drawn facing right.
     */
    public Sprite(String name, double position, boolean facingRight)
    {
        try {
            this.name = name;
            this.x = position;
            this.right = facingRight;
            this.idle = new BufferedImage[4];
            this.run = new BufferedImage[6];
            this.jump = new BufferedImage[4];
            this.draw = new BufferedImage[4];
            this.attack = new BufferedImage[4];
            this.sheathe = new BufferedImage[4];
            for(int i = 0; i < 4; i++){
                this.idle[i] = ImageIO.read(new File("sprites/adventurer-idle-0" + i + ".png"));
            }
            for(int i = 0; i < 6; i++){
                this.run[i] = ImageIO.read(new File("sprites/adventurer-run-0" + i + ".png"));
            }
            for(int i = 0; i < 4; i++){
                this.jump[i] = ImageIO.read(new File("sprites/adventurer-jump-0" + i + ".png"));
                this.draw[i] = ImageIO.read(new File("sprites/adventurer-swrd-drw-0" + i + ".png"));
                this.attack[i] = ImageIO.read(new File("sprites/adventurer-attack1-0" + i + ".png"));
                this.sheathe[i] = ImageIO.read(new File("sprites/adventurer-swrd-shte-0" + i + ".png"));
            }
            this.currentImage = idle[0];
            this.oldImage = currentImage;
            this.draw();
        } catch (IOException e) {
            UI.println("Error reading file: " + e.getMessage());
        }
    }
    
    /**
     * The parameter can only be either "left" or "right".
     * This method moves the sprite a short distance in the specified
     * direction.
     */
    public void move(String direction) {
        // xMove will be how many pixels we move left or right
        double xMove = 0.0;
        if (direction.equalsIgnoreCase("right")) {
            //walk to the right
            right = true;
            xMove = 5.0;
        } else if (direction.equalsIgnoreCase("left")) {
            //walk to the left
            right = false;
            xMove = -5.0;
        }
        // The run animation has six frames, and we move 5 pixels every frame
        // So we need it to run for 12 frames (5*12 = 60 pixels).
        for(int i = 0; i < 12; i++) {
            this.updateSprite(run[i%6], x+xMove, y);
        }
        // Reset back to idle pose, without a delay
        this.updateSprite(idle[0], x, y, false);
    }

    /**
     * Makes the character jump a short distance
     */
    public void jump() {
        // Jumping moves you more than running
        double xMove = 7.5;
        if(!right) {
            xMove = -7.5;
        }
        /*
         * This is the velocity (speed) of the character in the y (upwards) direction.
         * Every frame update, we move the character vertically by this amount.
         */
        double yVelo = 15;  
        // The first two frames are the character bending and launching
        this.updateSprite(jump[0], x, y);
        this.updateSprite(jump[1], x, y);
        /*
         * The vertical speed gradually gets small as the character gets higher, due to a pesky thing called gravity
         * To make the jump look natural, we need it to curve like a parabola -- this is what does that.
         * 
         * Once the y velocity is <=0, the character will begin falling back downwards
         */
        while(yVelo > 0) {
            this.updateSprite(jump[2], x + xMove, y - yVelo);
            yVelo -= 0.5*9.8; //Apply gravity (9.8m^2 is the acceleration due to gravity on earth)
        }
        // y = 300 is the ground, so we stop once we hit it
        while(y < 300) {
            double newY = Math.min(300, y-yVelo); // Don't fall through the ground
            this.updateSprite(jump[3], x + xMove, newY);
            yVelo -= 0.5*9.8; // apply gravity
        }
        // We cycle 0-1-0 because it gives a more natural landing look. Sometimes aesthetics is important!
        this.updateSprite(jump[0], x, y);
        this.updateSprite(jump[1], x, y);
        this.updateSprite(jump[0], x, y);
        // Finally, back to the idle pose
        this.updateSprite(idle[0], x, y);
    }
    
    /**
     * This method makes the sprite announce themselves using the
     * specified announcment, and their name. 
     *
     * @param  announcement  the text to say
     */
    public void announce(String announcement)
    {
        String introduction = "I am " + name;
        double offset = right ? 10 : -10;
        // Figure out how high and long the text on the screen is
        FontMetrics metrics = UI.getGraphics().getFontMetrics(UI.getGraphics().getFont());
        double xText1 = x - metrics.stringWidth(announcement)/2 + offset;
        double yText1 = y - currentImage.getHeight() - 20 - metrics.getHeight();
        double xText2 = x - metrics.stringWidth(introduction)/2 + offset;
        double yText2 = y - currentImage.getHeight() - 20;        
        //Say some stuff
        UI.drawString(announcement, xText1, yText1);
        UI.drawString(introduction, xText2, yText2);
        UI.drawLine(x, y - currentImage.getHeight() - 1,
                    x + offset, y - currentImage.getHeight() - 18);
        //UI.drawArc(x-5,y-currentImage.getHeight()-45,textLength*4/3,textHeight*4/3,220,340);
        //UI.eraseArc(x-5,y-currentImage.getHeight()-45,textLength*4/3-1,textHeight*4/3-1,220,340);
        this.idle();
        this.idle();
        UI.eraseString(announcement, xText1, yText1);
        UI.eraseString(introduction, xText2, yText2);
        UI.eraseLine(x, y - currentImage.getHeight() - 1,
                     x + offset, y - currentImage.getHeight() - 18);
    }
   
    /**
     * Makes the character wait around for a bit.
     */
    private void idle() {
        // Runs through the entire idle animation cycle + returns to the start
        // ( Begins and Ends with idle[0] )
        for(int i = 0; i <= idle.length; i++) {
            this.updateSprite(idle[i%idle.length],x,y);
        }
    }
    
    /**
     * Makes the character draw their sword, attack once, and put their sword away.
     */
    public void attack() {
        this.drawSword();
        this.swingSword();
        this.sheatheSword();
    }
    
    /*
     * These methods just iterate through the animations
     */
    private void drawSword() {
        for(int i = 0; i < draw.length; i++) {
            this.updateSprite(draw[i],x,y);
        }
    }
    
    private void swingSword() {
        for(int i = 0; i < attack.length; i++) {
            this.updateSprite(attack[i],x,y);
        }
    }
    
    private void sheatheSword() {
        for(int i = 0; i < sheathe.length; i++) {
            this.updateSprite(sheathe[i],x,y);
        }
    }
    
    /*
     * This updates the sprite on the screen (but actually just passes it to the other method)
     */
    private void updateSprite(BufferedImage image, double newX, double newY){
        this.updateSprite(image, newX, newY, true);
    }
    
    /*
     * This erases the old image and replaces it with a new image. It also updates x and y.
     * 
     * If delay is true, we pause for a bit here. This is important, otherwise everything would
     * happen much too quickly and we wouldn't see it.
     */
    private void updateSprite(BufferedImage image, double newX, double newY, boolean delay){
        this.oldImage = currentImage;
        this.currentImage = image;
        this.oldX = this.x;
        this.oldY = this.y;
        this.x = newX;
        this.y = newY;
        this.draw();
        if(delay) {
            UI.sleep(100);
        }
    }
    
    /*
     * Draw the character on the screen
     */
    private void draw() {
        // First we erase the old sprite
        UI.eraseImage(oldImage, oldX-oldImage.getWidth()/2, oldY-oldImage.getHeight()-1);
        // Then we need to check which direction we're facing
        if(right) {
            // If we're facing right, just draw the sprite
            UI.drawImage(currentImage, x-currentImage.getWidth()/2, y-currentImage.getHeight()-1);
        } else {
            // We're facing left, and the sprite images are facing right
            // We need to do some magic to flip them around
            AffineTransform flip = new AffineTransform();
            // If we scale the image by (-1,1), it reflects it along the vertical axis
            flip.concatenate(AffineTransform.getScaleInstance(-1,1));
            // But now we need to move it back into the right position
            flip.concatenate(AffineTransform.getTranslateInstance(-currentImage.getWidth(),0));
            // Turn it into something we can apply to an image (the JavaDocs have more info)
            AffineTransformOp op = new AffineTransformOp(flip, AffineTransformOp.TYPE_BILINEAR);
            // Flip the image and draw it
            UI.drawImage(op.filter(currentImage, null), x-currentImage.getWidth()/2, y-currentImage.getHeight()-1);
        }
    }
}
