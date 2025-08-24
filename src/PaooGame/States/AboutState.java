package PaooGame.States;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;

import java.awt.*;

/**
 * @class AboutState
 * @brief Implementeaza ecranul "About" (Despre Joc).
 * Aceasta stare afiseaza informatii despre proiect, cum ar fi autorii si
 * contextul dezvoltarii. Este o stare informationala, cu o logica minima,
 * axata pe desenarea textului si a elementelor grafice.
 */
public class AboutState extends State
{
    /** Atribute finale pentru stilizarea vizuala a ecranului (culori, fonturi).*/
    private final Color backgroundColor = new Color(0, 0, 0); // Fallback color
    private final Color textColor = new Color(255, 255, 255); // Text alb pentru lizibilitate
    private final Color titleColor = new Color(255, 215, 0); // Aur pentru titlu
    private final Font titleFont = new Font("Papyrus", Font.BOLD, 36);
    private final Font textFont = new Font("SansSerif", Font.PLAIN, 18);
    private final Font instructionFont = new Font("SansSerif", Font.PLAIN, 14);
    /** Flag pentru a gestiona o singura apasare a tastei ESC, prevenind tranzitii multiple.*/
    private boolean escapePressed = false;

    /**
     * @brief Constructorul clasei AboutState.
     * @param refLink O referinta catre obiectul RefLinks.
     */
    public AboutState(RefLinks refLink)
    {
        super(refLink);
        System.out.println("AboutState initializat");
    }

    /**
     * @brief Actualizeaza starea ecranului "About".
     * Verifica daca tasta ESC a fost apasata pentru a reveni la meniul principal.
     */
    @Override
    public void Update()
    {
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

    /**
     * @brief Deseneaza (randeaza) continutul ecranului "About".
     * @param g Contextul grafic in care se va desena.
     */
    @Override
    public void Draw(Graphics g)
    {
        // Desenarea fundalului - Utilizam imaginea de fundal a meniului
        if (Assets.backgroundMenu != null) {
            g.drawImage(Assets.backgroundMenu, 0, 0, refLink.GetWidth(), refLink.GetHeight(), null);
        } else {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());
            //System.err.println("EROARE DEBUG: Fundal 'About' desenat cu culoare solida (Assets.backgroundMenu este NULL).");
        }

        // Overlay semi-transparent general peste fundal
        g.setColor(new Color(0, 0, 0, 120));
        g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());


        // Desenarea titlului
        g.setColor(titleColor);
        g.setFont(titleFont);
        FontMetrics titleFm = g.getFontMetrics();
        String title = "DESPRE JOC";
        int titleWidth = titleFm.stringWidth(title);
        g.drawString(title, (refLink.GetWidth() - titleWidth) / 2, 80);

        // NOU: Dreptunghi semi-transparent in spatele textului informațional
        int rectPadding = 20;
        int rectWidth = 600;
        int rectX = (refLink.GetWidth() - rectWidth) / 2;

        // Continutul mesajului "DESPRE JOC"
        g.setFont(textFont); // Setam fontul pentru masurare
        FontMetrics textFm = g.getFontMetrics(); // NOU: Declaram textFm aici

        String[] infoLines = {
                "Joc dezvoltat de: Echipa Nr. 11",
                "Membri: Gîza Ana-Maria",
                "        Ştefanache Elisa Michela",
                "Versiune: 1.0",
                "An: " + java.time.Year.now().getValue(),
                " ",
                "Acest joc este un proiect demonstrativ",
                "pentru disciplina Programarea Aplicatiilor Orientate pe Obiecte (PAOO).",
                "Foloseste Java AWT pentru randare si control.",
                " ",
                "Speram sa va bucurati de aventura!"
        };

        // Calculeaza inaltimea totala a dreptunghiului bazat pe text
        int totalTextHeight = 0;
        for (String line : infoLines) {
            totalTextHeight += textFm.getHeight() + 5;
        }
        int rectHeight = totalTextHeight + 2 * rectPadding;
        int rectY = 150 - textFm.getAscent() - rectPadding;

        // Desenam dreptunghiul semi-transparent
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(rectX, rectY, rectWidth, rectHeight);


        // Desenarea informatiilor
        g.setFont(textFont);
        g.setColor(textColor);
        int startY = 150;

        for (String line : infoLines) {
            int lineWidth = textFm.stringWidth(line);
            g.drawString(line, rectX + (rectWidth - lineWidth) / 2, startY);
            startY += textFm.getHeight() + 5;
        }

        // Instructiune de revenire
        g.setFont(instructionFont);
        g.setColor(new Color(255, 215, 0));
        String instruction = "Apasa ESC pentru a reveni la meniu.";
        int instrWidth = g.getFontMetrics().stringWidth(instruction);
        g.drawString(instruction, (refLink.GetWidth() - instrWidth) / 2, refLink.GetHeight() - 50);

        // Bordura decorativa
        g.setColor(titleColor);
        g.drawRect(20, 20, refLink.GetWidth() - 40, refLink.GetHeight() - 40);
    }
}