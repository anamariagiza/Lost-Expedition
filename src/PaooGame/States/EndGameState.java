package PaooGame.States;

import PaooGame.Graphics.Assets;
import PaooGame.RefLinks;
import java.awt.*;

/**
 * @class EndGameState
 * @brief Implementeaza starea de final de joc (ecranul de victorie).
 * Aceasta stare este activata atunci cand jucatorul termina jocul cu succes.
 * Afiseaza un mesaj de felicitare si ofera optiuni pentru a reveni la meniul
 * principal sau pentru a parasi jocul.
 */
public class EndGameState extends State {

    /** Atribute finale pentru stilizarea vizuala a ecranului.*/
    private final Color backgroundColor = new Color(0, 0, 0, 200);
    private final Color textColor = new Color(255, 215, 0);
    private final Color buttonColor = new Color(255, 255, 255);
    private final Color selectedColor = new Color(160, 82, 45);
    private final Font titleFont = new Font("Impact", Font.BOLD, 64);
    private final Font textFont = new Font("Papyrus", Font.BOLD, 28);
    /** Atribute pentru gestionarea meniului si a optiunilor sale.*/
    private final String[] menuOptions = {"RETURN TO MAIN MENU", "QUIT"};
    private int selectedOption = 0;
    /** Flag-uri pentru a gestiona o singura apasare a tastelor de navigare si selectie.*/
    private boolean enterPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    /**
     * @brief Constructorul clasei EndGameState.
     * @param refLink O referinta catre obiectul RefLinks.
     */
    public EndGameState(RefLinks refLink) {
        super(refLink);
        System.out.println("✓ EndGameState initializat");
    }

    /**
     * @brief Actualizeaza starea ecranului de final de joc.
     */
    @Override
    public void Update() {
        handleInput();
    }

    /**
     * @brief Deseneaza (randeaza) continutul ecranului de final de joc.
     * @param g Contextul grafic in care se va desena.
     */
    @Override
    public void Draw(Graphics g) {
        if (Assets.backgroundMenu != null) {
            g.drawImage(Assets.backgroundMenu, 0, 0, refLink.GetWidth(), refLink.GetHeight(), null);
        } else {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());
        }

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());

        g.setColor(textColor);
        g.setFont(titleFont);
        FontMetrics titleFm = g.getFontMetrics();
        String title = "FELICITARI, AVENTURIERULE!";
        int titleWidth = titleFm.stringWidth(title);
        g.drawString(title, (refLink.GetWidth() - titleWidth) / 2, refLink.GetHeight() / 2 - 120);

        g.setFont(textFont);
        FontMetrics buttonFm = g.getFontMetrics();
        int startY = refLink.GetHeight() / 2 - 20;
        int gap = 70;
        int buttonWidth = 450;
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

    /**
     * @brief Gestioneaza input-ul de la tastatura pentru navigarea in meniu.
     */
    private void handleInput() {
        if (refLink.GetKeyManager() == null) {
            return;
        }

        if (refLink.GetKeyManager().up && !upPressed) {
            upPressed = true;
            selectedOption--;
            if (selectedOption < 0) {
                selectedOption = menuOptions.length - 1;
            }
        } else if (!refLink.GetKeyManager().up) {
            upPressed = false;
        }

        if (refLink.GetKeyManager().down && !downPressed) {
            downPressed = true;
            selectedOption++;
            if (selectedOption >= menuOptions.length) {
                selectedOption = 0;
            }
        } else if (!refLink.GetKeyManager().down) {
            downPressed = false;
        }

        boolean enterKey = refLink.GetKeyManager().enter;
        boolean spaceKey = refLink.GetKeyManager().space;

        if ((enterKey || spaceKey) && !enterPressed) {
            enterPressed = true;
            executeSelectedOption();
        } else if (!enterKey && !spaceKey) {
            enterPressed = false;
        }
    }

    /**
     * @brief Executa actiunea corespunzatoare optiunii de meniu selectate.
     */
    private void executeSelectedOption() {
        switch (selectedOption) {
            case 0:
                System.out.println("Revenire la meniul principal...");
                refLink.SetState(new MenuState(refLink));
                break;
            case 1:
                System.out.println("Inchidere joc...");
                System.exit(0);
                break;
        }
    }
}