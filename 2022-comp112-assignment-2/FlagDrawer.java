// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 2
 * Name: Jackson Rakena
 * Username: rakenajack
 * ID: 300609159
 */

import java.awt.Color;

import ecs100.UI;

/**
 * Draws various flags
 *
 * You can find lots of flag details (including the correct dimensions and
 * colours)
 * from http://www.crwflags.com/fotw/flags/
 */

public class FlagDrawer {

    public static final double LEFT = 100; // the left side of the flags
    public static final double TOP = 50; // the top of the flags
    public static final double WIDTH_TO_HEIGHT_RATIO = 13.0 / 15.0;

    /**
     * CORE
     * Draw the flag of Belgium.
     * The flag has three vertical stripes;
     * The left is black, the middle is yellow, and the right is red.
     * The flag is 13/15 as high as it is wide (ratio 13:15).
     */
    public void drawBelgiumFlag() {
        UI.clearGraphics();
        UI.println("Belgium Flag");
        double width = UI.askDouble("How wide: ");

        double widthOfStripe = width / 3;
        double heightOfFlag = WIDTH_TO_HEIGHT_RATIO * width;

        // Draw black stripe
        UI.setColor(Color.black);
        UI.fillRect(LEFT, TOP, widthOfStripe, heightOfFlag);

        // Draw yellow stripe
        UI.setColor(Color.yellow.darker());
        UI.fillRect(LEFT + (widthOfStripe), TOP, widthOfStripe, heightOfFlag);

        // Draw red stripe
        UI.setColor(Color.red.darker());
        UI.fillRect(LEFT + (widthOfStripe * 2), TOP, widthOfStripe, heightOfFlag);
    }

    /**
     * CORE
     * The Red Cross flag consists of a white square with a red cross in the center
     * The cross can be drawn as a horizontal rectangle and a vertical rectangle.
     */
    public void drawRedCrossFlag() {
        UI.println("Red Cross Flag: ");
        UI.clearGraphics();
        double size = UI.askDouble("How wide: ");
        /* # YOUR CODE HERE */

        double widthOfFlag = size;
        double heightOfFlag = size; // square flag

        Color red = Color.red.darker();

        // Draw background
        UI.setColor(Color.white);
        UI.fillRect(LEFT, TOP, heightOfFlag, widthOfFlag);

        // Draw vertical rect
        UI.setColor(red);
        UI.fillRect(LEFT + (size * 0.4), TOP + (size * 0.2), size * 0.2, size * 0.6);

        // Draw horizontal rect
        UI.fillRect(LEFT + (size * 0.2), TOP + (size * 0.4), size * 0.6, size * 0.2);
    }

    /**
     * COMPLETION
     * Pacman
     * A red pacman facing up on a black background chasing yellow, green, and blue
     * dots.
     * 
     */
    public void drawPacman() {
        UI.clearGraphics();
        UI.println("Pacman Flag");
        double width = UI.askDouble("How wide: ");
        /* # YOUR CODE HERE */
        double height = width * 2;

        // Draw background
        UI.setColor(Color.black);
        UI.fillRect(LEFT, TOP, width, height);

        // Top section (three circles)
        double topSectionHeight = height / 2;
        double diameterOfSmallCircles = 0.15 * width;
        double radiusOfSmallCircles = diameterOfSmallCircles / 2;
        double circleOffset = (topSectionHeight / 3);

        /// Draw first circle
        UI.setColor(Color.blue);
        UI.fillOval((LEFT + width / 2) - radiusOfSmallCircles,
                TOP + ((circleOffset / 2) - radiusOfSmallCircles), diameterOfSmallCircles,
                diameterOfSmallCircles);

        /// Draw second circle
        UI.setColor(Color.green);
        UI.fillOval((LEFT + width / 2) - (diameterOfSmallCircles / 2),
                TOP + (circleOffset) + (circleOffset / 2) - radiusOfSmallCircles,
                diameterOfSmallCircles,
                diameterOfSmallCircles);

        /// Draw third circle
        UI.setColor(Color.yellow);
        UI.fillOval((LEFT + width / 2) - (diameterOfSmallCircles / 2),
                TOP + (2 * circleOffset) + (circleOffset / 2) - radiusOfSmallCircles,
                diameterOfSmallCircles,
                diameterOfSmallCircles);

        // Bottom section (Pac-Man)
        double bottomSectionOffset = TOP + topSectionHeight;
        UI.setColor(Color.red);
        double drawableAreaWidth = width * 0.8;
        UI.fillArc((LEFT + width * 0.1), bottomSectionOffset, drawableAreaWidth, drawableAreaWidth, 120, 300);
    }

    /**
     * COMPLETION
     * Draw the flag of Greenland.
     * The top half of the flag is white, and the bottom half is red.
     * There is a circle in the middle (off-set to left) which is
     * also half white/red but on the opposite sides.
     * The flag is 2/3 as high as it is wide (ratio 2:3).
     */
    public void drawGreenlandFlag() {
        UI.clearGraphics();
        UI.println("Greenland Flag");
        double width = UI.askDouble("How wide: ");
        double height = width * 2 / 3;

        Color greenlandRed = Color.red.darker();

        double diskDiameter = (double) 8 / 18;
        double diskDiameterActual = diskDiameter * width;
        double diskMarginFromLeft = ((double) 7 / 2) / (double) 18;

        // Draw container
        UI.setColor(Color.black);
        UI.drawRect(LEFT, TOP, width, height);

        // Draw background
        UI.setColor(greenlandRed);
        UI.fillRect(LEFT, TOP + (height / 2), width, height / 2);

        // Draw top half
        UI.setColor(Color.white);
        UI.fillArc(LEFT + (diskMarginFromLeft * width), TOP + (height / 2) - (diskDiameterActual / 2),
                diskDiameterActual, diskDiameterActual, 0,
                -180);

        // Draw bottom half
        UI.setColor(greenlandRed);
        UI.fillArc(LEFT + (diskMarginFromLeft * width), TOP + (height / 2) - (diskDiameterActual / 2),
                diskDiameterActual, diskDiameterActual, 0,
                180);
    }

    public void drawTriangle(double startX, double startY, double midX, double midY, double endX, double endY) {
        UI.fillPolygon(new double[] {
                startX,
                midX,
                endX,
        }, new double[] {
                startY,
                midY,
                endY
        }, 3);
    }

    /**
     * CHALLENGE
     * The Jamaican flag has a yellow diagonal cross with
     * green triangles top and bottom, and black triangles left and right.
     */
    public void drawJamaicaFlag() {
        UI.clearGraphics();
        UI.println("Flag of Jamaica");
        double width = UI.askDouble("How wide: ");
        double height = width * ((double) 2 / 3);

        Color yellow = Color.yellow;
        Color green = Color.green.darker().darker();

        UI.setColor(yellow);
        UI.fillRect(LEFT, TOP, width, height);

        double horizontalTriangleMargins = 0.092592592592593 * height;
        double verticalTriangleMargin = 0.092592592592593 * width;

        double innerWidth = 0.194444444444444 * width;
        double innerWidthHalf = innerWidth / 2;

        double innerHeight = 0.185185185185185 * height;
        double innerHeightHalf = innerHeight / 2;

        double centrelineY = TOP + (height / 2);
        double centrelineX = LEFT + (width / 2);

        // Triangles going horizontally (pointing left and right)
        double hTriangleWidth = (width / 2) - innerWidthHalf;
        double hTriangleHeight = height - horizontalTriangleMargins;

        double vTriangleWidth = width - (2 * verticalTriangleMargin);
        double vTriangleHeight = TOP + (height / 2) - innerHeightHalf;

        // Left (black) triangle
        UI.setColor(Color.black);
        drawTriangle(LEFT, TOP + horizontalTriangleMargins,
                LEFT, TOP + hTriangleHeight,
                LEFT + hTriangleWidth, centrelineY);

        // Right (black) triangle
        UI.setColor(Color.black);
        drawTriangle(LEFT + width, TOP + horizontalTriangleMargins,
                LEFT + width, TOP + hTriangleHeight,
                LEFT + width - hTriangleWidth, centrelineY);

        // Top (green) triangle
        UI.setColor(green);
        drawTriangle(LEFT + verticalTriangleMargin, TOP,
                centrelineX, vTriangleHeight,
                LEFT + verticalTriangleMargin + vTriangleWidth, TOP);

        // Bottom (green) triangle
        UI.setColor(green);
        drawTriangle(LEFT + verticalTriangleMargin, TOP + height,
                centrelineX, TOP + height - (height / 2) + innerHeightHalf,
                LEFT + width - verticalTriangleMargin, TOP + height);

    }

    public void drawStar(double x, double y, double boundingWidth, double boundingHeight) {
        double numberOfPoints = 5;
        double numberOfVertices = numberOfPoints * 2;
        double degreesPerPoint = 360 / numberOfPoints; // usually 72
        double degreesPerVertice = 360 / numberOfVertices; // usually 36
        double degreesPerHalfVertice = degreesPerVertice / 2; // usually 18

        double innerDiameter = 0.382352941176471 * boundingWidth;
        double innerRadius = innerDiameter / 2;
        double virtualCenterlineX = (boundingWidth / 2);
        double outerRadius = boundingWidth / 2;
        double virtualCenterlineY = (boundingHeight / 2);

        UI.fillPolygon(new double[] {
                x + virtualCenterlineX + (outerRadius * Math.cos(Math.toRadians(90))),
                x + virtualCenterlineX + (innerRadius * Math.cos(Math.toRadians(54))),
                x + virtualCenterlineX
                        + (outerRadius * Math.cos(Math.toRadians(degreesPerHalfVertice))),
                x + virtualCenterlineX
                        + (innerRadius * Math.cos(Math.toRadians(degreesPerHalfVertice))),
                x + virtualCenterlineX + (outerRadius * Math.cos(Math.toRadians(54))),

                x + virtualCenterlineX - (innerRadius * Math.cos(Math.toRadians(90))),

                x + virtualCenterlineX - (outerRadius * Math.cos(Math.toRadians(54))),
                x + virtualCenterlineX
                        - (innerRadius * Math.cos(Math.toRadians(degreesPerHalfVertice))),
                x + virtualCenterlineX
                        - (outerRadius * Math.cos(Math.toRadians(degreesPerHalfVertice))),
                x + virtualCenterlineX - (innerRadius * Math.cos(Math.toRadians(54))),
                x + virtualCenterlineX - (innerRadius * Math.cos(Math.toRadians(90))),
        }, new double[] {
                y,
                y + virtualCenterlineY - (innerRadius * Math.sin(Math.toRadians(54))),
                y + virtualCenterlineY - (outerRadius * Math.sin(Math.toRadians(18))),
                y + virtualCenterlineY + (innerRadius * Math.sin(Math.toRadians(18))),
                y + virtualCenterlineY + (outerRadius * Math.sin(Math.toRadians(54))),
                y + virtualCenterlineY + innerRadius,
                y + virtualCenterlineY + (outerRadius * Math.sin(Math.toRadians(54))),
                y + virtualCenterlineY + (innerRadius * Math.sin(Math.toRadians(18))),
                y + virtualCenterlineY - (outerRadius * Math.sin(Math.toRadians(18))),
                y + virtualCenterlineY - (innerRadius * Math.sin(Math.toRadians(54))),
                y,
        }, 11);
    }

    /**
     * CHALLENGE
     * The 3 stars flag has a blue vertical stripe on the left and black
     * vertical stripe on the right and 3 red 5 pointed stars in the middle stripe
     * The height is 2/3 of the width,
     * A full marks solution will have a method for drawing a 5 pointed star,
     * and call that method for each of the stars
     */
    public void drawThreeStarsFlag() {
        UI.clearGraphics();
        UI.println("Three stars Flag");
        double width = UI.askDouble("How wide: ");
        double height = width * ((double) 2 / 3);

        double tricolorStripeWidth = width / 3;

        UI.setColor(Color.blue);
        UI.fillRect(LEFT, TOP, tricolorStripeWidth, height);

        UI.setColor(Color.white);
        UI.fillRect(LEFT + tricolorStripeWidth, TOP, tricolorStripeWidth, height);

        UI.setColor(Color.black);
        UI.fillRect(LEFT + (tricolorStripeWidth * 2), TOP, tricolorStripeWidth, height);

        double buffer = height * 0.1;
        double starBoundSection = height * 0.8 / 3;
        double starBoundSectionCenterOffset = starBoundSection / 2;
        double triangleScalePct = 0.1 * width;
        UI.setColor(Color.red);
        drawStar(LEFT + (width / 2) - triangleScalePct / 2,
                TOP + buffer + starBoundSection - starBoundSectionCenterOffset - triangleScalePct / 2,
                triangleScalePct,
                triangleScalePct);

        drawStar(LEFT + (width / 2) - triangleScalePct / 2,
                TOP + buffer + starBoundSection * 2 - starBoundSectionCenterOffset
                        - triangleScalePct / 2,
                triangleScalePct,
                triangleScalePct);
        drawStar(LEFT + (width / 2) - triangleScalePct / 2,
                TOP + buffer + starBoundSection * 3 - starBoundSectionCenterOffset
                        - triangleScalePct / 2,
                triangleScalePct,
                triangleScalePct);
    }

    public void setupGUI() {
        UI.addButton("Clear", UI::clearPanes);
        UI.addButton("Core: Flag of Belgium", this::drawBelgiumFlag);
        UI.addButton("Core: Red Cross Flag", this::drawRedCrossFlag);
        // COMPLETION
        UI.addButton("Completion: Pacman Flag", this::drawPacman);
        UI.addButton("Completion: Flag of Greenland", this::drawGreenlandFlag);
        // CHALLENGE
        UI.addButton("Challenge: Flag of Jamaica", this::drawJamaicaFlag);
        UI.addButton("Challenge: Three stars flag", this::drawThreeStarsFlag);
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.3);
    }

    public static void main(String[] arguments) {
        FlagDrawer fd = new FlagDrawer();
        fd.setupGUI();
    }

}
