import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
/**
 * 
 *
 * 
 * */

public class Tetris extends Applet implements Runnable {
	 	private Thread thread;
	     int[][] screen;
	    TetrisPiece blocks = new TetrisPiece();  // gets the game piece
	    private Image doubleImage = null;
	    private Graphics doubleGraphics = null;  //create a image to draw on
	    private int speed; // current game speed
	    int numberOfRowClear=0;         
	    boolean gameOver=false;
		int sizeX=20; int sizeY=12;
		boolean pause=false;
		int score;
public void init (){
		this.setSize(600,sizeX*25);
	    speed = 500;
	    resetBoard(); // reset the board to -1 as in no piece
    doubleImage = createImage(getSize().width,getSize().height);
    doubleGraphics = doubleImage.getGraphics();
}	 
public void resetBoard(){
	   for (int i=0;i<screen.length;i++){ // reset the board to -1 as in no piece
           for (int j=0;j<screen[i].length;j++){
               screen[i][j] = -1;
           }
      }
	   pickPiece();
}

public Tetris (){
	 thread = null;
     screen = new int[sizeX+4][sizeY];
	  addKeyListener(  // keyboard listener
	            new KeyAdapter() 
	            {            
	            	public void keyPressed(KeyEvent e) {  //keyboard listener
	            		// TODO Auto-generated method stub
	            		if (!gameOver){
	            			if (!pause){
	            		if (e.getKeyCode()==KeyEvent.VK_LEFT){
	            			//if (currentX-1>=0)
	            			movePiece(-1,0);
	                	}
	                	if (e.getKeyCode()==KeyEvent.VK_RIGHT){
	                	//	if (currentX+1+currentPiece<screen.length)
	                		movePiece(1,0);
	                	}
	                  	if (e.getKeyCode()==KeyEvent.VK_DOWN){
	                  		//if (currentY+1+currentPiece<screen.length)
		                		movePiece(0,1);
	                  	}
	                  	if (e.getKeyCode()==KeyEvent.VK_UP){
		                  	rotate(1);
	                  	}
	                  	if(e.getKeyCode()==KeyEvent.VK_SPACE){
	                  		drop();
	                  	}
	            			}
	                	if(e.getKeyCode()==KeyEvent.VK_P){
	                  		if (pause==false){pause = true; thread.suspend();}
	                  		else {pause = false; thread.resume();}
	                  	}
	                  	repaint();
	            	}
	            	}
	            } 
	        ); 
	  
	  addMouseListener( 
	            new MouseAdapter()
	            {            
	                public void mousePressed(MouseEvent e)
	                {if(e.getX() >=350 && e.getX()<=350+80 && e.getY()>=80 && e.getY()<=80+80){
	                //	System.out.println("clicked");
	                	gameOver=false; speed=500; score=0; numberOfRowClear=0;
	                	resetBoard(); 
	                    }
	                }
	            } 
	        );
	       
	  setFocusable(true); 
}
public void placePiece(int piece){ // place the current piece
	 int points[][] = blocks.piece[currentPiece][rotation];
	  for(int i=0;i<points.length;i++){ // copy the piece to the screen array( the image)
          screen[points[i][1] + currentY][points[i][0] + currentX] = piece;
	  }

  }
int currentPiece,rotation,currentX,currentY;
int chain=0;
public void remove (){ // removing rows that has been fill up
	chain =0;
	if (numberOfRowClear==3){
		speed-=50; 
		if (speed<100){
			speed=100;
			}
		numberOfRowClear=0;
	}
	for (int i=0; i <24;++i){
		for (int j=0;j<12;++j){
			if (screen[i][j]==-1){break;}
			if (j==12-1){
				++chain;
				removeRow(i);
			}
		}
	}
	score =score + (int) ( Math.pow(2, chain));
	if ((int) ( Math.pow(2, chain))==1) --score;
	//System.out.println(score+" "+((int) ( Math.pow(2, chain))));
}
public void removeRow(int row){ // does the removing rows method
	for (int i=0; i<12;++i){
		screen[row][i]=-1;
	}
	repaint();
	try{
	Thread.sleep(300);}
	catch(Exception e){}
	if (row-1>=0){
		--row;
		for (int i =row;i>=0;--i){
			for (int j=0;j<12;++j){
				screen[i+1][j]=screen[i][j];
			}
		}
		++numberOfRowClear;
	}
	repaint();
}
public void pickPiece(){  // randomly picks a pice and rotation and reset the position to middle
    currentPiece =(int)(Math.random()*7);
    rotation = (int)(Math.random()*4);
    currentX = 5;
    currentY = 2;
}
public boolean  movePiece( int x, int y){ //moves the piece base on what input was given  and check if that move is possible
	placePiece(-1); boolean stop = false; // if not, restore the old move
	int oldX=currentX; int oldY=currentY;
	 currentY += y;
 	 currentX += x;
	if (!validMove()){
		currentX=oldX;
		currentY=oldY;
		stop = true;
	}
 	  placePiece(currentPiece);
 	 return stop;
}
public void rotate(int r){  // rotate the shape, check if possible, if not change to old rotation
	placePiece(-1);
	int oldr=rotation;
	if (r>0){++rotation; if (rotation>3)rotation=0;}
	if (!validMove()){
		rotation=oldr;
	} placePiece(currentPiece);
}
public boolean validMove (){ //check if the move is valid, by check if the position plus the array length has excess the board limit
	 int[][] points = blocks.piece[currentPiece][rotation]; // or if a piece has already been there.
	 int x,y;
     for(int i=0;i<points.length;i++){
        x = points[i][0] + currentX;
        y = points[i][1] + currentY;
         if(x < 0 || x >= sizeY ||y < 0 || y >= sizeX+4)
             return false;
         if(screen[y][x] != -1)
             return false;
     }
     return true;
}
public void drop(){  // drop the piece to the bottom
	placePiece(-1);
	while (validMove()){
		currentY+=1;
	}
	currentY--;
	placePiece(currentPiece);
}
public void lose(){
	gameOver=true;
}
public void update(Graphics g){
    paint(g);
}
String temp;
public void paint(Graphics g){  // draws the game
    int s = 25; //scale
    this.doubleGraphics.setColor(Color.black); //setting colors to draw on the image
    this.doubleGraphics.fillRect(0,0,getSize().width,getSize().height);
    doubleGraphics.setColor(Color.white);
    for (int i=0;i<(sizeX+4)*25;i+=25){
    	doubleGraphics.drawLine(0, i, sizeY*25, i);
    }
    for (int i=0;i<=sizeY*25;i+=25){
    	doubleGraphics.drawLine(i, 0, i, sizeX*25);
    }
    doubleGraphics.setColor(Color.YELLOW);
   temp=Integer.toString(score);
   doubleGraphics.drawString("score | current speed:" + speed, 350, 20);
    doubleGraphics.drawString(temp, 350, 40);
    doubleGraphics.drawRect(350, 80, 80, 80);
    doubleGraphics.drawString("Restart" , 360, 120);
    doubleGraphics.setColor(Color.white);
        for (int i=0;i<(20+4);i++){  //loop trough board
            for (int j=0;j<12;j++) {
                if (screen[i][j] !=-1) {
                	switch (screen[i][j]){ //sets the color of the piece
                	case 0: doubleGraphics.setColor(Color.blue); break;
                	case 1:doubleGraphics.setColor(Color.cyan); break;
                	case 2:doubleGraphics.setColor(Color.GREEN); break;
                	case 3:doubleGraphics.setColor(Color.magenta); break;
                	case 4:doubleGraphics.setColor(Color.red); break;
                	case 5:doubleGraphics.setColor(Color.pink); break;
                	default: doubleGraphics.setColor(Color.yellow);
                		
                	}
                    doubleGraphics.fillRect(j*s,i*s-100,s,s);  //draws the piece square by sqaure
                }
            }
        }
    // blit double buffer to frame buffer
    g.drawImage(doubleImage,0,0,null); // finally draws the image to the screen
}

public void start(){  
    if (thread == null) {
        thread = new Thread(this);
        thread.start();
    }
}

public void run(){  // makes the pieces fall and the game moves on
   while (!gameOver) {
	   if (	movePiece(0,1)){
		   remove();
		   pickPiece();
		   if (!validMove()){lose();}
	   }
            repaint();
            try {
                Thread.sleep(speed);
            } 
            catch (InterruptedException e) {
            }
    }
}

public void stop(){
    thread = null;
}


}
