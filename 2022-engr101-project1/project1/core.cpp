#include <iostream>
#include <math.h>
#include "wav.hpp"

using namespace std;

/**
 * Project 1 (Core) - Jackson Rakena, 300609159 - Victoria University, ENGR 101, 2022
 */

int main()
{
    // Number of samples per second
    int sample_rate = 3000;

    // Length of each sample, in seconds (1/sample_rate)
    float sample_length = 1.0f / (float)sample_rate;

    // Duration of outputted waveform, in seconds
    float duration = 5;

    // Total number of samples (sample rate * duration)
    int total_samples = ((float)sample_rate) * duration;

    // Frequency of sound, in Hz.
    // Human hearing is 20 - 20000 (generally)
    int frequency = 900;

    // Volume, in arbitrary units.
    int volume = 6000;

    int *waveform = new int[total_samples]; // creates the array

    for (int current_sample = 0; current_sample < total_samples; current_sample++)
    {
        float current_sample_time = current_sample * sample_length;
        float pressure = volume * sinf(2.0f * M_PI * frequency * current_sample_time);

        waveform[current_sample] = (int)pressure;

        cout << "sample " << current_sample << ": " << pressure << endl;
    }

    MakeWavFromInt("core.wav", sample_rate, waveform, total_samples);
    delete (waveform);
    return 0;
}