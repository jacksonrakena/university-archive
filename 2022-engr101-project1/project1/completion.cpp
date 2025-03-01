#include <iostream> // input-output library 
#include <math.h>  // library for sin function 
#include "wav.hpp" // make sure to include this helper library 
// " " instead of <> means that library is in the same folder as your program 

using namespace std;

/**
 * Project 1 (Completion) - Jackson Rakena, 300609159 - Victoria University, ENGR 101, 2022
 */

void write_waveform_samples(float sample_length, int num_samples, float frequency, float volume, int* waveform, int start) {
    for (int current_sample = 0; current_sample < num_samples ; current_sample++){
        float current_sample_time = current_sample * sample_length;
        float pressure = volume * sinf(2.0f * M_PI * frequency * current_sample_time);

        waveform[current_sample+start] = (int) pressure;

        cout << "sample " << current_sample << ": " << pressure << endl;
    }
}

int main(){
    // Number of samples per second
    int sample_rate = 3000;// samples per second, select value which provides good quality sound
    float sample_length = 1.0f/sample_rate;

    int total_samples = 0;
    int repetitions = 5; // Number of repeats of the loop

    int ambulance_high = 650; // First tone
    int ambulance_low = 1000; // Second tone
    float duration_of_each_tone_in_repeat = 0.25f; // Duration of each tone, in seconds
    total_samples = repetitions * duration_of_each_tone_in_repeat * 2 * sample_rate; // allocate enough memory

    int* waveform = new int[total_samples]; // creates the array

    for (int repeat_counter = 0; repeat_counter < repetitions; repeat_counter++) {
        write_waveform_samples(sample_length, sample_rate*duration_of_each_tone_in_repeat, ambulance_high, 6000, waveform, total_samples);
        total_samples += duration_of_each_tone_in_repeat* (float) sample_rate;
        write_waveform_samples(sample_length, sample_rate*duration_of_each_tone_in_repeat, ambulance_low, 6000, waveform, total_samples);
        total_samples += duration_of_each_tone_in_repeat * (float) sample_rate;
    }

    MakeWavFromInt("completion.wav", sample_rate, waveform, total_samples);
    delete(waveform);
    return 0;
} 