import java.util.ArrayList;

public class ShortMovesThread extends Thread {
    // <-- in
    public int hold, rotation, direction;
    public Game game;
    // --> out
    public ArrayList<MoveScore> retList;
    public void run() {
        retList = new ArrayList<MoveScore>();
        boolean repeat = true;
        int moveAmount = direction;
        while (repeat) {
            Game newGame = game.getGame();
            String moveList = "";
            if (hold == 1) { newGame.holdPiece(); moveList += "h"; }
            switch (rotation) {
                case 0: break;
                case 1: newGame.rotateCW(); moveList += "."; break;
                case 2: newGame.rotate180(); moveList += ";"; break;
                case 3: newGame.rotateCCW(); moveList += ","; break;
            }
            for (int i=0; i<moveAmount; i++) {
                if (direction == 0) {
                    repeat = newGame.moveLeft(); // if last move cannot execute, stop repeating
                    moveList += "a";
                } else {
                    repeat = newGame.moveRight(); // if last move cannot execute, stop repeating
                    moveList += "d";
                }
            }
            if (!repeat) break;
            newGame.hardDrop();
            moveList += "w";
            double score = Score.scoreTotal(newGame);
            moveAmount++;
            retList.add(new MoveScore(moveList, score));
        }
    }
}
