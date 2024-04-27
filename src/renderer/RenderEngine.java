package renderer;

import java.awt.event.*;

public class RenderEngine implements ActionListener {
	private static RenderEngine instance = null;
	public static synchronized RenderEngine get()
    {
        if (instance == null)
            instance = new RenderEngine();
  
        return instance;
    }
    public static boolean Create() {
        return RenderEngine.get().create();
    }

    protected boolean create() {
        if (!Window.create()) {
            return false;
        }

        return true;
    }

    public void actionPerformed(ActionEvent evt) {
        RenderEngine.get().update();
    }

    protected void update() {
    }
}
