package game;
import java.util.ArrayList;

public class Board {
    public int[][] board;

    public Board() {
        this.board = new int[Consts.BoardHeight * 2][Consts.BoardWidth];
    }

    public Board getBoard() {
        Board returnBoard = new Board();
        for (int y=0; y<Consts.BoardHeight*2; y++) {
            for (int x=0; x<Consts.BoardWidth; x++) {
                returnBoard.board[y][x] = this.board[y][x];
            }
        }
        return returnBoard;
    }

    public Boolean tileOccupied(int x, int y) {
        return this.tileOccupied(new PiecePos(x, y));
    }

    public Boolean tileOccupied(PiecePos pos) {
        if (pos.x < 0) { return true; }
        if (pos.x >= Consts.BoardWidth) { return true; }
        if (pos.y < 0) { return true; }
        if (pos.y >= Consts.BoardHeight*2) { return true; }
        return (this.board[(int)pos.y][(int)pos.x] != 0);
    }

    public void setTile(int id, PiecePos pos) {
        this.board[(int)pos.y][(int)pos.x] = id;
    }

    public int clearFullLines() {
        int linescleared = 0;
        boolean continueClearing = true;
        int y = 0;
        while (y<this.board.length) {
            continueClearing = true;
            for (int x=0; x<Consts.BoardWidth; x++) {
                if (!this.tileOccupied(new PiecePos(x, y))) {
                    continueClearing = false;
                    break;
                }
            }
            if (continueClearing) {
                for (int a=y; a>1; a--) {
                    for (int x=0; x<Consts.BoardWidth; x++) {
                        this.board[a][x] = this.board[a-1][x];
                    }
                }
                this.board[0] = new int[Consts.BoardWidth];
                linescleared++;
                //System.out.println(y);
            } else {
                y++;
            }
        }
        return linescleared;
    }

    public void addGarbageToBoard(int amount, int x) {
        for (int y=amount; y<Consts.BoardHeight*2; y++) {
            for (int a=0; a<Consts.BoardWidth; a++) {
                try {
                    board[y-amount][a] = board[y][a];
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("(" + (y-amount) + "," + y + "," + a + ")");
                }
            }
        }
        for (int y=Consts.BoardHeight*2-amount; y<Consts.BoardHeight*2; y++) {
            for (int a=0; a<Consts.BoardWidth; a++) {
                if (a==x) {
                    board[y][a] = 0;
                } else {
                    board[y][a] = 8;
                }
            }
        }
    }

    public int[] getColumnHeights() {
        int[] returnHeights = new int[Consts.BoardWidth];
        for (int x=0; x<Consts.BoardWidth; x++) {
            for (int y=0; y<Consts.BoardHeight*2; y++) {
                if (tileOccupied(x,y)) {
                    returnHeights[x] = Consts.BoardHeight*2 - y;
                    break;
                }
            }
        }
        return returnHeights;
    }

    public int getTallestPoint() {
        int tallestPoint = 0;
        int[] boardHeights = this.getColumnHeights();
        for (int i=0; i<Consts.BoardWidth; i++) {
            if (boardHeights[i] > tallestPoint) {
                tallestPoint = boardHeights[i];
            }
        }
        return tallestPoint;
    }

    public int getAverageHeight() {
        int average = 0;
        int[] boardHeights = this.getColumnHeights();
        for (int i=0; i<Consts.BoardWidth; i++) {
            average += boardHeights[i]/Consts.BoardWidth;
        }
        return average;
    }

    public int countHoles() {
        int holeCount = 0;
        Boolean tileFilled = false;
        for (int x=0; x<Consts.BoardWidth; x++) {
            tileFilled = false;
            for (int y=0; y<Consts.BoardHeight*2; y++) {
                if (tileOccupied(new PiecePos(x, y))) {
                    tileFilled = true;
                } else {
                    if (tileFilled) {
                        holeCount += 1;
                    }
                }
            }
        }
        return holeCount;
    }

    public Integer[] getHoles() {
        ArrayList<Integer> holes = new ArrayList<Integer>();
        Boolean tileFilled = false;
        for (int x=0; x<Consts.BoardWidth; x++) {
            tileFilled = false;
            for (int y=Consts.BoardHeight; y<Consts.BoardHeight*2; y++) { // will not check for holes above board height to improve performance
                if (tileOccupied(new PiecePos(x, y))) {
                    tileFilled = true;
                } else {
                    if (tileFilled) {
                        holes.add(Consts.BoardHeight*2-y);
                    }
                }
            }
        }
        Integer[] arr = new Integer[holes.size()];
        return holes.toArray(arr);
    }

    public int countWeightedHoles(int x) {
        int holeCount = 0;
        int filledCount = 0;
        Boolean tileFilled = false;
        for (int y=0; y<Consts.BoardHeight*2; y++) {
            if (tileOccupied(new PiecePos(x, y))) {
                tileFilled = true;
                filledCount += 1;
            } else {
                if (tileFilled) {
                    holeCount += filledCount;
                    filledCount = 0;
                }
            }
        }
        return holeCount;
    }

    private int findPatterns(int[][] pattern) {
        int count = 0;
        int patternwidth = pattern[0].length;
        int patternheight = pattern.length;
        if (patternheight > Consts.BoardHeight*2 || patternwidth > Consts.BoardWidth) {
            return 0;
        }
        int tallestpoint = getTallestPoint();
        for (int y=tallestpoint; y<Consts.BoardHeight*2-patternheight+1; y++) {
            for (int x=0; x<Consts.BoardWidth-patternwidth+1; x++) {
                Boolean patternMatches = true;
                for (int a=0; a<patternheight&&patternMatches; a++) {
                    for (int b=0; b<patternwidth&&patternMatches; b++) {
                        if (pattern[a][b] == 0) {
                            if (this.tileOccupied(new PiecePos(x+b, y+a))) {
                                patternMatches = false;
                                break;
                            }
                        }
                        if (pattern[a][b] == 1) {
                            if (!this.tileOccupied(new PiecePos(x+b, y+a))) {
                                patternMatches = false;
                                break;
                            }
                        }
                    }
                    if (!patternMatches) break;
                }
                if (patternMatches) {
                    count += 1;
                }
            }
        }
        return count;
    }

    public int findPatterns(int[][] pattern, Boolean mirrored) {
        int count = findPatterns(pattern);
        if (mirrored) {
            int[][] newPattern = new int[pattern.length][pattern[0].length];
            for (int y=0; y<pattern.length; y++) {
                for (int x=0; x<pattern[0].length; x++) {
                    newPattern[y][pattern[0].length - 1 - x] = pattern[y][x];
                }
            }
            count += findPatterns(newPattern);
        }
        return count;
    }

    /*public int[][] board = {
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,5,0},
        {1,0,0,0,0,0,0,0,5,5},
        {1,2,2,0,0,0,0,0,0,5},
        {1,2,3,3,3,7,7,0,4,4},
        {1,2,3,0,0,0,7,7,4,4},
    };*/
}
