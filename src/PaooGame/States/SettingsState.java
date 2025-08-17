package PaooGame.States;

import PaooGame.RefLinks;
import PaooGame.Utils.DatabaseManager;
import PaooGame.Graphics.Assets;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;

public class SettingsState extends State
{
    private final Color backgroundColor = new Color(0, 0, 0);
    private final Color textColor = new Color(255, 255, 255);
    private final Color titleColor = new Color(255, 215, 0);
    private final Color selectedColor = new Color(160, 82, 45);
    private final Color unselectedButtonColor = new Color(200, 200, 200, 100);
    private final Color selectedTextColor = Color.WHITE;
    private final Color unselectedTextColor = Color.BLACK;
    private final Font titleFont = new Font("Papyrus", Font.BOLD, 24);
    private final Font optionFont = new Font("SansSerif", Font.BOLD, 16);
    private final Font instructionFont = new Font("SansSerif", Font.PLAIN, 12);

    private String[] settingOptions = {"VOLUM: 100%", "SALVARE SETARI", "INAPOI LA MENIU"};
    private Rectangle[] buttonBounds;
    private Rectangle leftArrowBounds;
    private Rectangle rightArrowBounds;
    private int selectedOption = 0;
    private boolean enterPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean escapePressed = false;

    private int volume;

    private String saveMessage = null;
    private long saveMessageTime = 0;
    private final long MESSAGE_DURATION_MS = 2000;

    private int lastWidth, lastHeight;

    public SettingsState(RefLinks refLink)
    {
        super(refLink);
        loadSettings();
        updateSettingDisplays();
        lastWidth = refLink.GetWidth();
        lastHeight = refLink.GetHeight();
        setupButtons();
        System.out.println("✓ SettingsState initializat");
    }

    private void setupButtons() {
        buttonBounds = new Rectangle[settingOptions.length];
        int startY = 150;
        int gap = 60;
        int buttonWidth = 350;
        int buttonHeight = 40;
        for (int i = 0; i < settingOptions.length; i++) {
            int x = (refLink.GetWidth() - buttonWidth) / 2;
            int y = startY + i * gap;
            buttonBounds[i] = new Rectangle(x, y - buttonHeight / 2, buttonWidth, buttonHeight);
        }

        int volumeY = startY + 0 * gap; // Am ajustat indexul pentru volum
        int buttonX = (refLink.GetWidth() - buttonWidth) / 2;
        leftArrowBounds = new Rectangle(buttonX - 40, volumeY - buttonHeight / 2, 30, buttonHeight);
        rightArrowBounds = new Rectangle(buttonX + buttonWidth + 10, volumeY - buttonHeight / 2, 30, buttonHeight);
    }

    @Override
    public void Update()
    {
        if (refLink.GetWidth() != lastWidth || refLink.GetHeight() != lastHeight) {
            setupButtons();
            lastWidth = refLink.GetWidth();
            lastHeight = refLink.GetHeight();
        }

        if (saveMessage != null && System.currentTimeMillis() - saveMessageTime > MESSAGE_DURATION_MS) {
            saveMessage = null;
        }

        handleInput();
        handleMouseInput();
        updateSettingDisplays();
    }

    private void handleInput()
    {
        if (refLink.GetKeyManager() == null) {
            System.err.println("KeyManager este null in SettingsState!");
            return;
        }

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

        if(refLink.GetKeyManager().left && !leftPressed)
        {
            leftPressed = true;
            modifySetting(-1);
        }
        else if(!refLink.GetKeyManager().left)
        {
            leftPressed = false;
        }

        if(refLink.GetKeyManager().right && !rightPressed)
        {
            rightPressed = true;
            modifySetting(1);
        }
        else if(!refLink.GetKeyManager().right)
        {
            rightPressed = false;
        }

        if((refLink.GetKeyManager().enter || refLink.GetKeyManager().space) && !enterPressed)
        {
            enterPressed = true;
            executeSelectedOption();
        }
        else if(!refLink.GetKeyManager().enter && !refLink.GetKeyManager().space)
        {
            enterPressed = false;
        }

        if(refLink.GetKeyManager().escape && !escapePressed)
        {
            escapePressed = true;
            refLink.SetState(refLink.GetPreviousState());
        }
        else if(!refLink.GetKeyManager().escape)
        {
            escapePressed = false;
        }
    }

    private void handleMouseInput() {
        if (refLink.GetMouseManager() == null || buttonBounds == null) return;
        for (int i = 0; i < buttonBounds.length; i++) {
            if (buttonBounds[i].contains(refLink.GetMouseManager().getMouseX(), refLink.GetMouseManager().getMouseY())) {
                selectedOption = i;
                if (refLink.GetMouseManager().isMouseJustClicked()) {
                    if (selectedOption == 0) {
                        return;
                    }
                    executeSelectedOption();
                }
                break;
            }
        }

        if (selectedOption == 0) { // Am ajustat indexul pentru volum
            if (leftArrowBounds.contains(refLink.GetMouseManager().getMouseX(), refLink.GetMouseManager().getMouseY())) {
                if (refLink.GetMouseManager().isMouseJustClicked()) {
                    modifySetting(-1);
                }
            } else if (rightArrowBounds.contains(refLink.GetMouseManager().getMouseX(), refLink.GetMouseManager().getMouseY())) {
                if (refLink.GetMouseManager().isMouseJustClicked()) {
                    modifySetting(1);
                }
            }
        }
    }

    private void modifySetting(int direction)
    {
        switch(selectedOption)
        {
            case 0:
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
            case 0:
                break;
            case 1:
                saveSettings();
                break;
            case 2:
                refLink.SetState(refLink.GetPreviousState());
                break;
        }
    }

    private void updateSettingDisplays()
    {
        settingOptions[0] = "VOLUM: " + volume + "%";
    }

    private void saveSettings()
    {
        refLink.GetDatabaseManager().saveSettingsData(true, true, volume);
        saveMessage = "Setari salvate!";
        saveMessageTime = System.currentTimeMillis();
        System.out.println("Setari salvate in baza de date.");
    }

    private void loadSettings()
    {
        DatabaseManager.SettingsData loadedSettings = refLink.GetDatabaseManager().loadSettingsData();
        if (loadedSettings != null) {
            volume = loadedSettings.volume;
            System.out.println("Setari incarcate din baza de date: Sunet=" + loadedSettings.soundEnabled + ", Muzica=" + loadedSettings.musicEnabled + ", Volum=" + volume);
        } else {
            volume = 100;
            System.out.println("Nu s-au gasit setari salvate. Se folosesc setari implicite.");
        }
    }

    @Override
    public void Draw(Graphics g)
    {
        if (Assets.backgroundMenu != null) {
            g.drawImage(Assets.backgroundMenu, 0, 0, refLink.GetWidth(), refLink.GetHeight(), null);
        } else {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());
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

            Color currentButtonColor = (i == selectedOption) ? selectedColor : unselectedButtonColor;
            Color currentTextColor = (i == selectedOption) ? selectedTextColor : unselectedTextColor;
            Color outlineColor = (i == selectedOption) ? Color.WHITE : Color.GRAY;
            g.setColor(currentButtonColor);
            g.fillRect(x, y - buttonHeight / 2, buttonWidth, buttonHeight);

            g.setColor(currentTextColor);
            int textWidth = optionFm.stringWidth(settingOptions[i]);
            int textX = x + (buttonWidth - textWidth) / 2;
            int textY = y + optionFm.getAscent() / 2;
            g.drawString(settingOptions[i], textX, textY);

            g.setColor(outlineColor);
            g.drawRect(x, y - buttonHeight / 2, buttonWidth, buttonHeight);
            if(i < 1) // Am ajustat condiția pentru a afișa săgețile doar la volum
            {
                g.setColor(currentTextColor);
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