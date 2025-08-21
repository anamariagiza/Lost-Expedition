package PaooGame.Tiles;

import PaooGame.Graphics.Assets;
import PaooGame.Graphics.SpriteSheet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Tile
{
    public static final Map<Integer, Tile> tiles = new HashMap<>();
    public static final int NO_TILE_GID = 0;

    public static final int GRASS_TILE_GID_SOLID = 82;
    public static final int WALL_TILE_GID_SOLID = 33;
    public static final int[] ROCK_TILE_GIDS = {216, 97, 232, 217, 233, 249, 234, 235, 236, 221};

    public static final int DOOR_CLOSED_TOP_LEFT_GID = 56;
    public static final int DOOR_CLOSED_TOP_RIGHT_GID = 57;
    public static final int DOOR_CLOSED_BOTTOM_LEFT_GID = 88;
    public static final int DOOR_CLOSED_BOTTOM_RIGHT_GID = 89;
    public static final int DOOR_OPEN_TOP_LEFT_GID = 60;
    public static final int DOOR_OPEN_TOP_RIGHT_GID = 61;
    public static final int DOOR_OPEN_BOTTOM_LEFT_GID = 92;
    public static final int DOOR_OPEN_BOTTOM_RIGHT_GID = 93;

    public static final int TILE_WIDTH  = SpriteSheet.getTileWidth();
    public static final int TILE_HEIGHT = SpriteSheet.getTileHeight();

    protected final int id;

    public Tile(int gid)
    {
        this.id = gid;
        tiles.put(gid, this);
    }

    public static void InitTiles() {
        // Această metodă poate rămâne goală, deoarece GetTile creează dalele la nevoie
    }

    public void Update() { }

    public void Draw(Graphics g, int x, int y, int width, int height, BufferedImage tilesetImage) {
        BufferedImage tileImage = Assets.getTileImageByGID(this.id, tilesetImage);
        if (tileImage != null) {
            g.drawImage(tileImage, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }

    public boolean IsSolid() {
        return false;
    }

    public int GetId() {
        return id;
    }

    public static Tile GetTile(int gid) {
        if (gid == NO_TILE_GID) {
            return GetDefaultTile();
        }
        Tile tile = tiles.get(gid);
        if (tile == null) {
            if (gid == GRASS_TILE_GID_SOLID) {
                tile = new GrassTile(gid);
            } else if (gid == WALL_TILE_GID_SOLID || gid == 64) {
                tile = new WallTile(gid);
            } else if (gid == DOOR_CLOSED_TOP_LEFT_GID || gid == DOOR_CLOSED_TOP_RIGHT_GID ||
                    gid == DOOR_CLOSED_BOTTOM_LEFT_GID || gid == DOOR_CLOSED_BOTTOM_RIGHT_GID) {
                tile = new DoorTile(gid, true);
            } else if (gid == DOOR_OPEN_TOP_LEFT_GID || gid == DOOR_OPEN_TOP_RIGHT_GID ||
                    gid == DOOR_OPEN_BOTTOM_LEFT_GID || gid == DOOR_OPEN_BOTTOM_RIGHT_GID) {
                tile = new DoorTile(gid, false);
            }
            // Logica pentru ușa finală de la Nivelul 3
            else if (gid == 70 || gid == 71 || gid == 116 || gid == 117) {
                tile = new DoorTile(gid, true);
            }
            else if (gid == 74 || gid == 75 || gid == 120 || gid == 121) {
                tile = new DoorTile(gid, false);
            }
            else {
                boolean isRock = false;
                for (int rockGid : ROCK_TILE_GIDS) {
                    if (gid == rockGid) {
                        tile = new RockTile(gid);
                        isRock = true;
                        break;
                    }
                }
                if (!isRock) {
                    tile = new Tile(gid) {};
                }
            }
            tiles.put(gid, tile);
        }
        return tile;
    }

    public static Tile GetDefaultTile() {
        return new Tile(NO_TILE_GID) {};
    }
}