package PaooGame.States;

import PaooGame.Graphics.Assets;
import PaooGame.RefLinks;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;

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
    private String[] menuOptions = {"NEW GAME", "LOAD GAME", "SETTINGS", "HELP", "ABOUT", "QUIT"};
    private Rectangle[] buttonBounds;
    private int selectedOption = 0;
    private boolean enterPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    private int lastWidth, lastHeight;
    private long stateEnterTime;
    private final long INPUT_COOLDOWN_MS = 200;
    private long lastDebugTime = 0;

    private boolean saveGameExists;
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

        lastWidth = refLink.GetWidth();
        lastHeight = refLink.GetHeight();
        setupButtons();
    }

    private void setupButtons() {
        buttonBounds = new Rectangle[menuOptions.length];
        int startY = 200;
        int gap = 60;
        int buttonWidth = 200;
        int buttonHeight = 40;
        for (int i = 0; i < menuOptions.length; i++) {
            int x = (refLink.GetWidth() - buttonWidth) / 2;
            int y = startY + i * gap;
            buttonBounds[i] = new Rectangle(x, y - buttonHeight / 2, buttonWidth, buttonHeight);
        }
    }

    @Override
    public void Update()
    {
        if (refLink.GetWidth() != lastWidth || refLink.GetHeight() != lastHeight) {
            setupButtons();
            lastWidth = refLink.GetWidth();
            lastHeight = refLink.GetHeight();
        }

        if (System.currentTimeMillis() - stateEnterTime < INPUT_COOLDOWN_MS) {
            return;
        }

        handleInput();
        handleMouseInput();
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDebugTime > 2000) {
            System.out.println("MenuState activ - optiune selectata: " + selectedOption + " (" + menuOptions[selectedOption] + ")");
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
            executeSelectedOption();
        }
        else if(!enterKey && !spaceKey)
        {
            enterPressed = false;
        }
    }

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


    private void executeSelectedOption()
    {
        switch(selectedOption)
        {
            case 0: // NEW GAME
                refLink.SetState(new GameState(refLink));
                break;
            case 1: // LOAD GAME
                if (saveGameExists) {
                    refLink.SetState(new GameState(refLink, true));
                }
                break;
            case 2: // SETTINGS
                refLink.SetState(new SettingsState(refLink));
                break;
            case 3: // HELP (opțiunea nouă)
                refLink.SetState(new HelpState(refLink));
                break;
            case 4: // ABOUT
                refLink.SetState(new AboutState(refLink));
                break;
            case 5: // QUIT
                System.exit(0);
                break;
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
                "Foloseste W/S pentru navigare sau mouse-ul",
                "Apasa ENTER/SPACE sau clic pentru selectare"
        };
        int instrY = refLink.GetHeight() - 40;
        for(String instruction : instructions)
        {
            int instrWidth = instrFm.stringWidth(instruction);
            g.drawString(instruction, (refLink.GetWidth() - instrWidth) / 2, instrY);
            instrY += 15;
        }
    }
}