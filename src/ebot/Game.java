package ebot;

public class Game {
    public static final int QLENGTH = 7;
    public Board board;
    public Piece piece;
    public int hold;
    public int[] queue;
    public boolean lastMoveWasTurn;

    public Game() {
        this.board = new Board();
        this.piece = new Piece(1);
        this.hold = 0;
        this.queue = new int[QLENGTH];
        this.lastMoveWasTurn = false;
    }

    public Boolean softDrop() {
        PiecePos[] pos = Piece.returnPiecePositions(piece.id, piece.rotation);
        piece.pos.y += 1;
        for (int a=0; a<pos.length; a++) {
            if (board.getTile(PiecePos.Combine(pos[a], piece.pos))) {
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

    public int fastInstantSoftDrop() { // 2x speed on normal case, approx identical speed on bad case
        int[] heights = board.getColumnHeights();
        PiecePos[] pos = Piece.returnPiecePositions(piece.id, piece.rotation);
        double min = 0;
        for (int a=0; a<pos.length; a++) {
            // dropped amount = piece pos of tile (from bottom up) - highest point on that tile
            double height = Board.BH*2 - (pos[a].y + piece.pos.y) - heights[(int)(pos[a].x + piece.pos.x)];
            if (height < min || a==0) {
                min = height;
            }
        }
        min--;
        if (min == 0) {
            return 0;
        } else if (min < 0) {
            return instantSoftDrop();
        } else {
            piece.pos.y += min;
            return (int)min;
        }
    }

    public Boolean rotateCW() {
        piece.rotation = (piece.rotation+1)%4;
        Boolean revertmove = false;
        for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
            if (board.getTile(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos))) {
                revertmove = true;
            }
        }
        if (!revertmove) { lastMoveWasTurn = true; return true; }
        if (piece.id == 1) {
            for (int n=0; n<Piece.WallKicksI[(piece.rotation+3)%4].length; n++) {
                revertmove = false;
                for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
                    if (board.getTile(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos, Piece.WallKicksI[(piece.rotation+3)%4][n]))) {
                        revertmove = true;
                        break;
                    }
                }
                if (!revertmove) {
                    piece.pos = PiecePos.Combine(piece.pos, Piece.WallKicksI[(piece.rotation+3)%4][n]);
                    lastMoveWasTurn = true;
                    return true;
                }
            }
        } else {
            for (int n=0; n<Piece.WallKicksMain[(piece.rotation+3)%4].length; n++) {
                revertmove = false;
                for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
                    if (board.getTile(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos, Piece.WallKicksMain[(piece.rotation+3)%4][n]))) {
                        revertmove = true;
                        break;
                    }
                }
                if (!revertmove) {
                    piece.pos = PiecePos.Combine(piece.pos, Piece.WallKicksMain[(piece.rotation+3)%4][n]);
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
            if (board.getTile(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos))) {
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
            if (board.getTile(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos))) {
                revertmove = true;
            }
        }
        if (!revertmove) { lastMoveWasTurn = true; return true; }
        if (piece.id == 1) {
            for (int n=0; n<Piece.WallKicksI[piece.rotation].length; n++) {
                revertmove = false;
                for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
                    if (board.getTile(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos, PiecePos.Invert(Piece.WallKicksI[piece.rotation][n])))) {
                        revertmove = true;
                        break;
                    }
                }
                if (!revertmove) {
                    piece.pos = PiecePos.Combine(piece.pos, PiecePos.Invert(Piece.WallKicksI[piece.rotation][n]));
                    lastMoveWasTurn = true;
                    return true;
                }
            }
        } else {
            for (int n=0; n<Piece.WallKicksMain[piece.rotation].length; n++) {
                revertmove = false;
                for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
                    if (board.getTile(PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos, PiecePos.Invert(Piece.WallKicksMain[piece.rotation][n])))) {
                        revertmove = true;
                        break;
                    }
                }
                if (!revertmove) {
                    piece.pos = PiecePos.Combine(piece.pos, PiecePos.Invert(Piece.WallKicksMain[piece.rotation][n]));
                    lastMoveWasTurn = true;
                    return true;
                }
            }
        }
        piece.rotation = (piece.rotation+1)%4;
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
                } else if (combo >= 16 && combo <= 39) {
                    linesSent = 3;
                } else if (combo >= 40 && combo <= 89) {
                    linesSent = 4;
                } else if (combo >= 90 && combo <= 149) {
                    linesSent = 5;
                } else if (combo >= 150) {
                    linesSent = 6;
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

    public void PrintBoardDisplay() {
        for (int y=0; y<Board.BH*2; y++) {
            System.out.print("\u001B[37m" + ((y-Board.BH+1)>0 ? (y-Board.BH+1) : " ") + ((y-Board.BH+1)<10 ? "  " : " "));
            for (int x=0; x<Board.BW; x++) {
                if (board.board[y][x]) {
                    System.out.print("\u001B[37m[]");
                } else {
                    Boolean hasDrawnPiece = false;
                    for (int a=0; a<Piece.returnPiecePositions(piece.id, piece.rotation).length; a++) {
                        if (PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos).x == x && PiecePos.Combine(Piece.returnPiecePositions(piece.id, piece.rotation)[a], piece.pos).y == y) {
                            System.out.print("\u001B[37m[]");
                            hasDrawnPiece = true;
                            break;
                        }
                    }
                    if (!hasDrawnPiece) System.out.print("\u001B[30m::");
                }
            }
            System.out.println();
        }
    }
}
