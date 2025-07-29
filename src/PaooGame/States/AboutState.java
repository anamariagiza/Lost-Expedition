package PaooGame.States;

import PaooGame.RefLinks;

import java.awt.*;
import java.awt.event.KeyEvent;

/*! \class public class AboutState extends State
    \brief Implementeaza notiunea de credentiale (about)
 */
public class AboutState extends State
{
    private final Color backgroundColor = new Color(0, 0, 0);
    private final Color textColor = new Color(175, 146, 0);
    private final Font titleFont = new Font("Papyrus", Font.BOLD, 36);
    private final Font textFont = new Font("SansSerif", Font.PLAIN, 18);
    private final Font instructionFont = new Font("SansSerif", Font.PLAIN, 14);

    private boolean escapePressed = false;

    /*! \fn public AboutState(RefLinks refLink)
        \brief Constructorul de initializare al clasei.

        \param refLink O referinta catre un obiect "shortcut", obiect ce contine o serie de referinte utile in program.
     */
    public AboutState(RefLinks refLink)
    {
        super(refLink);
        System.out.println("✓ AboutState initializat");
    }

    /*! \fn public void Update()
        \brief Actualizeaza starea curenta a meniu about.
     */
    @Override
    public void Update()
    {
        // Verifica tasta ESC pentru a reveni la meniu
        if(refLink.GetKeyManager().keys[KeyEvent.VK_ESCAPE] && !escapePressed)
        {
            escapePressed = true;
            refLink.SetState(new MenuState(refLink)); // Revine la meniu principal
        }
        else if(!refLink.GetKeyManager().keys[KeyEvent.VK_ESCAPE])
        {
            escapePressed = false;
        }
    }

    /*! \fn public void Draw(Graphics g)
        \brief Deseneaza (randeaza) pe ecran starea curenta a meniu about.

        \param g Contextul grafic in care trebuie sa deseneze starea jocului pe ecran.
     */
    @Override
    public void Draw(Graphics g)
    {
        // Desenarea fundalului
        g.setColor(backgroundColor);
        g.fillRect(0, 0, refLink.GetWidth(), refLink.GetHeight());

        // Desenarea titlului
        g.setColor(textColor);
        g.setFont(titleFont);
        FontMetrics titleFm = g.getFontMetrics();
        String title = "DESPRE JOC";
        int titleWidth = titleFm.stringWidth(title);
        g.drawString(title, (refLink.GetWidth() - titleWidth) / 2, 80);

        // Desenarea informatiilor
        g.setFont(textFont);
        FontMetrics textFm = g.getFontMetrics();
        String[] infoLines = {
                "Joc dezvoltat de: [Numele Tau/Echipa Ta]",
                "Versiune: 1.0",
                "An: " + java.time.Year.now().getValue(),
                " ", // Linia goala pentru spatiere
                "Acest joc este un proiect demonstrativ",
                "pentru disciplina Programarea Orientata pe Obiecte (POO).",
                "Foloseste Java AWT pentru randare si control.",
                " ",
                "Speram sa va bucurati de aventura!"
        };

        int startY = 150;
        int lineHeight = textFm.getHeight() + 5; // Spatiu intre linii

        for (String line : infoLines) {
            int lineWidth = textFm.stringWidth(line);
            g.drawString(line, (refLink.GetWidth() - lineWidth) / 2, startY);
            startY += lineHeight;
        }

        // Instructiune de revenire
        g.setFont(instructionFont);
        g.setColor(Color.WHITE);
        String instruction = "Apasa ESC pentru a reveni la meniu.";
        int instrWidth = g.getFontMetrics().stringWidth(instruction);
        g.drawString(instruction, (refLink.GetWidth() - instrWidth) / 2, refLink.GetHeight() - 50);

        // Bordura decorativa
        g.setColor(textColor);
        g.drawRect(20, 20, refLink.GetWidth() - 40, refLink.GetHeight() - 40);
    }
}