package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.Tiles.Tile;
import java.awt.*;
import java.awt.image.BufferedImage;

/*!
 * \class public class Agent extends Entity
 * \brief Implementeaza antagonistul principal al jocului, Agentul lui Magnus Voss.
 * Acesta este un inamic cu o prezenta vizuala si un comportament de baza.
 */
public class Agent extends Entity {

    private static final int DEFAULT_AGENT_WIDTH = 64;
    private static final int DEFAULT_AGENT_HEIGHT = 64;
    private static final float DEFAULT_SPEED = 0.8f;

    private Animation anim;
    private float moveX, moveY;
    private float speed;

    private float patrolStartX, patrolEndX;
    private boolean movingRight = true;
    private boolean isPatrolling = false;

    private int damage = 30;

    /*!
     * \fn public Agent(RefLinks refLink, float x, float y, float patrolStartX, float patrolEndX, boolean isPatrolling)
     * \brief Constructorul de initializare al clasei Agent.
     * \param refLink Referinta catre obiectul RefLinks.
     * \param x Coordonata X initiala.
     * \param y Coordonata Y initiala.
     * \param patrolStartX Limita de start a patrularii pe axa X (pixeli).
     * \param patrolEndX Limita de final a patrularii pe axa X (pixeli).
     * \param isPatrolling Daca este true, agentul va patrula, altfel va sta pe loc.
     */
    public Agent(RefLinks refLink, float x, float y, float patrolStartX, float patrolEndX, boolean isPatrolling) {
        super(refLink, x, y, DEFAULT_AGENT_WIDTH, DEFAULT_AGENT_HEIGHT);
        this.speed = DEFAULT_SPEED;
        this.patrolStartX = patrolStartX;
        this.patrolEndX = patrolEndX;
        this.isPatrolling = isPatrolling;

        this.bounds = new Rectangle(0, 0, width, height);

        if (Assets.playerIdleDown != null && Assets.playerIdleDown.length > 0) {
            anim = new Animation(200, Assets.playerIdleDown);
        } else {
            System.err.println("DEBUG Agent: Animatia placeholder pentru Agent este null. Desenam placeholder magenta.");
            anim = new Animation(100, new BufferedImage[]{new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)});
        }
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea agentului (miscarea, interacțiunea).
     */
    @Override
    public void Update() {
        anim.Update();
        if (isPatrolling) {
            moveAgent();
        }
        checkPlayerCollision();
    }

    /*!
     * \fn private void moveAgent()
     * \brief Implementeaza logica de miscare a agentului (patrulare orizontala).
     */
    private void moveAgent() {
        if (movingRight) {
            moveX = speed;
            if (x + moveX + width >= patrolEndX) {
                movingRight = false;
            }
        } else {
            moveX = -speed;
            if (x + moveX <= patrolStartX) {
                movingRight = true;
            }
        }
        x += moveX;
        bounds.x = (int) x;
    }

    /*!
     * \fn private void checkPlayerCollision()
     * \brief Verifica coliziunea cu jucatorul si aplica daune.
     */
    private void checkPlayerCollision() {
        Player player = refLink.GetPlayer();
        if (player == null) return;

        if (this.bounds.intersects(player.GetBounds())) {
            System.out.println("DEBUG Agent: Coliziune cu jucatorul!");
            player.takeDamage(damage);
        }
    }


    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza agentul pe ecran.
     * \param g Contextul grafic.
     */
    @Override
    public void Draw(Graphics g) {
        int drawX = (int)((x - refLink.GetGameCamera().getxOffset()) * refLink.GetGameCamera().getZoomLevel());
        int drawY = (int)((y - refLink.GetGameCamera().getyOffset()) * refLink.GetGameCamera().getZoomLevel());
        int scaledWidth = (int)(width * refLink.GetGameCamera().getZoomLevel());
        int scaledHeight = (int)(height * refLink.GetGameCamera().getZoomLevel());

        BufferedImage currentFrame = anim.getCurrentFrame();
        if (currentFrame != null) {
            if (!movingRight) {
                g.drawImage(currentFrame, drawX + scaledWidth, drawY, -scaledWidth, scaledHeight, null);
            } else {
                g.drawImage(currentFrame, drawX, drawY, scaledWidth, scaledHeight, null);
            }
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(drawX, drawY, scaledWidth, scaledHeight);
        }
    }
}