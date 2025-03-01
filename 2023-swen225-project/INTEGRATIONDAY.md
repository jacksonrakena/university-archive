# Breakpoints

- Domain -> Renderer
  - Shows the renderer listening to model changes: `nz/ac/wgtn/swen225/lc/renderer/MainWindow.java:49`
  - Shows the domain updating the model and alerting listeners: `nz/ac/wgtn/swen225/lc/domain/state/GameModel.java:203`
- Domain -> Persistency
  - Shows the model requesting the first level from the persistency manager: `nz/ac/wgtn/swen225/lc/domain/state/GameModel.java:85`
- Domain -> Recorder
  - Shows the recorder listening to model changes: `nz/ac/wgtn/swen225/lc/recorder/Recorder.java:52`
  - The domain telling all listeners (incl. Recorder) that the player has moved: `nz/ac/wgtn/swen225/lc/domain/state/GameModel.java:70`
- App -> Domain
  - nz/ac/wgtn/swen225/lc/app/input/GameKeyListener.java:59
  - Line 59 through 62
- App -> Renderer
  - Currently buggy, but the Renderer initialises the app window: `nz/ac/wgtn/swen225/lc/renderer/MainWindow.java:38`
