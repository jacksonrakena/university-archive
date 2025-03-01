#include "path_algorithm.cpp"
#include <numeric>

class MoveControl {
private:
    double previous_error;
public:
    void try_follow_line_old(Camera& camera, RobotContext& context) {
        // Calculate scalar product (error)
        double total = 0;
        for (int row = 0; row < camera_image.height; row++)
        {
            vector<int> row_values = camera.calculate_row(row);
            vector<int> index_values = camera.calculate_indexes(camera_image.width);

            int error = inner_product(row_values.begin(), row_values.end(), index_values.begin(), 0);
            total += error;
        }
        double error = total / camera_image.height;

        // Calculate the error derivative
        int Vl2 = 0;
        int Vr2 = 0;

        double kp = 0.5;
        double kd = 1;
        double dv = ((error * kp) + (error - previous_error) * kd);
        previous_error = error;

        // Print the error, dv, kp, and kd
        cout << "[move_control1] error: " << error << " dv: " << dv << " kp/kd: " << kp << "/" << kd << endl;

        // Translate the velocity derivative into a motor speed
        double dv_pct = dv / 2000.0;
        if (dv_pct > 0)
        {
            Vl2 = 20;
            Vr2 = dv_pct * 20;
        }
        else if (dv_pct < 0)
        {
            Vl2 = dv_pct * 20;
            Vr2 = 20;
        }

        context.set_motor_speed(Vl2, Vr2);
    }

private:
    double Vf = 10;
    double Vl = 10;
    double Vr = 10;
public:

    void try_follow_line_new(Camera& camera, RobotContext& robotContext) {
        double kp = 45.0/1000;
        double kd = 1.0/1000;

        double total = 0;
        for (int row = 0; row < camera_image.height; row++)
        {
            vector<int> row_values = camera.calculate_row(row);
            vector<int> index_values = camera.calculate_indexes(camera_image.width);

            int error = inner_product(row_values.begin(), row_values.end(), index_values.begin(), 0);
            total += error;
        }
        double error = total / camera_image.height;

        double dv = (error * kp) + (error - previous_error) * kd;
        previous_error = error;

        Vl = Vf + dv;
        Vr = Vf-dv;
        // Print the error, dv, kp, and kd
        cout << "[move_control] error: " << error << " dv: " << dv << " kp/kd: " << kp << "/" << kd << endl;
        robotContext.set_motor_speed(Vl, Vr);
    }
};
