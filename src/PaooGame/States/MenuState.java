package PaooGame.States;

import PaooGame.Graphics.Assets;
import PaooGame.RefLinks;
import java.awt.*;
import java.awt.event.KeyEvent;

/*!
 * \class public class MenuState extends State
 * \brief Implementeaza notiunea de menu pentru joc cu functionalitate completa.
 */
public class MenuState extends State
{
    private final Color backgroundColor = new Color(0, 0, 0);
    private final Color buttonColor = new Color(255, 255, 255);
    private final Color textColor = new Color(175, 146, 0);
    private final Color selectedColor = new Color(160, 82, 45);
    private final Color disabledColor = new Color(100, 100, 100);
    private final Font titleFont = new Font("Papyrus", Font.BOLD, 36);
    private final Font buttonFont = new Font("Papyrus", Font.BOLD, 18);

    private String[] menuOptions = {"NEW GAME", "LOAD GAME", "SETTINGS", "ABOUT", "QUIT"};
    private int selectedOption = 0;
    private boolean enterPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    private long stateEnterTime;
    private final long INPUT_COOLDOWN_MS = 200;
    private long lastDebugTime = 0;

    private boolean saveGameExists;

    /*!
     * \fn public MenuState(RefLinks refLink)
     * \brief Constructorul de initializare al clasei.
     * \param refLink O referinta catre un obiect "shortcut", obiect ce contine o serie de referinte utile in program.
     */
    public MenuState(RefLinks refLink)
    {
        super(refLink);
        System.out.println("✓ MenuState initializat (Constructor)");
        stateEnterTime = System.currentTimeMillis();
        refLink.GetKeyManager().clearKeys();

        saveGameExists = refLink.GetDatabaseManager().hasGameSave();
        if (!saveGameExists && selectedOption == 1) {
            selectedOption = 0;
        }

        System.out.println("DEBUG: Se încearcă încărcarea Assets.backgroundMenu.");
        if (Assets.backgroundMenu != null) {
            System.out.println("DEBUG: Assets.backgroundMenu a fost încărcat cu succes (dimensiuni: " + Assets.backgroundMenu.getWidth() + "x" + Assets.backgroundMenu.getHeight() + ").");
        } else {
            System.err.println("EROARE DEBUG: Fundal meniu desenat cu culoare solidă (Assets.backgroundMenu este NULL).");
        }
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea curenta a meniului.
     */
    @Override
    public void Update()
    {
        if (System.currentTimeMillis() - stateEnterTime < INPUT_COOLDOWN_MS) {
            return;
        }

        handleInput();
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDebugTime > 2000) {
            System.out.println("MenuState activ - optiunea selectata: " + selectedOption + " (" + menuOptions[selectedOption] + ")");
            lastDebugTime = currentTime;
        }
    }

    private void handleInput()
    {
        if (refLink.GetKeyManager() == null) {
            System.err.println("KeyManager este null!");
            return;
        }

        // Navigare sus
        if(refLink.GetKeyManager().up && !upPressed)
        {
            upPressed = true;
            selectedOption--;
            if(selectedOption < 0)
                selectedOption = menuOptions.length - 1;
            if (!saveGameExists && selectedOption == 1) {
                selectedOption--;
                if (selectedOption < 0) selectedOption = menuOptions.length - 1;
            }
            System.out.println("Navigare sus - optiune selectata: " + menuOptions[selectedOption]);
        }
        else if(!refLink.GetKeyManager().up)
        {
            upPressed = false;
        }

        // Navigare jos
        if(refLink.GetKeyManager().down && !downPressed)
        {
            downPressed = true;
            selectedOption++;
            if(selectedOption >= menuOptions.length)
                selectedOption = 0;
            if (!saveGameExists && selectedOption == 1) {
                selectedOption++;
                if (selectedOption >= menuOptions.length) selectedOption = 0;
            }
            System.out.println("Navigare jos - optiune selectata: " + menuOptions[selectedOption]);
        }
        else if(!refLink.GetKeyManager().down)
        {
            downPressed = false;
        }

        // Selectare optiune (Enter sau Space)
        boolean enterKey = refLink.GetKeyManager().enter;
        boolean spaceKey = refLink.GetKeyManager().space;

        if((enterKey || spaceKey) && !enterPressed)
        {
            enterPressed = true;
            System.out.println("Optiune selectata: " + menuOptions[selectedOption]);
            executeSelectedOption();
        }
        else if(!enterKey && !spaceKey)
        {
            enterPressed = false;
        }
    }

    private void executeSelectedOption()
    {
        switch(selectedOption)
        {
            case 0: // NEW GAME
                System.out.println("Pornire joc nou...");
                refLink.SetState(new GameState(refLink, false));
                break;
            case 1: // LOAD GAME
                if (saveGameExists) {
                    System.out.println("Incarcare joc...");
                    refLink.SetState(new GameState(refLink, true));
                } else {
                    System.out.println("Nu exista joc salvat pentru a fi incarcat.");
                }
                break;
            case 2: // SETTINGS
                System.out.println("Deschidere Settings...");
                refLink.SetState(new SettingsState(refLink));
                break;
            case 3: // ABOUT
                System.out.println("Deschidere About...");
                refLink.SetState(new AboutState(refLink));
                break;
            case 4: // QUIT
                System.out.println("Inchidere joc...");
                System.exit(0);
                break;
        }
    }

    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza (randeaza) pe ecran starea curenta a meniului.
     * \param g Contextul grafic in care trebuie sa deseneze starea jocului pe ecran.
     */
    @Override
    public void Draw(Graphics g)
    {
        if (Assets.backgroundMenu != null) {
            g.drawImage(Assets.backgroundMenu, 0, 0, refLink.GetWidth(), refLink.GetHeight(), null);
        } else {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());
            System.err.println("EROARE DEBUG: Fundal meniu desenat cu culoare solidă (Assets.backgroundMenu este NULL).");
        }

        g.setColor(new Color(0, 0, 0, 120));
        g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());

        g.setColor(new Color(220, 200, 120));
        g.setFont(titleFont);
        FontMetrics titleFm = g.getFontMetrics();
        String title = "LOST EXPEDITION";
        int titleWidth = titleFm.stringWidth(title);
        g.drawString(title, (refLink.GetWidth() - titleWidth) / 2, 100);
        Font subtitleFont = new Font("Papyrus", Font.ITALIC, 16);
        g.setFont(subtitleFont);
        FontMetrics subtitleFm = g.getFontMetrics();
        String subtitle = "A Journey Into the Unknown";
        int subtitleWidth = subtitleFm.stringWidth(subtitle);
        g.drawString(subtitle, (refLink.GetWidth() - subtitleWidth) / 2, 130);

        g.setFont(buttonFont);
        FontMetrics buttonFm = g.getFontMetrics();

        int startY = 200;
        int gap = 60;
        int buttonWidth = 200;
        int buttonHeight = 40;
        for(int i = 0; i < menuOptions.length; i++)
        {
            int x = (refLink.GetWidth() - buttonWidth) / 2;
            int y = startY + i * gap;

            boolean isDisabled = (i == 1 && !saveGameExists);

            if(i == selectedOption)
            {
                g.setColor(isDisabled ? disabledColor.darker() : selectedColor);
                int pulse = (int)(Math.sin(System.currentTimeMillis() * 0.005) * 5);
                g.fillRect(x - pulse, y - buttonHeight / 2 - pulse, buttonWidth + 2*pulse, buttonHeight + 2*pulse);
            }
            else
            {
                g.setColor(isDisabled ? disabledColor : buttonColor);
                g.fillRect(x, y - buttonHeight / 2, buttonWidth, buttonHeight);
            }

            g.setColor(isDisabled ? Color.DARK_GRAY : (i == selectedOption ? Color.WHITE : textColor));
            int textWidth = buttonFm.stringWidth(menuOptions[i]);
            int textX = x + (buttonWidth - textWidth) / 2;
            int textY = y + buttonFm.getAscent() / 2;
            g.drawString(menuOptions[i], textX, textY);
            g.setColor(textColor);
            g.drawRect(x, y - buttonHeight / 2, buttonWidth, buttonHeight);

            if(i == selectedOption && !isDisabled)
            {
                g.setColor(Color.YELLOW);
                int arrowY = y;
                g.fillPolygon(new int[]{x - 20, x - 10, x - 20}, new int[]{arrowY - 5, arrowY, arrowY + 5}, 3);
                g.fillPolygon(new int[]{x + buttonWidth + 10, x + buttonWidth + 20, x + buttonWidth + 10}, new int[]{arrowY - 5, arrowY, arrowY + 5}, 3);
            }
        }

        Font instructionFont = new Font("SansSerif", Font.PLAIN, 12);
        g.setFont(instructionFont);
        g.setColor(textColor);
        FontMetrics instrFm = g.getFontMetrics();

        String[] instructions = {
                "Foloseste W/S pentru navigare",
                "Apasa ENTER/SPACE pentru selectare"
        };
        int instrY = refLink.GetHeight() - 40;
        for(String instruction : instructions)
        {
            int instrWidth = instrFm.stringWidth(instruction);
            g.drawString(instruction, (refLink.GetWidth() - instrWidth) / 2, instrY);
            instrY += 15;
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.drawString("Optiune: " + selectedOption + "/" + (menuOptions.length-1), 10, 20);
        g.drawString("W: " + refLink.GetKeyManager().up + " S: " + refLink.GetKeyManager().down, 10, 35);
        g.drawString("Save Exists: " + saveGameExists, 10, 50);
    }
}