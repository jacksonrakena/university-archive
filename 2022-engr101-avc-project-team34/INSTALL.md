# Team 34: Installation Guide

This repository is always in a state where it is ready to test the entire map.

1) Clone the repository.  
  
In a console, run:  
For HTTPS:
```bash
git clone https://gitlab.ecs.vuw.ac.nz/course-work/engr101/2022/project3/t34/avc-code.git
```
or SSH:
```bash
git clone git@gitlab.ecs.vuw.ac.nz:course-work/engr101/2022/project3/t34/avc-code.git
```

2) Compile `main.cpp` with GCC:
```bash
cd avc-code
g++ main.cpp -o main
```

3) Open the robot monitor.
You can open in your browser either `display.html` (which combines `display_scene.html` and `display_camera.html`), or the individual files.


4) Run `main`.
```bash
./main
```
