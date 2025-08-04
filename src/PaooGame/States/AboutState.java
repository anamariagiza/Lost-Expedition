package PaooGame.States;

import PaooGame.RefLinks;
import PaooGame.Graphics.Assets;

import java.awt.*;
import java.awt.event.KeyEvent;

/*!
 * \class public class AboutState extends State
 * \brief Implementeaza notiunea de credentiale (about)
 */
public class AboutState extends State
{
    private final Color backgroundColor = new Color(0, 0, 0); // Fallback color
    private final Color textColor = new Color(255, 255, 255); // Text alb pentru lizibilitate
    private final Color titleColor = new Color(255, 215, 0); // Aur pentru titlu
    private final Font titleFont = new Font("Papyrus", Font.BOLD, 36);
    private final Font textFont = new Font("SansSerif", Font.PLAIN, 18);
    private final Font instructionFont = new Font("SansSerif", Font.PLAIN, 14);
    private boolean escapePressed = false;

    /*!
     * \fn public AboutState(RefLinks refLink)
     * \brief Constructorul de initializare al clasei.
     * \param refLink O referinta catre un obiect "shortcut", obiect ce contine o serie de referinte utile in program.
     */
    public AboutState(RefLinks refLink)
    {
        super(refLink);
        System.out.println("✓ AboutState initializat");
    }

    /*!
     * \fn public void Update()
     * \brief Actualizeaza starea curenta a meniu about.
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

    /*!
     * \fn public void Draw(Graphics g)
     * \brief Deseneaza (randeaza) pe ecran starea curenta a meniu about.
     * \param g Contextul grafic in care trebuie sa deseneze starea jocului pe ecran.
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
            System.err.println("EROARE DEBUG: Fundal 'About' desenat cu culoare solidă (Assets.backgroundMenu este NULL).");
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

        // NOU: Dreptunghi semi-transparent în spatele textului informațional
        int rectPadding = 20;
        int rectWidth = 600;
        int rectX = (refLink.GetWidth() - rectWidth) / 2;

        // Conținutul mesajului "DESPRE JOC"
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

        // Calculează înălțimea totală a dreptunghiului bazat pe text
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