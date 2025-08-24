package PaooGame.States;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;

import java.awt.*;
import java.awt.Rectangle;

/**
 * @class PauseState
 * @brief Implementeaza notiunea de meniu de pauza.
 * Aceasta stare este activata atunci cand jucatorul apasa tasta de pauza (P)
 * in timpul jocului (in GameState). Opreste temporar actiunea si afiseaza
 * o serie de optiuni, cum ar fi reluarea jocului, accesarea setarilor,
 * salvarea si iesirea.
 */
public class PauseState extends State {

    /** Atribute finale pentru stilizarea vizuala a ecranului.*/
    private final Color backgroundColor = new Color(0, 0, 0, 150);
    private final Color buttonColor = new Color(255, 255, 255);
    private final Color textColor = new Color(175, 146, 0);
    private final Color selectedColor = new Color(160, 82, 45);
    private final Font titleFont = new Font("Papyrus", Font.BOLD, 36);
    private final Font buttonFont = new Font("Papyrus", Font.BOLD, 24);

    /** Atribute pentru gestionarea meniului si a optiunilor sale.*/
    private String[] menuOptions = {"RESUME", "SETTINGS", "HELP", "RETURN TO MAIN MENU", "SAVE AND QUIT"};
    private Rectangle[] buttonBounds;
    private int selectedOption = 0;
    /** Flag-uri pentru a gestiona o singura apasare a tastelor de navigare si selectie.*/
    private boolean enterPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean escapePressed = false;

    /** Stocheaza ultimele dimensiuni ale ferestrei pentru a detecta redimensionarea.*/
    private int lastWidth, lastHeight;
    /** Stocheaza starea de joc anterioara pentru a o putea relua. Deprecated in favoarea refLink.getPersistedGameState().*/
    private State gameToBeResumed;


    /**
     * @brief Constructorul clasei PauseState.
     * @param refLink O referinta catre obiectul RefLinks.
     */
    public PauseState(RefLinks refLink) {
        super(refLink);
        System.out.println("PauseState initializat");
        refLink.GetKeyManager().clearKeys();

        lastWidth = refLink.GetWidth();
        lastHeight = refLink.GetHeight();
        setupButtons();
    }

    /**
     * @brief Actualizeaza starea meniului de pauza in fiecare cadru.
     */
    @Override
    public void Update() {
        if (refLink.GetWidth() != lastWidth || refLink.GetHeight() != lastHeight) {
            setupButtons();
            lastWidth = refLink.GetWidth();
            lastHeight = refLink.GetHeight();
        }

        handleInput();
        handleMouseInput();
    }

    /**
     * @brief Deseneaza (randeaza) continutul meniului de pauza.
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

        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());
        // Desenarea titlului
        g.setColor(textColor);
        g.setFont(titleFont);
        FontMetrics titleFm = g.getFontMetrics();
        String title = "PAUSED";
        int titleWidth = titleFm.stringWidth(title);
        g.drawString(title, (refLink.GetWidth() - titleWidth) / 2, refLink.GetHeight() / 2 - 150);
        // Desenarea optiunilor de meniu
        g.setFont(buttonFont);
        FontMetrics buttonFm = g.getFontMetrics();
        int startY = refLink.GetHeight() / 2 - 50;
        int gap = 60;
        int buttonWidth = 400;
        int buttonHeight = 50;
        for (int i = 0; i < menuOptions.length; i++) {
            int x = (refLink.GetWidth() - buttonWidth) / 2;
            int y = startY + i * gap;

            if (i == selectedOption) {
                g.setColor(selectedColor);
                int pulse = (int) (Math.sin(System.currentTimeMillis() * 0.005) * 5);
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
     * @brief Calculeaza pozitiile si dimensiunile butoanelor din meniu.
     */
    private void setupButtons() {
        buttonBounds = new Rectangle[menuOptions.length];
        int startY = refLink.GetHeight() / 2 - 50;
        int gap = 60;
        int buttonWidth = 350;
        int buttonHeight = 50;
        for (int i = 0; i < menuOptions.length; i++) {
            int x = (refLink.GetWidth() - buttonWidth) / 2;
            int y = startY + i * gap;
            buttonBounds[i] = new Rectangle(x, y - buttonHeight / 2, buttonWidth, buttonHeight);
        }
    }

    /**
     * @brief Gestioneaza input-ul de la tastatura pentru navigarea in meniu.
     */
    private void handleInput() {
        if (refLink.GetKeyManager() == null) {
            System.err.println("KeyManager este null in PauseState!");
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

        // Iesire din pauza cu ESC
        if (refLink.GetKeyManager().escape && !escapePressed) {
            escapePressed = true;
            refLink.SetState(refLink.GetPreviousState());
        } else if (!refLink.GetKeyManager().escape) {
            escapePressed = false;
        }
    }

    /**
     * @brief Gestioneaza input-ul de la mouse pentru selectarea optiunilor.
     */
    private void handleMouseInput() {
        if (refLink.GetMouseManager() == null || buttonBounds == null) return;
        for (int i = 0; i < buttonBounds.length; i++) {
            if (buttonBounds[i].contains(refLink.GetMouseManager().getMouseX(), refLink.GetMouseManager().getMouseY())) {
                selectedOption = i;
                if (refLink.GetMouseManager().isMouseJustClicked()) {
                    executeSelectedOption();
                }
                break;
            }
        }
    }

    /**
     * @brief Executa actiunea corespunzatoare optiunii de meniu selectate.
     */
    private void executeSelectedOption() {
        switch (selectedOption) {
            case 0: // RESUME
                // Folosim metoda corecta pentru a relua jocul
                refLink.SetState(refLink.getPersistedGameState());
                break;
            case 1: // SETTINGS
                refLink.SetState(new SettingsState(refLink));
                break;
            case 2: // HELP
                refLink.SetState(new HelpState(refLink));
                break;
            case 3: // RETURN TO MAIN MENU
                refLink.SetState(new MenuState(refLink));
                break;
            case 4: // SAVE AND QUIT
                // Folosim metoda corecta pentru a obtine starea jocului ce trebuie salvata
                GameState gameStateToSave = refLink.getPersistedGameState();
                if (gameStateToSave != null) {
                    gameStateToSave.saveCurrentState();
                    System.out.println("Progresul jocului a fost salvat. Se inchide aplicatia...");
                } else {
                    System.out.println("Eroare critica: Nu s-a putut gasi starea jocului pentru a o salva.");
                }
                System.exit(0);
                break;
        }
    }
}