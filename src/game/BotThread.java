package game;

public class BotThread extends Thread {
    // <-- in
    public Game game;
    public int depth, cullAmount;
    // <-> vars
    public int garbageamount;
    // --> out
    public String retval;
    public BotThread(Game game, int depth, int cullAmount) {
        this.game = game.getGame();
        this.depth = depth;
        this.cullAmount = cullAmount;
        this.setPriority(1);
    }
    public void run() {
        if (garbageamount == 0) garbageamount = game.garbage.amount;
        retval = Bot.compute3(game, depth, cullAmount);
        try {Thread.sleep(0);} catch (InterruptedException e) {}
    }
}
