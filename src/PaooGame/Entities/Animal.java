package PaooGame.Entities;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;
import PaooGame.Graphics.Animation;
import PaooGame.Tiles.Tile;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @class Animal
 * @brief Implementeaza un tip de inamic simplu: un animal salbatic.
 * Aceasta clasa extinde Entity si defineste un inamic care se misca
 * pe o traiectorie orizontala predefinita (patrulare). Provoaca daune
 * jucatorului la contact. Clasa poate reprezenta diferite tipuri de animale
 * (JAGUAR, MONKEY, BAT), fiecare cu propriile atribute.
 */
public class Animal extends Entity {

    /** @enum AnimalType @brief Defineste tipurile de animale care pot fi create.*/
    public enum AnimalType {
        JAGUAR,
        MONKEY,
        BAT
    }

    /** Atributele specifice ale unui animal, incluzand animatia, viteza si parametrii de patrulare.*/
    private final Animation anim;
    private final float speed;
    private final float patrolStartX;
    private final float patrolEndX;
    private boolean movingRight = true;
    private final int damage;

    /**
     * @brief Constructorul clasei Animal.
     * @param refLink Referinta catre obiectul RefLinks.
     * @param x Coordonata X initiala a animalului.
     * @param y Coordonata Y initiala a animalului.
     * @param patrolStartX Limita stanga a patrularii (in pixeli).
     * @param patrolEndX Limita dreapta a patrularii (in pixeli).
     * @param type Tipul animalului de creat, din enum-ul AnimalType.
     */
    public Animal(RefLinks refLink, float x, float y, float patrolStartX, float patrolEndX, AnimalType type) {
        super(refLink, x, y, getAnimalWidth(type), getAnimalHeight(type));
        this.patrolStartX = patrolStartX;
        this.patrolEndX = patrolEndX;
        this.speed = getAnimalSpeed(type);

        SetPosition(x, y);
        this.damage = getAnimalDamage(type);

        BufferedImage[] frames = switch (type) {
            case JAGUAR -> Assets.jaguarWalkAnim;
            case MONKEY -> Assets.monkeyWalkAnim;
            case BAT -> Assets.batAnim;
        };

        if (frames != null && frames.length > 0) {
            anim = new Animation(150, frames);
        } else {
            //System.err.println("DEBUG Animal: Animatia pentru " + type + " este null sau goala. Desenam placeholder.");
            anim = new Animation(100, new BufferedImage[]{new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)});
        }
    }

    /**
     * @brief Metode statice ajutatoare (helper) care functioneaza ca o fabrica de configuratii.
     * Returneaza atributele specifice (latime, inaltime, viteza, daune) pentru un anumit tip de animal.
     * @param type Tipul de animal pentru care se cer atributele.
     * @return Valoarea atributului corespunzator.
     */
    private static int getAnimalWidth(AnimalType type) {
        return switch (type) {
            case JAGUAR -> Assets.JAGUAR_FRAME_WIDTH;
            case MONKEY -> Assets.MONKEY_FRAME_WIDTH;
            case BAT -> Assets.BAT_FRAME_WIDTH;
            default -> Tile.TILE_WIDTH;
        };
    }

    private static int getAnimalHeight(AnimalType type) {
        return switch (type) {
            case JAGUAR -> Assets.JAGUAR_FRAME_HEIGHT;
            case MONKEY -> Assets.MONKEY_FRAME_HEIGHT;
            case BAT -> Assets.BAT_FRAME_HEIGHT;
            default -> Tile.TILE_HEIGHT;
        };
    }

    private static float getAnimalSpeed(AnimalType type) {
        return switch (type) {
            case JAGUAR -> 1.5f;
            case MONKEY -> 1.0f;
            case BAT -> 2.0f;
            default -> 1.0f;
        };
    }

    private static int getAnimalDamage(AnimalType type) {
        return switch (type) {
            case JAGUAR -> 50;
            case MONKEY -> 30;
            case BAT -> 20;
            default -> 0;
        };
    }

    /**
     * @brief Actualizeaza starea animalului in fiecare cadru.
     * Actualizeaza animatia si logica de miscare.
     */
    @Override
    public void Update() {
        anim.Update();
        moveAnimal();
    }

    /**
     * @brief Implementeaza logica de miscare a animalului (patrulare orizontala).
     * Animalul se misca intre punctele patrolStartX si patrolEndX. Cand atinge
     * o limita, isi schimba directia de miscare.
     */
    private void moveAnimal() {
        float moveX;
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

    /**
     * @brief Deseneaza animalul pe ecran.
     * Include logica de a oglindi orizontal imaginea in functie de directia
     * de miscare, pentru a crea un efect natural.
     * @param g Contextul grafic in care se va desena.
     */
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

    /**
     * @brief Returneaza daunele pe care le provoaca animalul la contact.
     * @return Valoarea daunelor.
     */
    public int getDamage() {
        return damage;
    }
}