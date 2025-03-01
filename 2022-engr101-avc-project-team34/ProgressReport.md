# Team 34 Progress Report
Prepared by team.  
## Roadmap progress as at Monday, May 23, 2022
| Week | Planned progress | Actual progress as of May 22 |
| --   | --    | --        |
| Week 1<br /> May 9 - May 15 | Write and submit project plan<br />Complete core section:<br />- Calculates error using scalar product<br />- Acts on error and moves accordingly | **Core achieved:** <br /> - Robot can reach 'Core' marker.<br />- Scalar product calculations correct. <br />- Project plan submitted. <br />Marked 100%. |
| Week 2<br />May 16 - May 22|Complete completion section:<br />- Can classify intersections<br />- Can turn accordingly | **Completion achieved:**<br /> Robot can classify intersections<br />Robot can reach Completion marker|
| Week 3<br />May 23 - May 29 | Complete challenge section:<br />- Can remember complex routes/previous traversals<br />- Can navigate to challenge marker<br />- Robot movement optimized and clean |**Challenge partially achieved:**<br />Can navigate to challenge marker<br/>Robot movement somewhat optimised and clean |
| Week 4<br />May 30 - June 5 | Complete report<br />Submit all documents |**Week 4 not achieved** |

## Progress analysis and commentary
#### What did/didn't go according to plan?
**Pros:**
- In general, we are ahead of schedule and have achieved the goals we set out to do by May 23.
- The project plan was prepared on time and we got a perfect mark for it.
- Completing 'core' was a quick task as our first attempt at calculating error with no `Kp/Kd` tuning worked.
- Challenge was completed by turning correctly at four-way intersections according to a pre-programmed turning counter.
  
**Cons:**
- Sorting out how to put code into different files was an unexpected and substantial challenge that was resolved by changing the architecture of our solution.
- Classifying intersections turned out to be a very difficult task given how the camera is not always perpendicular to the intersection.
- Our pathfinding Engineer contracted SARS-CoV2 (Covid-19) before development of the junction classification could be completed.
- We found that there were many solutions to the problems in the project.
  - We decided to each approach the problem differently, and have a meeting to decide which method to choose.

#### Bottlenecks and development obstacles
**Logistics:**
- Project Lead (Jackson), Pathfinding Engineer (Ben), and programmer Sam have all contracted COVID-19 around the same time (positive tests Wednesday 18th, Friday 20th, Saturday 21st), which has lead to a performance hit as COVID-19 affects one's ability to work and think.
  - Likely to recover by the end of Week 3 (May 29th). We are ahead of schedule, having already completed parts of Completion (which is due by then), so we are in a comfortable position to bounce back from these infections.
  - Could possibly seek extension or other form of support for other classes, if not for ENGR 101.
- The high number of assignments from ENGR 121/141 and COMP 102/112 continue to be a huge obstacle for our team, as they take a lot of time and effort to complete, and new ones are issued each week.
  - As these are due weekly, we prioritise these first.
  - This remains a time-management pressure for all members of the team.

**Technical:**
- The latency-inducing nature of a reloading web browser window to monitor robot movement slows down the iteration and debugging process.
  - This issue was partially solved by using the following methods:
     - Editing the HTML file to make the window update faster 
     - Using CLion's built-in HTML viewer, which seems to handle file updates better

#### Team recommendation on how to advance project
**Time management:**  
- Continue to eliminate ENGR 121/141 tasks first as they are due every week, and make sure to spend some time working on the project.
- Continue to have weekly meetings to assign roles and check progress.
- Attend ENGR 101 weekly labs to work together and get assistance.
