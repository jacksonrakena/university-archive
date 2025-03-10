// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.picturepuzzle.model;

/**
 * Represents a single "cell" in the game showing part of the image. A cell is
 * just a collection of raw image data.
 *
 * @author better
 *
 */
public class Cell {

	/**
	 * The pixel values of the cell.
	 */
	private int[] image;
	/**
	 * The width of the image in the cell.
	 */
	private int width;

	/**
	 * The rotation (in degrees) of this cell.
	 */
	private int rotation = 0;

	/**
	 * Construct a new cell with the specific pixels, width and index.
	 *
	 * @param image
	 *            Array that stores the image pixels.
	 * @param width
	 *            Width of the image in the cell.
	 */
	public Cell(int[] image, int width) {
		this.image = image;
		this.width = width;
	}

	/**
	 * Get width of the image in the cell.
	 *
	 * @return Return the width of the image (in pixels).
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the array of the image pixels in the cell.
	 *
	 * @return array representing pixels of this image.
	 */
	public int[] getImage() {
		return image;
	}

	/**
	 * Set the value of pixel at the coordinate(x,y).
	 *
	 * @param x
	 *            The x-coordinate of the pixel.
	 * @param y
	 *            The y-coordinate of the pixel.
	 * @param rgb
	 *            The value of the pixel.
	 */
	public void setRGB(int x, int y, int rgb) {
		image[y * width + x] = rgb;
	}

	/**
	 * Get the pixel value for the given coordinate of the image in the cell.
	 *
	 * @param x The x-coordinate of the pixel.
	 * @param y The y-coordinate of the pixel.
	 * @return get an integer value representing the Red, Green and Blue components
	 *         of the pixel at a given position.
	 */
	public int getRGB(int x, int y) {
		return image[y * width + x];
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof Cell c) && c.image == this.image;
	}

	/**
	 * Sets the rotation of this cell to a new value in degrees.
	 * @param newRotation The value, in degrees, to set the rotation to.
	 */
	public void setRotation(int newRotation) {
		this.rotation = newRotation;
	}

	/**
	 * Returns the current orientation of this cell, in degrees.
	 * @return The current orientation of this cell, in degrees.
	 */
	public int getRotation() { return this.rotation; }
}
