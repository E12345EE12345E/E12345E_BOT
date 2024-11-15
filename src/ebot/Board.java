package ebot;

public class Board {
    public static final int BH = 20;
    public static final int BW = 10;
    public boolean[][] board;
    public int maxheight;

    public Board() {
        board = new boolean[BH*2][BW];
    }

    public Board(Board b) {
        this();
        for (int y=BH*2-b.maxheight; y<BH*2; y++) {
            System.arraycopy(b.board[y], 0, this.board[y], 0, BW);
        }
    }

    public boolean getTile(PiecePos pos) {
        if (pos.x < 0) { return true; }
        if (pos.x >= BW) { return true; }
        if (pos.y < 0) { return true; }
        if (pos.y >= BH*2) { return true; }
        return board[(int)pos.y][(int)pos.x];
    }

    public void setTile(PiecePos pos) {
        board[(int)pos.y][(int)pos.x] = true;
    }

    public void setTile(boolean a, PiecePos pos) {
        board[(int)pos.y][(int)pos.x] = a;
    }

    public int[] getColumnHeights() {
        int[] returnHeights = new int[BW];
        for (int x=0; x<BW; x++) {
            for (int y=0; y<BH*2; y++) {
                if (board[y][x]) {
                    returnHeights[x] = BH*2 - y;
                    break;
                }
            }
        }
        return returnHeights;
    }
}
