package gui;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.Serializable;

import javax.swing.*;

public class GameWindow extends JInternalFrame implements VetoableChangeListener, Settable
{
    private final GameVisualizer m_visualizer;

    public GameWindow()
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        addVetoableChangeListener(this);
    }

    public void windowClosing(WindowEvent we) {
        System.out.println("закрытие");
        String ObjButtons[] = {"Yes", "No"};
        int PromptResult = JOptionPane.showOptionDialog(null,
                "Are you sure you want to exit?", "Online Examination System",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                ObjButtons, ObjButtons[1]);
        if (PromptResult == 0) {
            System.exit(0);
        }
    }

    public void vetoableChange(PropertyChangeEvent pce)
            throws PropertyVetoException {
        if (pce.getPropertyName().equals(IS_CLOSED_PROPERTY)) {
            boolean changed = (Boolean) pce.getNewValue();
            if (changed) {
                int option = JOptionPane.showOptionDialog(this, "Close " +
                                getTitle() + "?",
                        "Close Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, null, null);
                if (option != JOptionPane.YES_OPTION) {
                    throw new PropertyVetoException("Cancelled",null);
                }
            }
        }
    }

    private Object writeReplace() {
        int state = isIcon() ? 1 : 0;
        System.out.println("Game window state " + state);
        Point location = isIcon() ? null : getLocation();
        System.out.println("Game window position " + location);
        return new Settings(getSize(), location, state, getClass().getSimpleName());
    }

    public void setSettings(Settings settings) {
        if (settings.state == 1) {
            try {
                setIcon(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        }
        if (settings.location == null) {
            setSize(settings.screenSize);
        } else {
            setBounds(settings.location.x, settings.location.y,
                    settings.screenSize.width,
                    settings.screenSize.height);
        }
    }
}
