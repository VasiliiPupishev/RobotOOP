package gui;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

public class LogWindow extends JInternalFrame implements LogChangeListener, VetoableChangeListener, Settable
{
    private LogWindowSource m_logSource;
    private TextArea m_logContent;

    public LogWindow(LogWindowSource logSource)
    {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(400, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
        addVetoableChangeListener(this);
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public void windowClosing(WindowEvent we) {
        m_logSource.unregisterListener(this);
        System.out.println("aaaaaaaaaaaaaaaaaa");
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
                    m_logSource.unregisterAllListener();
                    throw new PropertyVetoException("Cancelled",null);
                }
            }
        }
    }

    private Object writeReplace() {
        int state = isIcon() ? 1 : 0;
        System.out.println("Log window state " + state);
        Point location = isIcon() ? null : getLocation();
        System.out.println("Log window position " + location);
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
