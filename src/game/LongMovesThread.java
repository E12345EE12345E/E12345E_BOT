package game;
import java.util.ArrayList;

public class LongMovesThread extends Thread {
    // <-- in
    public int hold, rotation, direction;
    public Game game;
    // --> out
    public ArrayList<MoveScore> retList;
    public LongMovesThread() {
        this.setPriority(1);
    }
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
            newGame.fastInstantSoftDrop();
            moveList += "s";
            moveAmount++;
            if (repeat) {
                for (int i=1; i<4; i++) { // 8 for 2 turn spins, 4 for 1 turn
                    for (int b=0; b<1; b++) {
                        Game newNewGame = newGame.getGame();
                        String newMoveList = moveList;
                        Boolean repeat1 = true;
                        switch (b) {
                            case 1:
                                while (repeat1) {
                                    repeat1 = newNewGame.moveLeft();
                                    if (repeat1) { newMoveList += "a"; }
                                }
                                break;
                            case 2:
                                while (repeat1) {
                                    repeat1 = newNewGame.moveRight();
                                    if (repeat1) { newMoveList += "d"; }
                                }
                                break;
                        }
                        switch (i) {
                            case 0: break;
                            case 1: newNewGame.rotateCW(); newMoveList += "."; break;
                            case 2: newNewGame.rotate180(); newMoveList += ";"; break;
                            case 3: newNewGame.rotateCCW(); newMoveList += ","; break;
                            case 4: newNewGame.rotateCW(); newNewGame.instantSoftDrop(); newNewGame.rotateCW(); newMoveList += ".s."; break;
                            case 5: newNewGame.rotateCCW(); newNewGame.instantSoftDrop(); newNewGame.rotateCCW(); newMoveList += ",s,"; break;
                            case 6: newNewGame.rotateCW(); newNewGame.instantSoftDrop(); newNewGame.rotateCW(); newNewGame.instantSoftDrop(); newNewGame.rotateCW(); newMoveList += ".s.s."; break;
                            case 7: newNewGame.rotateCCW(); newNewGame.instantSoftDrop(); newNewGame.rotateCCW(); newNewGame.instantSoftDrop(); newNewGame.rotateCCW(); newMoveList += ",s,s,"; break;
                        }
                        if (!newNewGame.pieceExposed()) {
                            newNewGame.hardDrop();
                            newMoveList += "w";
                            retList.add(new MoveScore(newMoveList, Score.scoreTotal(newNewGame)));
                        }
                    }
                }
            }
        }
    }
}
