package renderer;

import game.Renderer;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Window extends JFrame implements KeyListener {
    private static Window instance = null;
	protected static synchronized Window get()
    {
        if (instance == null)
            instance = new Window();
  
        return instance;
    }

    public static final String WINDOW_TITLE = "windowtitle";
    public static final int DEFAULT_WINDOW_WIDTH = 960;
    public static final int DEFAULT_WINDOW_HEIGHT = 600;

    protected Window() {
        super(WINDOW_TITLE);
        setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
        setMinimumSize(new Dimension(DEFAULT_WINDOW_WIDTH / 2, DEFAULT_WINDOW_HEIGHT / 2));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    protected static boolean create() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                get().add(World.get());
                get().setVisible(true);
            }
        });
        return true;
    }

    public void keyPressed(KeyEvent event) {
        Renderer.keyPressed(event);
    }
    public void keyReleased(KeyEvent event) {
        Renderer.keyReleased(event);
    }
    public void keyTyped(KeyEvent e) {    
    }
}
