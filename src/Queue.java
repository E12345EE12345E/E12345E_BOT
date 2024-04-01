import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Queue {
    public List<Piece> queue = new ArrayList<Piece>();

    public Queue() {
        this.updateQueue();
    }

    public Queue(Boolean empty) {
        if (!empty) { this.updateQueue(); }
    }

    public Queue getQueue() {
        Queue returnQueue = new Queue(true);
        for (int i=0; i<this.queue.size(); i++) {
            returnQueue.queue.add(new Piece(this.queue.get(i).id));
        }
        return returnQueue;
    }

    public void updateQueue() {
        while (this.queue.size() < Consts.MinQueueLength) {
            if (Consts.Use7Bag) {
                add7Bag();
            } else {
                addRandom();
            }
        }
    }

    public void addRandom() {
        int randompieceid = 1 + new Random().nextInt(7);
        queue.add(new Piece(randompieceid));
    }

    public void add7Bag() {
        List<Piece> bag = new ArrayList<Piece>();
        for (int i=1; i<=7; i++) {
            bag.add(new Piece(i));
        }
        Collections.shuffle(bag);
        queue.addAll(bag);
    }

    public static void PrintQueueDisplay(Queue queue) {
        System.out.print("\u001B[37mQ: ");
        for (int i=0; i<Math.min(queue.queue.size(),Consts.DisplayQueueLength); i++) {
            System.out.print(Piece.returnPieceColorString(queue.queue.get(i).id) + (queue.queue.get(i).id == 4 ? "() " : "[] "));
        }
        System.out.println();
    }
}