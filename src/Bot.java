import java.util.ArrayList;
import java.util.List;

public class Bot {
    public static String compute1(Game game, int depth, int cullAmount) { // "laser" looking search lol
        List<MoveScore> moveList = recurse1(game, "", depth, 0, cullAmount);
        MoveScore topScore = new MoveScore("", 100000000);
        for (int i=0; i<moveList.size(); i++) {
            if (moveList.get(i).score < topScore.score) {
                topScore = new MoveScore(moveList.get(i).movelist, moveList.get(i).score);
            }
        }
        System.out.println(topScore.movelist + ":" + topScore.score);
        return topScore.movelist;
    }

    public static String compute2(Game game, int depth, int cullAmount) { // wide search
        return recurse2(game, new ArrayList<MoveScore>(), depth, 1.1, cullAmount).get(0).movelist;
    }

    public static List<MoveScore> recurse1(Game game, String moves, int depth, double score, int cullAmount) {
        Game newGame = game.getGame();
        newGame.doMovement(moves);
        List<MoveScore> moveTree = moveTree(newGame, game.board.countHoles() > 0);
        List<MoveScore> moveList = new ArrayList<MoveScore>();
        MoveScore.sort(moveTree);
        for (int i=0; i<Math.min(cullAmount,moveTree.size()); i++) {
            moveList.add(moveTree.get(i));
        }
        List<MoveScore> totalMoveList = new ArrayList<MoveScore>();
        for (int i=0; i<moveList.size(); i++) {
            if (depth > 1) {
                //System.out.println((int)Math.pow(cullAmount,depth));
                //totalMoveList.addAll(recurse1(game, moves + moveList.get(i).movelist, depth-1, score + moveList.get(i).score, (int)((1.0/(1+100.0*((double)i/moveList.size())))*cullAmount)));
                int a = 0;
                if (i==0) {
                    a = cullAmount;
                } else if (i<cullAmount/5) {
                    a = cullAmount/5;
                } else {
                    a = 1;
                }
                totalMoveList.addAll(recurse1(game, moves + moveList.get(i).movelist, depth-1, score + moveList.get(i).score*depth, a));
            } else { // "Bottom" of recursive
                Game newScoreGame = newGame.getGame();
                newScoreGame.doMovement(moveList.get(i).movelist);
                totalMoveList.add(new MoveScore(moves + moveList.get(i).movelist, score + Score.scoreTotal(newScoreGame)));
                
                if (Consts.PrintBotMoves) {
                    MoveScore.sort(totalMoveList);
                    System.out.print(i + ": " + moves + ":" + score + "    ");
                    System.out.print(moveList.get(i).movelist + ":" + moveList.get(i).score + "    ");
                    System.out.println(moves + moveList.get(i).movelist + ":" + Score.scoreTotal(newScoreGame) + "[" + (score + Score.scoreTotal(newScoreGame)) + "]");
                    //Game.PrintBoardDisplay(newGame);
                    //Game.PrintBoardDisplay(newScoreGame);
                }
            }
        }
        return totalMoveList;
    }

    public static List<MoveScore> recurse2(Game game, List<MoveScore> moves, int depth, double scoreDecay, int cullAmount) {
        List<MoveScore> moveList = new ArrayList<MoveScore>();
        for (int i=0; i<moves.size(); i++) {
            Game newGame = game.getGame();
            newGame.doMovement(moves.get(i).movelist);
            // garbage prediction (TODO: MAKE THIS BETTER)
            if (newGame.board.getTallestPoint() < 14 && newGame.combo == 0 && newGame.piecesPlaced > 10) {
                newGame.garbage.addGarbageToQueue(4);
                newGame.garbage.addGarbageToBoard(newGame.board);
            } else if (newGame.board.getTallestPoint() < 16 && newGame.combo == 0 && newGame.piecesPlaced > 10) {
                newGame.garbage.addGarbageToQueue(2);
                newGame.garbage.addGarbageToBoard(newGame.board);
            }
            // garbage prediction above
            List<MoveScore> secondaryList = moveTree(newGame, game.board.countHoles() > 0);
            for (int j=0; j<secondaryList.size(); j++) {
                moveList.add(new MoveScore(moves.get(i).movelist + secondaryList.get(j).movelist, moves.get(i).score*scoreDecay + secondaryList.get(j).score));
            }
        }
        if (moves.size() == 0) {
            Game newGame = game.getGame();
            List<MoveScore> secondaryList = moveTree(newGame, game.board.countHoles() > 0);
            for (int j=0; j<secondaryList.size(); j++) {
                moveList.add(new MoveScore(secondaryList.get(j).movelist, secondaryList.get(j).score));
            }
        }
        MoveScore.sort(moveList);
        List<MoveScore> totalMoveList = new ArrayList<MoveScore>();
        for (int i=0; i<Math.min(cullAmount, moveList.size()); i++) {
            totalMoveList.add(moveList.get(i));
            if (Consts.PrintBotMoves) { System.out.println(moveList.get(i).movelist); }
        }
        if (depth == 1) {
            return totalMoveList;
        }
        return recurse2(game, totalMoveList, depth-1, scoreDecay, cullAmount);
    }

    public static List<MoveScore> moveTree(Game game, Boolean longMoves) { // TODO: fix tsm (currently ignores tsm)
        List<MoveScore> returnList = new ArrayList<MoveScore>();
        // short moves
        ArrayList<ShortMovesThread> threads = new ArrayList<ShortMovesThread>();
        for (int hold=0; hold<2; hold++) {
            for (int rotation=0; rotation<(hold == 0 ? (game.piece.id == 4 ? 1 : 4) : (game.hold.id == 4 ? 1 : 4)); rotation++) {
                for (int direction=0; direction<2; direction++) {
                    ShortMovesThread temp = new ShortMovesThread();
                    temp.hold = hold;
                    temp.rotation = rotation;
                    temp.direction = direction;
                    temp.game = game;
                    temp.run();
                    threads.add(temp);
                }
            }
        }
        boolean finished = false;
        while (!finished) {
            finished = true;
            for (int i=0; i<threads.size(); i++) if (threads.get(i).isAlive()) finished = false;
        }
        for (int i=0; i<threads.size(); i++) {
            returnList.addAll(threads.get(i).retList);
        }
        if (!longMoves) { return returnList; }
        // long moves
        ArrayList<LongMovesThread> threads2 = new ArrayList<LongMovesThread>();
        for (int hold=0; hold<2; hold++) {
            for (int rotation=0; rotation<(hold == 0 ? (game.piece.id == 4 ? 1 : 4) : (game.hold.id == 4 ? 1 : 4)); rotation++) {
                for (int direction=0; direction<2; direction++) {
                    LongMovesThread temp = new LongMovesThread();
                    temp.hold = hold;
                    temp.rotation = rotation;
                    temp.direction = direction;
                    temp.game = game;
                    temp.run();
                    threads2.add(temp);
                }
            }
        }
        finished = false;
        while (!finished) {
            finished = true;
            for (int i=0; i<threads2.size(); i++) if (threads2.get(i).isAlive()) finished = false;
        }
        for (int i=0; i<threads2.size(); i++) {
            returnList.addAll(threads2.get(i).retList);
        }
        return returnList;
    }

    /*public static List<MoveScore> moveTree(Game game, Boolean longMoves) {
        Boolean repeat = true;
        int moveAmount = 0;
        List<MoveScore> returnList = new ArrayList<MoveScore>();
        double score = 0;
        // short moves
        for (int hold=0; hold<2; hold++) {
            for (int rotation=0; rotation<(hold == 0 ? (game.piece.id == 4 ? 1 : 4) : (game.hold.id == 4 ? 1 : 4)); rotation++) {
                for (int direction=0; direction<2; direction++) {
                    repeat = true;
                    moveAmount = direction; // when moving right it will skip instantly dropping it since thats covered by the moving left portion
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
                        newGame.hardDrop();
                        moveList += "w";
                        score = Score.scoreTotal(newGame);
                        moveAmount += 1;
                        if (repeat) {
                            returnList.add(new MoveScore(moveList, score));
                        }
                    }
                }
            }
        }
        if (!longMoves) { return returnList; }
        // long moves
        repeat = true;
        moveAmount = 0;
        for (int hold=0; hold<2; hold++) {
            for (int rotation=0; rotation<(hold == 0 ? (game.piece.id == 4 ? 1 : 4) : (game.hold.id == 4 ? 1 : 4)); rotation++) {
                for (int direction=0; direction<2; direction++) {
                    repeat = true;
                    moveAmount = direction;
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
                        newGame.instantSoftDrop();
                        moveList += "s";
                        moveAmount += 1;
                        if (repeat) {
                            for (int i=0; i<8; i++) { // 8 for 2 turn spins, 4 for 1 turn
                                for (int b=0; b<1; b++) {
                                    Game newNewGame = newGame.getGame();
                                    String newMoveList = moveList;
                                    Boolean repeat1 = true;
                                    switch (b) {
                                        case 0:
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
                                        returnList.add(new MoveScore(newMoveList, Score.scoreTotal(newNewGame)));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return returnList;
    }*/
}