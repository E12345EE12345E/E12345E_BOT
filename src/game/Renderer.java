package game;

import renderer.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

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
    public static void tick(Graphics2D g) {
        long currenttime = System.currentTimeMillis();
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
                bot = new BotThread(otherGame.getGame(), otherGame.board.getAverageHeight()>8?3:5, otherGame.board.getAverageHeight()>8?100:150);
                if (garbagereset) {
                    bot.garbageamount = Integer.MAX_VALUE;
                    System.out.println("garbage reset");
                }
                bot.start();
            }
        }
        if (Consts.Player1IsBot) {
            if (plr1bot != null && !plr1bot.isAlive()) {
                game.doMovement(plr1bot.retval, 1);
                if (game.mostRecentLinesSent > 0) {
                    GarbageHandler.sendGarbage(game, otherGame, game.mostRecentLinesSent);
                    game.mostRecentLinesSent = 0;
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
                    startTime = System.currentTimeMillis();
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

        oldtime = currenttime;
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
                    drawTile(g, (int)(x+pos.x), (int)(y+pos.y)-Consts.BoardHeight, uwidth, getPieceColor(game.board.board[y][x]));
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
    protected static final int HARDDROP = 87;
    protected static final int SOFTDROP = 83;
    protected static final int LEFT = 65;
    protected static final int RIGHT = 68;
    protected static final int CCW = 75; // 37 left arrow
    protected static final int TURN180 = 79; // 38 up arrow
    protected static final int CW = 59; // 39 right arrow
    protected static final int HOLD = 16;
    protected static final int RESET = 82;

    protected static final int DAS = 100;
}