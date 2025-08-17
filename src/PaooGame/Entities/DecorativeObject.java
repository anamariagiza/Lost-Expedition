package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.Graphics.ImageLoader;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import PaooGame.States.State;
import PaooGame.States.GameState;
import java.awt.event.KeyEvent;
import java.awt.*;

/*!
 * \class DecorativeObject
 * \brief O clasa pentru entitati decorative, care pot fi optionale solide.
 */
public class DecorativeObject extends Entity {

    private BufferedImage image;
    private boolean solid;
    private String dialogueMessage;

    public DecorativeObject(RefLinks refLink, float x, float y, int width, int height, BufferedImage image, boolean solid) {
        super(refLink, x, y, width, height);
        this.image = image;
        this.solid = solid;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setDialogueMessage(String message) {
        this.dialogueMessage = message;
    }

    @Override
    public void Update() {
        if (refLink.GetPlayer() == null) return;

        // Logica pentru a verifica interacțiunea
        if (this.bounds.intersects(refLink.GetPlayer().GetBounds())) {
            if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_E) && dialogueMessage != null) {

                // Trimitere mesaj către GameState pentru a fi afișat
                if (State.GetState() instanceof GameState) {
                    ((GameState) State.GetState()).showWoodSignMessage(dialogueMessage);
                }
            }
        }
    }

    @Override
    public void Draw(Graphics g) {
        if (image != null) {
            int drawX = (int)((x - refLink.GetGameCamera().getxOffset()));
            int drawY = (int)((y - refLink.GetGameCamera().getyOffset()));
            int scaledWidth = (int)(width);
            int scaledHeight = (int)(height);
            g.drawImage(image, drawX, drawY, scaledWidth, scaledHeight, null);

            // Desenăm pop-up-ul E doar dacă este un panou de lemn și nu se afișează deja un mesaj
            if (image == Assets.woodSignImage && State.GetState() instanceof GameState && !((GameState) State.GetState()).isWoodSignMessageShowing()) {
                drawInteractionPopup(g);
            }
        }
    }

    public String getDialogueMessage() {
        return this.dialogueMessage;
    }
}