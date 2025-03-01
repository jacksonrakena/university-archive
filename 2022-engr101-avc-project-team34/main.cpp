#include "move_control.cpp"

using namespace std;

int main()
{
    // Initialise instances
    RobotContext robotContext = RobotContext{};
    PathAlgorithm pather = PathAlgorithm{};
    MoveControl movement = MoveControl{};
    Camera camera = core(); // change to core(), completion() or challenge() as needed
    robotContext.core_reached = false; // this line needs to be adjusted
    robotContext.completion_reached = false;
    robotContext.challenge_reached = false;

    // Credits
    cout << endl
         << endl
         << endl;

    cout << "== Team 34 AVC Robot Controller" << endl;
    cout << "Jackson Rakena, Benjamin Park, Sam Pennington, Dev Shamihoke, Levi Hawkins" << endl;
    cout << "ENGR 101, Trimester 1, 2022, Victoria University of Wellington" << endl;

    cout << endl
         << endl
         << endl;

    double prev_err = 0;

    // Start the main loop
    while (true)
    {
        cout << "Frame: " << robotContext.frame << " (" << (robotContext.frame * FRAME_DELAY_MSEC)/1000 << " sec) Stages completed: ";
        if (robotContext.core_reached) cout << "Core ";
        if (robotContext.completion_reached) cout << "Completion ";
        if (robotContext.challenge_reached) cout << "Challenge";
        cout << endl;
        if (camera.image_contains_color_pixels(1) && !robotContext.core_reached) {
            cout << "Reached core target." << endl;
            
            robotContext.core_reached = true;
        }
        if (camera.number_of_color_pixels(2) > 50 && !robotContext.completion_reached) {
            cout << "Reached completion target." << endl;
            
            robotContext.completion_reached = true;
        }
        if (camera.number_of_color_pixels(3) > 300*10 && !robotContext.challenge_reached) {
            cout << "Reached challenge target." << endl;
            cout << "Program terminated." << endl;
            robotContext.challenge_reached = true;
            break;
        }

        // Handle deviations from the line (intersections)
        std::string intersection = camera.classify_intersection();
        
        // Check if the robot has lost the line
        if (!camera.image_contains_nonwhite_pixels() || intersection == DEAD_END || !camera.row_contains_black_pixels(camera_image.height-1))
        {
            cout << "Robot lost the line." << endl;
            //moves a bit forward at a dead end or lost line situation so that when it turns around will be able to see an intersection
            robotContext.set_motor_speed(40, 40);
            robotContext.process_frame(1);

            while (!camera.image_contains_nonwhite_pixels() || intersection == DEAD_END || !camera.row_contains_black_pixels(camera_image.height-1))
            {
                //turns in place quite slowly but accurately
                cout<< "Robot turning, trying to find line... "<<endl;

                robotContext.set_motor_speed(-5, 5);
                robotContext.process_frame(1);
                intersection = camera.classify_intersection();
            }
            cout << "Robot found the line." << endl;
        }

        // Call the movement controller to try and follow the line
        movement.try_follow_line_new(camera, robotContext);

        

        // The controller bugs out on the first frame.
        // Ignore intersections on the first frame.
        // Don't try to handle intersections while on a marker.
        if (robotContext.frame > 0 && !camera.image_contains_any_color_pixels())
        {
            pather.handle_intersection(robotContext, intersection);
        }

        // Process the frame.
        robotContext.process_frame(1);
        cout << endl;
    }
    return 0;
}
