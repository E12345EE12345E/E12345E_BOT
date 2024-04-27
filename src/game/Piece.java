package game;
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

    public static String returnPieceColorString(int id) {
        switch (id) {
            case 1: return "\u001B[36m";
            case 2: return "\u001B[34m";
            case 3: return "\u001B[33m";
            case 4: return "\u001B[33m";
            case 5: return "\u001B[32m";
            case 6: return "\u001B[35m";
            case 7: return "\u001B[31m";
        }
        return "\u001B[37m";
    }

    public static PiecePos pieceSpawnPosition(int id) {
        switch (id) {
            case 1: return new PiecePos(Consts.BoardWidth/2 - 0.5, Consts.BoardHeight - 0.5);
            case 2: return new PiecePos(Consts.BoardWidth/2 - 1, Consts.BoardHeight);
            case 3: return new PiecePos(Consts.BoardWidth/2 - 1, Consts.BoardHeight);
            case 4: return new PiecePos(Consts.BoardWidth/2 - 0.5, Consts.BoardHeight - 0.5);
            case 5: return new PiecePos(Consts.BoardWidth/2 - 1, Consts.BoardHeight);
            case 6: return new PiecePos(Consts.BoardWidth/2 - 1, Consts.BoardHeight);
            case 7: return new PiecePos(Consts.BoardWidth/2 - 1, Consts.BoardHeight);
        }
        return new PiecePos(1, 20);
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
}
