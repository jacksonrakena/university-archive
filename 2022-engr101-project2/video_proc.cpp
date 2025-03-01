#include "video_proc.hpp"
#include <vector>
#include <algorithm>

/**
 * Defines the ratio of red to green that is required to detect a pixel as being sufficiently red.
 */
#define PIXEL_RED_GREEN_ACTIVATION_THRESHOLD 1.6

/**
 * @brief 
 * 
 */
 * to interpret that movement as actual movement of the object, and not a defect
 * in the camera.
 */
#define MOVEMENT_DETECTION_THRESHOLD_PCT 0.1

/**
 * This shortcut defines a coordinate with points (0, 0)
 */
#define EMPTY_COORDINATE make_pair(0, 0)

/**
 * This type definition defines a 'coordinate' as simply a pair of two integers.
 */
#define coordinate pair<int, int>

/**
 * Determines if a given pixel at (col, row) is sufficiently red.
 * @param row The row of the pixel.
 * @param col The column of the pixel.
 * @return Whether the pixel at (col, row) is sufficiently red.
 */
bool is_pixel_red(int row, int col)
{
    int red = get_pixel(row, col, 0);
    int grn = get_pixel(row, col, 1);
    if (grn == 0)
        grn = 1;
    if (red == 0)
        red = 1;
    return (red / (double)grn) > PIXEL_RED_GREEN_ACTIVATION_THRESHOLD;
}

void mark_coord(coordinate c)
{
    set_pixel(c.first, c.second, (char)0, (char)0, (char)0);
}

/**
 * Converts a co-ordinate to a human-friendly representation (x, y).
 */
string coord_str(coordinate c)
{
    string str;
    str.append("(");
    str.append(to_string(c.second));
    str.append(",");
    str.append(to_string(c.first));
    str.append(")");
    return str;
}

/**
 * Represents a polygon mask, or a collection of top, left, right, and bottom points
 * that represent tracking points for the ruby.
 */
struct polygon_layer_mask
{
    coordinate top;
    coordinate right;
    coordinate left;
    coordinate bottom;
};

/**
 * Converts a layer mask to a human-friendly representation.
 */
string layer_mask_str(polygon_layer_mask mask)
{
    string str;
    str
        .append("top=")
        .append(coord_str(mask.top))
        .append(", left=")
        .append(coord_str(mask.left))
        .append(", right=")
        .append(coord_str(mask.right))
        .append(", bottom=")
        .append(coord_str(mask.bottom));
    return str;
}

/**
 * Determines whether the object is present in the given layer mask.
 * This function essentially just checks that at least one point
 * in the layer mask is not (0, 0), which indicates that the object
 * is not present.
 * @return True, if the object is present. Otherwise, false.
 */
bool detect_object_presence(polygon_layer_mask mask)
{
    return mask.top != EMPTY_COORDINATE || mask.left != EMPTY_COORDINATE ||
           mask.right != EMPTY_COORDINATE || mask.bottom != EMPTY_COORDINATE;
}

/**
 * This function scans the image for red points, and constructs
 * a tracking mask for the image. It finds the topmost,
 * leftmost, rightmost, and bottommost red points and saves it
 * into the tracking mask.
 * @param image_in The image to scan.
 * @return A layer mask representing the extremities in the image.
 */
polygon_layer_mask build_layer_mask(ImagePPM &image_in)
{
    polygon_layer_mask mask = {};

    std::vector<pair<int, int>> marked;
    pair<int, int> zero = make_pair(0, 0);

    for (int row = 0; row < image.height; row++)
    {
        for (int column = 0; column < image.width; column++)
        {
            if (is_pixel_red(row, column))
            {
                if (row < mask.top.first || mask.top == zero)
                    mask.top = make_pair(row, column);
                if (row > mask.bottom.first || mask.bottom == zero)
                    mask.bottom = make_pair(row, column);
                if (column < mask.left.second || mask.left == zero)
                    mask.left = make_pair(row, column);
                if (column > mask.right.second || mask.right == zero)
                    mask.right = make_pair(row, column);
            }
        }
    }
    mark_coord(mask.left);
    mark_coord(mask.right);
    mark_coord(mask.top);
    mark_coord(mask.bottom);

    return mask;
}

/**
 * This function detects whether a given integer has deviated from the template integer
 * by using MOVEMENT_DETECTION_THRESHOLD_PCT and the given boundary.
 * @param original The original position of the pixel.
 * @param current The current position of the pixel.
 * @param boundary The range of possible pixel positions. Usually the width or height of the image.
 * @return True, if the pixel has deviated by more than MOVEMENT_DETECTION_THRESHOLD_PCT multiplied
 * by boundary.
 */
bool detect_movement_pixel(int original, int current, int boundary)
{
    int difference = abs(current - original);
    return difference > (MOVEMENT_DETECTION_THRESHOLD_PCT * (double)boundary);
}

/**
 * This function detects whether a given co-ordinate has moved by detecting whether it's X or Y
 * coordinate has moved, using detect_movement_pixel.
 * @param original The original position of the co-ordinate.
 * @param current The current position of the co-ordinate.
 * @param boundary The range of possible co-ordinate positions. Usually the width or height of the image.
 * @return True, if the co-ordinate has deviated by more than MOVEMENT_DETECTION_THRESHOLD_PCT multiplied
 * by boundary.
 */
bool detect_movement_coord(coordinate original, coordinate current, int boundary)
{
    return detect_movement_pixel(original.first, current.first, boundary) || detect_movement_pixel(original.second, current.second, boundary);
}

/**
 * This function detects whether a given polygon layer mask has deviated from a template (or, essentially,
 * whether the object that the layer mask is tracking has moved by an unacceptable amount), and returns the number of
 * co-ordinates (out of 4 layer mask coordinates) that have moved.
 * @param original The original layer mask (or master template).
 * @param current The current layer mask.
 * @return The number of co-ordinates that have deviated from the original.
 */
int detect_polygon_movement(polygon_layer_mask original, polygon_layer_mask current)
{
    int points_moved = 0;
    if (detect_movement_coord(original.top, current.top, image.height))
        points_moved++;
    if (detect_movement_coord(original.bottom, current.bottom, image.height))
        points_moved++;
    if (detect_movement_coord(original.left, current.left, image.width))
        points_moved++;
    if (detect_movement_coord(original.right, current.right, image.width))
        points_moved++;

    return points_moved;
}

int main()
{
    std::string folder = "/Users/jackson/iCloudDrive/University/2022/ENGR 101/Project 2/Images/Challenge/";
    int nFrames = 19;
    polygon_layer_mask master;
    for (int iFrame = 0; iFrame < nFrames; iFrame++)
    {
        if (get_image_file(folder, iFrame) != 0)
        {
            continue;
        }

        if (iFrame == 0)
        {
            // We are assuming that in frame 0, the object is present, and in its normal position.
            // From that assumption, we can compute the master/template layer mask.
            // If later frames deviate from this master,
            // the object has been moved, and we should raise the alarm.
            master = build_layer_mask(image);
            cout << "init with master positions " << layer_mask_str(master) << endl;

            // Save the current mask, for debugging purposes.
            if (save_bmp_file("master_mask.bmp") != 0)
            {
                cout << "err saving " << iFrame << " layer mask" << endl;
            }
        }

        // Calculate the current position of the object,
        // by building a tracking mask.
        polygon_layer_mask frame_mask = build_layer_mask(image);

        // Core & Completion: Presence
        // Is the object present at all? (Do we have a valid polygon mask?)
        if (!detect_object_presence(frame_mask))
        {
            cout << "(STOLEN) object is NOT present in frame " << iFrame << endl;
            cout << "frame layer mask: " << layer_mask_str(frame_mask) << endl
                 << endl;
        }

        // Challenge: Movement
        // Has the object moved?
        // We determine this by seeing if at least TWO of the co-ordinates
        // of the object have moved.
        else if (detect_polygon_movement(master, frame_mask) > 2)
        {
            cout << "(BURGLARY IN PROGRESS) movement detected in frame " << iFrame << endl;
            cout << "original: " << layer_mask_str(master) << endl;
            cout << "frame: " << layer_mask_str(frame_mask) << endl
                 << endl;
        }

        // If no alarm has been raised, the object is present and in its original position.
        else
        {
            cout << "(SAFE) object IS present in frame " << iFrame << endl
                 << endl;
        }

        std::this_thread::sleep_for(std::chrono::milliseconds(500));
    }
    return 0;
}