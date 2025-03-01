class GameMap {
 //ArrayList<Wall> walls = new ArrayList<Wall>();
 int cellWidth;
 int cellHeight;
 ArrayList<MapMarker> markers = new ArrayList<MapMarker>();
}

class MapMarker {
  float x;
  float y;
  float height;
  float width;
  String id;
  String data;
  
  MapMarker(float x, float y, float width, float height, String id, String data) {
    this.x = x;
    this.y = y;
    this.height = height;
    this.width = width;
    this.id = id;
    this.data = data;
  }
}

GameMap readMap(String path) {
  Table t = loadTable(path, "csv");
 
  GameMap m = new GameMap();
  m.cellWidth = height / t.getColumnCount();
  m.cellHeight = width / t.getRowCount();
  println("Loaded " + t.getRowCount() + "x" + t.getColumnCount() +" map from " + path);
  for (int r = 0; r < t.getRowCount(); r++) {
    TableRow row = t.getRow(r);
    for (int c = 0; c < row.getColumnCount(); c++) {
      String cell = row.getString(c);
      if (!cell.equals("")) {
        String[] components = cell.split(":");
        String type = components[0];
        String data = components[1];
        m.markers.add(new MapMarker(c*m.cellWidth, r*m.cellHeight, m.cellWidth, m.cellHeight, type, data));
          //case "w":
            //if (cw == null) {
            //  cw = new Wall(c*m.cellWidth,r*m.cellHeight, m.cellWidth, m.cellHeight, new DefaultWallTexture());
            //} else {
            //  cw.dimensions.add(m.cellWidth,0);
            //}
            //if (wStartCol == -1) wStartCol = c;
            //m.walls.add(new Wall(c*m.cellWidth,r*m.cellHeight, m.cellWidth, m.cellHeight, new DefaultWallTexture()));
            //break;

        //if (!type.equals("w") && cw != null) {
          //m.walls.add(cw);
          //cw = null;
        //}
      }
    }
  }
  return m;
}
