| Module      | Team Member Name  | GitLab username |
|-------------|-------------------|-----------------|
| Domain      | Jackson Rakena    | rakenajack      |
| App         | Rene Sharma       | sharmarene      |
| Renderer    | Kahurangi Burkitt | burkitkahu      |
| Persistency | Tyff Habwe        | habwetyff       |
| Recorder    | Lachie Sim        | simlach         |


## Game plan
### Domain
- Model + controller - state, property changes, game logic
- Preconditions, postconditions, state checks
- Background agents
### App
- Handling user input
- Ensuring Renderer module is activated and initialised
- Integrating App and Domain together
- Handling closing of app
### Renderer
- Listens to Domain model and calls Domain controller when things change
- Animations, transitions, sounds
### Persistency
- Reading and writing Domain models (`domain.world.level.Map`) to and from JSON files
### Recorder
- Record games, integrate with Renderer to allow replay 