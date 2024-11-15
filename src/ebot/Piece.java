package ebot;

public class Piece {
    public int id;
    public PiecePos pos;
    public int rotation;

    public Piece(int id) {
        this.id = id;
        this.pos = pieceSpawnPosition(id);
        this.rotation = 0;
    }

    public Piece(int id, double x, double y, int rotation) {
        this.id = id;
        this.pos = new PiecePos(x, y);
        this.rotation = rotation;
    }

    public static PiecePos pieceSpawnPosition(int id) {
        if (id == 1 || id == 4) {
            return new PiecePos(Board.BW/2 - 0.5, Board.BH - 1.5);
        }
        return new PiecePos(Board.BW/2 - 1, Board.BH - 1);
    }

    public static PiecePos[] returnPiecePositions(int id) {
        PiecePos[] piecepositions = {new PiecePos(0,0),new PiecePos(0,0),new PiecePos(0,0),new PiecePos(0,0)};
        switch (id) { // IJLOSTZ
            case 1:
                piecepositions[0] = new PiecePos(-1.5,-0.5);
                piecepositions[1] = new PiecePos(-0.5,-0.5);
                piecepositions[2] = new PiecePos(0.5,-0.5);
                piecepositions[3] = new PiecePos(1.5,-0.5);
                break;
            case 2:
                piecepositions[0] = new PiecePos(-1,-1);
                piecepositions[1] = new PiecePos(-1,0);
                piecepositions[2] = new PiecePos(0,0);
                piecepositions[3] = new PiecePos(1,0);
                break;
            case 3:
                piecepositions[0] = new PiecePos(-1,0);
                piecepositions[1] = new PiecePos(0,0);
                piecepositions[2] = new PiecePos(1,0);
                piecepositions[3] = new PiecePos(1,-1);
                break;
            case 4:
                piecepositions[0] = new PiecePos(0.5,0.5);
                piecepositions[1] = new PiecePos(-0.5,0.5);
                piecepositions[2] = new PiecePos(-0.5,-0.5);
                piecepositions[3] = new PiecePos(0.5,-0.5);
                break;
            case 5:
                piecepositions[0] = new PiecePos(-1,0);
                piecepositions[1] = new PiecePos(0,0);
                piecepositions[2] = new PiecePos(0,-1);
                piecepositions[3] = new PiecePos(1,-1);
                break;
            case 6:
                piecepositions[0] = new PiecePos(-1,0);
                piecepositions[1] = new PiecePos(0,0);
                piecepositions[2] = new PiecePos(0,-1);
                piecepositions[3] = new PiecePos(1,0);
                break;
            case 7:
                piecepositions[0] = new PiecePos(-1,-1);
                piecepositions[1] = new PiecePos(0,-1);
                piecepositions[2] = new PiecePos(0,0);
                piecepositions[3] = new PiecePos(1,0);
                break;
        }
        return piecepositions;
    }

    public static PiecePos[] returnPiecePositions(int id, int rotation) {
        PiecePos[] piecepositions = returnPiecePositions(id);
        for (int a=0; a<piecepositions.length; a++) {
            switch (rotation) { // Rotation
                case 1:
                    piecepositions[a].setpos(-piecepositions[a].y,piecepositions[a].x);
                    break;
                case 2:
                    piecepositions[a].setpos(-piecepositions[a].x,-piecepositions[a].y);
                    break;
                case 3:
                    piecepositions[a].setpos(piecepositions[a].y,-piecepositions[a].x);
                    break;
            }
        }
        return piecepositions;
    }

    public static final PiecePos[][] WallKicksMain = { // [starting rotation state, test number] for cw, first one is ending rotation state for ccw
        {new PiecePos(-1,0), new PiecePos(-1,-1), new PiecePos(0,2), new PiecePos(-1,2)}, // 0
        {new PiecePos(1,0), new PiecePos(1,1), new PiecePos(0,-2), new PiecePos(1,-2)}, // 1
        {new PiecePos(1,0), new PiecePos(1,-1), new PiecePos(0,2), new PiecePos(1,2)}, // 2
        {new PiecePos(-1,0), new PiecePos(-1,1), new PiecePos(0,-2), new PiecePos(-1,-2)}, // 3
    };

    public static final PiecePos[][] WallKicksI = {
        {new PiecePos(-2,0), new PiecePos(1,0), new PiecePos(1,-2), new PiecePos(-2,1)},
        {new PiecePos(-1,0), new PiecePos(2,0), new PiecePos(-1,-2), new PiecePos(2,1)},
        {new PiecePos(2,0), new PiecePos(-1,0), new PiecePos(2,-1), new PiecePos(-1,1)},
        {new PiecePos(-2,0), new PiecePos(1,0), new PiecePos(-2,-1), new PiecePos(1,2)},
    };
}