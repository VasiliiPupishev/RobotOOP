package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame implements Serializable, Settable
{
    private int wightWindow = 400;
    private int heightWindow = 400;
    private int posXWindow = 0;
    private int posYWindow = 0;
    private String stateWindow = "Normal";

    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);


        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        MenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar.initMenu());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitMainWindow();
            }
        });

        //addVetoableChangeListener(this);
        //addWindowListener(new WindowAdapter() {

          //  @Override
          //  public void windowClosing(WindowEvent we) {
          //      String ObjButtons[] = {"Yes", "No"};
          //      int PromptResult = JOptionPane.showOptionDialog(null,
           //             "Are you sure you want to exit?", "Online Examination System",
          //              JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
           //             ObjButtons, ObjButtons[1]);
          //      if (PromptResult == 0) {
            //        System.exit(0);
          //      }
         //   }
        //});

        readSettings();
    }

    private boolean confirmClosing(Component window) {
        Object[] options = {"Да", "Нет"};
        int answer = JOptionPane.showOptionDialog(window,
                "Закрыть окно?",
                "Выход",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        return answer == 0;
    }

    private void serialize() {
        File file = new File("data.bin");
        try (OutputStream os = new FileOutputStream(file)) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(os))) {
                oos.writeObject(this);
                for (JInternalFrame frame: desktopPane.getAllFrames()) {
                    oos.writeObject(frame);
                    System.out.println("Serialize frame: " + frame);
                }
                oos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object writeReplace() {
        return new Settings(getSize(), getLocationOnScreen(), getState(), getClass().getSimpleName());
    }

    private void exitMainWindow() {
        if (confirmClosing(gui.MainApplicationFrame.this)) {
            serialize();
            System.exit(0);
        }
    }

    private void readSettings() {
        File file = new File("data.bin");
        if (file.exists()) {
            try (InputStream is = new FileInputStream(file)) {
                try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is))) {
                    Settings settings = (Settings)ois.readObject();
                    setSettings(settings);
                    for (int i = 0; i < desktopPane.getAllFrames().length; i++) {
                        settings = (Settings) ois.readObject();
                        for (JInternalFrame frame: desktopPane.getAllFrames()) {
                            if (frame.getClass().getSimpleName().equals(settings.windowName)) {
                                ((Settable)frame).setSettings(settings);
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSettings(Settings settings) {
        setState(settings.state);
        setBounds(settings.location.x, settings.location.y,
                settings.screenSize.width,
                settings.screenSize.height);
    }

    private  int loadSettingWindow(){
        return 0;

    }

    private int saveSettingWindow(){
        return 0;
    }

    protected MenuBar createMenuBar()
    {
        MenuBar menuBar = new MenuBar(this);

        return menuBar;
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
}
