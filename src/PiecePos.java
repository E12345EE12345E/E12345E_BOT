public class PiecePos {
    public double x;
    public double y;

    public PiecePos(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setpos(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static PiecePos Combine(PiecePos pos1, PiecePos pos2) {
        return new PiecePos(pos1.x + pos2.x, pos1.y + pos2.y);
    }

    public static PiecePos Combine(PiecePos pos1, PiecePos pos2, PiecePos pos3) {
        return new PiecePos(pos1.x + pos2.x + pos3.x, pos1.y + pos2.y + pos3.y);
    }

    public static PiecePos Invert(PiecePos pos) {
        return new PiecePos(-pos.x, -pos.y);
    }

    public static PiecePos rotatePiecePosAroundOrigin(PiecePos pos1, int rotation) {
        switch (rotation) { // Rotation
            case 1:
                return new PiecePos(-pos1.y,pos1.x);
            case 2:
                return new PiecePos(-pos1.x,-pos1.y);
            case 3:
                return new PiecePos(pos1.y,-pos1.x);
            }
        return new PiecePos(pos1.x, pos1.y);
    }
}
