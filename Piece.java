   public class Piece {
       public String kind = "LRight";
       public String pos = "up";
       public Block block[] = new Block[4];
       public int x = 0;
       public int y = 0;
       public Piece(String kind) {
           this.kind = kind;
           if(kind.equals("LLeft")) {
               block = new Block[4];
               block[0] = new Block();
               block[0].x = this.x;
               block[0].y = this.y;
               block[1] = new Block();
               block[1].x = this.x + 50;
               block[1].y = this.y;
               block[2] = new Block();
               block[2].x = this.x + 100;
               block[2].y = this.y;
               block[3] = new Block();
               block[3].x = this.x + 100;
               block[3].y = this.y - 50;
           } else if(kind.equals("LRight")) {
               block = new Block[4];
               block[0] = new Block();
               block[0].x = this.x;
               block[0].y = this.y;
               block[1] = new Block();
               block[1].x = this.x;
               block[1].y = this.y + 50;
               block[2] = new Block();
               block[2].x = this.x + 50;
               block[2].y = this.y + 50;
               block[3] = new Block();
               block[3].x = this.x + 100;
               block[3].y = this.y + 50;
           } else if(kind.equals("Square")) {
               block = new Block[4];
               block[0] = new Block();
               block[0].x = this.x;
               block[0].y = this.y;
               block[1] = new Block();
               block[1].x = this.x + 50;
               block[1].y = this.y;
               block[2] = new Block();
               block[2].x = this.x;
               block[2].y = this.y + 50;
               block[3] = new Block();
               block[3].x = this.x + 50;
               block[3].y = this.y + 50;
           } else if(kind.equals("Straight")) {
               block = new Block[4];
               block[3] = new Block();
               block[3].x = this.x;
               block[3].y = this.y;
               block[2] = new Block();
               block[2].x = this.x;
               block[2].y = this.y + 50;
               block[1] = new Block();
               block[1].x = this.x;
               block[1].y = this.y + 100;
               block[0] = new Block();
               block[0].x = this.x;
               block[0].y = this.y + 150;
           } else if(kind.equals("Tank")) {
               block = new Block[4];
               block[0] = new Block();
               block[0].x = this.x;
               block[0].y = this.y;
               block[1] = new Block();
               block[1].x = this.x - 50;
               block[1].y = this.y + 50;
               block[2] = new Block();
               block[2].x = this.x;
               block[2].y = this.y + 50;
               block[3] = new Block();
               block[3].x = this.x + 50;
               block[3].y = this.y + 50;
           } else if(kind.equals("LZ")) {
               block = new Block[4];
               block[0] = new Block();
               block[0].x = this.x;
               block[0].y = this.y;
               block[1] = new Block();
               block[1].x = this.x;
               block[1].y = this.y + 50;
               block[2] = new Block();
               block[2].x = this.x - 50;
               block[2].y = this.y + 50;
               block[3] = new Block();
               block[3].x = this.x - 50;
               block[3].y = this.y + 100;
           } else if(kind.equals("RZ")) {
               block = new Block[4];
               block[0] = new Block();
               block[0].x = this.x;
               block[0].y = this.y;
               block[1] = new Block();
               block[1].x = this.x;
               block[1].y = this.y + 50;
               block[2] = new Block();
               block[2].x = this.x + 50;
               block[2].y = this.y + 50;
               block[3] = new Block();
               block[3].x = this.x + 50;
               block[3].y = this.y + 100;
           }
       }
   }