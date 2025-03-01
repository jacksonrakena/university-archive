import React, { useState } from "react";
import { SketchPicker } from "react-color";

interface Tile {
  tileType: TileType;
  colour?: number;
  x: number;
  y: number;
}
const types = [
  "ExitLockTile",
  "ExitTile",
  "FreeTile",
  "InfoTile",
  "KeyTile",
  "LockDoorTile",
  "TreasureTile",
  "WallTile",
];
export type TileType = (typeof types)[number];
function App() {
  const [level, setLevel] = useState<Tile[][]>([
    [{ tileType: "FreeTile", x: 0, y: 0 }],
  ]);
  const [editor, setEditor] = useState("");
  const [selectedBrush, setSelectedBrush] = useState<TileType>("FreeTile");
  const [color, setColor] = useState("#000000");
  const [selectedTile, setSelectedTile] = useState<{
    x: number;
    y: number;
  } | null>(null);
  return (
    <div style={{ all: "unset" }}>
      <div>
        Size: {level[0].length}x{level.length}
      </div>
      <div>
        <SketchPicker
          color={color}
          onChangeComplete={(c) => {
            setColor(c.hex);
            console.log(c.hex);
          }}
        />
      </div>
      <div>
        <select
          onChange={(d) => {
            setSelectedBrush(d.target.value);
          }}
          value={selectedBrush}
        >
          {types.map((t) => (
            <option key={t}>{t}</option>
          ))}
        </select>
      </div>
      <table>
        {level.map((row) => (
          <tr>
            {row.map((cell) => (
              <td
                style={{
                  backgroundColor: (() => {
                    if (!cell.colour) return "unset";
                    return "#" + cell.colour?.toString(16);
                  })(),
                }}
                onClick={() => {
                  setLevel((l) => {
                    let n = l.map((d) => d.map((e) => e));
                    n[cell?.y!][cell?.x!].tileType = selectedBrush;
                    n[cell?.y!][cell?.x!].colour = parseInt(
                      color.replace("#", ""),
                      16
                    );
                    return n;
                  });
                }}
              >
                <img width={"30px"} src={"/tiles/" + cell.tileType + ".png"} />
              </td>
            ))}
          </tr>
        ))}
      </table>
      <button
        onClick={() => {
          setLevel((l) =>
            l.map((d) => [
              ...d,
              { tileType: "FreeTile", y: d[0].y, x: d.slice(-1)[0].x + 1 },
            ])
          );
        }}
      >
        Add column
      </button>
      <button
        onClick={() => {
          setLevel((l) => [
            ...l,
            ...l.slice(-1).map((e) => e.map((f) => ({ ...f, y: e[0].y + 1 })))!,
          ]);
        }}
      >
        Add row
      </button>
      <br />
      <code>
        {JSON.stringify({
          mapId: "level1",
          position: { x: 5, y: 5 },
          tiles: level,
        })}
      </code>
      <br />
      <textarea
        onChange={(e) => {
          setEditor(e.currentTarget.value);
        }}
        rows={50}
        cols={50}
      >
        {editor}
      </textarea>

      <button
        onClick={() => {
          setEditor(JSON.stringify(level));
        }}
      >
        Export
      </button>
      <button
        onClick={() => {
          setLevel(JSON.parse(editor).tiles);
        }}
      >
        Load
      </button>
    </div>
  );
}

export default App;
