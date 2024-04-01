public class Consts {
    public static int BoardHeight = 20;
    public static int BoardWidth = 10;
    public static int MinQueueLength = 20;
    public static int DisplayQueueLength = 7;
    public static Boolean Use7Bag = true;
    public static Boolean Bot1v1 = true;
    public static Boolean Player1IsBot = false;

    public static Boolean PrintBotMoves = false; // will introduce more delay

    public static PiecePos[][] WallKicksMain = { // [starting rotation state, test number] for cw, first one is ending rotation state for ccw
        {new PiecePos(-1,0), new PiecePos(-1,-1), new PiecePos(0,2), new PiecePos(-1,2)}, // 0
        {new PiecePos(1,0), new PiecePos(1,1), new PiecePos(0,-2), new PiecePos(1,-2)}, // 1
        {new PiecePos(1,0), new PiecePos(1,-1), new PiecePos(0,2), new PiecePos(1,2)}, // 2
        {new PiecePos(-1,0), new PiecePos(-1,1), new PiecePos(0,-2), new PiecePos(-1,-2)}, // 3
    };

    public static PiecePos[][] WallKicksI = {
        {new PiecePos(-2,0), new PiecePos(1,0), new PiecePos(1,-2), new PiecePos(-2,1)},
        {new PiecePos(-1,0), new PiecePos(2,0), new PiecePos(-1,-2), new PiecePos(2,1)},
        {new PiecePos(2,0), new PiecePos(-1,0), new PiecePos(2,-1), new PiecePos(-1,1)},
        {new PiecePos(-2,0), new PiecePos(1,0), new PiecePos(-2,-1), new PiecePos(1,2)},
    };

    public static int[][] TSD_PATTERN = {
        {1,0,0},
        {0,0,0},
        {1,0,1},
    };

    public static int[][] PARTIAL_TST_PATTERN = {
        {2,0,0,0},
        {1,0,1,1},
        {1,0,0,1},
        {1,0,1,1},
        {1,1,2,1},
    };

    public static int[][] TST_PATTERN = { // modified to also check stsd
        {2,1,0,0},
        {2,0,0,0},
        {1,0,1,1},
        {1,0,0,1},
        {1,0,2,1},
    };
}
