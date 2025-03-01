using namespace std;
#include "camera.hpp"

#define FRAME_DELAY_MSEC 250

/**
 * @brief Represents a virtual robot.
 *
 */
class RobotContext
{
private:
    int left_motor = 0;
    int right_motor = 0;

public:
    /**
     * @brief The current execution frame.
     *
     */
    int frame = 0;

    bool core_reached;
    bool completion_reached;
    bool challenge_reached;

    /**
     * @brief Sets the speed of the motors.
     *
     */
    void set_motor_speed(int left, int right)
    {
        left_motor = left;
        right_motor = right;
    }

    /**
     * @brief Updates the robot's properties and processes {frames} number of frames.
     * Normally {frames} = 1.
     */
    void process_frame(int frames)
    {
        set_motors(this->left_motor, this->right_motor);
        for (int i = 0; i < frames; i++)
        {
            update_sim(FRAME_DELAY_MSEC);
            this->frame++;
        }
    }
};
