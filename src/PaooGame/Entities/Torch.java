package PaooGame.Entities;

import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.RefLinks;
import PaooGame.Tiles.Tile;
import java.awt.Graphics;

public class Torch extends Entity {
    private Animation anim;

    public Torch(RefLinks refLink, float x, float y) {
        super(refLink, x, y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
        this.anim = new Animation(150, Assets.torchAnim); // 150 ms/cadru
    }

    @Override
    public void Update() {
        if (anim != null) {
            anim.Update();
        }
    }

    @Override
    public void Draw(Graphics g) {
        if (anim != null && anim.getCurrentFrame() != null) {
            int drawX = (int)((x - refLink.GetGameCamera().getxOffset()) * refLink.GetGameCamera().getZoomLevel());
            int drawY = (int)((y - refLink.GetGameCamera().getyOffset()) * refLink.GetGameCamera().getZoomLevel());
            int scaledWidth = (int)(width * refLink.GetGameCamera().getZoomLevel());
            int scaledHeight = (int)(height * refLink.GetGameCamera().getZoomLevel());
            g.drawImage(anim.getCurrentFrame(), drawX, drawY, scaledWidth, scaledHeight, null);
        }
    }
}