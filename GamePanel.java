import java.awt.*;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GamePanel extends JPanel {

   private Graphics g = this.getGraphics();
   private int x = 0;
   private int y = 0;
   public Piece pieces[] = new Piece[100000];
   public int currentPieceNumber;
   private Image image;
   private Image bgimage = null;

   public GamePanel() {

        try {
            URL url = this.getClass().getResource("/mario-brick.gif");
            this.image = new ImageIcon(url).getImage();
        } catch(Exception e) {

        }

   }

    public void paintComponent(Graphics g) {
    }

    protected void draw(Graphics g) {

       try {

            /*create image icon to get image*/
            ImageIcon imageicon = new ImageIcon(getClass().getResource("/background.gif"));
            bgimage = imageicon.getImage();

            /*Draw image on the panel*/
            super.paintComponent(g);

            if(bgimage != null) {
                g.drawImage(bgimage, 0, 0, getWidth(), getHeight(), this);
            }

           //super.paintComponent(g = this.getGraphics()); //error!

            for(int i=0; i<this.pieces.length && i<=this.currentPieceNumber; i++) {
                for(int j=0; j<this.pieces[i].block.length; j++) {
                    if(this.pieces[i].block[j]!=null) {
                        if(this.pieces[i].block[j].y <=900) {
                            g.setColor(Color.RED);
                            g.drawImage(image,this.pieces[i].block[j].x,this.pieces[i].block[j].y,50,50,null);
                        }
                    }
                }
            }

       } catch(Exception e) {
        e.printStackTrace();
       }

   }
   
   public void drawSquares() {

       g = this.getGraphics();
       this.draw(g);

   }

}