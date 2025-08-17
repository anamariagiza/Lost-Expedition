package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.Tiles.Tile;
import java.awt.*;
import java.awt.image.BufferedImage;

/*!
 * \class public class Animal extends Entity
 * \brief Implementeaza un tip de inamic simplu: un animal salbatic.
 * Se misca pe o traiectorie predefinita si provoaca daune periodice jucatorului la contact.
 */
public class Animal extends Entity {

    public enum AnimalType {
        JAGUAR,
        MONKEY,
        BAT
    }

    private AnimalType type;
    private Animation anim;
    private float speed;
    private float patrolStartX, patrolEndX;
    private boolean movingRight = true;
    private float moveX;
    private int damage;

    /*!
     * \fn public Animal(RefLinks refLink, float x, float y, float patrolStartX, float patrolEndX, AnimalType type)
     * \brief Constructorul de initializare al clasei Animal cu specificarea tipului.
     * \param refLink Referinta catre obiectul RefLinks.
     * \param x Coordonata X initiala.
     * \param y Coordonata Y initiala.
     * \param patrolStartX Limita de start a patrularii pe axa X.
     * \param patrolEndX Limita de final a patrularii pe axa X.
     * \param type Tipul animalului (JAGUAR, MONKEY, BAT).
     */
    public Animal(RefLinks refLink, float x, float y, float patrolStartX, float patrolEndX, AnimalType type) {
        super(refLink, x, y, getAnimalWidth(type), getAnimalHeight(type));
        this.type = type;
        this.patrolStartX = patrolStartX;
        this.patrolEndX = patrolEndX;
        this.speed = getAnimalSpeed(type);

        SetPosition(x, y);
        this.damage = getAnimalDamage(type);

        BufferedImage[] frames = null;
        switch (type) {
            case JAGUAR:
                frames = Assets.jaguarWalkAnim;
                break;
            case MONKEY:
                frames = Assets.monkeyWalkAnim;
                break;
            case BAT:
                frames = Assets.batAnim;
                break;
        }

        if (frames != null && frames.length > 0) {
            anim = new Animation(150, frames);
        } else {
            System.err.println("DEBUG Animal: Animatia pentru " + type + " este null sau goala. Desenam placeholder.");
            anim = new Animation(100, new BufferedImage[]{new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)});
        }
    }

    private static int getAnimalWidth(AnimalType type) {
        switch (type) {
            case JAGUAR: return Assets.JAGUAR_FRAME_WIDTH;
            case MONKEY: return Assets.MONKEY_FRAME_WIDTH;
            case BAT:    return Assets.BAT_FRAME_WIDTH;
            default:     return Tile.TILE_WIDTH;
        }
    }

    private static int getAnimalHeight(AnimalType type) {
        switch (type) {
            case JAGUAR: return Assets.JAGUAR_FRAME_HEIGHT;
            case MONKEY: return Assets.MONKEY_FRAME_HEIGHT;
            case BAT:    return Assets.BAT_FRAME_HEIGHT;
            default:     return Tile.TILE_HEIGHT;
        }
    }

    private static float getAnimalSpeed(AnimalType type) {
        switch (type) {
            case JAGUAR: return 1.5f;
            case MONKEY: return 1.0f;
            case BAT:    return 2.0f;
            default:     return 1.0f;
        }
    }

    private static int getAnimalDamage(AnimalType type) {
        switch (type) {
            case JAGUAR: return 50;
            case MONKEY: return 30;
            case BAT:    return 20;
            default:     return 0;
        }
    }

    @Override
    public void Update() {
        anim.Update();
        moveAnimal();
    }

    private void moveAnimal() {
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
        bounds.setLocation((int)x, (int)y);
    }

    @Override
    public void Draw(Graphics g) {
        int drawX = (int)((x - refLink.GetGameCamera().getxOffset()));
        int drawY = (int)((y - refLink.GetGameCamera().getyOffset()));
        int scaledWidth = (int)(width);
        int scaledHeight = (int)(height);
        BufferedImage currentFrame = anim.getCurrentFrame();
        if (currentFrame != null) {
            if (!movingRight) {
                g.drawImage(currentFrame, drawX, drawY, scaledWidth, scaledHeight, null);
            } else {
                g.drawImage(currentFrame, drawX + scaledWidth, drawY, -scaledWidth, scaledHeight, null);
            }
        } else {
            g.setColor(Color.MAGENTA);
            g.fillRect(drawX, drawY, scaledWidth, scaledHeight);
        }
    }

    public int getDamage() {
        return damage;
    }
}