# AVC Team 34 Project Plan

**Members**
- Levi Hawkins ([hawkinlevi@ecs.vuw.ac.nz](mailto:hawkinlevi@ecs.vuw.ac.nz))
- Benjamin Park ([parkbenj@ecs.vuw.ac.nz](mailto:parkbenj@ecs.vuw.ac.nz))
- Sam Pennington ([penninsamu@ecs.vuw.ac.nz](mailto:penninsamu@ecs.vuw.ac.nz))
- Jackson Rakena ([rakenajack@ecs.vuw.ac.nz](mailto:rakenajack@ecs.vuw.ac.nz))
- Dev Shamihoke ([shamihdev@ecs.vuw.ac.nz](mailto:shamihdev@ecs.vuw.ac.nz))

**Roles**
- **Project Lead** (Jackson Rakena)  
The project lead manages the team and ensures that deadlines are met, as well as preparing the project plan, progress reports, and any group-work documents.
- **Movement Engineer** (Dev Shamihoke)  
The movement engineer is in charge of making decisions related to the movement of the robot, such as calculating optimal tuning values and picking a strategy to keep the robot moving.
- **Pathfinding Engineer** (Benjamin Park)  
The pathfinding engineer is a high-level role that specializes in keeping the robot on-track and following the maze layout.
- **Programmers** (Sam Pennington, Levi Hawkins)    
Programmers work on the C++ code that powers the robot, as well as performing in-situ testing and evaluation of the robot.


**Communication**  
Communication between group members will be on Discord, a free-to-use social platform, and also in-person, as our group contains no remote members.  
  
**Solution Structure**
- **Navigation & Traversal**  
The navigation section is managed by the **Pathfinding Engineer** and is responsible for identifying and classifying intersections and other junctions, and remembering already traversed paths, to avoid getting the robot in a loop.
  - Pathfinding and navigation will be initally handled with an implementation of (possibly) Tremaux's algorithm to stop the robot from getting stuck forever in a loop.
  - An optimal method to classify junctions will be researched so the robot can effectively discover when it's at an intersection and make the appropriate movement.
    - **Follow a straight line** method will make the robot just follow the line using the **Movement** code for that section
    - **Decision making** method will run at an intersection to tell the robot which direction to turn in and handle the turning.
- **Movement**  
The movement section is managed by the **Movement Engineer** and contains code that optimizes the movement of the robot, tuning `kp` and `kd`.
  - Movement is handled by using the C++ standard library's `inner_product` function and integral calculus to calculate the optimal power to provide to the motors to drive in an appropriate direction.
  - In order to stop the robot from over-correcting for deviation from the line (error), the robot will use principles of derivative and integral calculus (scalar products) to calculate the appropriate speed for the motors.
- **Robot**  
The robot section ties together movement and navigation systems, and checks the progression throughout the maze, as well as other minor bits of code, such as showing the user information about the robot and presenting it.
  - **Image processing** is the general code to open the image and store the values, ready for processing by navigation or movement.

**Structure: UML Diagram**
![image.png](https://i.imgur.com/mj099F5.png)

**Roadmap**
| Week | Goals | Obstacles |
| --   | --    | --        |
| Week 1<br /> May 9 - May 15 | Write and submit project plan<br />Complete core section:<br />- Calculates error using scalar product<br />- Acts on error and moves accordingly |  Project 2 Reflection <br/>ENGR 121 Assignment 5 <br/>COMP 102 Assignment 7 <br />CYBR 171 Lab 3<br />ENGR 141 - Assignment 6<br />ENGR 141 - Lab 3 |
| Week 2<br />May 16 - May 22|Complete completion section:<br />- Can classify intersections<br />- Can turn accordingly|ENGR 121 Lab 3<br />ENGR 121 Assignment 6<br />COMP 102 Assignment 8<br />ENGR 141 - Assignment 7<br />ENGR 141 - Tutorial 2|
| Week 3<br />May 23 - May 29 | Complete challenge section:<br />- Can remember complex routes/previous traversals<br />- Can navigate to challenge marker<br />- Robot movement optimized and clean | COMP 102 Assignment 9<br />ENGR 121 Assignment 7<br />CYBR 171 Assignment 2<br />ENGR 141 - Assignment 8<br />ENGR 141 - Lab 4|
| Week 4<br />May 30 - June 5 | Complete report<br />Submit all documents | COMP 102 Assignment 10<br />CYBR 171 Lab 4<br />ENGR 121 Assignment 8<br />ENGR 121 Lab 4<br />ENGR 141 - Tutorial 5 |


**Deadlines**
| Date | Description | Lead Members |
| --   | --          | --      |
| May 8, 2022 | Project plan template written | Team |
| May 9, 2022 | Group roles distributed | Team |
| May 13, 2022 | Project plan due | Team |
| May 10, 2022 | Complete 'core' section | Programmers |
| May 19, 2022 | Complete 'completion' section | Programmers |
| May 23, 2022 | Progress report due | Lead |
| May 25, 2022 | Complete 'challenge' section | Programmers |
| June 3, 2022 | Code + submission due | Lead |
