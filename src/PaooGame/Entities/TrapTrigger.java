package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Tiles.Tile;
import java.awt.Graphics;
import java.awt.Rectangle;

public class TrapTrigger extends Entity {

    public TrapTrigger(RefLinks refLink, float x, float y, int width, int height) {
        super(refLink, x, y, width, height);
        this.bounds = new Rectangle((int) x, (int) y, width, height);
    }

    @Override
    public void Update() {
        // Nicio logica de daune aici. Doar detecteaza coliziunea in GameState.
    }

    @Override
    public void Draw(Graphics g) {
        // Obiectul este invizibil.
    }
}