package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.States.GameState;
import PaooGame.States.State;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class LevelExit extends Entity {

    public LevelExit(RefLinks refLink, float x, float y, int width, int height) {
        super(refLink, x, y, width, height);
    }

    @Override
    public void Update() {
        Player player = refLink.GetPlayer();
        if (player != null && this.bounds.intersects(player.GetBounds())) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E)) {
                State currentState = State.GetState();
                if (currentState instanceof GameState) {
                    ((GameState) currentState).passToLevel3();
                }
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        // Acest obiect este invizibil. Poti desena un chenar pentru debug.
        // g.setColor(Color.BLUE);
        // g.drawRect((int)x, (int)y, width, height);
    }
}