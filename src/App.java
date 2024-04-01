import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        boolean keepRunning = true;
        Game currentGame = new Game();
        Game otherGame = Consts.Player1IsBot ? new Game() : currentGame.getGame();
        Scanner scanner = new Scanner(System.in);
        String input = "";
        if (Consts.Bot1v1) {
            Game.PrintBoardDisplay(currentGame, otherGame);
        } else {
            Game.PrintBoardDisplay(currentGame);
        }

        while (keepRunning) {
            input = Consts.Player1IsBot ? Bot.compute2(currentGame, 5, 100) : scanner.next();
            if (input.charAt(0) == '/') {
                switch (input.substring(1)) {
                    case "bot":
                        long t = System.currentTimeMillis();
                        long t2 = System.currentTimeMillis();
                        for (int i=0; i<1000; i++) {
                            t = t2;
                            Game other2Game = currentGame.getGame();
                            String moveString = Bot.compute2(currentGame, 5, 150);
                            t2 = System.currentTimeMillis();
                            currentGame.doMovement(moveString, 1);
                            other2Game.doMovement(moveString);
                            Game.PrintBoardDisplay(currentGame, other2Game);
                            System.out.println("ATTACK: " + currentGame.attackSent + ", PLACED: " + currentGame.piecesPlaced + ", TIME: " + (t2-t));
                            if (t%6==0) currentGame.garbage.addGarbageToQueue(4);
                        }
                        break;
                    case "movetree":
                        long t3 = System.currentTimeMillis();
                        for (int i=0; i<100; i++) Bot.moveTree(currentGame, true);
                        long t4 = System.currentTimeMillis();
                        System.out.println("100 iterations: " + (t4-t3));
                        break;
                    case "clone":
                        currentGame = currentGame.getGame();
                        break;
                    case "piece":
                        currentGame.nextPiece();
                        break;
                    case "garbage":
                        currentGame.garbage.addGarbageToQueue(4);
                        break;
                }
            } else {
                currentGame.doMovement(input, 1);
                if (Consts.Bot1v1) {
                    if (currentGame.mostRecentLinesSent > 0) {
                        GarbageHandler.sendGarbage(currentGame, otherGame, currentGame.mostRecentLinesSent);
                        currentGame.mostRecentLinesSent = 0;
                    }
                    for (int i=0; i<input.length(); i++) {
                        if (input.charAt(i) == 'w') {
                            otherGame.doMovement(Bot.compute2(otherGame, 5, 100), 1);
                            if (otherGame.mostRecentLinesSent > 0) {
                                GarbageHandler.sendGarbage(otherGame, currentGame, otherGame.mostRecentLinesSent);
                            }
                            break;
                        }
                    }
                } else {
                    Game tempGame = new Game();
                    if (currentGame.mostRecentLinesSent > 0) {
                        GarbageHandler.sendGarbage(currentGame, tempGame, currentGame.mostRecentLinesSent);
                        currentGame.mostRecentLinesSent = 0;
                    }
                }
            }
            if (Consts.Bot1v1) {
                Game.PrintBoardDisplay(currentGame, otherGame);
            } else {
                Game.PrintBoardDisplay(currentGame);
            }
            System.out.print(Score.scoreBoard(currentGame.board) + ", ATTACK: " + currentGame.attackSent + ", PLACED: " + currentGame.piecesPlaced + ", CLEARED: " + currentGame.mostRecentLinesCleared + ", HEIGHTS: ");
            for (int i=0; i<Consts.BoardWidth; i++) { System.out.print(currentGame.board.getColumnHeights()[i]); }
            System.out.print(", COMBO: " + currentGame.combo + ", B2B: " + currentGame.b2b + ", MOST RECENT ATTACK: " + currentGame.mostRecentLinesSent + ", TSPIN TYPE: " + currentGame.mostRecentTspinType + ", GARBAGE: " + currentGame.garbage.amount);
        }
        scanner.close();
    }
}