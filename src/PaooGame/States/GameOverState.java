package PaooGame.States;

import PaooGame.RefLinks;
import PaooGame.Entities.Player;
import PaooGame.Graphics.Assets;

import java.awt.*;
import java.awt.event.KeyEvent;

/*!
 * \class public class GameOverState extends State
 * \brief Implementeaza notiunea de ecran de Game Over.
 */
public class GameOverState extends State {

    private final Color backgroundColor = new Color(0, 0, 0, 200);
    private final Color textColor = new Color(200, 0, 0);
    private final Color buttonColor = new Color(255, 255, 255);
    private final Color selectedColor = new Color(160, 82, 45);
    private final Font titleFont = new Font("Impact", Font.BOLD, 64);
    private final Font buttonFont = new Font("Papyrus", Font.BOLD, 28);

    private String[] menuOptions = {"TRY AGAIN", "RETURN TO MAIN MENU", "QUIT"};
    private int selectedOption = 0;
    private boolean enterPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    /*!
     * \fn public GameOverState(RefLinks refLink)
     * \brief Constructorul de initializare al clasei GameOverState.
     * \param refLink O referinta catre un obiect "shortcut".
     */
    public GameOverState(RefLinks refLink) {
        super(refLink);
        System.out.println("✓ GameOverState initializat");
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea curenta a ecranului de Game Over.
     */
    @Override
    public void Update() {
        handleInput();
    }

    private void handleInput() {
        if (refLink.GetKeyManager() == null) {
            System.err.println("KeyManager este null in GameOverState!");
            return;
        }

        // Navigare sus
        if (refLink.GetKeyManager().up && !upPressed) {
            upPressed = true;
            selectedOption--;
            if (selectedOption < 0) {
                selectedOption = menuOptions.length - 1;
            }
        } else if (!refLink.GetKeyManager().up) {
            upPressed = false;
        }

        // Navigare jos
        if (refLink.GetKeyManager().down && !downPressed) {
            downPressed = true;
            selectedOption++;
            if (selectedOption >= menuOptions.length) {
                selectedOption = 0;
            }
        } else if (!refLink.GetKeyManager().down) {
            downPressed = false;
        }

        // Selectare optiune (Enter sau Space)
        boolean enterKey = refLink.GetKeyManager().enter;
        boolean spaceKey = refLink.GetKeyManager().space;

        if ((enterKey || spaceKey) && !enterPressed) {
            enterPressed = true;
            executeSelectedOption();
        } else if (!enterKey && !spaceKey) {
            enterPressed = false;
        }
    }

    private void executeSelectedOption() {
        switch (selectedOption) {
            case 0: // TRY AGAIN (reincepe nivelul curent)
                System.out.println("Incercare din nou...");
                refLink.GetPlayer().resetHealth();
                refLink.SetState(new GameState(refLink));
                break;
            case 1: // RETURN TO MAIN MENU
                System.out.println("Revenire la meniul principal...");
                refLink.SetState(new MenuState(refLink));
                break;
            case 2: // QUIT
                System.out.println("Inchidere joc din ecranul Game Over...");
                System.exit(0);
                break;
        }
    }

    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza (randeaza) pe ecran ecranul de Game Over.
     * \param g Contextul grafic in care trebuie sa deseneze starea jocului pe ecran.
     */
    @Override
    public void Draw(Graphics g) {
        if (Assets.backgroundMenu != null) {
            g.drawImage(Assets.backgroundMenu, 0, 0, refLink.GetWidth(), refLink.GetHeight(), null);
        } else {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());
            System.err.println("EROARE DEBUG: Fundal 'Game Over' desenat cu culoare solidă (Assets.backgroundMenu este NULL).");
        }

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());

        g.setColor(textColor);
        g.setFont(titleFont);
        FontMetrics titleFm = g.getFontMetrics();
        String title = "GAME OVER";
        int titleWidth = titleFm.stringWidth(title);
        g.drawString(title, (refLink.GetWidth() - titleWidth) / 2, refLink.GetHeight() / 2 - 120);

        g.setFont(buttonFont);
        FontMetrics buttonFm = g.getFontMetrics();

        int startY = refLink.GetHeight() / 2 - 20;
        int gap = 70;
        int buttonWidth = 450; // NOU: Mărit la 450px pentru a asigura încadrarea textului
        int buttonHeight = 60;

        for (int i = 0; i < menuOptions.length; i++) {
            int x = (refLink.GetWidth() - buttonWidth) / 2;
            int y = startY + i * gap;

            if (i == selectedOption) {
                g.setColor(selectedColor);
                int pulse = (int) (Math.sin(System.currentTimeMillis() * 0.007) * 7);
                g.fillRect(x - pulse, y - buttonHeight / 2 - pulse, buttonWidth + 2 * pulse, buttonHeight + 2 * pulse);
            } else {
                g.setColor(new Color(255, 255, 255, 180));
                g.fillRect(x, y - buttonHeight / 2, buttonWidth, buttonHeight);
            }

            g.setColor(i == selectedOption ? Color.WHITE : textColor);
            int textWidth = buttonFm.stringWidth(menuOptions[i]);
            int textX = x + (buttonWidth - textWidth) / 2;
            int textY = y + buttonFm.getAscent() / 2;
            g.drawString(menuOptions[i], textX, textY);

            g.setColor(textColor);
            g.drawRect(x, y - buttonHeight / 2, buttonWidth, buttonHeight);
        }
    }
}