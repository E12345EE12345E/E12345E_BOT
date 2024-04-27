package game;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GarbageHandler {
    public List<Garbage> garbagequeue;
    public long seed;
    public int amount;

    public GarbageHandler(long seed) {
        garbagequeue = new ArrayList<Garbage>();
        this.seed = seed;
        this.amount = 0;
    }

    public GarbageHandler getGarbageHandler() {
        GarbageHandler duplicate = new GarbageHandler(seed);
        duplicate.amount = this.amount;
        for (int i=0; i<this.garbagequeue.size(); i++) {
            duplicate.garbagequeue.add(new Garbage(this.garbagequeue.get(i).amount, this.garbagequeue.get(i).column));
        }
        return duplicate;
    }

    public void addGarbageToQueue(int a) {
        if (a > 0) {
            Random random = new Random(seed);
            garbagequeue.add(new Garbage(a, random.nextInt(Consts.BoardWidth)));
            seed = random.nextLong();
            amount += a;
        }
    }

    public void addGarbageToBoard(Board board) {
        int totalAmnt = 0;
        for (int i=0; i<this.garbagequeue.size();) {
            if (totalAmnt < Consts.GarbageCap) {
                totalAmnt += this.garbagequeue.get(i).amount;
                int diff = totalAmnt - Consts.GarbageCap;
                if (totalAmnt > Consts.GarbageCap) {
                    board.addGarbageToBoard(this.garbagequeue.get(i).amount - diff, this.garbagequeue.get(i).column);
                    this.garbagequeue.get(i).amount = diff;
                } else {
                    board.addGarbageToBoard(this.garbagequeue.get(i).amount, this.garbagequeue.get(i).column);
                    this.garbagequeue.remove(i);
                }
            } else {
                break;
            }
        }
        this.amount -= totalAmnt;
    }

    public static void sendGarbage(Game game1, Game game2, int amnt) {
        // cancellation
        if (game1.garbage.amount <= amnt) {
            amnt -= game1.garbage.amount;
            game1.garbage.garbagequeue = new ArrayList<Garbage>();
            game1.garbage.amount = 0;
            // sending
            if (amnt > 0) {
                game2.garbage.addGarbageToQueue(amnt);
            }
        } else {
            while (amnt > 0) {
                if (game1.garbage.garbagequeue.get(0).amount <= amnt) {
                    amnt -= game1.garbage.garbagequeue.get(0).amount;
                    game1.garbage.amount -= game1.garbage.garbagequeue.get(0).amount;
                    game1.garbage.garbagequeue.remove(0);
                } else {
                    game1.garbage.garbagequeue.get(0).amount -= amnt;
                    game1.garbage.amount -= amnt;
                    amnt = 0;
                }
            }
        }
    }

    private class Garbage {
        public int amount;
        public int column;
        public Garbage(int a, int b) {
            this.amount = a;
            this.column = b;
        }
    }
}
