package PaooGame.States;

import PaooGame.Graphics.Assets;
import PaooGame.RefLinks;
import java.awt.*;
import java.awt.event.KeyEvent;

/*! \class public class MenuState extends State
    \brief Implementeaza notiunea de menu pentru joc cu functionalitate completa.
 */
public class MenuState extends State
{
    private final Color backgroundColor = new Color(0, 0, 0);
    private final Color buttonColor = new Color(255, 255, 255);
    private final Color textColor = new Color(175, 146, 0);
    private final Color selectedColor = new Color(160, 82, 45);
    private final Font titleFont = new Font("Papyrus", Font.BOLD, 36);
    private final Font buttonFont = new Font("Papyrus", Font.BOLD, 18);

    private String[] menuOptions = {"NEW GAME", "LOAD GAME", "SETTINGS", "ABOUT", "QUIT"};
    private int selectedOption = 0;
    private boolean enterPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    // Timer pentru debugging
    private long lastDebugTime = 0;

    /*! \fn public MenuState(RefLinks refLink)
        \brief Constructorul de initializare al clasei.

        \param refLink O referinta catre un obiect "shortcut", obiect ce contine o serie de referinte utile in program.
     */
    public MenuState(RefLinks refLink)
    {
        super(refLink);
        System.out.println("✓ MenuState initializat");
    }

    /*! \fn public void Update()
        \brief Actualizeaza starea curenta a meniului.
     */
    @Override
    public void Update()
    {
        handleInput();

        // Debug la fiecare 2 secunde
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDebugTime > 2000) {
            System.out.println("MenuState activ - optiunea selectata: " + selectedOption + " (" + menuOptions[selectedOption] + ")");
            lastDebugTime = currentTime;
        }
    }

    private void handleInput()
    {
        // Verifica daca KeyManager functioneaza
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
            System.out.println("Navigare jos - optiune selectata: " + menuOptions[selectedOption]);
        }
        else if(!refLink.GetKeyManager().down)
        {
            downPressed = false;
        }

        // Selectare optiune (Enter sau Space)
        boolean enterKey = refLink.GetKeyManager().keys[KeyEvent.VK_ENTER];
        boolean spaceKey = refLink.GetKeyManager().keys[KeyEvent.VK_SPACE];

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
                refLink.SetState(new GameState(refLink)); // ATENTIE: GameState, nu PlayState
                break;
            case 1: // LOAD GAME
                System.out.println("incarcare joc...");
                refLink.SetState(new GameState(refLink)); // Pentru moment, porneste un joc nou
                break;
            case 2: // SETTINGS
                System.out.println("Deschidere Settings...");
                refLink.SetState(new SettingsState(refLink));
                break;
            case 3: // ABOUT (NOU)
                System.out.println("Deschidere About...");
                refLink.SetState(new AboutState(refLink));
                break;
            case 4: // QUIT
                System.out.println("inchidere joc...");
                System.exit(0);
                break;
        }
    }

    /*! \fn public void Draw(Graphics g)
        \brief Deseneaza (randeaza) pe ecran starea curenta a meniului.

        \param g Contextul grafic in care trebuie sa deseneze starea jocului pe ecran.
     */
    @Override
    public void Draw(Graphics g)
    {
        // Desenarea fundalului
        if (Assets.backgroundMenu != null) {
            g.drawImage(Assets.backgroundMenu, 0, 0, refLink.GetWidth(), refLink.GetHeight(), null);
        } else {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());
        }

        // Overlay semi-transparent pentru a face textul mai vizibil
        g.setColor(new Color(0, 0, 0, 120));
        g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());

        // Desenarea titlului
        g.setColor(new Color(220, 200, 120)); // Auriu mai cald
        g.setFont(titleFont);
        FontMetrics titleFm = g.getFontMetrics();
        String title = "LOST EXPEDITION";
        int titleWidth = titleFm.stringWidth(title);
        g.drawString(title, (refLink.GetWidth() - titleWidth) / 2, 100);

        // Desenarea subtitlului
        Font subtitleFont = new Font("Papyrus", Font.ITALIC, 16);
        g.setFont(subtitleFont);
        FontMetrics subtitleFm = g.getFontMetrics();
        String subtitle = "A Journey Into the Unknown";
        int subtitleWidth = subtitleFm.stringWidth(subtitle);
        g.drawString(subtitle, (refLink.GetWidth() - subtitleWidth) / 2, 130);

        // Desenarea optiunilor de meniu
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

            // Desenarea fundalului butonului
            if(i == selectedOption)
            {
                g.setColor(selectedColor);
                // Efect de pulsare pentru optiunea selectata
                int pulse = (int)(Math.sin(System.currentTimeMillis() * 0.005) * 5);
                g.fillRect(x - pulse, y - buttonHeight / 2 - pulse, buttonWidth + 2*pulse, buttonHeight + 2*pulse);
            }
            else
            {
                g.setColor(buttonColor);
                g.fillRect(x, y - buttonHeight / 2, buttonWidth, buttonHeight);
            }

            // Desenarea textului butonului
            g.setColor(i == selectedOption ? Color.WHITE : textColor);
            int textWidth = buttonFm.stringWidth(menuOptions[i]);
            int textX = x + (buttonWidth - textWidth) / 2;
            int textY = y + buttonFm.getAscent() / 2;
            g.drawString(menuOptions[i], textX, textY);

            // Desenarea bordurii
            g.setColor(textColor);
            g.drawRect(x, y - buttonHeight / 2, buttonWidth, buttonHeight);

            // Indicatori pentru optiunea selectata
            if(i == selectedOption)
            {
                g.setColor(Color.YELLOW);
                int arrowY = y;
                // Sageti stanga si dreapta
                g.fillPolygon(new int[]{x - 20, x - 10, x - 20}, new int[]{arrowY - 5, arrowY, arrowY + 5}, 3);
                g.fillPolygon(new int[]{x + buttonWidth + 10, x + buttonWidth + 20, x + buttonWidth + 10}, new int[]{arrowY - 5, arrowY, arrowY + 5}, 3);
            }
        }

        // Desenarea instructiunilor
        Font instructionFont = new Font("SansSerif", Font.PLAIN, 12);
        g.setFont(instructionFont);
        g.setColor(textColor);
        FontMetrics instrFm = g.getFontMetrics();

        String instruction1 = "Foloseste W/S pentru navigare";
        String instruction2 = "Apasa ENTER/SPACE pentru selectare";

        int instr1Width = instrFm.stringWidth(instruction1);
        int instr2Width = instrFm.stringWidth(instruction2);

        g.drawString(instruction1, (refLink.GetWidth() - instr1Width) / 2, refLink.GetHeight() - 40);
        g.drawString(instruction2, (refLink.GetWidth() - instr2Width) / 2, refLink.GetHeight() - 20);

        // Debug info in coltul din stanga sus
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.drawString("Optiune: " + selectedOption + "/" + (menuOptions.length-1), 10, 20);
        g.drawString("W: " + refLink.GetKeyManager().up + " S: " + refLink.GetKeyManager().down, 10, 35);
    }
}