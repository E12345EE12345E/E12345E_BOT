package renderer;

import game.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
// import java.io.File;
// import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

//import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class World extends JPanel implements ActionListener, MouseListener {
	private static World instance = null;
	public static synchronized World get()
    {
        if (instance == null)
            instance = new World();
  
        return instance;
    }
    
    public static final Color COLOR_SHADOW_TEXT = new Color(60, 60, 60);
    public static final int RENDER_TIMER_PERIOD = 1;

    private Vec2 origin = new Vec2(0, 0);
    private Vec2 canvasSize = new Vec2(0, 0);
    public double pixelsPerUnit = 1;
    private Timer renderTimer;

    public Vec2 getOrigin() {
        return origin;
    }
    public Vec2 getCanvasSize() {
        return canvasSize;
    }
    public double getPixelsPerUnit() {
        return pixelsPerUnit;
    }

    protected World() {
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                canvasSize = new Vec2(getWidth(), getHeight());
                pixelsPerUnit = Math.floor(Math.min(canvasSize.x*0.5625, canvasSize.y)/24);
            }
        });

        if (renderTimer != null && renderTimer.isRunning()) {
            renderTimer.stop();
        }
        renderTimer = new Timer(RENDER_TIMER_PERIOD, this);
        renderTimer.start();

        this.addMouseListener(this);
    }

    // Render timer...
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == renderTimer) {
            repaint();
        }
    }

    // Mouse events...
    public void mousePressed(MouseEvent event) {
    }
    public void mouseReleased(MouseEvent event) {
    }
    public void mouseEntered(MouseEvent event) {
    }
    public void mouseExited(MouseEvent event) {
    }
    public void mouseClicked(MouseEvent event) {
    }
    
    // Draw functions...
    public void drawWorld(Graphics2D g) {
        Draw.beginRender(g);
        Draw.setBaseTransform(g.getTransform());

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Map<Object, Object> hints = new LinkedHashMap<Object, Object>();
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.addRenderingHints(hints);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        Renderer.tick(g);
    }
    public void paint(Graphics g) {
        drawWorld((Graphics2D)g);
    }
}
