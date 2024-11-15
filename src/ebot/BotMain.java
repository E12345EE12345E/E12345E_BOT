package ebot;

public class BotMain extends Thread {
    // in
    private Game g;
    private int depth, n;
    // out
    private String moveString;
    // methods
    public BotMain(Game g, int depth, int n) {
        this.g = g;
        this.depth = depth;
        this.n = n;
    }

    public void run() {
        g.instantSoftDrop();
    }
}
