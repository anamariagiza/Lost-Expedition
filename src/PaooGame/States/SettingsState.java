package PaooGame.States;

import PaooGame.RefLinks;
import PaooGame.Utils.DatabaseManager;
import PaooGame.Graphics.Assets;

import java.awt.*;
import java.awt.event.KeyEvent;

/*!
 * \class public class SettingsState extends State
 * \brief Implementeaza notiunea de settings pentru joc cu functionalitate completa.
 * Aici setarile vor fi salvate/incarcate intr-o baza de date SQLite.
 */
public class SettingsState extends State
{
    private final Color backgroundColor = new Color(0, 0, 0);
    private final Color textColor = new Color(255, 255, 255);
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

    private boolean soundEnabled;
    private boolean musicEnabled;
    private int volume;

    private String saveMessage = null;
    private long saveMessageTime = 0;
    private final long MESSAGE_DURATION_MS = 2000;

    /*!
     * \fn public SettingsState(RefLinks refLink)
     * \brief Constructorul de initializare al clasei.
     * La initializare, incearca sa incarca setarile din baza de date.
     * \param refLink O referinta catre un obiect "shortcut", obiect ce contine o serie de referinte utile in program.
     */
    public SettingsState(RefLinks refLink)
    {
        super(refLink);
        loadSettings();
        updateSettingDisplays();
        System.out.println("✓ SettingsState initializat");
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea setarilor.
     */
    @Override
    public void Update()
    {
        if (saveMessage != null && System.currentTimeMillis() - saveMessageTime > MESSAGE_DURATION_MS) {
            saveMessage = null;
        }

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

        // Navigare dreapta (for modifying settings)
        if(refLink.GetKeyManager().right && !rightPressed)
        {
            rightPressed = true;
            modifySetting(1);
        }
        else if(!refLink.GetKeyManager().right)
        {
            rightPressed = false;
        }

        // Selectare optiune (Enter sau Space)
        if((refLink.GetKeyManager().enter || refLink.GetKeyManager().space) && !enterPressed)
        {
            enterPressed = true;
            executeSelectedOption();
        }
        else if(!refLink.GetKeyManager().enter && !refLink.GetKeyManager().space)
        {
            enterPressed = false;
        }

        // Intoarcere la meniu cu ESC
        if(refLink.GetKeyManager().escape && !escapePressed)
        {
            escapePressed = true;
            refLink.SetState(new MenuState(refLink));
        }
        else if(!refLink.GetKeyManager().escape)
        {
            escapePressed = false;
        }
    }

    /*!
     * \fn private void modifySetting(int direction)
     * \brief Modifica valoarea setarii curente in functie de directie.
     * \param direction -1 pentru decrementare, 1 pentru incrementare sau toggle.
     */
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
                refLink.SetState(refLink.GetGame().getPreviousState()); // NOU: Returneaza la starea anterioara (MenuState sau PauseState)
                break;
        }
    }

    /*!
     * \fn private void updateSettingDisplays()
     * \brief Actualizeaza string-urile afisate pentru optiunile de setari.
     */
    private void updateSettingDisplays()
    {
        settingOptions[0] = "SUNET: " + (soundEnabled ? "ON" : "OFF");
        settingOptions[1] = "MUZICA: " + (musicEnabled ? "ON" : "OFF");
        settingOptions[2] = "VOLUM: " + volume + "%";
    }

    /*!
     * \fn private void saveSettings()
     * \brief Salveaza setarile curente in baza de date folosind DatabaseManager.
     */
    private void saveSettings()
    {
        refLink.GetDatabaseManager().saveSettingsData(soundEnabled, musicEnabled, volume);
        saveMessage = "Setari salvate!";
        saveMessageTime = System.currentTimeMillis();
        System.out.println("Setari salvate in baza de date.");
    }

    /*!
     * \fn private void loadSettings()
     * \brief Incarca setarile din baza de date la initializarea starii.
     * Daca nu exista setari salvate, initializeaza cu valori implicite.
     */
    private void loadSettings()
    {
        DatabaseManager.SettingsData loadedSettings = refLink.GetDatabaseManager().loadSettingsData();
        if (loadedSettings != null) {
            soundEnabled = loadedSettings.soundEnabled;
            musicEnabled = loadedSettings.musicEnabled;
            volume = loadedSettings.volume;
            System.out.println("Setari incarcate din baza de date: Sunet=" + soundEnabled + ", Muzica=" + musicEnabled + ", Volum=" + volume);
        } else {
            soundEnabled = true;
            musicEnabled = true;
            volume = 100;
            System.out.println("Nu s-au gasit setari salvate. Se folosesc setari implicite.");
        }
    }


    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza (randeaza) pe ecran setarile.
     * \param g Contextul grafic in care trebuie sa deseneze starea setarilor pe ecran.
     */
    @Override
    public void Draw(Graphics g)
    {
        if (Assets.backgroundMenu != null) {
            g.drawImage(Assets.backgroundMenu, 0, 0, refLink.GetWidth(), refLink.GetHeight(), null);
        } else {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());
            System.err.println("EROARE DEBUG: Fundal 'Settings' desenat cu culoare solidă (Assets.backgroundMenu este NULL).");
        }

        g.setColor(new Color(0, 0, 0, 120));
        g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());

        g.setColor(titleColor);
        g.setFont(titleFont);
        FontMetrics titleFm = g.getFontMetrics();
        String title = "SETARI";
        int titleWidth = titleFm.stringWidth(title);
        g.drawString(title, (refLink.GetWidth() - titleWidth) / 2, 80);
        g.setFont(optionFont);
        FontMetrics optionFm = g.getFontMetrics();

        int startY = 150;
        int gap = 60;
        int buttonWidth = 350;
        int buttonHeight = 40;
        for(int i = 0; i < settingOptions.length; i++)
        {
            int x = (refLink.GetWidth() - buttonWidth) / 2;
            int y = startY + i * gap;

            if(i == selectedOption)
            {
                g.setColor(selectedColor);
            }
            else
            {
                g.setColor(buttonColor);
            }
            g.fillRect(x, y - buttonHeight / 2, buttonWidth, buttonHeight);
            g.setColor(i == selectedOption ? Color.WHITE : textColor);
            int textWidth = optionFm.stringWidth(settingOptions[i]);
            int textX = x + (buttonWidth - textWidth) / 2;
            int textY = y + optionFm.getAscent() / 2;
            g.drawString(settingOptions[i], textX, textY);
            g.setColor(textColor);
            g.drawRect(x, y - buttonHeight / 2, buttonWidth, buttonHeight);

            if(i < 3)
            {
                g.setColor(textColor);
                g.drawString("<", x - 30, textY);
                g.drawString(">", x + buttonWidth + 10, textY);
            }
        }

        Font instructionFont = new Font("SansSerif", Font.PLAIN, 12);
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

        if (saveMessage != null) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics msgFm = g.getFontMetrics();
            int msgWidth = msgFm.stringWidth(saveMessage);
            g.drawString(saveMessage, (refLink.GetWidth() - msgWidth) / 2, refLink.GetHeight() - 150);
        }

        g.setColor(textColor);
        g.drawRect(20, 20, refLink.GetWidth() - 40, refLink.GetHeight() - 40);
    }
}