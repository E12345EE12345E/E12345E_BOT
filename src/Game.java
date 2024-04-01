public class Game {
    public Board board;
    public Piece piece;
    public Queue queue;
    public Piece hold;
    public GarbageHandler garbage;

    public Boolean canHold;
    public Boolean lastMoveWasTurn;
    public int piecesPlaced;
    public int combo;
    public int b2b;
    public int attackSent;
    public int mostRecentLinesCleared;
    public int mostRecentLinesSent;
    public int mostRecentTspinType;
    public Boolean mostRecentLineClearBrokeB2B;
    public int mostRecentPlacedPiece;

    public Game() {
        reset();
    }

    public void reset() {
        this.board = new Board();
        this.piece = new Piece(0);
        this.hold = new Piece(0);
        this.queue = new Queue();
        this.garbage = new GarbageHandler(1000);
        this.canHold = true;
        this.lastMoveWasTurn = false;
        this.piecesPlaced = 0;
        this.combo = 0;
        this.b2b = 0;
        this.attackSent = 0;
        this.mostRecentLinesCleared = 0;
        this.mostRecentLinesSent = 0;
        this.mostRecentTspinType = 0;
        this.mostRecentLineClearBrokeB2B = false;
        this.mostRecentPlacedPiece = 0;
        nextPiece();
    }

    public Game getGame() {
        Game returnGame = new Game();
        returnGame.board = this.board.getBoard();
        returnGame.piece = new Piece(this.piece.id, this.piece.pos.x, this.piece.pos.y, this.piece.rotation);
        returnGame.hold = new Piece(this.hold.id);
        returnGame.queue = this.queue.getQueue();
        returnGame.garbage = this.garbage.getGarbageHandler();
        returnGame.canHold = this.canHold;
        returnGame.lastMoveWasTurn = this.lastMoveWasTurn;
        returnGame.piecesPlaced = this.piecesPlaced;
        returnGame.combo = this.combo;
        returnGame.b2b = this.b2b;
        returnGame.attackSent = this.attackSent;
        returnGame.mostRecentLinesCleared = this.mostRecentLinesCleared;
        returnGame.mostRecentLinesSent = this.mostRecentLinesSent;
        returnGame.mostRecentTspinType = this.mostRecentTspinType;
        returnGame.mostRecentLineClearBrokeB2B = this.mostRecentLineClearBrokeB2B;
        returnGame.mostRecentPlacedPiece = this.mostRecentPlacedPiece;
        return returnGame;
    }

    public Boolean rotateCW() {
        piece.rotation = (piece.rotation+1)%4;
        Boolean revertmove = false;
        for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
            if (board.tileOccupied(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos))) {
                revertmove = true;
            }
        }
        if (!revertmove) { lastMoveWasTurn = true; return true; }
        if (piece.id == 1) {
            for (int n=0; n<Consts.WallKicksI[(piece.rotation+3)%4].length; n++) {
                revertmove = false;
                for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
                    if (board.tileOccupied(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos, Consts.WallKicksI[(piece.rotation+3)%4][n]))) {
                        revertmove = true;
                        break;
                    }
                }
                if (!revertmove) {
                    piece.pos = PiecePos.Combine(piece.pos, Consts.WallKicksI[(piece.rotation+3)%4][n]);
                    lastMoveWasTurn = true;
                    return true;
                }
            }
        } else {
            for (int n=0; n<Consts.WallKicksMain[(piece.rotation+3)%4].length; n++) {
                revertmove = false;
                for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
                    if (board.tileOccupied(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos, Consts.WallKicksMain[(piece.rotation+3)%4][n]))) {
                        revertmove = true;
                        break;
                    }
                }
                if (!revertmove) {
                    piece.pos = PiecePos.Combine(piece.pos, Consts.WallKicksMain[(piece.rotation+3)%4][n]);
                    lastMoveWasTurn = true;
                    return true;
                }
            }
        }
        piece.rotation = (piece.rotation+3)%4;
        return false;
    }

    public Boolean rotate180() { // TODO: add tetrio 180 kicks
        piece.rotation = (piece.rotation+2)%4;
        Boolean revertmove = false;
        for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
            if (board.tileOccupied(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos))) {
                revertmove = true;
            }
        }
        if (!revertmove) { lastMoveWasTurn = true; return true; }
        piece.rotation = (piece.rotation+2)%4;
        return false;
    }

    public Boolean rotateCCW() {
        piece.rotation = (piece.rotation+3)%4;
        Boolean revertmove = false;
        for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
            if (board.tileOccupied(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos))) {
                revertmove = true;
            }
        }
        if (!revertmove) { lastMoveWasTurn = true; return true; }
        if (piece.id == 1) {
            for (int n=0; n<Consts.WallKicksI[piece.rotation].length; n++) {
                revertmove = false;
                for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
                    if (board.tileOccupied(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos, PiecePos.Invert(Consts.WallKicksI[piece.rotation][n])))) {
                        revertmove = true;
                        break;
                    }
                }
                if (!revertmove) {
                    piece.pos = PiecePos.Combine(piece.pos, PiecePos.Invert(Consts.WallKicksI[piece.rotation][n]));
                    lastMoveWasTurn = true;
                    return true;
                }
            }
        } else {
            for (int n=0; n<Consts.WallKicksMain[piece.rotation].length; n++) {
                revertmove = false;
                for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
                    if (board.tileOccupied(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos, PiecePos.Invert(Consts.WallKicksMain[piece.rotation][n])))) {
                        revertmove = true;
                        break;
                    }
                }
                if (!revertmove) {
                    piece.pos = PiecePos.Combine(piece.pos, PiecePos.Invert(Consts.WallKicksMain[piece.rotation][n]));
                    lastMoveWasTurn = true;
                    return true;
                }
            }
        }
        piece.rotation = (piece.rotation+1)%4;
        return false;
    }

    public Boolean moveLeft() {
        piece.pos.x -= 1;
        for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
            if (board.tileOccupied(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos))) {
                piece.pos.x += 1;
                return false;
            }
        }
        lastMoveWasTurn = false;
        return true;
    }

    public Boolean moveRight() {
        piece.pos.x += 1;
        for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
            if (board.tileOccupied(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos))) {
                piece.pos.x -= 1;
                return false;
            }
        }
        lastMoveWasTurn = false;
        return true;
    }

    public Boolean softDrop() {
        piece.pos.y += 1;
        for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
            if (board.tileOccupied(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos))) {
                piece.pos.y -= 1;
                return false;
            }
        }
        lastMoveWasTurn = false;
        return true;
    }

    public int instantSoftDrop() {
        Boolean result = true;
        int count = 0;
        while (result) {
            result = this.softDrop(); // repeats until it cant soft drop anymore
            count++;
        }
        return count - 1;
    }

    public int hardDrop() { // huge line randomly is probably fixed but we will see
        int droppedtiles = instantSoftDrop();
        for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
            board.setTile(piece.id,PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos));
        }

        // check for tspin
        int tspintype = 0;
        Boolean tspinfull = false;
        int corners = 0;
        if (droppedtiles == 0 && piece.id == 6 && lastMoveWasTurn) {
            if (
                board.tileOccupied(PiecePos.Combine(PiecePos.rotatePiecePosAroundOrigin(new PiecePos(-1, -1), piece.rotation), piece.pos)) &&
                board.tileOccupied(PiecePos.Combine(PiecePos.rotatePiecePosAroundOrigin(new PiecePos(1, -1), piece.rotation), piece.pos))
            ) {
                tspinfull = true;
            }
            if (board.tileOccupied(PiecePos.Combine(PiecePos.rotatePiecePosAroundOrigin(new PiecePos(1, 1), piece.rotation), piece.pos))) { corners++; }
            if (board.tileOccupied(PiecePos.Combine(PiecePos.rotatePiecePosAroundOrigin(new PiecePos(1, -1), piece.rotation), piece.pos))) { corners++; }
            if (board.tileOccupied(PiecePos.Combine(PiecePos.rotatePiecePosAroundOrigin(new PiecePos(-1, 1), piece.rotation), piece.pos))) { corners++; }
            if (board.tileOccupied(PiecePos.Combine(PiecePos.rotatePiecePosAroundOrigin(new PiecePos(-1, -1), piece.rotation), piece.pos))) { corners++; }
        }
        int linesCleared = board.clearFullLines();
        if (linesCleared > 0 && corners >= 3) {
            if (tspinfull) { tspintype = 1; } else { tspintype = 2; }
        }
        Boolean specialMove = (linesCleared == 4) || (tspintype > 0);
        int linesSent = getAttack(linesCleared, combo, b2b, tspintype, board.getTallestPoint()==0);
        piecesPlaced += 1;
        attackSent += linesSent;
        if (linesCleared > 0) { combo++; } else {
            combo = 0;
            if (garbage.amount > 0) garbage.addGarbageToBoard(board);
        }
        mostRecentLineClearBrokeB2B = false;
        if (specialMove) { b2b++; } else { if (linesCleared > 0 && b2b > 0) { b2b = 0; mostRecentLineClearBrokeB2B = true; } }
        mostRecentLinesCleared = linesCleared;
        mostRecentLinesSent = linesSent;
        mostRecentTspinType = tspintype;
        mostRecentPlacedPiece = piece.id;

        nextPiece();
        canHold = true;
        lastMoveWasTurn = false;

        return linesSent;
    }

    public Boolean holdPiece() {
        if (canHold) {
            if (hold.id == 0) {
                hold.id = piece.id;
                nextPiece();
            } else {
                int swapid = hold.id;
                hold.id = piece.id;
                piece = new Piece(swapid);
            }
            canHold = false;
            lastMoveWasTurn = false;
            return true;
        } else {
            return false;
        }
    }

    public void nextPiece() {
        piece = queue.queue.remove(0);
        queue.updateQueue();
    }

    public int closestPiece(int id) {
        if (piece.id == id || hold.id == id) { return 0; }
        for (int i=0; i<Consts.MinQueueLength; i++) {
            if (queue.queue.get(i).id == id) {
                return i+1;
            }
        }
        return Consts.MinQueueLength+1;
    }

    public Boolean pieceExposed() {
        Game newGame = this.getGame();
        int tallestPoint = board.getTallestPoint();
        while (Consts.BoardHeight*2-newGame.piece.pos.y<tallestPoint) {
            newGame.piece.pos.y -= 1;
            for (int a=0; a<Piece.returnPiecePositions(newGame.piece.id, newGame.piece.rotation).length; a++) {
                if (board.tileOccupied(PiecePos.Combine(Piece.returnPiecePositions(newGame.piece.id, newGame.piece.rotation)[a], newGame.piece.pos))) {
                    return false;
                }
            }
        }
        return true;
    }

    public void doMovement(String input) {
        for (int i=0; i<input.length(); i++) {
            switch (input.charAt(i)) {
                case 'w':
                    this.hardDrop();
                    break;
                case 'a':
                    this.moveLeft();
                    break;
                case 's':
                    this.instantSoftDrop();
                    break;
                case 'd':
                    this.moveRight();
                    break;
                case '.':
                    this.rotateCW();
                    break;
                case ';':
                    this.rotate180();
                    break;
                case ',':
                    this.rotateCCW();
                    break;
                case 'h':
                    this.holdPiece();
                    break;
                case 'r':
                    this.reset();
                    break;
            }
        }
    }

    public Boolean doMovement(String input, int cutoff) {
        for (int i=0; i<input.length(); i++) {
            if (input.charAt(i) == 'w') {
                cutoff -= 1;
                if (cutoff <= 0) {
                    doMovement(input.substring(0, i+1));
                    return true;
                }
            }
        }
        doMovement(input);
        return false;
    }

    public static int getAttack(int linesCleared, int combo, int b2b, int tspintype, Boolean allclear) {
        int linesSent = 0;

        if (tspintype == 0) {
            if (linesCleared == 1) {
                if (combo >= 2 && combo <= 5) {
                    linesSent = 1;
                } else if (combo >= 6 && combo <= 15) {
                    linesSent = 2;
                } else if (combo >= 16) {
                    linesSent = 3;
                }
            } else if (linesCleared == 2) {
                linesSent = 1+combo/4; // 1 1 1 1 2 2 2 2 3 3 3 3 etc
            } else if (linesCleared == 3) {
                linesSent = 2+combo/2;
            } else if (linesCleared == 4) {
                linesSent = (int)Math.floor((4+getB2BLevel(b2b)) * (1+(double)combo/4));
            }
        } else if (tspintype == 1) {
            linesSent = (int)Math.floor((2*linesCleared+getB2BLevel(b2b)) * (1+(double)combo/4));
        } else if (tspintype == 2) {
            linesSent = (int)Math.floor((getB2BLevel(b2b)+linesCleared-1) * (1+(double)combo/4));
        }

        if (allclear) {
            linesSent += 10;
        }

        return linesSent;
    }

    public static int getB2BLevel(int b2b) {
        if (b2b < 1) return 0;
        if (b2b >= 1 && b2b <= 2) return 1;
        if (b2b >= 3 && b2b <= 7) return 2;
        if (b2b >= 8 && b2b <= 23) return 3;
        if (b2b >= 24 && b2b <= 66) return 4;
        return 5; // im not gonna bother with higher b2b levels
    }

    public static void PrintBoardDisplay(Game game) {
        int[][] BOARD = game.board.board;
        Piece PIECE = game.piece;

        for (int y=0; y<Consts.BoardHeight*2; y++) {
            System.out.print("\u001B[37m" + ((y-Consts.BoardHeight+1)>0 ? (y-Consts.BoardHeight+1) : " ") + ((y-Consts.BoardHeight+1)<10 ? "  " : " "));
            for (int x=0; x<Consts.BoardWidth; x++) {
                if (BOARD[y][x] == 0) {
                    Boolean hasDrawnPiece = false;
                    for (int a=0; a<Piece.returnPiecePositions(PIECE.id, PIECE.rotation).length; a++) {
                        if (PiecePos.Combine(Piece.returnPiecePositions(PIECE.id, PIECE.rotation)[a], PIECE.pos).x == x && PiecePos.Combine(Piece.returnPiecePositions(PIECE.id, PIECE.rotation)[a], PIECE.pos).y == y) {
                            System.out.print(Piece.returnPieceColorString(PIECE.id) + "[]");
                            hasDrawnPiece = true;
                            break;
                        }
                    }
                    if (!hasDrawnPiece) {
                        //System.out.print(((y-Consts.BoardHeight+1)>0 ? ((x+y)%2 == 0 ? "\u001B[30m||" : "\u001B[30m--") : "\u001B[30m  "));
                        System.out.print(((y-Consts.BoardHeight+1)>0 ? "\u001B[30m. " : "\u001B[30m  "));
                    }
                } else {
                    System.out.print(Piece.returnPieceColorString(BOARD[y][x]) + "[]");
                }
            }
            System.out.println();
        }

        Queue.PrintQueueDisplay(game.queue);

        System.out.print("\u001B[37mH: ");
        if (game.hold.id > 0) {
            System.out.print(Piece.returnPieceColorString(game.hold.id) + (game.hold.id == 4 ? "() " : "[] "));
        }
        System.out.println("\u001B[37m");
    }

    public static void PrintBoardDisplay(Game game, Game game2) {
        int[][] BOARD = game.board.board;
        Piece PIECE = game.piece;
        int[][] BOARD2 = game2.board.board;
        Piece PIECE2 = game2.piece;

        for (int y=0; y<Consts.BoardHeight*2; y++) {
            System.out.print("\u001B[37m" + ((y-Consts.BoardHeight+1)>0 ? (y-Consts.BoardHeight+1) : " ") + ((y-Consts.BoardHeight+1)<10 ? "  " : " "));
            for (int x=0; x<Consts.BoardWidth; x++) {
                if (BOARD[y][x] == 0) {
                    Boolean hasDrawnPiece = false;
                    for (int a=0; a<Piece.returnPiecePositions(PIECE.id, PIECE.rotation).length; a++) {
                        if (PiecePos.Combine(Piece.returnPiecePositions(PIECE.id, PIECE.rotation)[a], PIECE.pos).x == x && PiecePos.Combine(Piece.returnPiecePositions(PIECE.id, PIECE.rotation)[a], PIECE.pos).y == y) {
                            System.out.print(Piece.returnPieceColorString(PIECE.id) + "[]");
                            hasDrawnPiece = true;
                            break;
                        }
                    }
                    if (!hasDrawnPiece) {
                        //System.out.print(((y-Consts.BoardHeight+1)>0 ? ((x+y)%2 == 0 ? "\u001B[30m||" : "\u001B[30m--") : "\u001B[30m  "));
                        System.out.print(((y-Consts.BoardHeight+1)>0 ? "\u001B[30m. " : "\u001B[30m  "));
                    }
                } else {
                    System.out.print(Piece.returnPieceColorString(BOARD[y][x]) + "[]");
                }
            }
            System.out.print("\u001B[37m        " + ((y-Consts.BoardHeight+1)>0 ? (y-Consts.BoardHeight+1) : " ") + ((y-Consts.BoardHeight+1)<10 ? "  " : " "));
            for (int x=0; x<Consts.BoardWidth; x++) {
                if (BOARD2[y][x] == 0) {
                    Boolean hasDrawnPiece = false;
                    for (int a=0; a<Piece.returnPiecePositions(PIECE2.id, PIECE2.rotation).length; a++) {
                        if (PiecePos.Combine(Piece.returnPiecePositions(PIECE2.id, PIECE2.rotation)[a], PIECE2.pos).x == x && PiecePos.Combine(Piece.returnPiecePositions(PIECE2.id, PIECE2.rotation)[a], PIECE2.pos).y == y) {
                            System.out.print(Piece.returnPieceColorString(PIECE2.id) + "[]");
                            hasDrawnPiece = true;
                            break;
                        }
                    }
                    if (!hasDrawnPiece) {
                        //System.out.print(((y-Consts.BoardHeight+1)>0 ? ((x+y)%2 == 0 ? "\u001B[30m||" : "\u001B[30m--") : "\u001B[30m  "));
                        System.out.print(((y-Consts.BoardHeight+1)>0 ? "\u001B[30m. " : "\u001B[30m  "));
                    }
                } else {
                    System.out.print(Piece.returnPieceColorString(BOARD2[y][x]) + "[]");
                }
            }
            System.out.println();
        }

        Queue.PrintQueueDisplay(game.queue);

        System.out.print("\u001B[37mH: ");
        if (game.hold.id > 0) {
            System.out.print(Piece.returnPieceColorString(game.hold.id) + (game.hold.id == 4 ? "() " : "[] "));
        }
        System.out.println("\u001B[37m");
    }
}
