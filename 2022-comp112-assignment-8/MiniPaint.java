// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 8
 * Name:
 * Username:
 * ID:
 */

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JColorChooser;

import ecs100.UI;
import ecs100.UIFileChooser;

/**
 * Represents a virtual triangle, which, is guaranteed
 * to have a non-negative width
 * and height, offset from a virtual origin.
 */
class Rectangle {
    /**
     * The origin co-ordinate of the rectangle,
     * which is the top-left corner.
     */
    public Point2D.Double origin;

    /**
     * The width of the rectangle.
     */
    public double width;

    /**
     * The height of the rectangle.
     */
    public double height;

    private Rectangle(Point2D.Double origin, double width, double height) {
        this.origin = origin;
        this.width = width;
        this.height = height;
    }

    /**
     * Constructs a rectangle. This function will
     * handle cases where the second co-ordinate is
     * diagonally opposite to the first (i.e, when
     * the rectangle would have negative height and width)
     */
    public static Rectangle safe(Point2D.Double first, Point2D.Double second) {
        double width = second.x - first.x;
        double height = second.y - first.y;
        double x = first.x;
        double y = first.y;

        if (second.x < first.x) {
            width = first.x - second.x;
            x = second.x;
        }
        if (second.y < first.y) {
            height = first.y - second.y;
            y = second.y;
        }

        return new Rectangle(new Point2D.Double(x, y), width, height);
    }

    /**
     * Returns the virtual center of the rectangle.
     */
    public Point2D.Double center() {
        return new Point2D.Double(this.origin.x + (width / 2), this.origin.y + (height / 2));
    }

    /**
     * Returns the end (bottom-right) of the triangle.
     */
    public Point2D.Double end() {
        return new Point2D.Double(this.origin.x + width, this.origin.y + height);
    }
}

/**
 * Represents the current state of the application
 * during a draw operation.
 */
class DrawActionContext {
    /**
     * The original position of the tool (where the user clicked)
     */
    public Point2D.Double toolStartPosition;

    /**
     * The end position of the tool (where the user released)
     */
    public Point2D.Double toolEndPosition;

    /**
     * Whether the user is still holding down the mouse button
     * (i.e. not ready to draw it to the canvas)
     */
    public boolean isRubberBanding;

    public DrawActionContext(Point2D.Double toolStartPosition, Point2D.Double toolEndPosition, boolean invert) {
        this.toolEndPosition = toolEndPosition;
        this.isRubberBanding = invert;
        this.toolStartPosition = toolStartPosition;
    }
}

/**
 * A simple drawing program.
 * The user can select from a variety of tools and options using the buttons and
 * elements down the left side, and can use the mouse to add elements to the
 * drawing
 * according to the current tool and options
 * Note, most of the "action" in the program happens in response to mouse
 * events;
 * the buttons, textFields, and sliders mostly record information that is used
 * later by the mouse responding.
 */
public class MiniPaint {

    // fields to remember:
    // - the "tool" - what will be drawn when the mouse is next released.
    // may be a shape, or an image, or a caption,
    // [Completion] or eraser, or flower
    // - whether the shape should be filled or not
    // - the position the mouse was pressed,
    // - the string for the text caption
    // - the width of the lines and the font size of the text captions.
    // - [Completion] the name of the image file
    // - [Completion] the colors for the border and fill for shapes and captions

    /**
     * The current tool.
     */
    private Consumer<DrawActionContext> tool;

    /**
     * Represents the starting position of a draggable tool invocation.
     */
    private Point2D.Double toolFirstPosition = new Point2D.Double(0, 0);

    /**
     * The currently active tool color.
     */
    private Color lineColor = Color.cyan.darker();

    /**
     * The currently active fill color.
     */
    private Color fillColor = Color.orange;

    /**
     * Whether to fill in hollow objects.
     */
    private boolean fill = false;

    /**
     * The current caption text.
     */
    private String caption;

    /**
     * Whether the eraser is currently on.
     */
    private boolean erasing = false;

    /**
     * Whether the freehand mode is on.
     */
    private boolean drawingFreehand = false;

    /**
     * The freehand button.
     */
    private JButton freehandButton;

    /**
     * The fill button (state-responsive)
     */
    private JButton fillButton;

    /**
     * The line color button (state-responsive)
     */
    private JButton lineColorButton;

    /**
     * The fill color button (state-responsive)
     */
    private JButton fillColorButton;

    /**
     * The erase button (state-responsive)
     */
    private JButton eraseButton;

    /**
     * The current image location.
     */
    private String imagePath;

    /**
     * The line width.
     */
    private double lineWidth = 2;

    /**
     * The text size.
     */
    private double textSize = 10;

    /**
     * A hash map of tool name to available tools.
     */
    private HashMap<String, Consumer<DrawActionContext>> tools;

    /**
     * The previous render's primary rectangle.
     */
    Rectangle lastFramePrimaryRect = null;

    /**
     * Stored points for the triangle.
     */
    ArrayList<Point2D.Double> trianglePoints = new ArrayList<Point2D.Double>();

    /**
     * Are we currently drawing a triangle?
     */
    private boolean recordingPointsForTriangle = false;

    /**
     * Set up the interface: buttons, textfields, sliders,
     * listening to the mouse
     */
    public void setupGUI() {
        UI.addButton("Clear", UI::clearGraphics);

        // Default tool is a line
        tool = this::drawALine;

        // Initialise the automatic tools
        tools = new HashMap<String, Consumer<DrawActionContext>>();
        tools.put("Line", this::drawALine);
        tools.put("Rect", this::drawARectangle);
        tools.put("Oval", this::drawAnOval);
        tools.put("Flower", this::drawAFlower);

        // For each tool, add a button that sets the active tool to that one
        tools.forEach((name, tool) -> {
            UI.addButton(name, () -> {
                // If the user selects a tool, turn off freehand, triangle, and erasing
                disableAllToggles();
                setTool(tool);
            });
        });

        // Freehand ON or OFF button
        freehandButton = UI.addButton("Freehand OFF", () -> {
            this.drawingFreehand = !this.drawingFreehand;
            redrawButtonState();
        });

        // Erase button
        eraseButton = UI.addButton("Eraser OFF", () -> {
            this.erasing = !this.erasing;
            redrawButtonState();
        });

        // Caption text-field
        UI.addTextField("Caption", (text) -> {
            this.caption = text;
            this.tool = this::drawACaption;
            this.drawingFreehand = false;
            this.erasing = false;
            redrawButtonState();
        });

        // Image button
        UI.addButton("Image", () -> {
            imagePath = UIFileChooser.open("Please select an image:");
            this.recordingPointsForTriangle = false;
            this.drawingFreehand = false;
            this.tool = this::drawAnImage;
        });

        // Line color button
        lineColorButton = UI.addButton("Line Color", () -> {
            this.lineColor = JColorChooser.showDialog(null, "Set Line Color", this.lineColor);
            redrawButtonState();
        });
        this.lineColorButton.setForeground(this.lineColor);

        // Fill-color button
        fillColorButton = UI.addButton("Fill Color", () -> {
            this.fillColor = JColorChooser.showDialog(null, "Set Fill Color", this.fillColor);
            this.fill = true;
            redrawButtonState();
        });
        this.fillColorButton.setForeground(this.fillColor);

        // Mouse motion
        UI.setMouseMotionListener(this::doMouse);

        // Fill on or off butotn
        fillButton = UI.addButton(this.fill ? "Fill ON" : "Fill OFF", () -> {
            this.fill = !this.fill;
            redrawButtonState();
        });

        // Triangle button
        UI.addButton("Triangle", () -> {
            this.recordingPointsForTriangle = true;
        });

        // Size and width sliders
        UI.addSlider("Line Width", this.lineWidth, 10, this.lineWidth, (newWidth) -> {
            this.lineWidth = newWidth;
            redrawButtonState();
        });
        UI.addSlider("Text Size", this.textSize, 30, this.textSize, (newSize) -> {
            this.textSize = newSize;
            redrawButtonState();
        });
        redrawButtonState();

        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.0); // Hide the text area.
    }

    /**
     * Recalculates all button state - labels and color.
     */
    public void redrawButtonState() {
        eraseButton.setText(this.erasing ? "Eraser ON" : "Eraser OFF");
        freehandButton.setText(this.drawingFreehand ? "Freehand ON" : "Freehand OFF");
        fillButton.setText(this.fill ? "Fill ON" : "Fill OFF");

        this.lineColorButton.setForeground(this.lineColor);
        this.lineColorButton.setBackground(findContrastingColor(this.lineColor));
        this.fillColorButton.setForeground(this.fillColor);
        this.fillColorButton.setBackground(findContrastingColor(this.fillColor));

        UI.setLineWidth(this.lineWidth);
        UI.setFontSize(this.textSize);
    }

    /**
     * Returns the contrasting color to the input color.
     */
    public Color findContrastingColor(Color color) {
        double brightness = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return brightness >= 128 ? Color.black : Color.white;
    }

    /**
     * Turns off triangle, freehand, and eraser modes.
     */
    public void disableAllToggles() {
        this.recordingPointsForTriangle = false;
        this.trianglePoints.clear();
        this.drawingFreehand = false;
        this.erasing = false;
        eraseButton.setText(this.erasing ? "Eraser ON" : "Eraser OFF");
        freehandButton.setText(this.drawingFreehand ? "Freehand ON" : "Freehand OFF");
    }

    /**
     * Sets the active tool to the specified tool.
     */
    public void setTool(Consumer<DrawActionContext> tool) {
        this.tool = tool;
    }

    /**
     * Respond to mouse events
     * When pressed, remember the position.
     * When released, draw what is specified by current tool
     * Uses the value stored in the field to determine which kind of tool to draw.
     * It should call the drawALine, drawARectangle, drawAnOval, etc, methods
     * passing the pressed and released positions
     * [Completion] should respond to "dragged" events also to do erasing
     */
    public void doMouse(String action, double x, double y) {
        // If we are currently drawing a triangle, record the points
        if (this.recordingPointsForTriangle && action == "pressed") {
            // Record this point
            this.trianglePoints.add(new Point2D.Double(x, y));

            // Draw a line if we've only selected two points
            if (this.trianglePoints.size() == 2) {
                UI.invertLine(this.trianglePoints.get(0).x, this.trianglePoints.get(0).y, this.trianglePoints.get(1).x,
                        this.trianglePoints.get(1).y);
            }

            // Draw the triangle if we've selected three points, and un-draw the rubberband
            if (this.trianglePoints.size() == 3) {
                UI.invertLine(this.trianglePoints.get(0).x, this.trianglePoints.get(0).y, this.trianglePoints.get(1).x,
                        this.trianglePoints.get(1).y);
                UI.setColor(this.lineColor);
                UI.setLineWidth(this.lineWidth);

                // I hate Java
                double[] xpts = new double[3];
                double[] ypts = new double[3];
                for (int i = 0; i < 3; i++) {
                    xpts[i] = this.trianglePoints.get(i).x;
                    ypts[i] = this.trianglePoints.get(i).y;
                }

                // Draw the triangle
                UI.drawPolygon(
                        xpts, ypts, 3);

                // Fill the triangle
                if (this.fill) {
                    UI.setColor(this.fillColor);
                    UI.fillPolygon(xpts, ypts, 3);
                }

                // Erase the list
                this.trianglePoints.clear();
            }
        } else if (!this.erasing && !this.drawingFreehand && !this.recordingPointsForTriangle) {
            // Record the first position of the tool
            if (action == "pressed") {
                this.toolFirstPosition = new Point2D.Double(x, y);
            } else if (action == "released" && !this.erasing) {
                // Activate the tool
                Point2D.Double newPosition = new Point2D.Double(x, y);
                this.tool.accept(new DrawActionContext(this.toolFirstPosition, newPosition, false));
            } else if (action == "dragged") {
                // Activate the tool in rubber-band mode
                Point2D.Double newPosition = new Point2D.Double(x, y);
                this.tool.accept(new DrawActionContext(this.toolFirstPosition, newPosition, true));
            }

        } else if (action == "dragged") {
            // If we're in erase mode, erase the selected area
            if (this.erasing)
                UI.eraseOval(x - Math.max(5, this.lineWidth / 2), y - Math.max(5, this.lineWidth / 2),
                        Math.max(5, this.lineWidth), Math.max(5, this.lineWidth));
            else {
                UI.setColor(this.lineColor);
                UI.fillOval(x - Math.max(5, this.lineWidth / 2), y - Math.max(5, this.lineWidth / 2),
                        Math.max(5, this.lineWidth),
                        Math.max(5, this.lineWidth));
            }

        }
    }

    /*
     * For rubber-banding the line, this represents the first position of the last
     * frame's rubber-banded line
     */
    private Point2D.Double lastFrameLineFirst;

    /*
     * For rubber-banding the line, this represents the second position of the last
     * frame's rubber-banded line
     */
    private Point2D.Double lastFrameLineSecond;

    /**
     * Draw a line between the two positions (x1, y1) and (x2, y2)
     */
    public void drawALine(DrawActionContext context) {
        UI.setColor(this.lineColor);
        if (context.isRubberBanding) {
            // If we rubber-banded last frame, delete that rubber-band
            if (this.lastFrameLineFirst != null && this.lastFrameLineSecond != null) {
                UI.invertLine(this.lastFrameLineFirst.x, this.lastFrameLineFirst.y,
                        this.lastFrameLineSecond.x, this.lastFrameLineSecond.y);
            }

            // Rubber-band this frame
            UI.invertLine(context.toolStartPosition.x, context.toolStartPosition.y, context.toolEndPosition.x,
                    context.toolEndPosition.y);

            // Tell the next frame that we rubber-banded in this frame
            lastFrameLineFirst = context.toolStartPosition;
            lastFrameLineSecond = context.toolEndPosition;
        } else {
            // Actually draw the line
            UI.drawLine(context.toolStartPosition.x, context.toolStartPosition.y, context.toolEndPosition.x,
                    context.toolEndPosition.y);
            lastFrameLineFirst = null;
            lastFrameLineSecond = null;
        }
    }

    /**
     * Draw a rectangle between the two diagonal corners
     * [Completion] Works out the left, top, width, and height
     * Then draws the rectangle, based on the options
     */
    public void drawARectangle(DrawActionContext context) {
        Rectangle rectangle = Rectangle.safe(context.toolStartPosition, context.toolEndPosition);

        UI.setColor(this.lineColor);
        if (context.isRubberBanding) {
            // If we rubber-banded last frame, clear that rubber-band
            if (this.lastFramePrimaryRect != null) {
                UI.invertRect(this.lastFramePrimaryRect.origin.x, this.lastFramePrimaryRect.origin.y,
                        this.lastFramePrimaryRect.width, this.lastFramePrimaryRect.height);
            }
            // Rubber-band this frame
            UI.invertRect(rectangle.origin.x, rectangle.origin.y, rectangle.width, rectangle.height);
            this.lastFramePrimaryRect = rectangle;
        } else {
            UI.drawRect(rectangle.origin.x, rectangle.origin.y, rectangle.width, rectangle.height);
            if (this.fill) {
                UI.setColor(this.fillColor);
                UI.fillRect(rectangle.origin.x + this.lineWidth / 2, rectangle.origin.y + this.lineWidth / 2,
                        rectangle.width + 1 - this.lineWidth,
                        rectangle.height + 1 - this.lineWidth);
            }
            this.lastFramePrimaryRect = null;
        }
    }

    /**
     * Draw an oval to fit the rectangle between the the two diagonal corners
     * [Completion] Works out the left, top, width, and height
     * Then draws the oval, based on the options
     */
    public void drawAnOval(DrawActionContext context) {
        Rectangle rectangle = Rectangle.safe(context.toolStartPosition, context.toolEndPosition);
        UI.setColor(this.lineColor);

        if (context.isRubberBanding) {

            // If we rubber-banded last frame, clear that rubber-band
            if (this.lastFramePrimaryRect != null) {
                UI.invertOval(this.lastFramePrimaryRect.origin.x, this.lastFramePrimaryRect.origin.y,
                        this.lastFramePrimaryRect.width, this.lastFramePrimaryRect.height);
            }

            // Rubber-band this frame
            UI.invertOval(rectangle.origin.x, rectangle.origin.y, rectangle.width, rectangle.height);
            this.lastFramePrimaryRect = rectangle;
        } else {
            UI.drawOval(rectangle.origin.x, rectangle.origin.y, rectangle.width, rectangle.height);
            if (this.fill) {
                UI.setColor(this.fillColor);
                UI.fillOval(rectangle.origin.x + this.lineWidth / 2, rectangle.origin.y + this.lineWidth / 2,
                        rectangle.width - this.lineWidth / 2,
                        rectangle.height - this.lineWidth / 2);
            }
            this.lastFramePrimaryRect = null;
        }
    }

    /**
     * Draws the current caption at the mouse released point.
     */
    public void drawACaption(DrawActionContext context) {
        UI.setColor(this.lineColor);
        UI.drawString(this.caption, context.toolStartPosition.x, context.toolStartPosition.y);
    }

    /**
     * [Completion]
     * Draws the current image between the two diagonal corners, unless
     * they are very close, and then just draws the image at its natural size
     * Works out the left, top, width, and height
     * Then draws the image, if there is one.
     */
    public void drawAnImage(DrawActionContext context) {
        if (context.isRubberBanding)
            return;
        Rectangle rectangle = Rectangle.safe(context.toolStartPosition, context.toolEndPosition);

        if (rectangle.height < 5 || rectangle.width < 5) {
            UI.drawImage(this.imagePath, rectangle.origin.x, rectangle.origin.y);
        } else {
            UI.drawImage(this.imagePath, rectangle.origin.x, rectangle.origin.y, rectangle.width, rectangle.height);
        }
    }

    // For the flower, this represents the ending position of the mouse on the last
    // frame
    private Point2D.Double flower_lastToolPosition = null;

    // For the flower, this represents the radius o
    private double flower_lastRadius = 0;

    /**
     * [Completion]
     * Draws a simple flower with 6 petals, centered at (x,y) with the given radius
     */
    public void drawAFlower(DrawActionContext context) {
        Rectangle rectangle = Rectangle.safe(context.toolStartPosition, context.toolEndPosition);
        double radius = rectangle.origin.distance(rectangle.end());
        // double radius = Math.sqrt(Math.pow(rectangle.width, 2) +
        // Math.pow(rectangle.height, 2));
        if (context.isRubberBanding) {
            if (flower_lastToolPosition == null) {
                // If we're rubber banding and we didn't rubber-band last frame,
                // rubber-band this frame
                UI.invertOval(context.toolStartPosition.x, context.toolStartPosition.y - radius / 4,
                        radius / 2, radius / 2);

                UI.invertOval(context.toolStartPosition.x + radius / 2, context.toolStartPosition.y - radius / 4,
                        radius / 2,
                        radius / 2);
                UI.invertOval(context.toolStartPosition.x - radius / 2, context.toolStartPosition.y - radius / 4,
                        radius / 2,
                        radius / 2);
                UI.invertOval(context.toolStartPosition.x - radius / 4, context.toolStartPosition.y + radius / 5,
                        radius / 2,
                        radius / 2);
                UI.invertOval(context.toolStartPosition.x + radius / 4, context.toolStartPosition.y + radius / 5,
                        radius / 2,
                        radius / 2);
                UI.invertOval(context.toolStartPosition.x - radius / 4,
                        context.toolStartPosition.y - radius / 4 - radius / 4 - radius / 5, radius / 2, radius / 2);
                UI.invertOval(context.toolStartPosition.x + radius / 4,
                        context.toolStartPosition.y - radius / 4 - radius / 4 - radius / 5, radius / 2, radius / 2);

                flower_lastToolPosition = context.toolStartPosition;
                flower_lastRadius = radius;
            } else {
                // Un-rubberband this frame
                UI.invertOval(flower_lastToolPosition.x, flower_lastToolPosition.y - flower_lastRadius / 4,
                        flower_lastRadius / 2, flower_lastRadius / 2);

                UI.invertOval(flower_lastToolPosition.x + flower_lastRadius / 2,
                        flower_lastToolPosition.y - flower_lastRadius / 4,
                        flower_lastRadius / 2,
                        flower_lastRadius / 2);
                UI.invertOval(flower_lastToolPosition.x - flower_lastRadius / 2,
                        flower_lastToolPosition.y - flower_lastRadius / 4,
                        flower_lastRadius / 2,
                        flower_lastRadius / 2);
                UI.invertOval(flower_lastToolPosition.x - flower_lastRadius / 4,
                        flower_lastToolPosition.y + flower_lastRadius / 5,
                        flower_lastRadius / 2,
                        flower_lastRadius / 2);
                UI.invertOval(flower_lastToolPosition.x + flower_lastRadius / 4,
                        flower_lastToolPosition.y + flower_lastRadius / 5,
                        flower_lastRadius / 2,
                        flower_lastRadius / 2);
                UI.invertOval(flower_lastToolPosition.x - flower_lastRadius / 4,
                        flower_lastToolPosition.y - flower_lastRadius / 4 - flower_lastRadius / 4
                                - flower_lastRadius / 5,
                        flower_lastRadius / 2, flower_lastRadius / 2);
                UI.invertOval(flower_lastToolPosition.x + flower_lastRadius / 4,
                        flower_lastToolPosition.y - flower_lastRadius / 4 - flower_lastRadius / 4
                                - flower_lastRadius / 5,
                        flower_lastRadius / 2, flower_lastRadius / 2);
                flower_lastToolPosition = null;
                flower_lastRadius = 0;

            }
        } else {
            // Actually draw the flower
            UI.setColor(Color.yellow);
            UI.fillOval(context.toolStartPosition.x, context.toolStartPosition.y - radius / 4,
                    radius / 2, radius / 2);

            UI.setColor(this.fillColor);
            UI.fillOval(context.toolStartPosition.x + radius / 2, context.toolStartPosition.y - radius / 4,
                    radius / 2,
                    radius / 2);
            UI.fillOval(context.toolStartPosition.x - radius / 2, context.toolStartPosition.y - radius / 4,
                    radius / 2,
                    radius / 2);
            UI.fillOval(context.toolStartPosition.x - radius / 4, context.toolStartPosition.y + radius / 5,
                    radius / 2,
                    radius / 2);
            UI.fillOval(context.toolStartPosition.x + radius / 4, context.toolStartPosition.y + radius / 5,
                    radius / 2,
                    radius / 2);
            UI.fillOval(context.toolStartPosition.x - radius / 4,
                    context.toolStartPosition.y - radius / 4 - radius / 4 - radius / 5, radius / 2, radius / 2);
            UI.fillOval(context.toolStartPosition.x + radius / 4,
                    context.toolStartPosition.y - radius / 4 - radius / 4 - radius / 5, radius / 2, radius / 2);
            flower_lastToolPosition = null;
            flower_lastRadius = 0;
        }

    }

    // Main: constructs a new MiniPaint object and set up GUI
    public static void main(String[] arguments) {
        MiniPaint mp = new MiniPaint();
        mp.setupGUI();
    }
}
