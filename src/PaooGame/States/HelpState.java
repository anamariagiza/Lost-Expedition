package PaooGame.States;

import PaooGame.Graphics.Assets;
import PaooGame.RefLinks;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;

public class HelpState extends State {

    private final Color backgroundColor = new Color(0, 0, 0);
    private final Color textColor = Color.WHITE;
    private final Color titleColor = new Color(255, 215, 0);
    private final Font titleFont = new Font("Papyrus", Font.BOLD, 36);
    private final Font textFont = new Font("SansSerif", Font.PLAIN, 18);
    private final Font instructionFont = new Font("SansSerif", Font.PLAIN, 14);

    private Rectangle backButtonBounds = null;
    private final String backInstruction = "Apasa ESC pentru a reveni.";

    private int lastWidth, lastHeight;


    public HelpState(RefLinks refLink) {
        super(refLink);
        System.out.println("✓ HelpState initializat");

        lastWidth = refLink.GetWidth();
        lastHeight = refLink.GetHeight();
    }

    @Override
    public void Update() {
        if (refLink.GetWidth() != lastWidth || refLink.GetHeight() != lastHeight) {
            backButtonBounds = null;
            lastWidth = refLink.GetWidth();
            lastHeight = refLink.GetHeight();
        }

        if (refLink.GetKeyManager().isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
            refLink.SetState(refLink.GetPreviousState());
        }

        handleMouseInput();
    }

    private void handleMouseInput() {
        if (refLink.GetMouseManager() == null || backButtonBounds == null) return;

        if (backButtonBounds.contains(refLink.GetMouseManager().getMouseX(), refLink.GetMouseManager().getMouseY())) {
            if (refLink.GetMouseManager().isMouseJustClicked()) {
                refLink.SetState(refLink.GetPreviousState());
            }
        }
    }


    @Override
    public void Draw(Graphics g) {
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
        String title = "CONTROLUL JOCULUI";
        int titleWidth = titleFm.stringWidth(title);
        g.drawString(title, (refLink.GetWidth() - titleWidth) / 2, 80);

        g.setFont(textFont);
        g.setColor(textColor);
        FontMetrics textFm = g.getFontMetrics();
        String[] infoLines = {
                "Mişcare: W, A, S, D",
                "Alergare: Shift",
                "Săritură: Space",
                "Interacţiune: E",
                "Atac: J, K, /",
                "Meniu Pauză: P",
                "",
                "Obiectivele misiunii: Rezolvaţi puzzle-uri străvechi, navigaţi prin ruine",
                "periculoase şi luptaţi împotriva mercenarilor pentru a găsi El Dorado."
        };

        int startY = 150;
        int rectPadding = 20;
        int rectWidth = 600;
        int rectX = (refLink.GetWidth() - rectWidth) / 2;
        int lineHeight = textFm.getHeight();
        int totalTextHeight = infoLines.length * (lineHeight + 5);
        int rectHeight = totalTextHeight + 2 * rectPadding;
        int rectY = 150 - textFm.getAscent() - rectPadding;

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(rectX, rectY, rectWidth, rectHeight);

        g.setColor(Color.WHITE); // Culoare font pentru textul principal
        for (String line : infoLines) {
            int lineWidth = textFm.stringWidth(line);
            g.drawString(line, rectX + (rectWidth - lineWidth) / 2, startY);
            startY += lineHeight + 5;
        }

        g.setFont(instructionFont);
        g.setColor(titleColor);

        FontMetrics instrFm = g.getFontMetrics();
        int instrWidth = instrFm.stringWidth(backInstruction);
        int instrX = (refLink.GetWidth() - instrWidth) / 2;
        int instrY = refLink.GetHeight() - 50;

        g.drawString(backInstruction, instrX, instrY);

        if (backButtonBounds == null) {
            backButtonBounds = new Rectangle(instrX, instrY - instrFm.getAscent(), instrWidth, instrFm.getHeight());
        }

        g.setColor(titleColor);
        g.drawRect(20, 20, refLink.GetWidth() - 40, refLink.GetHeight() - 40);
    }
}