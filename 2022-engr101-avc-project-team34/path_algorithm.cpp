#include "camera.hpp"
#include "robot_controller.cpp"

#define INTERSECTION_CLASSIFICATION_FRAME_SEPARATION 20

/**
 * @brief Handles activities related to pathing.
 * This class generally handles intersections,
 * when the robot is not travelling in a straight line.
 */
class PathAlgorithm
{

public:
    
    /**
     * Handles an intersection on a given frame.
     */
    void handle_intersection(RobotContext &context, const std::string& intersection)
    {
        
        cout << "[path_algorithm] intersection classified as: " << intersection << endl;
        // Instructions specific to the core map
        if (!context.core_reached && !context.completion_reached && !context.challenge_reached) {
            // No instructions
        }

        // Instructions specific to the completion map
        else if (context.core_reached ) {
            if(intersection == T_INTERSECTION || intersection == FOUR_WAY_INTERSECTION || intersection == T_INTERSECTION_RIGHT){
                // chosen option = right
                context.set_motor_speed(20, 20);
                context.process_frame(1);
                
                context.set_motor_speed(20, 0);
                context.process_frame(3);
                

            }else if(intersection == T_INTERSECTION_LEFT){
                
                if(!context.completion_reached){
                // chosen option = straight
                context.set_motor_speed(20, 20);
                context.process_frame(3);
                } else {
                // chosen option = left
                context.set_motor_speed(0, 20);
                context.process_frame(3);
                }
                

            }
            
        }

        
    }
};
