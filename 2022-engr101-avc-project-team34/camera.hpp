#ifndef CAMERA_H
#define CAMERA_H
#include "lib/images.h"

#define FOUR_WAY_INTERSECTION "Four-way intersection"
#define T_INTERSECTION "T intersection"
#define T_INTERSECTION_LEFT "T intersection left"
#define T_INTERSECTION_RIGHT "T intersection right"
#define MUST_TURN_LEFT "Must turn left"
#define MUST_TURN_RIGHT "Must turn right"
#define STRAIGHT_WITH_LEFT_TURN_APPROACH "Straight with left turn approach"
#define STRAIGHT_WITH_RIGHT_TURN_APPROACH "Straight with right turn approach"
#define STRAIGHT "Straight line"
#define DEAD_END "Dead end"
#define UNKNOWN "Unknown"

using namespace std;

/**
 * Represents the functionality of the robot directly
 * related to reading and parsing input.
 */
class Camera
{
public:
    // Constructor just calls init of images lib
    Camera(int robotX, int robotY, double angleDegrees)
    {
        init(robotX, robotY, angleDegrees * (M_PI/180));
    }

    ~Camera()
    = default;

    /**
     * Determines the intersection classification in the robot's field of view.
     * @return
     */
    std::string classify_intersection()
    {
        int num_right = this->number_of_column_black_pixels(camera_image.width - 1);
        int num_left = this->number_of_column_black_pixels(0);
        int num_bot = this->number_of_row_black_pixels(camera_image.height - 1);
        int num_top = this->number_of_row_black_pixels(0);
        int num_middle = this->num_grid_middle_black_pix();
        int num_top_left = this->num_grid_top_left_black_pix();
        int num_top_right = this->num_grid_top_right_black_pix();
        int num_middle_bot = this->number_of_row_black_pixels(camera_image.height - 25);

        bool top_left = num_top_left > 10;
        bool top_right = num_top_right > 10;
        bool mid = num_middle > 25;
        bool midbot = num_middle_bot > 0;
        bool left = num_left > 0;
        bool right = num_right > 0;
        bool bottom = num_bot > 0;
        bool top = num_top > 0;

        
        cout << "[camera] top="<<num_top<<",left="<<num_left<<",right="<<num_right<<",bot="<<num_bot<<endl;
        //uses a combination of positional classifications to define a junction
        //prioritises more complex junctions (first to be checked)
        if (num_top > 10 && num_top < 40 && num_left < 40 && num_left > 10 && num_right < 40 && num_right > 10 && !top_right && !top_left)
            return FOUR_WAY_INTERSECTION;
        if (num_top > 10 && num_top < 40 && num_left == 0 && num_right < 40 && num_right > 10 && num_bot<40 & num_bot> 10 && mid && !top_right)
            return T_INTERSECTION_RIGHT;
        if (num_top > 10 && num_top < 40 && num_left < 40 && num_left > 10 && num_right == 0 && num_bot<40 & num_bot> 10 && mid && !top_left)
            return T_INTERSECTION_LEFT;
        if (num_left > 10 && num_left < 40 && num_right < 40 && num_right > 10 && num_top == 0 && mid && !top_right && !top_left && num_bot<40 & num_bot> 10)
            return T_INTERSECTION;
        if (left && !right && !top)
            return MUST_TURN_LEFT;
        if (right && !left && !top)
            return MUST_TURN_RIGHT;
        if (top && left && !right)
            return STRAIGHT_WITH_LEFT_TURN_APPROACH;
        if (top && num_left == 0 && num_right > 10 && num_right < 30)
            return STRAIGHT_WITH_RIGHT_TURN_APPROACH;
        if (top && !left && !right)
            return STRAIGHT;
        if(bottom && !left && !right && !top && !mid && !midbot)
            return DEAD_END;
        return STRAIGHT;

    }

    /**
     * Returns true if the pixel at [col, row] is not white.
     */
    bool is_pixel_not_white(int row, int col)
    {
        Pixel pixel = get_camera_pixel(row, col);
        double red_pct = ((double)pixel.r) / 255.0;
        double grn_pct = ((double)pixel.g) / 255.0;
        double blu_pct = ((double)pixel.b) / 255.0;
        return ((red_pct + grn_pct + blu_pct) / 3.0) < 0.2;
    }


    /**
     * Returns true if the row at {row} contains any black pixels.
     */
    bool row_contains_black_pixels(int row)
    {
        for (int i = 0; i < camera_image.width; i++)
        {
            if (is_pixel_not_white(row, i))
                return true;
        }
        return false;
    }

    /**
     * Returns true if the column at {col} contains any black pixels.
     */
    bool column_contains_black_pixels(int col){
        for (int i = 0; i < camera_image.height; i++){
            if(is_pixel_not_white(i,col))
                return true;
        }
        return false;
    }

    int number_of_column_black_pixels(int col) {
        int result = 0;
        for (int i = 0; i < camera_image.height; i++){
            if(is_pixel_not_white(i,col)) result++;
        }
        return result;
    }

    int number_of_row_black_pixels(int row) {
        int result = 0;
        for (int i = 0; i < camera_image.width; i++)
        {
            if (is_pixel_not_white(row, i)) result++;
        }
        return result;
    }

    int number_of_color_pixels(int color) {
        int result = 0;
        for (int col = 0; col < camera_image.width; col++) {
            for (int row = 0; row < camera_image.height; row++) {
                Pixel pix = get_camera_pixel(row, col);

                double r = ((double)pix.r) / 255.0;
                double g = ((double)pix.g) / 255.0;
                double b = ((double)pix.b) / 255.0;

                if(1 == color && r > 0.8 && ((g+b)/2.0)<0.2){
                    result++;
                }
                if( 2 == color  && b>0.8 && ((g+r)/2.0)<0.2){
                    result++;
                }
                if(3 == color  && g>0.8 && ((b+r)/2.0)<0.2){
                    result++;
                }
            }
        }
        return result;
    }
    /**
     * returns number of black pixels in middle 20x20 grid
     */
    int num_grid_middle_black_pix(){
        int result = 0;
        for (int i = camera_image.width/2-10; i < camera_image.width/2+10; i++)
        {
            for(int j = camera_image.height/2 - 10; j < camera_image.height/2 + 10; j++){

            
            if (is_pixel_not_white(j, i))
                result++;
            }
        }
        return result;
    }
    /**
     * returns num of black pixels in top left in a 20x20 grid
    */
    int num_grid_top_left_black_pix(){
        int result = 0;
        for (int i = 0; i < 20; i++)
        {
            for(int j = 0; j < 20; j++){

            
            if (is_pixel_not_white(j, i))
                result++;
            }
        }
        return result;
    }
    /**
     * returns num of black pixels in top right in a 20x20 grid
    */
    int num_grid_top_right_black_pix(){
        int result = 0;
        for (int i = camera_image.width-1; i > camera_image.width-21; i--)
        {
            for(int j = 0; j < 20; j++){

            
            if (is_pixel_not_white(j, i))
                result++;
            }
        }
        return result;
    }



    bool image_contains_any_color_pixels() {
        for (int col = 0; col < camera_image.width; col++) {
            for (int row = 0; row < camera_image.height; row++) {
                Pixel pix = get_camera_pixel(row, col);

                double r = ((double)pix.r) / 255.0;
                double g = ((double)pix.g) / 255.0;
                double b = ((double)pix.b) / 255.0;

                if(r > 0.8 && ((g+b)/2.0)<0.2){
                    return true;
                }
                if(b>0.8 && ((g+r)/2.0)<0.2){
                    return true;
                }
                if(g>0.8 && ((b+r)/2.0)<0.2){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 1 = red
     * 2 = blue
     * 3 = green
     */
    bool image_contains_color_pixels(int color) {
        return number_of_color_pixels(color) > 0;
    }


    bool image_contains_nonwhite_pixels() {
        for (int column = 1; column < camera_image.width; column++) {
            if (column_contains_black_pixels(column)) return true;
        }
        return false;
    }



    /**
     * Calculates the error array for a given row of the camera.
     * @param row
     * @return
     */
    std::vector<int> calculate_row(int row)
    {
        std::vector<int> row_values;
        row_values.reserve(camera_image.width);

        for (int col = 0; col < camera_image.width; col++)
        {
            row_values.push_back(is_pixel_not_white(row, col));
        }
        return row_values;
    }

    /**
     * Calculates the index array for a given length.
     */
    std::vector<int> calculate_indexes(int length)
    {
        std::vector<int> indexes_values;
        indexes_values.reserve(length);
        if (length % 2 == 0)
        {
            int mid0 = floor(length / 2.0);
            int mid1 = ceil(length / 2.0);
            for (int i = 0; i < length; i++)
            {
                if (i < mid0)
                {
                    indexes_values.push_back(0 - (mid1 - i));
                }
                else if (i == mid0 || i == mid1)
                {
                    indexes_values.push_back(0);
                }
                else if (i > mid1)
                {
                    indexes_values.push_back(i - mid1);
                }
            }
        }
        else
        {
            int mid = floor(length / 2.0);
            for (int i = 0; i < length; i++)
            {
                if (i < mid)
                {
                    indexes_values.push_back(0 - i);
                }
                if (i == mid)
                {
                    indexes_values.push_back(0);
                }
                else
                {
                    indexes_values.push_back(i - mid);
                }
            }
        }
        return indexes_values;
    }
};

Camera core() {
    return {110, 160, 15};
}

Camera challenge() {
    return {815, 720, 90};
}

Camera completion() {
    return {1698, 1018, 90};
}
#endif
