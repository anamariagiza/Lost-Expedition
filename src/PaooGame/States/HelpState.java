package PaooGame.States;

import PaooGame.Graphics.Assets;
import PaooGame.RefLinks;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;

/**
 * @class HelpState
 * @brief Implementeaza ecranul de "Help" (Ajutor/Instructiuni).
 * Aceasta stare ofera jucatorului informatii esentiale despre controalele
 * si obiectivele jocului. Este o stare simpla, informationala, care poate fi
 * accesata atat din meniul principal, cat si din meniul de pauza.
 */
public class HelpState extends State {

    /** Atribute finale pentru stilizarea vizuala a ecranului.*/
    private final Color backgroundColor = new Color(0, 0, 0);
    private final Color textColor = Color.WHITE;
    private final Color titleColor = new Color(255, 215, 0);
    private final Font titleFont = new Font("Papyrus", Font.BOLD, 36);
    private final Font textFont = new Font("SansSerif", Font.PLAIN, 18);
    private final Font instructionFont = new Font("SansSerif", Font.PLAIN, 14);

    /** Dreptunghiul de coliziune pentru textul de revenire (pentru clic cu mouse-ul).*/
    private Rectangle backButtonBounds = null;
    /** Textul instructiunii de revenire.*/
    private final String backInstruction = "Apasa ESC pentru a reveni.";

    /** Stocheaza ultimele dimensiuni ale ferestrei pentru a detecta redimensionarea.*/
    private int lastWidth, lastHeight;

    /**
     * @brief Constructorul clasei HelpState.
     * @param refLink O referinta catre obiectul RefLinks.
     */
    public HelpState(RefLinks refLink) {
        super(refLink);
        System.out.println("HelpState initializat");

        lastWidth = refLink.GetWidth();
        lastHeight = refLink.GetHeight();
    }

    /**
     * @brief Actualizeaza starea ecranului "Help".
     * Gestioneaza revenirea la starea anterioara prin apasarea tastei ESC sau clic.
     */
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

    /**
     * @brief Deseneaza (randeaza) continutul ecranului "Help".
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
                "Miscare: W, A, S, D",
                "Alergare: Shift",
                "Saritura: Space",
                "Interactiune: E",
                "Atac: K",
                "Meniu Pauza: P",
                "",
                "Obiectivele misiunii: Rezolvaţi puzzle-uri stravechi, navigaţi prin ruine",
                "periculoase si luptati impotriva inamicului pentru a gasi comoara."
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

        g.setColor(Color.WHITE);
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

    /**
     * @brief Gestioneaza input-ul de la mouse pentru a reveni la meniul anterior.
     */
    private void handleMouseInput() {
        if (refLink.GetMouseManager() == null || backButtonBounds == null) return;
        if (backButtonBounds.contains(refLink.GetMouseManager().getMouseX(), refLink.GetMouseManager().getMouseY())) {
            if (refLink.GetMouseManager().isMouseJustClicked()) {
                refLink.SetState(refLink.GetPreviousState());
            }
        }
    }
}