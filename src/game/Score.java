package game;
public class Score {
    public static double getHoleWeight(Integer[] a, int tallestPoint) {
        double retval = 0;
        for (int b : a) {
            if (tallestPoint-b < tallestPoint/4) {
                retval += 6;
            } else if (tallestPoint-b < tallestPoint/2) {
                retval += 2;
            }
        }
        return retval;
    }

    public static double getWeightedHoleWeight(int a) {
        switch (a) {
            case 0: return 0;
            case 1: return 15;
            case 2: return 20;
            case 3: return 23;
            case 4: return 25;
            default: return 17+2*a;
        }
    }

    public static double getHorizontalGapWeight(int a) {
        return 20*a*(a-1);
    }

    public static double getHeightWeight(int a) {
        if (a > Consts.BoardHeight) return 2000+a;
        return (a>8) ? 2*a : -0.5*a;
    }

    public static double getHeightDifferenceWeight(int a) {
        return (a>4) ? 10*a : -2*a;
    }

    public static double getAllClearWeight(Boolean allCleared) {
        return allCleared ? -1000000 : 0;
    }

    public static double getTSDslotScore(int a, int nearestT, int averageHeight) {
        if (a == 1) {
            return 10*nearestT-40+averageHeight*4;
        }
        if (a > 1) {
            return 10*nearestT-80+averageHeight*4;
        }
        return 0;
    }

    public static double getTSTslotScore(int a, int b, int nearestT) {
        return 0;
    }

    public static double getLineClearedWeight(int a, int b) {
        if (a == 1) return (b>10) ? -3*b : 4-b;
        if (a == 2) return (b>10) ? -3*b : -b;
        if (a == 3) return (b>10) ? -4*b : -10-b*2;
        if (a == 4) return (b>10) ? -6*b : -30-b*2;
        return 0;
    }

    public static double getAttackWeight(int a) {
        return a*(a-1)*(a>6?2:1);
    }

    public static double getComboWeight(int a) {
        return a>5?-30:-2*a*(a-2);
    }

    public static double getBreakB2bWeight(Boolean a, int averageHeight) {
        return a ? Math.max(0, 200-averageHeight*12) : 0;
    }

    public static double getTspinWeight(int a, int b) {
        if (a == 1) {
            if (b == 1) return -80;
            if (b == 2) return -200;
            if (b == 3) return -240;
        }
        if (a == 2) {
            return -60;
        }
        return 0;
    }

    public static double getWasteTWeight(Boolean a, int averageHeight) {
        return 20-averageHeight;
    }

    public static double getWasteIWeight(Boolean a, int linesCleared) {
        return 36-(linesCleared-1)*(linesCleared-1)*4;
    }

    public static double scoreBoard(Board board) {
        double finalScore = 0;
        int tallestPoint = board.getTallestPoint();

        // Vertical Hole Score
        finalScore += getHoleWeight(board.getHoles(), tallestPoint);
        for (int x=0; x<Consts.BoardWidth; x++) {
            finalScore += getWeightedHoleWeight(board.countWeightedHoles(x));
        }

        // Horizontal Gap Score
        int holeCount = 0;
        Boolean tileFilled = true;
        for (int y=0; y<Consts.BoardHeight*2; y++) {
            holeCount = 0;
            tileFilled = true;
            for (int x=0; x<Consts.BoardWidth; x++) {
                if (board.tileOccupied(new PiecePos(x, y))) {
                    tileFilled = true;
                } else {
                    if (tileFilled) {
                        holeCount += 1;
                        tileFilled = false;
                    }
                }
            }
            finalScore += getHorizontalGapWeight(holeCount);
        }

        // Height Score
        finalScore += getHeightWeight(tallestPoint);

        // Height Differences Score
        int[] a = board.getColumnHeights();
        for (int x=0; x<Consts.BoardWidth-1; x++) {
            getHeightDifferenceWeight(Math.abs(a[x] - a[x+1]));
        }

        // All Clear Score
        finalScore += getAllClearWeight(tallestPoint == 0);

        return finalScore;
    }

    public static double scoreTotal(Game game) {
        double finalScore = 0;
        int tallestPoint = game.board.getTallestPoint();
        int averageHeight = game.board.getAverageHeight();

        finalScore += getTSDslotScore(game.board.findPatterns(Consts.TSD_PATTERN, true), game.closestPiece(6), averageHeight);
        //finalScore += getTSTslotScore(game.board.findPatterns(Consts.TST_PATTERN, true),game.board.findPatterns(Consts.PARTIAL_TST_PATTERN, true),game.closestPiece(6));
        finalScore += getLineClearedWeight(game.mostRecentLinesCleared, tallestPoint);
        finalScore += getAttackWeight(game.mostRecentLinesSent);
        finalScore += getComboWeight(game.combo);
        finalScore += getBreakB2bWeight(game.mostRecentLineClearBrokeB2B, averageHeight);
        finalScore += getTspinWeight(game.mostRecentTspinType, game.mostRecentLinesCleared);
        finalScore += getWasteTWeight(game.mostRecentPlacedPiece == 6 && game.mostRecentTspinType == 0, averageHeight);
        finalScore += getWasteIWeight(game.mostRecentPlacedPiece == 1, game.mostRecentLinesCleared);
        
        return scoreBoard(game.board) + finalScore;
    }
}
