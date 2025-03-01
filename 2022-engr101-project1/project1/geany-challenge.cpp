//
// Created by Jackson Rakena on 7/03/22.
//

#include <iostream>
#include <cmath>
#include "wav.hpp"

using namespace std;

/**
 * Project 1 (Challenge) - Jackson Rakena, 300609159 - Victoria University, ENGR 101, 2022
 * Notes for extensibility:
 * - Additional transforms could be added (current: linear (attack, decay, release), constant (sustain))
 *      - Perhaps an exponential or root-mean-square transformer?
 * - Additional shapes could be added (current: piano_shape, and a very broken trumpet_shape)
 */

/**
 * Represents the shape of a musical envelope.
 */
struct shape
{
    /**
     * The percentage of the envelope that is occupied by the 'Attack' stage.
     */
    virtual double attack() { return 0; };

    /**
     * The percentage of the envelope that is occupied by the 'Decay' stage.
     */
    virtual double decay() { return 0; };

    /**
     * The percentage of the envelope that is occupied by the 'Sustain' stage.
     */
    virtual double sustain() { return 0; };

    /**
     * The percentage of the envelope that is occupied by the 'Release' stage.
     */
    virtual double release() { return 0; };

    /**
     * Validates the integrity of the envelope. Returns false if the components don't equal 100%.
     */
    bool validate()
    {
        return (attack() + decay() + sustain() + release()) == 1;
    }
};

/**
 * Implements a generic piano-style musical envelope.
 */
struct piano_shape : shape
{
    double attack() override { return 0.2; }
    double decay() override { return 0.7; }
    double sustain() override { return 0.05; }
    double release() override { return 0.05; }
};

/**
 * Implements an experimental trumpet-style musical envelope.
 */
struct __attribute__((unused)) trumpet_shape : shape
{
    double attack() override { return 1.0 / 3.0; }
    double decay() override { return 0.0; }
    double sustain() override { return 1.0 / 3.0; }
    double release() override { return 1.0 / 3.0; }
};

/**
 * Represents data associated with the calculation of a note.
 */
struct note_context
{
public:
    /**
     * The index of this note in the overall composition.
     */
    int index;

    /**
     * The frequency of this note.
     */
    double frequency;

    /**
     * The shape of this note.
     */
    shape *note_shape;

    /**
     * The volume of this note.
     */
    double volume;
};

/**
 * Settings and parameters that apply to all notes.
 */
struct global_context
{
    /**
     * A pointer to the waveform vector.
     */
    vector<int> *waveform;

    /**
     * The length of a note, in seconds.
     */
    double length_of_note = 0.4;

    /**
     * The sample rate of the produced audio, in Hz.
     */
    int sample_rate = 44100;

    /**
     * Represents the current sample 'write head', or the next sample to be written into the waveform vector.
     */
    int current_sample_head = 0;

    /**
     * Returns the length of each sample, in seconds. This is a function of the sample rate.
     */
    double get_sample_length() const { return 1.0 / this->sample_rate; }

    /**
     * Returns the number of samples per note, as a function of the length of each note (in seconds),
     * and the sample rate.
     */
    int get_samples_per_note() const { return this->sample_rate * this->length_of_note; }

    /**
     * Returns the first absolute sample number (vector index) of a specific note.
     */
    int get_first_sample_of_note(note_context *note) const
    {
        return note->index * get_samples_per_note();
    }

    /**
     * Calculates the sample time (how far into the composition a certain sample is).
     */
    double calculate_time_of_sample(int sample) const
    {
        return this->get_sample_length() * sample;
    }
};

/**
 * Represents a generic sample transform, which is a change in volume throughout the playback of a note.
 */
struct note_transform
{
    /**
     * Returns the current (y) value of this transform, by a given (x) value.
     * @param current_sample_in_this_transform The current sample, within the bounds of the transform.
     * @param total_samples_in_this_transform The number of total samples in this transform.
     * @return The amplitude to use when measuring the sample.
     */
    virtual double get_value(int current_sample_in_this_transform, int total_samples_in_this_transform) { return 0.0f; }
};

/**
 * This transform holds a note at a given amplitude.
 */
struct constant_transform : note_transform
{
private:
    double amplitude;

public:
    /**
     * The first constructor.
     * @param amplitude The amplitude to hold the note at.
     */
    explicit constant_transform(double amplitude)
    {
        this->amplitude = amplitude;
    }

    double get_value(int current_sample_in_this_transform, int total_samples_in_this_transform) override
    {
        return this->amplitude;
    }
};

/**
 * Represents a simple y=x/y=-x linear transform of a note.
 */
struct linear_transform : note_transform
{
private:
    bool positive;
    double base_pct;
    double max;
    /**
     * The first constructor.
     * @param base_pct The baseline percentage. The volume of the note will never go below base_pct% of the max`.
     * @param max The maximum volume of the note.
     * @param is_positive Whether to follow the graph of y=x or y=-x.
     */
public:
    linear_transform(double base_pct, double max, bool is_positive)
    {
        this->positive = is_positive;
        this->max = max;
        this->base_pct = base_pct;
    }
    double get_value(int current_sample_in_this_transform, int total_samples_in_this_transform) override
    {
        double progression = (double)current_sample_in_this_transform / (double)total_samples_in_this_transform;
        if (!this->positive)
        {
            progression = 1.0 - progression;
            double calc = progression * max;
            if (calc < base_pct * max)
                return base_pct * max;
            return calc;
        }
        double calc = progression * max;
        if (calc < base_pct * max)
            return base_pct * max;
        return calc;
    }
};

/**
 * Writes a single sample to the waveform vector.
 * This function calculates the required pressure and inserts it into the vector at the given position.
 * @param sample_length The length of an individual sample, in seconds.
 * @param amplitude The amplitude (volume) of the sample.
 * @param frequency The frequency of the sample, in Hz.
 * @param waveform The targeted waveform vector (usually global->waveform)
 * @param position The position of the sample in the vector.
 * @return The air pressure of the sample.
 */
double write_single(double sample_length, double amplitude, double frequency, vector<int> *waveform, int position)
{
    int result = amplitude * sin(2.0 * M_PI * frequency * sample_length * position);
    (*waveform)[position] = result;
    return result;
}

/**
 * This function applies a transform to a number of samples of a given note and then writes the
 * transformed waveform to the waveform vector.
 * @param label The label of this transform, for logging purposes.
 * @param global Global context.
 * @param note The note context to write.
 * @param transform The transform to apply. Usually a linear or constant transform.
 * @param num_samples The number of samples to write.
 * @return The final volume of the waveform (that is, the volume of the last sample).
 */
double write_transformed_waveform(const string &label, global_context *global, note_context *note, note_transform *transform, int num_samples)
{
    double starting_volume = transform->get_value(0, num_samples);
    double ending_volume = 0;

    int starting_sample = global->current_sample_head;

    cout << "write_transformed_waveform: (" << label << ") starting at sample " << global->current_sample_head << ", writing " << num_samples << " samples. Starting volume is " << starting_volume << "." << endl;
    for (int current_sample = starting_sample; current_sample < global->current_sample_head + num_samples; current_sample++)
    {
        // Calculate the current required volume.
        double volume = transform->get_value(current_sample - starting_sample, num_samples);

        // Add another notes worth of sample values to the vector's size,
        // if it's not big enough.
        if ((*global->waveform).size() < current_sample)
            (*global->waveform).resize(current_sample + global->get_samples_per_note());

        // Write the sample.
        write_single(global->get_sample_length(), volume, note->frequency, global->waveform, current_sample);
        ending_volume = volume;
    }
    global->current_sample_head += num_samples;
    cout << "write_transformed_waveform: (" << label << ") ended at sample " << global->current_sample_head << ", wrote " << num_samples << " samples. Ending volume is " << ending_volume << "." << endl;
    return ending_volume;
}

/**
 * This function applies a given shape definition to a note and then writes the shaped/enveloped note
 * to the global waveform.
 * @param global The global context.
 * @param note The note to shape, and then write.
 * @param shape The shape to apply. Usually a piano shape.
 */
void write_shape(global_context *global, note_context *note, shape *shape)
{
    if (!shape->validate())
    {
        cout << "write_shape: fatal error: invalid shape model - shape components do not equal 1 (=" << (shape->release() + shape->sustain() + shape->attack() + shape->decay()) << ")" << endl;
        exit(123);
    }

    // Attack
    note_transform *attack_transform = new linear_transform(0.0, note->volume, true);
    int number_of_attack_samples = shape->attack() * global->get_samples_per_note();
    write_transformed_waveform("attack", global, note, attack_transform, number_of_attack_samples);

    // Decay to 60%
    note_transform *decay_transform = new linear_transform(0.6, note->volume, false);
    int number_of_decay_samples = shape->decay() * global->get_samples_per_note();
    write_transformed_waveform("decay", global, note, decay_transform, number_of_decay_samples);

    // Sustain at 60%
    note_transform *sustain_transform = new constant_transform(0.6 * note->volume);
    int number_of_sustain_samples = shape->sustain() * global->get_samples_per_note();
    write_transformed_waveform("sustain", global, note, sustain_transform, number_of_sustain_samples);

    // Release to 0%
    note_transform *release_transform = new linear_transform(0.0, 0.6 * note->volume, false);
    int number_of_release_samples = shape->release() * global->get_samples_per_note();
    write_transformed_waveform("release", global, note, release_transform, number_of_release_samples);
}

/**
 * This function computes the shape and size of a note and then writes it to the global waveform vector.
 * @param global The global context.
 * @param note The note to compute.
 */
void write_note(global_context *global, note_context *note)
{
    int first_sample = global->get_first_sample_of_note(note);
    cout << "write_note: beginning to write note " << note->index << " (frequency " << note->frequency << ") to waveform at sample " << first_sample << " (time=" << global->calculate_time_of_sample(first_sample) << ")" << endl;

    cout << "write_note: beginning to write shape" << endl;
    write_shape(global, note, note->note_shape);
    cout << "write_note: finished writing shape" << endl;

    cout << "write_note: finished writing note " << note->index << "." << endl;
    cout << endl;
}

int main()
{
    // Chosen file.
    string file_name = "odetojoy.txt";
    ifstream target_file(file_name);

    // Chosen shape:
    shape *shape = new piano_shape;

    // Initialise the waveform
    global_context *global = new global_context;
    global->waveform = new vector<int>(global->get_samples_per_note() * 30);

    string input;
    cout << "global: beginning production with sample_rate=" << global->sample_rate << ", sample_length=" << global->get_sample_length() << ", samples_per_note=" << global->get_samples_per_note() << endl;
    cout << endl;

    // Read the file.
    int current_note = 0;
    while (getline(target_file, input))
    {

        // Record the note's properties.
        note_context *note = new note_context;
        note->note_shape = shape;
        note->index = current_note;
        note->frequency = stof(input);
        note->volume = 6000;

        // Write the note.
        write_note(global, note);

        // Delete the note's properties from memory.
        delete (note);
        current_note++;
    }

    // Write the output.
    MakeWavFromVector("challenge.wav", global->sample_rate, *global->waveform);

    // Clean up.
    delete (global->waveform);
    delete (global);
    target_file.close();
}
