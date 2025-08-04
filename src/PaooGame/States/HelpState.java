package PaooGame.States;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;

import java.awt.*;
import java.awt.event.KeyEvent;

/*!
 * \class public class HelpState extends State
 * \brief Implementeaza starea de joc pentru afisarea instructiunilor si a informatiilor de ajutor.
 */
public class HelpState extends State {

    private final Color backgroundColor = new Color(0, 0, 0);
    private final Color textColor = new Color(255, 255, 255);
    private final Color titleColor = new Color(255, 215, 0);
    private final Font titleFont = new Font("Papyrus", Font.BOLD, 36);
    private final Font textFont = new Font("SansSerif", Font.PLAIN, 18);
    private final Font instructionFont = new Font("SansSerif", Font.PLAIN, 14);
    private boolean escapePressed = false;

    /*!
     * \fn public HelpState(RefLinks refLink)
     * \brief Constructorul de initializare al clasei HelpState.
     * \param refLink O referinta catre un obiect "shortcut".
     */
    public HelpState(RefLinks refLink) {
        super(refLink);
        System.out.println("✓ HelpState initializat");
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea curenta a meniului de ajutor.
     */
    @Override
    public void Update() {
        if(refLink.GetKeyManager().escape && !escapePressed) {
            escapePressed = true;
            refLink.SetState(new PauseState(refLink));
        } else if(!refLink.GetKeyManager().escape) {
            escapePressed = false;
        }
    }

    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza (randeaza) pe ecran starea curenta a meniului de ajutor.
     * \param g Contextul grafic in care trebuie sa deseneze.
     */
    @Override
    public void Draw(Graphics g) {
        if (Assets.backgroundMenu != null) {
            g.drawImage(Assets.backgroundMenu, 0, 0, refLink.GetWidth(), refLink.GetHeight(), null);
        } else {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());
            System.err.println("EROARE DEBUG: Fundal 'Help' desenat cu culoare solidă (Assets.backgroundMenu este NULL).");
        }

        g.setColor(new Color(0, 0, 0, 120));
        g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());

        g.setColor(titleColor);
        g.setFont(titleFont);
        FontMetrics titleFm = g.getFontMetrics();
        String title = "AJUTOR SI INSTRUCTIUNI";
        int titleWidth = titleFm.stringWidth(title);
        g.drawString(title, (refLink.GetWidth() - titleWidth) / 2, 80);

        int rectPadding = 20;
        int rectWidth = 600;
        int rectX = (refLink.GetWidth() - rectWidth) / 2;

        g.setFont(textFont);
        FontMetrics textFm = g.getFontMetrics();

        String[] helpLines = {
                "CONTROALE:",
                "   W / A / S / D  - Deplasare James Carter",
                "   SPACE          - Saritura",
                "   SHIFT          - Alunecare (Alergat)",
                "   E              - Interactiune cu obiecte (ex: colecteaza chei)",
                "   P              - Meniu Pauza",
                "   Z              - Comuta Zoom Camera",
                " ",
                "OBIECTIVE MISIUNE:",
                "   Nivel 1: Jungla",
                "     - Gaseste cele doua chei (cheia pesterii si cheia usii).",
                "     - Evita animalele salbatice si capcanele mortale.",
                "     - Foloseste tasta 'E' la poarta de iesire pentru a intra in pestera.",
                "   Nivel 2: Pestera",
                "     - Rezolva puzzle-urile stravechi pentru a avansa.",
                "     - Cronometrul scade! Rezolva rapid pentru a evita capcanele.",
                "     - Foloseste cheia usii pentru a debloca pasaje.",
                "   Nivel 3: Infruntarea Finala",
                "     - Invinge-l pe Agentul lui Magnus Voss.",
                "     - Gaseste comoara ascunsa.",
                "     - Completeaza expeditia!"
        };

        int totalTextHeight = 0;
        for (String line : helpLines) {
            totalTextHeight += textFm.getHeight() + 5;
        }
        int rectHeight = totalTextHeight + 2 * rectPadding;
        int rectY = 150 - textFm.getAscent() - rectPadding;

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(rectX, rectY, rectWidth, rectHeight);


        g.setFont(textFont);
        g.setColor(textColor);
        int startY = 150;

        for (String line : helpLines) {
            int lineWidth = textFm.stringWidth(line);
            g.drawString(line, rectX + (rectWidth - lineWidth) / 2, startY);
            startY += textFm.getHeight() + 5;
        }

        g.setFont(instructionFont);
        g.setColor(new Color(255, 215, 0));
        String instruction = "Apasa ESC pentru a reveni la meniul de pauza.";
        int instrWidth = g.getFontMetrics().stringWidth(instruction);
        g.drawString(instruction, (refLink.GetWidth() - instrWidth) / 2, refLink.GetHeight() - 50);

        g.setColor(titleColor);
        g.drawRect(20, 20, refLink.GetWidth() - 40, refLink.GetHeight() - 40);
    }
}