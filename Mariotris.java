import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.net.URL;
import javax.sound.sampled.*;
import javax.swing.ImageIcon;

public class Mariotris implements KeyListener {

    private FullscreenFrame gameFrame = new FullscreenFrame();
    private GamePanel gamePanel = new GamePanel();
    private JLabel thelevel = null;
    private int level = 1;
    private Graphics g;
    private Piece pieces[] = new Piece[100000];
    private int currentPieceNumber = 0;
    private int squares[][] = new int[710][460];
    private JLabel thescore = null;
    private int score = 0;
    private JLabel theline = null;
    private int line = 0;

    private class FullscreenFrame extends JFrame {
            public FullscreenFrame () {
                    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                    if(gd.isFullScreenSupported()) {
                            setUndecorated(true);
                            gd.setFullScreenWindow(this);
                    }
            }
    }

    public Mariotris() {
	
	  gameFrame.setLayout(null);
        
        JPanel titlePanel = new JPanel();
	   //titlePanel.setBackground(Color.LIGHT_GRAY);
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/mario-running.gif"));
        JLabel ru = new JLabel(imageIcon);
        titlePanel.add(ru);
        JLabel scoreLbl = new JLabel("Score: ");
        titlePanel.add(scoreLbl);
        this.thescore = new JLabel("0");
        titlePanel.add(this.thescore);
        JLabel lineLbl = new JLabel("Lines: ");
        titlePanel.add(lineLbl);
        this.theline = new JLabel("0");
        titlePanel.add(this.theline);
        JLabel levelLbl = new JLabel("Level: ");
        titlePanel.add(levelLbl);
        this.thelevel = new JLabel("1");
        titlePanel.add(this.thelevel);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        titlePanel.setSize(60, 500-15);
        titlePanel.setLocation(width/2 - 310, (0));
	   //gamePanel.setLayout(null);
        gamePanel.setSize(500, (int) 767-17);
        gamePanel.setLocation(width/2 - 310 + (60), (0));
        gameFrame.setSize(width, (int) screenSize.getHeight());
	   gameFrame.setLocation(0, 0);
	   //gameFrame.setBackground(Color.BLACK);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //gameFrame.setResizable(false);
	   //gameFrame.setUndecorated(true);
        gameFrame.add(titlePanel);
        gameFrame.add(gamePanel);
	   gameFrame.show();

        gameFrame.addKeyListener(this);
    }

    public void setLevel(int theLevel) {
        this.level = theLevel;
        this.thelevel.setText("" + this.level);
    }

    public void setLine(int theLine) {
        this.line = theLine;
        this.theline.setText("" + this.line);
    }

    public void setScore(int theScore) {
        this.score = theScore;
        this.thescore.setText("" + this.score);
    }

    public boolean juxtaposedPiece() {

        try {

            if(this.currentPieceNumber == 0) {
                return false;
            }

            for(int i=0; i<gamePanel.pieces.length - 1; i++) {
               for(int k=0; k<gamePanel.pieces[this.currentPieceNumber].block.length; k++) {
                    for(int j=0; j<gamePanel.pieces[i].block.length; j++) {
                        if(i != this.currentPieceNumber && gamePanel.pieces[i].block[j].y == gamePanel.pieces[this.currentPieceNumber].block[k].y && (gamePanel.pieces[i].block[j].x == gamePanel.pieces[this.currentPieceNumber].block[k].x + 50 || gamePanel.pieces[i].block[j].x == gamePanel.pieces[this.currentPieceNumber].block[k].x - 50)) {
                            return true;
                        }
                    }
                }
            }

            return false;

        } catch(NullPointerException npe) {

            return false;

        }
    }

    public boolean stackedOnTopOfPiece() {

        try {

            if(this.currentPieceNumber == 0) {
                return false;
            }

            for(int i=0; i<this.pieces.length - 1; i++) {
               for(int k=0; k<this.pieces[this.currentPieceNumber].block.length; k++) {
                    for(int j=0; j<this.pieces[i].block.length; j++) {
                        if(i != this.currentPieceNumber &&
                                this.pieces[i].block[j].x == this.pieces[this.currentPieceNumber].block[k].x && this.pieces[i].block[j].y - 50 == this.pieces[this.currentPieceNumber].block[k].y) {
                            return true;
                        }
                    }
                }
            }

            return false;

        } catch(NullPointerException npe) {

            return false;

        }
    }

    public static void main(String[] args) {
        Mariotris tg = new Mariotris();
        tg.play();
    }

    public void keyTyped(KeyEvent ke) {
    }

    public void keyPressed(KeyEvent ke) {
     int keyCode = ke.getKeyCode();
     switch( keyCode ) {
        case KeyEvent.VK_UP:

           Piece p = this.pieces[this.currentPieceNumber];

           if(p.kind.equals("LRight")) {
            if(p.pos.equals("up")) {

                p.block[0].x -= 50;
                p.block[0].y += 50;

                p.block[2].x -= 50;
                p.block[2].y -= 50;

                p.block[3].x -= 100;
                p.block[3].y -= 100;
                
                p.pos = "left";
            }
            else if(p.pos.equals("left"))  {

                p.block[0].x += 50;
                p.block[0].y += 50;

                p.block[2].x -= 50;
                p.block[2].y += 50;

                p.block[3].x -= 100;
                p.block[3].y += 100;

                p.pos = "down";
            }
            else if(p.pos.equals("down"))  {

                p.block[0].x += 50;
                p.block[0].y -= 50;

                p.block[2].x += 50;
                p.block[2].y += 50;

                p.block[3].x += 100;
                p.block[3].y += 100;

                p.pos = "right";
            }
            else if(p.pos.equals("right"))  {

                p.block[0].x -= 50;
                p.block[0].y -= 50;

                p.block[2].x += 50;
                p.block[2].y -= 50;

                p.block[3].x += 100;
                p.block[3].y -= 100;

                p.pos = "up";
            }
           } else if(p.kind.equals("LLeft")) {
            if(p.pos.equals("up")) {

                p.block[0].x += 100;
                p.block[0].y += 100;

                p.block[1].x += 50;
                p.block[1].y += 50;

                p.block[3].x -= 50;
                p.block[3].y += 50;

                p.pos = "left";
            }
            else if(p.pos.equals("left"))  {

                p.block[0].x += 100;
                p.block[0].y -= 100;

                p.block[1].x += 50;
                p.block[1].y -= 50;

                p.block[3].x += 50;
                p.block[3].y += 50;

                p.pos = "down";
            }
            else if(p.pos.equals("down"))  {

                p.block[0].x -= 100;
                p.block[0].y -= 100;

                p.block[1].x -= 50;
                p.block[1].y -= 50;

                p.block[3].x += 50;
                p.block[3].y -= 50;

                p.pos = "right";
            }
            else if(p.pos.equals("right"))  {

                p.block[0].x -= 100;
                p.block[0].y += 100;

                p.block[1].x -= 50;
                p.block[1].y += 50;

                p.block[3].x -= 50;
                p.block[3].y -= 50;

                p.pos = "up";
            }
           } else if(p.kind.equals("Straight")) {
            if(p.pos.equals("up")) {

                p.block[1].x += 50;
                p.block[1].y += 50;

                p.block[2].x += 100;
                p.block[2].y += 100;

                p.block[3].x += 150;
                p.block[3].y += 150;

                p.pos = "down";
            }
            else if(p.pos.equals("down"))  {

                p.block[1].x -= 50;
                p.block[1].y -= 50;

                p.block[2].x -= 100;
                p.block[2].y -= 100;

                p.block[3].x -= 150;
                p.block[3].y -= 150;

                p.pos = "up";
            }
           } else if(p.kind.equals("LZ")) {
            if(p.pos.equals("up")) {

                p.block[0].x -= 50;
                p.block[0].y += 50;

                p.block[2].x += 50;
                p.block[2].y += 50;

                p.block[3].x += 100;
                p.block[3].y -= 0;

                p.pos = "left";

            }
            else if(p.pos.equals("left"))  {

                p.block[0].x += 50;
                p.block[0].y += 50;

                p.block[2].x += 50;
                p.block[2].y -= 50;

                p.block[3].x += 0;
                p.block[3].y -= 100;

                p.pos = "down";
            }
            else if(p.pos.equals("down"))  {

                p.block[0].x += 100;
                p.block[0].y += 0;

                p.block[1].x += 50;
                p.block[1].y += 50;

                p.block[3].x -= 50;
                p.block[3].y += 50;

                p.pos = "right";
            }
            else if(p.pos.equals("right"))  {

                p.block[0].x -= 50;
                p.block[0].y -= 50;

                p.block[2].x -= 50;
                p.block[2].y += 50;

                p.block[3].x -= 0;
                p.block[3].y += 100;

                p.pos = "up";
            }
           } else if(p.kind.equals("RZ")) {
            if(p.pos.equals("up")) {

                p.block[0].x += 50;
                p.block[0].y += 50;

                p.block[2].x -= 50;
                p.block[2].y += 50;

                p.block[3].x -= 100;
                p.block[3].y -= 0;

                p.pos = "right";

            }
            else if(p.pos.equals("right"))  {

                p.block[0].x -= 50;
                p.block[0].y += 50;

                p.block[2].x -= 50;
                p.block[2].y -= 50;

                p.block[3].x -= 0;
                p.block[3].y -= 100;

                p.pos = "down";
            }
            else if(p.pos.equals("down"))  {

                p.block[0].x -= 50;
                p.block[0].y -= 50;

                p.block[2].x += 50;
                p.block[2].y -= 50;

                p.block[3].x += 100;
                p.block[3].y -= 0;

                p.pos = "left";
            }
            else if(p.pos.equals("left"))  {

                p.block[0].x += 50;
                p.block[0].y -= 50;

                p.block[2].x += 50;
                p.block[2].y += 50;

                p.block[3].x -= 0;
                p.block[3].y += 100;

                p.pos = "up";
            }
           } else if(p.kind.equals("Tank")) {
            if(p.pos.equals("up")) {

                p.block[1].x += 0;
                p.block[1].y -= 100;

                p.block[2].x -= 50;
                p.block[2].y -= 50;

                p.block[3].x -= 100;
                p.block[3].y += 0;

                p.pos = "right";

            }
            else if(p.pos.equals("right"))  {

                p.block[1].x += 100;
                p.block[1].y += 0;

                p.block[2].x += 50;
                p.block[2].y -= 50;

                p.block[3].x -= 0;
                p.block[3].y -= 100;

                p.pos = "down";
            }
            else if(p.pos.equals("down"))  {

                p.block[1].x += 0;
                p.block[1].y += 100;

                p.block[2].x += 50;
                p.block[2].y += 50;

                p.block[3].x += 100;
                p.block[3].y += 0;

                p.pos = "left";
            }
            else if(p.pos.equals("left"))  {

                p.block[1].x -= 100;
                p.block[1].y -= 0;

                p.block[2].x -= 50;
                p.block[2].y += 50;

                p.block[3].x += 0;
                p.block[3].y += 100;

                p.pos = "up";
            }
           }

            gamePanel.pieces = this.pieces;
            gamePanel.drawSquares();

            break;
        case KeyEvent.VK_DOWN:

            if(!stackedOnBottom() && !stackedOnTopOfPiece()) {

                this.pieces[this.currentPieceNumber].y+=50;
                gamePanel.pieces = this.pieces;
                gamePanel.pieces[this.currentPieceNumber].y+=50;

                this.pieces[this.currentPieceNumber].block[0].y+=50;
                this.pieces[this.currentPieceNumber].block[1].y+=50;
                this.pieces[this.currentPieceNumber].block[2].y+=50;
                this.pieces[this.currentPieceNumber].block[3].y+=50;

                if(stackedOnBottom() || stackedOnTopOfPiece()) {
                    this.pieces[this.currentPieceNumber].y-=50;
                    gamePanel.pieces = this.pieces;
                    gamePanel.pieces[this.currentPieceNumber].y-=50;

                    this.pieces[this.currentPieceNumber].block[0].y-=50;
                    this.pieces[this.currentPieceNumber].block[1].y-=50;
                    this.pieces[this.currentPieceNumber].block[2].y-=50;
                    this.pieces[this.currentPieceNumber].block[3].y-=50;
                }

                gamePanel.drawSquares();
            
            } else {

            }

            break;
        case KeyEvent.VK_LEFT:

            if(!juxtaposedPiece() &&
                    this.pieces[this.currentPieceNumber].block[0].x >= 50 &&
                    this.pieces[this.currentPieceNumber].block[1].x >= 50 &&
                    this.pieces[this.currentPieceNumber].block[2].x >= 50 &&
                    this.pieces[this.currentPieceNumber].block[3].x >= 50) {

                this.pieces[this.currentPieceNumber].x-=50;
                this.pieces[this.currentPieceNumber].block[0].x-=50;
                this.pieces[this.currentPieceNumber].block[1].x-=50;
                this.pieces[this.currentPieceNumber].block[2].x-=50;
                this.pieces[this.currentPieceNumber].block[3].x-=50;
                gamePanel.pieces = this.pieces;
                gamePanel.drawSquares();

            }

            break;
        case KeyEvent.VK_RIGHT :

            if(!juxtaposedPiece() &&
                    this.pieces[this.currentPieceNumber].block[0].x < 410 &&
                    this.pieces[this.currentPieceNumber].block[1].x < 410 &&
                    this.pieces[this.currentPieceNumber].block[2].x < 410 &&
                    this.pieces[this.currentPieceNumber].block[3].x < 410) {

                this.pieces[this.currentPieceNumber].x+=50;
                this.pieces[this.currentPieceNumber].block[0].x+=50;
                this.pieces[this.currentPieceNumber].block[1].x+=50;
                this.pieces[this.currentPieceNumber].block[2].x+=50;
                this.pieces[this.currentPieceNumber].block[3].x+=50;
                gamePanel.pieces = this.pieces;
                gamePanel.drawSquares();

            }

            break;
     }

    }

    public void keyReleased(KeyEvent ke) {

    }

    public void clearLineIfNeeded() {

        try {

            int clearLineNeeded = 0;
            ArrayList clearRows = new ArrayList();

            for(int i=0; i<710; i+=50) {

                clearLineNeeded = 0;

                for(int j=0; j<460; j+=50) {

                    if(this.squares[i][j] == 1) {

                        clearLineNeeded++;
                        if(clearLineNeeded == 10) {

                            clearRows.add(i);
                            break;

                        }

                    }

                }

                if(clearLineNeeded == 10) {

                    break;

                }

            }

            if(clearLineNeeded == 10) {

                for(int i=0; i<clearRows.size(); i++) {

                    this.setScore(this.score + 10);

                    this.setLine(this.line + 1);

                    for(int j=0; j<460; j+=50) {

                        int y = (Integer) clearRows.get(i);

                        this.squares[y][j] = 0;

                        for(int k=0; k<this.pieces.length && k<=this.currentPieceNumber; k++) {

                            for(int l=0; l<this.pieces[k].block.length; l++) {

                                if(this.pieces[k].block[l] != null) {

                                    if(this.pieces[k].block[l].x == j &&
                                            this.pieces[k].block[l].y == y) {

                                        this.pieces[k].block[l].y = 1000;

                                    }

                                }

                            }

                        }

                    }

                    for(int k=0; k<this.pieces.length && k<=this.currentPieceNumber; k++) {

                        if(this.pieces[k] != null) {

                            if((Integer) clearRows.get(i) > this.pieces[k].y) {

                                this.pieces[k].y += 50;

                            }

                        }

                        for(int l=0; l<this.pieces[k].block.length; l++) {

                            if(this.pieces[k].block[l] != null) {

                                if((Integer) clearRows.get(i) > this.pieces[k].block[l].y) {

                                    this.pieces[k].block[l].y += 50;

                                }
                                
                            }

                        }

                    }

                    for(int ii=0; ii<710; ii++) {

                        for(int jj=0; jj<460; jj++) {

                            this.squares[ii][jj] = 0;

                        }

                    }

                    for(int k=0; k<this.pieces.length && k<=this.currentPieceNumber; k++) {

                        for(int l=0; l<this.pieces[k].block.length; l++) {

                            if(this.pieces[k].block[l] != null) {

                                if(gamePanel.pieces[k].block[l].y <= 800) {

                                    this.squares[gamePanel.pieces[k].block[l].y][gamePanel.pieces[k].block[l].x] = 1;

                                }

                            }

                        }

                    }

                }

                gamePanel.pieces = this.pieces;

                clearLineNeeded = 0;

                gamePanel.drawSquares();

            }

        } catch(NullPointerException npe) {

        }

    }

    public void play() {

        int x = 150;
        int y = 20;

        double randomValue = Math.random();
        if(randomValue >= 0 && randomValue <= 0.15) {
            this.pieces[this.currentPieceNumber] = new Piece("LLeft");
        }
        else if(randomValue >= 0.15 && randomValue <= 0.3) {
            this.pieces[this.currentPieceNumber] = new Piece("Square");
        }
        else if(randomValue >= 0.3 && randomValue <= 0.45) {
            this.pieces[this.currentPieceNumber] = new Piece("LRight");
        }
        else if(randomValue >= 0.45 && randomValue <= 0.6) {
            this.pieces[this.currentPieceNumber] = new Piece("Square");
        }
        else if(randomValue >= 0.6 && randomValue <= 0.7) {
            this.pieces[this.currentPieceNumber] = new Piece("Straight");
        }
        else if(randomValue >= 0.7 && randomValue <= 0.8) {
            this.pieces[this.currentPieceNumber] = new Piece("Tank");
        }
        else if(randomValue >= 0.8 && randomValue <= 0.9) {
            this.pieces[this.currentPieceNumber] = new Piece("LZ");
        }
        else if(randomValue >= 0.9 && randomValue <= 1) {
            this.pieces[this.currentPieceNumber] = new Piece("RZ");
        }

        this.pieces[this.currentPieceNumber].x = x;
        this.pieces[this.currentPieceNumber].y = y;
        this.pieces[this.currentPieceNumber].block[0].x+=200;
        this.pieces[this.currentPieceNumber].block[1].x+=200;
        this.pieces[this.currentPieceNumber].block[2].x+=200;
        this.pieces[this.currentPieceNumber].block[3].x+=200;
        gamePanel.pieces = this.pieces;
        gamePanel.currentPieceNumber = this.currentPieceNumber;
        gamePanel.drawSquares();

        for(int i=0; i<710; i++) {

            for(int j=0; j<460; j++) {

                squares[i][j] = 0;

            }

        }

        try {
            URL url = BigClip.class.getClassLoader().getResource("mario.wav");
            BigClip clip = new BigClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            clip.open(ais);
            clip.start();
        } catch(Exception e) {}

        int TIMER_DELAY = 1740;

        while(true) {
            try {
                Thread.sleep(TIMER_DELAY);
            } catch(Exception e) {
            }
            gameLoop();
            if(this.line > 0 && this.line % 4 == 0) {
                this.setLevel(this.level + 1);
                this.setLine(this.line + 1);
                TIMER_DELAY -= 100;
            }
        }

    }

    boolean stackedOnBottom() {
        int y0 = gamePanel.pieces[this.currentPieceNumber].block[0].y;
        int y1 = gamePanel.pieces[this.currentPieceNumber].block[1].y;
        int y2 = gamePanel.pieces[this.currentPieceNumber].block[2].y;
        int y3 = gamePanel.pieces[this.currentPieceNumber].block[3].y;

        if(y0 >= y1 && y0 >= y2 && y0 >= y3) {
            if(y0 >= 700) {
                return true;
            }
            else {
                return false;
            }
        }
        else if(y1 >= y0 && y1 >= y2 && y1 >= y3) {
            if(y1 >= 700) {
                return true;
            }
            else {
                return false;
            }
        }
        else if(y2 >= y1 && y2 >= y0 && y2 >= y3) {
            if(y2 >= 700) {
                return true;
            }
            else {
                return false;
            }
        }
        else if(y3 >= y1 && y3 >= y2 && y3 >= y0) {
            if(y3 >= 750) {
                return true;
            }
            else {
                return false;
            }
        }

        return false;
    }

    boolean overlapPieces() {
        try {
            for(int i=0; i<this.pieces.length && i<=this.currentPieceNumber; i++) {
                for(int j=0; j<this.pieces.length && j<=this.currentPieceNumber; j++) {
                    if(i != j) {
                        for(int k=0; k<this.pieces[i].block.length; k++) {
                            for(int l=0; l<this.pieces[l].block.length; l++) {
                                if(this.pieces[i].block[k].x == this.pieces[j].block[l].x &&
                                        this.pieces[i].block[k].y == this.pieces[j].block[l].y &&
                                        (this.pieces[i].block[k].y < 250 || this.pieces[j].block[k].y < 250)) {
                                        return true;
                                }
                            }
                        }
                    }
                }
            }
            return false;
        } catch(Exception e) {
            return false;
        }
    }
    
    public void gameLoop() {

      try {

        if(this.currentPieceNumber > 0) {

            if(overlapPieces()) {

                this.setLevel(1);
                this.setLine(0);
                this.setScore(0);

                for(int i=0; i<this.pieces.length; i++) {
                    this.pieces[i] = null;
                }

                this.currentPieceNumber = 0;

                for(int i=0; i<710; i++) {

                    for(int j=0; j<460; j++) {

                        squares[i][j] = 0;

                    }

                }

                int x = 150;
                int y = 20;

                double randomValue = Math.random();
                if(randomValue >= 0 && randomValue <= 0.15) {
                    this.pieces[this.currentPieceNumber] = new Piece("LLeft");
                }
                else if(randomValue >= 0.15 && randomValue <= 0.3) {
                    this.pieces[this.currentPieceNumber] = new Piece("Square");
                }
                else if(randomValue >= 0.3 && randomValue <= 0.45) {
                    this.pieces[this.currentPieceNumber] = new Piece("LRight");
                }
                else if(randomValue >= 0.45 && randomValue <= 0.6) {
                    this.pieces[this.currentPieceNumber] = new Piece("Square");
                }
                else if(randomValue >= 0.6 && randomValue <= 0.7) {
                    this.pieces[this.currentPieceNumber] = new Piece("Straight");
                }
                else if(randomValue >= 0.7 && randomValue <= 0.8) {
                    this.pieces[this.currentPieceNumber] = new Piece("Tank");
                }
                else if(randomValue >= 0.8 && randomValue <= 0.9) {
                    this.pieces[this.currentPieceNumber] = new Piece("LZ");
                }
                else if(randomValue >= 0.9 && randomValue <= 1) {
                    this.pieces[this.currentPieceNumber] = new Piece("RZ");
                }

                this.pieces[this.currentPieceNumber].x = x-20;
                this.pieces[this.currentPieceNumber].y = y;
                this.pieces[this.currentPieceNumber].block[0].x+=250;
                this.pieces[this.currentPieceNumber].block[1].x+=250;
                this.pieces[this.currentPieceNumber].block[2].x+=250;
                this.pieces[this.currentPieceNumber].block[3].x+=250;
                gamePanel.pieces = this.pieces;
                gamePanel.currentPieceNumber = this.currentPieceNumber;
            }
        }

        gamePanel.drawSquares();
        this.pieces[this.currentPieceNumber].y+=50;
        gamePanel.pieces = this.pieces;
        gamePanel.currentPieceNumber = this.currentPieceNumber;
        gamePanel.pieces[this.currentPieceNumber].y+=50;
        this.pieces[this.currentPieceNumber].block[0].y+=50;
        this.pieces[this.currentPieceNumber].block[1].y+=50;
        this.pieces[this.currentPieceNumber].block[2].y+=50;
        this.pieces[this.currentPieceNumber].block[3].y+=50;

        if(stackedOnTopOfPiece()||stackedOnBottom()) {

            if(stackedOnTopOfPiece()) {

                if(gamePanel.pieces[this.currentPieceNumber].block[0].y > 700) {
                    gamePanel.pieces[this.currentPieceNumber].block[0].y = 700;
                }
                if(gamePanel.pieces[this.currentPieceNumber].block[1].y > 700) {
                    gamePanel.pieces[this.currentPieceNumber].block[1].y = 700;
                }
                if(gamePanel.pieces[this.currentPieceNumber].block[2].y > 700) {
                    gamePanel.pieces[this.currentPieceNumber].block[2].y = 700;
                }
                if(gamePanel.pieces[this.currentPieceNumber].block[3].y > 700) {
                    gamePanel.pieces[this.currentPieceNumber].block[3].y = 700;
                }
                squares[gamePanel.pieces[this.currentPieceNumber].block[0].y][gamePanel.pieces[this.currentPieceNumber].block[0].x] = 1;
                squares[gamePanel.pieces[this.currentPieceNumber].block[1].y][gamePanel.pieces[this.currentPieceNumber].block[1].x] = 1;
                squares[gamePanel.pieces[this.currentPieceNumber].block[2].y][gamePanel.pieces[this.currentPieceNumber].block[2].x] = 1;
                squares[gamePanel.pieces[this.currentPieceNumber].block[3].y][gamePanel.pieces[this.currentPieceNumber].block[3].x] = 1;

            } else if(stackedOnBottom()) {

                squares[700][gamePanel.pieces[this.currentPieceNumber].block[0].x] = 1;
                squares[700][gamePanel.pieces[this.currentPieceNumber].block[1].x] = 1;
                squares[700][gamePanel.pieces[this.currentPieceNumber].block[2].x] = 1;
                squares[700][gamePanel.pieces[this.currentPieceNumber].block[3].x] = 1;

            }

            this.currentPieceNumber = this.currentPieceNumber + 1;
            double randomValue = Math.random();
            if(randomValue >= 0 && randomValue <= 0.15) {
                this.pieces[this.currentPieceNumber] = new Piece("LLeft");
            }
            else if(randomValue >= 0.15 && randomValue <= 0.3) {
                this.pieces[this.currentPieceNumber] = new Piece("Square");
            }
            else if(randomValue >= 0.3 && randomValue <= 0.45) {
                this.pieces[this.currentPieceNumber] = new Piece("LRight");
            }
            else if(randomValue >= 0.45 && randomValue <= 0.6) {
                this.pieces[this.currentPieceNumber] = new Piece("Square");
            }
            else if(randomValue >= 0.6 && randomValue <= 0.7) {
                this.pieces[this.currentPieceNumber] = new Piece("Straight");
            }
            else if(randomValue >= 0.7 && randomValue <= 0.8) {
                this.pieces[this.currentPieceNumber] = new Piece("Tank");
            }
            else if(randomValue >= 0.8 && randomValue <= 0.9) {
                this.pieces[this.currentPieceNumber] = new Piece("LZ");
            }
            else if(randomValue >= 0.9 && randomValue <= 1) {
                this.pieces[this.currentPieceNumber] = new Piece("RZ");
            }
            this.pieces[this.currentPieceNumber].x = 200;
            this.pieces[this.currentPieceNumber].y = 20;
            this.pieces[this.currentPieceNumber].block[0].x+=200;
            this.pieces[this.currentPieceNumber].block[1].x+=200;
            this.pieces[this.currentPieceNumber].block[2].x+=200;
            this.pieces[this.currentPieceNumber].block[3].x+=200;
            gamePanel.pieces = this.pieces;
            gamePanel.currentPieceNumber = this.currentPieceNumber;
            gamePanel.pieces[this.currentPieceNumber].x = this.pieces[this.currentPieceNumber].x;
            gamePanel.pieces[this.currentPieceNumber].y = this.pieces[this.currentPieceNumber].y;
            gamePanel.drawSquares();
        }
        clearLineIfNeeded();

      } catch(Exception e) {

      }
    }

}
