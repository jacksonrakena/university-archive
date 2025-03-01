# Linear Congruential Generator Toolkit
Simulator and plotter for linear congruential generator (LCG) functions in Python 3 with MatPlotLib.  

This was made for academic purposes, to study the function of linear congruential generations and specifically the famed RANDU LCG function.


### Usage
To run as the generator is currently set, just run `python lcg.py` (`python3 lcg.py` on Mac).  
This will simulate `RANGE_END-RANGE_START` generations and plot them with a MatPlotLib display.  
To change the LCG parameters, set them in the top of `lcg.py`:
```py
# SEED VALUE
# RANDOM NUMBER = randint(1, 0x7FFFFFFF)
X0 = randint(1, 0x7FFFFFFF)

RANGE_START = 0
RANGE_END = 500

# MULTIPLICATIVE FACTOR
A = 1664525

# ADDITIVE FACTOR
C = 1013904223

# MODULUS FACTOR
M = 2**32
```
The LCG function is expressed as `X(i+1) = ((A*X(i)) + C) mod M`.

### Copyright
`lcg-toolkit` is copyright &copy; 2021 Jackson Rakena under the MIT License.  
You are free to use this in your academic papers or projects, but please include a credit and link to this repository.
