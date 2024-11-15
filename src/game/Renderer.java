package game;

import renderer.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import java.io.*;

public class Renderer {
    public static Game game;
    public static Game otherGame;
    public static long startTime;
    private static ArrayList<Integer> keysHeld = new ArrayList<Integer>();
    private static long oldtime = System.currentTimeMillis();
    private static int dastimer;
    private static boolean alreadyindasleft;
    private static boolean alreadyindasright;
    private static BotThread bot;
    private static BotThread plr1bot;
    private static int garbagetimer;
    private static int[] garbagePattern = {4};
    private static int garbagePatternDelay = 3000;
    private static int garbagePatternNum = 0;
    private static int invistimer;
    private static boolean firsttick = true;
    private static boolean controlsettingmode;
    private static int controlsettingnum;
    public static void tick(Graphics2D g) {
        long currenttime = System.currentTimeMillis();

        if (firsttick) {
            if (!Controls.copyControls()) {
                controlsettingmode = true;
            }
        }

        if (controlsettingmode) {
            for (int i=0; i<keysHeld.size();) {
                int k = keysHeld.remove(i);
                switch (controlsettingnum) {
                    case 0: Controls.HARDDROP = k; break;
                    case 1: Controls.SOFTDROP = k; break;
                    case 2: Controls.LEFT = k; break;
                    case 3: Controls.RIGHT = k; break;
                    case 4: Controls.CCW = k; break;
                    case 5: Controls.CW = k; break;
                    case 6: Controls.TURN180 = k; break;
                    case 7: Controls.HOLD = k; break;
                    case 8: Controls.RESET = k; break;
                }
                controlsettingnum++;
                break;
            }

            if (controlsettingnum > 8) {
                controlsettingmode = false;
                Controls.setControls(Controls.HARDDROP, Controls.SOFTDROP, Controls.LEFT, Controls.RIGHT, Controls.CCW, Controls.TURN180, Controls.CW, Controls.HOLD, Controls.RESET);
            }

            g.setTransform(Draw.getBaseTransform());
            g.setStroke(new BasicStroke());
            g.setColor(Color.WHITE);

            int uwidth = (int)World.get().pixelsPerUnit;

            g.setPaint(Color.RED);
            Vec2 s = World.get().getCanvasSize();
            g.fillRect(uwidth*4, uwidth*4, (int)s.x-uwidth*8, (int)s.y-uwidth*8);
            String button = "_";
            switch (controlsettingnum) {
                case 0: button = "HARD DROP"; break;
                case 1: button = "SOFT DROP"; break;
                case 2: button = "LEFT"; break;
                case 3: button = "RIGHT"; break;
                case 4: button = "ROTATE CCW"; break;
                case 5: button = "ROTATE CW"; break;
                case 6: button = "ROTATE 180"; break;
                case 7: button = "HOLD"; break;
                case 8: button = "RESET GAME"; break;
            }
            Draw.drawTextCentered(g, "SET [" + button + "] KEYBIND", new Vec2(s.x/uwidth/2,-s.y/uwidth/2), uwidth/20.0, Draw.FontSize.XLARGE, Color.WHITE);
        } else {
            if (Consts.Bot1v1) {
                boolean garbagereset = false;
                if (bot != null && !bot.isAlive()) {
                    if (otherGame.garbage.amount > bot.garbageamount && bot.garbageamount < Consts.GarbageCap) {
                        garbagereset = true;
                    } else {
                        if (bot.retval != null) otherGame.doMovement(bot.retval, 1);
                        if (otherGame.mostRecentLinesSent > 0) {
                            GarbageHandler.sendGarbage(otherGame, game, otherGame.mostRecentLinesSent);
                            otherGame.mostRecentLinesSent = 0;
                        }
                    }
                }
                if (bot == null || !bot.isAlive()) {
                    bot = new BotThread(otherGame.getGame(), otherGame.board.getAverageHeight()+otherGame.garbage.amount>8?3:4, otherGame.board.getAverageHeight()+otherGame.garbage.amount>8?40:80);
                    //bot = new BotThread(otherGame.getGame(), 2, otherGame.board.getAverageHeight()>8?10:20);
                    if (garbagereset) {
                        bot.garbageamount = Integer.MAX_VALUE;
                        System.out.println("garbage reset");
                    }
                    bot.start();
                }
            } else {
                garbagetimer += currenttime-oldtime;
                if (garbagetimer > garbagePatternDelay) {
                    garbagetimer -= garbagePatternDelay;
                    Game tempGame = new Game();
                    GarbageHandler.sendGarbage(tempGame, game, garbagePattern[garbagePatternNum]);
                    garbagePatternNum++;
                    if (garbagePatternNum == garbagePattern.length) garbagePatternNum = 0;
                }
            }
            if (Consts.DoInvis) {
                invistimer += currenttime-oldtime;
            }
            if (Consts.Player1IsBot) {
                /*try {
                    File f = new File("C:/Users/ethan/OneDrive/Documents/GitHub/bot/tetris/src/localdata.txt");
                    Scanner s = new Scanner(f);
                    String a = s.next();
                    s.close();
                    int garb = Integer.parseInt(a.substring(0,1));
                    int garbage = Integer.parseInt(a.substring(1));
                    System.out.println(garbage);
                    GarbageHandler.sendGarbage(new Game(), game, garbage);
                    if (garbage>0) {
                        FileWriter fw = new FileWriter(f);
                        fw.write((game.mostRecentLinesSent>0?Math.min(game.mostRecentLinesSent+garb,9):"0") + "0");
                        fw.close();
                    } else if (game.mostRecentLinesSent>0) {
                        FileWriter fw = new FileWriter(f);
                        fw.write((game.mostRecentLinesSent>0?Math.min(game.mostRecentLinesSent+garb,9):"0") + "0");
                        fw.close();
                    }
                    game.mostRecentLinesSent = 0;
                } catch (IOException e) {}*/
                if (plr1bot != null && !plr1bot.isAlive()) {
                    game.doMovement(plr1bot.retval, 1);
                    if (game.mostRecentLinesSent > 0) {
                        GarbageHandler.sendGarbage(game, Consts.Bot1v1?otherGame:new Game(), game.mostRecentLinesSent);
                        //game.mostRecentLinesSent = 0;
                    }
                }
                if (plr1bot == null || !plr1bot.isAlive()) {
                    plr1bot = new BotThread(game.getGame(), game.board.getAverageHeight()>8?3:4, game.board.getAverageHeight()>8?100:150);
                    plr1bot.start();
                }
            } else {
                if (keysHeld.contains(Controls.LEFT) || keysHeld.contains(Controls.RIGHT)) {
                    dastimer += currenttime-oldtime;
                } else {
                    dastimer = 0;
                    alreadyindasleft = false;
                    alreadyindasright = false;
                }
                for (int i=0; i<keysHeld.size();) {
                    int keyCode = keysHeld.get(i);
                    if (keyCode == Controls.LEFT) {
                        if (!alreadyindasleft) {
                            game.moveLeft();
                            alreadyindasleft = true;
                        }
                        if (dastimer >= Controls.DAS) {
                            while (game.moveLeft()) {}
                        }
                        i++;
                    } else if (keyCode == Controls.RIGHT) {
                        if (!alreadyindasright) {
                            game.moveRight();
                            alreadyindasright = true;
                        }
                        if (dastimer >= Controls.DAS) {
                            while (game.moveRight()) {}
                        }
                        i++;
                    } else if (keyCode == Controls.HARDDROP) {
                        game.hardDrop();
                        if (game.mostRecentLinesSent > 0) {
                            if (Consts.Bot1v1) {
                                GarbageHandler.sendGarbage(game, otherGame, game.mostRecentLinesSent);
                            } else {
                                Game tempGame = new Game();
                                GarbageHandler.sendGarbage(game, tempGame, game.mostRecentLinesSent);
                            }
                            game.mostRecentLinesSent = 0;
                        }
                        keysHeld.remove(i);
                    } else if (keyCode == Controls.SOFTDROP) {
                        game.instantSoftDrop();
                        i++;
                    } else if (keyCode == Controls.CCW) {
                        game.rotateCCW();
                        keysHeld.remove(i);
                    } else if (keyCode == Controls.CW) {
                        game.rotateCW();
                        keysHeld.remove(i);
                    } else if (keyCode == Controls.TURN180) {
                        game.rotate180();
                        keysHeld.remove(i);
                    } else if (keyCode == Controls.HOLD) {
                        game.holdPiece();
                        keysHeld.remove(i);
                    } else if (keyCode == Controls.RESET) {
                        game.reset();
                        if (Consts.Bot1v1) otherGame.reset();
                        startTime = System.currentTimeMillis();
                        garbagePatternNum = 0;
                        garbagetimer = 0;
                        invistimer = 0;
                        keysHeld.remove(i);
                    } else {
                        i++;
                    }
                }
            }
    
            drawGame(g, game, new Vec2(5,2));
            if (otherGame != null) {
                drawGame(g, otherGame, new Vec2(Consts.BoardWidth+15,2));
            }
        }

        oldtime = currenttime;
        firsttick = false;
    }
    public static void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (!keysHeld.contains(keyCode)) {
            keysHeld.add(keyCode);
            if (keyCode == Controls.LEFT) {
                dastimer = 0;
                alreadyindasleft = false;
            } else if (keyCode == Controls.RIGHT) {
                dastimer = 0;
                alreadyindasright = false;
            }
        }
    }
    public static void keyReleased(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int index = keysHeld.indexOf(keyCode);
        if (index != -1) {
            keysHeld.remove(index);
        }
    }

    private static void drawGame(Graphics2D g, Game game, Vec2 pos) {
        g.setTransform(Draw.getBaseTransform());
        g.setStroke(new BasicStroke());
        g.setColor(Color.WHITE);

        int uwidth = (int)World.get().pixelsPerUnit;
        
        // Grid
        g.setColor(Color.WHITE);
        for (int i = 0; i <= Consts.BoardWidth; i++) {
            int a = i*uwidth;
            int b = Consts.BoardHeight*uwidth;
            g.drawLine(a+(int)pos.x*uwidth, (int)pos.y*uwidth, a+(int)pos.x*uwidth, b+(int)pos.y*uwidth);
		}
		for (int i = 0; i <= Consts.BoardHeight; i++) {
            int a = i*uwidth;
            int b = Consts.BoardWidth*uwidth;
            g.drawLine((int)pos.x*uwidth, a+(int)pos.y*uwidth, b+(int)pos.x*uwidth, a+(int)pos.y*uwidth);
		}
        // Board
        for (int y=0; y < Consts.BoardHeight*2; y++) {
            for (int x=0; x < Consts.BoardWidth; x++) {
                if (game.board.tileOccupied(x, y)) {
                    if (Consts.DoInvis) {
                        if (game.board.board[y][x] == 8) {
                            drawTile(g, (int)(x+pos.x), (int)(y+pos.y)-Consts.BoardHeight, uwidth, getPieceColor(game.board.board[y][x]));
                        } else {
                            int ivt = invistimer % 5000;
                            if (ivt > 4000) {
                                drawTile(g, (int)(x+pos.x), (int)(y+pos.y)-Consts.BoardHeight, uwidth, Util.colorAlpha(getPieceColor(game.board.board[y][x]), 1-(float)(ivt-4000)/1000));
                            }
                        }
                    } else {
                        drawTile(g, (int)(x+pos.x), (int)(y+pos.y)-Consts.BoardHeight, uwidth, getPieceColor(game.board.board[y][x]));
                    }
                }
            }
        }
        // Piece
        PiecePos[] piecepos = Piece.returnPiecePositions(game.piece.id, game.piece.rotation);
        Game tempGame = game.getGame();
        tempGame.instantSoftDrop();
        for (int i=0; i<piecepos.length; i++) {
            drawTile(g, posToVec2(PiecePos.Combine(piecepos[i], tempGame.piece.pos)).add(new Vec2(pos.x, pos.y-Consts.BoardHeight)), uwidth, getPieceColor(game.piece.id).darker());
        }
        for (int i=0; i<piecepos.length; i++) {
            drawTile(g, posToVec2(PiecePos.Combine(piecepos[i], game.piece.pos)).add(new Vec2(pos.x, pos.y-Consts.BoardHeight)), uwidth, getPieceColor(game.piece.id));
        }
        // Queue
        for (int a=0; a<Consts.DisplayQueueLength; a++) {
            int id = game.queue.queue.get(a).id;
            piecepos = Piece.returnPiecePositions(id);
            for (int i=0; i<piecepos.length; i++) {
                Vec2 queueoffset = new Vec2(0,id==4?-0.5:0);
                drawTile(g, posToVec2(piecepos[i]).add(pos).add(new Vec2(Consts.BoardWidth+2, 2+a*3)).add(queueoffset), uwidth, getPieceColor(id));
            }
        }
        // Hold
        if (game.hold.id > 0) {
            piecepos = Piece.returnPiecePositions(game.hold.id);
            for (int i=0; i<piecepos.length; i++) {
                drawTile(g, posToVec2(piecepos[i]).add(pos).add(new Vec2(-3, 2)), uwidth, getPieceColor(game.hold.id));
            }
        }
        // Garbage
        g.setPaint(Color.RED);
        if (game.garbage.amount > 0) {
            g.fillRect((int)((pos.x-0.5)*uwidth), (int)((pos.y+Consts.BoardHeight-game.garbage.amount)*uwidth), (int)(uwidth*0.5), (int)(game.garbage.amount*uwidth));
        }
        // Text
        Draw.drawTextCentered(g, "PPS: " + Math.round(100*game.piecesPlaced/(double)((System.currentTimeMillis()-startTime)/1000.0))/100.0, new Vec2(pos.x-2.5, -pos.y-6), uwidth/24.0, Draw.FontSize.SMALL, Color.WHITE);
        Draw.drawTextCentered(g, "" + game.piecesPlaced, new Vec2(pos.x-2.5, -pos.y-7), uwidth/24.0, Draw.FontSize.SMALL, Color.WHITE);
        Draw.drawTextCentered(g, "APM: " + Math.round(6000*game.attackSent/(double)((System.currentTimeMillis()-startTime)/1000.0))/100.0, new Vec2(pos.x-2.5, -pos.y-8), uwidth/24.0, Draw.FontSize.SMALL, Color.WHITE);
        Draw.drawTextCentered(g, "" + game.attackSent, new Vec2(pos.x-2.5, -pos.y-9), uwidth/24.0, Draw.FontSize.SMALL, Color.WHITE);
        Draw.drawTextCentered(g, "APP: " + Math.round(100*game.attackSent/(double)game.piecesPlaced)/100.0, new Vec2(pos.x-2.5, -pos.y-10), uwidth/24.0, Draw.FontSize.SMALL, Color.WHITE);
    }
    
    private static void drawTile(Graphics2D g, Vec2 pos, int uwidth, Color color) {
        drawTile(g, pos.x, pos.y, uwidth, color);
    }

    private static void drawTile(Graphics2D g, double x, double y, int uwidth, Color color) {
        double borderwidth = 0.1;
        g.setTransform(Draw.getBaseTransform());
        g.setPaint(color.darker());
        g.fillRect((int)(x*uwidth), (int)(y*uwidth), uwidth, uwidth);
        g.setPaint(color);
        g.fillRect((int)((x+borderwidth)*uwidth), (int)((y+borderwidth)*uwidth), (int)(uwidth*(1-borderwidth*2)), (int)(uwidth*(1-borderwidth*2)));
    }

    private static Color getPieceColor(int id) {
        switch (id) {
            case 1: return Color.CYAN;
            case 2: return Color.BLUE;
            case 3: return Color.ORANGE;
            case 4: return Color.YELLOW;
            case 5: return Color.GREEN;
            case 6: return Color.MAGENTA;
            case 7: return Color.RED;
            case 8: return Color.GRAY;
            default: return null;
        }
    }

    private static Vec2 posToVec2(PiecePos pos) {
        return new Vec2(pos.x, pos.y);
    }
}

class Controls {
    protected static int HARDDROP = 87;
    protected static int SOFTDROP = 83;
    protected static int LEFT = 65;
    protected static int RIGHT = 68;
    protected static int CCW = 37; // 37 left arrow, 75 k
    protected static int TURN180 = 38; // 38 up arrow, 79 o
    protected static int CW = 39; // 39 right arrow, 59 ;
    protected static int HOLD = 16;
    protected static int RESET = 82;

    protected static int DAS = 100;

    private static File file = new File("controls.txt");

    protected static boolean copyControls() {
        if (!file.exists()) return false;
        try {
            Scanner s = new Scanner(file);
            HARDDROP = s.nextInt();
            SOFTDROP = s.nextInt();
            LEFT = s.nextInt();
            RIGHT = s.nextInt();
            CCW = s.nextInt();
            TURN180 = s.nextInt();
            CW = s.nextInt();
            HOLD = s.nextInt();
            RESET = s.nextInt();
            if (s.hasNextInt()) {
                DAS = s.nextInt();
            }
            s.close();
        } catch (FileNotFoundException e) {
            return false;
        } catch (Exception e) {
            resetControls();
            return false;
        }
        return true;
    }

    protected static boolean setControls(int hd, int sd, int l, int r, int ccw, int t180, int cw, int hold, int rs) {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(hd + " " + sd + " " + l + " " + r + " " + ccw + " " + t180 + " " + cw + " " + hold + " " + rs);
            fw.close();
            copyControls();
        } catch (IOException e) {
            resetControls();
            return false;
        }
        return true;
    }

    protected static void resetControls() {
        HARDDROP = 87;
        SOFTDROP = 83;
        LEFT = 65;
        RIGHT = 68;
        CCW = 37;
        TURN180 = 38;
        CW = 39;
        HOLD = 16;
        RESET = 82;
    }
}