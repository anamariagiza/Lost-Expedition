package PaooGame.States;

import PaooGame.RefLinks;
import java.awt.*;
import java.awt.event.KeyEvent;

/*! \class public class SettingsState extends State
    \brief Implementeaza notiunea de settings pentru joc cu functionalitate completa.

    Aici setarile vor trebui salvate/incarcate intr-un/dintr-un fisier/baza de date sqlite.
 */
public class SettingsState extends State
{
    private final Color backgroundColor = new Color(0, 0, 0);
    private final Color textColor = new Color(175, 146, 0);
    private final Color titleColor = new Color(255, 215, 0);
    private final Color selectedColor = new Color(160, 82, 45);
    private final Color buttonColor = new Color(255, 255, 255);
    private final Font titleFont = new Font("Papyrus", Font.BOLD, 24);
    private final Font optionFont = new Font("SansSerif", Font.BOLD, 16);
    private final Font instructionFont = new Font("SansSerif", Font.PLAIN, 12);

    private String[] settingOptions = {"SUNET: ON", "MUZICA: ON", "VOLUM: 100%", "SALVARE SETARI", "INAPOI LA MENIU"};
    private int selectedOption = 0;
    private boolean enterPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean escapePressed = false;

    // Variabile pentru setari
    private boolean soundEnabled = true;
    private boolean musicEnabled = true;
    private int volume = 100;

    /*! \fn public SettingsState(RefLinks refLink)
        \brief Constructorul de initializare al clasei.

        \param refLink O referinta catre un obiect "shortcut", obiect ce contine o serie de referinte utile in program.
     */
    public SettingsState(RefLinks refLink)
    {
        super(refLink);
        updateSettingDisplays();
        System.out.println("✓ SettingsState initializat");
    }

    /*! \fn public void Update()
        \brief Actualizeaza starea setarilor.
     */
    @Override
    public void Update()
    {
        handleInput();
        updateSettingDisplays();
    }

    private void handleInput()
    {
        if (refLink.GetKeyManager() == null) {
            System.err.println("KeyManager este null in SettingsState!");
            return;
        }

        // Navigare sus
        if(refLink.GetKeyManager().up && !upPressed)
        {
            upPressed = true;
            selectedOption--;
            if(selectedOption < 0)
                selectedOption = settingOptions.length - 1;
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
            if(selectedOption >= settingOptions.length)
                selectedOption = 0;
        }
        else if(!refLink.GetKeyManager().down)
        {
            downPressed = false;
        }

        // Navigare stanga (pentru modificarea setarilor)
        if(refLink.GetKeyManager().left && !leftPressed)
        {
            leftPressed = true;
            modifySetting(-1);
        }
        else if(!refLink.GetKeyManager().left)
        {
            leftPressed = false;
        }

        // Navigare dreapta (pentru modificarea setarilor)
        if(refLink.GetKeyManager().right && !rightPressed)
        {
            rightPressed = true;
            modifySetting(1);
        }
        else if(!refLink.GetKeyManager().right)
        {
            rightPressed = false;
        }

        // Selectare optiune
        if((refLink.GetKeyManager().keys[KeyEvent.VK_ENTER] || refLink.GetKeyManager().keys[KeyEvent.VK_SPACE]) && !enterPressed)
        {
            enterPressed = true;
            executeSelectedOption();
        }
        else if(!refLink.GetKeyManager().keys[KeyEvent.VK_ENTER] && !refLink.GetKeyManager().keys[KeyEvent.VK_SPACE])
        {
            enterPressed = false;
        }

        // Intoarcere la meniu cu ESC
        if(refLink.GetKeyManager().keys[KeyEvent.VK_ESCAPE] && !escapePressed)
        {
            escapePressed = true;
            refLink.SetState(new MenuState(refLink));
        }
        else if(!refLink.GetKeyManager().keys[KeyEvent.VK_ESCAPE])
        {
            escapePressed = false;
        }
    }

    private void modifySetting(int direction)
    {
        switch(selectedOption)
        {
            case 0: // SUNET
                soundEnabled = !soundEnabled;
                break;
            case 1: // MUZICA
                musicEnabled = !musicEnabled;
                break;
            case 2: // VOLUM
                volume += direction * 10;
                if(volume < 0) volume = 0;
                if(volume > 100) volume = 100;
                break;
        }
    }

    private void executeSelectedOption()
    {
        switch(selectedOption)
        {
            case 0: // SUNET
                soundEnabled = !soundEnabled;
                break;
            case 1: // MUZICA
                musicEnabled = !musicEnabled;
                break;
            case 2: // VOLUM
                // Volum se modifica cu stanga/dreapta
                break;
            case 3: // SALVARE SETARI
                saveSettings();
                break;
            case 4: // INAPOI LA MENIU
                refLink.SetState(new MenuState(refLink));
                break;
        }
    }

    private void updateSettingDisplays()
    {
        settingOptions[0] = "SUNET: " + (soundEnabled ? "ON" : "OFF");
        settingOptions[1] = "MUZICA: " + (musicEnabled ? "ON" : "OFF");
        settingOptions[2] = "VOLUM: " + volume + "%";
    }

    private void saveSettings()
    {
        // Placeholder pentru salvarea setarilor
        System.out.println("Setari salvate:");
        System.out.println("Sunet: " + soundEnabled);
        System.out.println("Muzica: " + musicEnabled);
        System.out.println("Volum: " + volume);
    }

    /*! \fn public void Draw(Graphics g)
        \brief Deseneaza (randeaza) pe ecran setarile.

        \param g Contextul grafic in care trebuie sa deseneze starea setarilor pe ecran.
     */
    @Override
    public void Draw(Graphics g)
    {
        // Desenarea fundalului
        g.setColor(backgroundColor);
        g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());

        // Titlul principal
        g.setColor(titleColor);
        g.setFont(titleFont);
        FontMetrics titleFm = g.getFontMetrics();
        String title = "SETARI";
        int titleWidth = titleFm.stringWidth(title);
        g.drawString(title, (refLink.GetWidth() - titleWidth) / 2, 80);

        // Desenarea optiunilor
        g.setFont(optionFont);
        FontMetrics optionFm = g.getFontMetrics();

        int startY = 150;
        int gap = 60;
        int buttonWidth = 300;
        int buttonHeight = 40;

        for(int i = 0; i < settingOptions.length; i++)
        {
            int x = (refLink.GetWidth() - buttonWidth) / 2;
            int y = startY + i * gap;

            // Desenarea fundalului optiunii
            if(i == selectedOption)
            {
                g.setColor(selectedColor);
            }
            else
            {
                g.setColor(buttonColor);
            }
            g.fillRect(x, y - buttonHeight / 2, buttonWidth, buttonHeight);

            // Desenarea textului optiunii
            g.setColor(i == selectedOption ? Color.WHITE : textColor);
            int textWidth = optionFm.stringWidth(settingOptions[i]);
            int textX = x + (buttonWidth - textWidth) / 2;
            int textY = y + optionFm.getAscent() / 2;
            g.drawString(settingOptions[i], textX, textY);

            // Desenarea bordurii
            g.setColor(textColor);
            g.drawRect(x, y - buttonHeight / 2, buttonWidth, buttonHeight);

            // Indicatori pentru setarile modificabile
            if(i < 3) // Primele 3 optiuni sunt modificabile
            {
                g.setColor(textColor);
                g.drawString("<", x - 30, textY);
                g.drawString(">", x + buttonWidth + 10, textY);
            }
        }

        // Instructiuni
        g.setFont(instructionFont);
        g.setColor(titleColor);
        FontMetrics instrFm = g.getFontMetrics();

        String[] instructions = {
                "W/S - Navigare sus/jos",
                "A/D - Modificare setari",
                "ENTER/SPACE - Selectare",
                "ESC - Inapoi la meniu"
        };

        int instrY = refLink.GetHeight() - 80;
        for(String instruction : instructions)
        {
            int instrWidth = instrFm.stringWidth(instruction);
            g.drawString(instruction, (refLink.GetWidth() - instrWidth) / 2, instrY);
            instrY += 15;
        }

        // Bordura decorativa
        g.setColor(textColor);
        g.drawRect(20, 20, refLink.GetWidth() - 40, refLink.GetHeight() - 40);
    }
}