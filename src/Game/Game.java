package Game;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Images.Background;
import Images.Bird;
import Images.Foreground;
import Images.Obstacle;

@SuppressWarnings("serial")
public class Game extends JFrame implements KeyListener{

	private Background background1;
	private Background background2;
	
	private Foreground foreground1;
	private Foreground foreground2;
	
	//obstacle 1 and 2 pair up
	private Obstacle obstacle1;
	private Obstacle obstacle2;
	//obstacle 3 and 4 pair up
	private Obstacle obstacle3;
	private Obstacle obstacle4;
	
	private Bird bird;
	
	private int birdImgNumber;//switches between FlappyBirdAni#.png
	private int score;
	private int animationTimer;//time for animation to switch to next photo
	private int wait;//thread sleep time
	private int currentChoice;//main menu choices
	private int highScore;
	private int spacing;//spacing between obstacles
	
	private double degrees;
	
	private String startGameText;//text that appears before the game starts
	private String highScoreName;//name that goes with the new high score
	private String title;//game title that appears in the main menu
	private String mainCard;//Main Menu Panel Name
	private String gameCard;//Game Panel Name
	private String[] options = {//options in main menu
		"Start", "Clear High Score", "Exit"
	};	
	
	private boolean running;//condition that controls whether or not the game is running
	private boolean startGame;//condition that controls whether or not the game has been "started"
	private boolean gravity;//condition that controls bird's gravity
	private boolean mainMenuOpen;
	private boolean clearing;
	
	private Font titleFont;
	private Font optionFont;
	
	private JPanel cards = new JPanel(new CardLayout());
	private GamePanel gamePanel;
	private MainPanel mainPanel;
	private CardLayout cardLayout;
	
	public static void main(String[] args){
		new Game();
	}
	
	@SuppressWarnings("static-access")
	public Game(){
		
		init();
		
		cards.add(mainPanel, mainCard);
		cards.add(gamePanel, gameCard);
		
		mainPanel.addKeyListener(this);
		gamePanel.addKeyListener(this);
		
		this.add(cards);
		this.setTitle("Flappy Bird");
		this.setResizable(false);
		this.pack();
		this.setSize(background1.getWidth(), background1.getHeight());
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		start();
	}
	
	public void init(){
		
		//image objects
		background1 = new Background("Background.png");
		background2 = new Background("Background.png", background1.getWidth(), 0);
		
		foreground1 = new Foreground( "foreground.png", 0, background1.getHeight() * 7/8 - 26);
		foreground2 = new Foreground( "foreground.png", background1.getWidth(), background1.getHeight() * 7/8 - 26);
		
		obstacle1 = new Obstacle("Obstacle.png", 
								  background1.getWidth() , 
								  -(int)(Math.random() * (background1.getHeight() /2 )) 
								);
		obstacle2 = new Obstacle("Obstacle.png", 
								  background1.getWidth(), 
								  obstacle1.getY() + obstacle1.getHeight() + 100
								);
		obstacle3 = new Obstacle("Obstacle.png", 
								  background1.getWidth() + background1.getWidth()/2 + obstacle1.getWidth(), 
								  -(int)(Math.random() * ( background1.getHeight() / 2) ) 
								);
		obstacle4 = new Obstacle("Obstacle.png", 
								  background1.getWidth() + background1.getWidth()/2 + obstacle1.getWidth(), 
								  obstacle3.getY() + obstacle3.getHeight() + 100
								);
		
		bird = new Bird("FlappyBirdAni1.png", background1.getWidth() * 2/5, background1.getHeight() / 4);
		
		//integers
		birdImgNumber = 2;
		score = 0;
		animationTimer = 0;//"thread" for animation
		wait = 7;//thread sleep
		currentChoice = 0;
		spacing = 100;
		
		//doubles
		degrees = 0.0;
		bird.setFalling(0.15);//reset the falling speed back to its initial value
		
		//strings
		startGameText = "Press \"ENTER\" To Begin";
		title = "Flappy Bird";
		mainCard = "Main";
		gameCard = "Game";
		
		//booleans
		running = true;//condition that controls the game
		startGame = false;//condition that controls whether or not the game has been "started"
		gravity = false;
		mainMenuOpen = true;
		clearing = false;//only true when the "clear high score" button is chosen
		
		//fonts
		titleFont = new Font("Comic Sans MS", Font.PLAIN, 48);
		optionFont = new Font("Comic Sans MS", Font.BOLD, 24);
		
		//panels
		mainPanel = new MainPanel();
		gamePanel = new GamePanel();
		
		//card layout
		cardLayout = (CardLayout)cards.getLayout();
	}
	
	public void start(){//thread
		
		while( running ){
			
			if(gravity){
				bird.move();
				degrees++;//rotates the bird forward
				
				if(degrees > 90){//boundaries for degrees while falling
					degrees = 90;
				}
			}
			
			if( startGame == true ){
				obstacle1.move();
				obstacle2.move();
				obstacle3.move();
				obstacle4.move();
			}
			
			background1.move();
			background2.move();
			foreground1.move();
			foreground2.move();
			
			score();
			
			animationTimer ++;
			
			if(animationTimer == 400 / wait){
			
				if( birdImgNumber > 3 ){ birdImgNumber = 2; }//animation
			
				try {
					bird.setImage( ImageIO.read(new File("FlappyBirdAni"+birdImgNumber+".png")) );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				birdImgNumber ++;
				
				animationTimer = 0;
			}
			//collisions
			if(running == true)running = bird.collide(foreground1);
			if(running == true)running = bird.collide(foreground2);
			if(running == true)running = bird.collide(obstacle1);
			if(running == true)running = bird.collide(obstacle2);
			if(running == true)running = bird.collide(obstacle3);
			if(running == true)running = bird.collide(obstacle4);
			
			//redrawing obstacles
			if( obstacle1.getX() < -obstacle1.getWidth() ){//redraw the obstacles once they get too far off the screen
				obstacle1.setX(background1.getWidth()+ obstacle1.getWidth());
				obstacle2.setX(obstacle1.getX());
				obstacle1.setY(-(int)(Math.random() * (background1.getHeight() * 3/4 ) ) );
				obstacle2.setY(obstacle1.getY() + obstacle1.getHeight() +spacing);
				
			}
			if( obstacle3.getX() < -obstacle3.getWidth() ){//redraw the obstacles once they get too far off the screen
				obstacle3.setX(background1.getWidth() + obstacle3.getWidth());
				obstacle4.setX(obstacle3.getX());
				obstacle3.setY(-(int)(Math.random() * (background1.getHeight() * 3/4 ) ) );
				obstacle4.setY(obstacle3.getY() + obstacle3.getHeight() +spacing);
				
			}
			
			//fixes the obstacles in the ground problem
			if( obstacle2.getY() >= foreground1.getY() ){//checks if obstacle is below foreground
				obstacle1.setY( obstacle1.getY() - 40);
				obstacle2.setY( obstacle2.getY() - 40);
			}
			if( obstacle4.getY() >= foreground1.getY() ){//checks if obstacle is below foreground
				obstacle3.setY( obstacle3.getY() - 40);
				obstacle4.setY( obstacle4.getY() - 40);
			}
			
			if(running == false){//once the game is over, death animation
				while(bird.getY() + bird.getHeight()/2 < foreground1.getY()){
					
					bird.move();
					
					if(degrees < 90){
						degrees ++;
					}
					repaint();
					
					try {
						Thread.sleep(wait);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}				
				//once the dying animation is done
				
				if(score > getHighScore()){//is it a high score?
					highScoreName = JOptionPane.showInputDialog(this, "Enter Name");
					
					allSpaces();
					
					recordHighScore(score);//when the game is over, check if new high score		
				}else{//not a high score
					JOptionPane.showMessageDialog(null, "SCORE HIGHER NEXT TIME!");
				}
				
				cardLayout.show(cards, mainCard);
				init();
				start();
				
			}
			
			repaint();
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void score(){//adds to score
		if( bird.getX() == obstacle1.getX() + obstacle1.getWidth()/2 || //checks if the bird has gone through the middle of the obstacle
			bird.getX() == obstacle3.getX() + obstacle3.getWidth()/2)
		{
			score += 1;
		}
	}
	
	public void allSpaces(){//checks for null or all spaces input for highscore
		try{
			highScoreName = highScoreName.trim();
			
			int length = highScoreName.length();
			int numberOfSpaces = 0;
	
			for(int i = 0; i < length; i ++){
				if(Character.isWhitespace(highScoreName.charAt(i))){
					numberOfSpaces ++;
				}
			}
			
			if(numberOfSpaces == length){
				
				highScoreName = JOptionPane.showInputDialog(this, "Enter Name");
				allSpaces();
				
			}
		}catch(NullPointerException npe){
			highScoreName = "Default_Name";//if no name entered
		}
	}
	
	public void clearHighScore(){	
		
		File homeFile = new File("C:/HighScore.txt");
		Scanner fileReader;
			
		FileWriter fileWriter;
		PrintWriter printWriter;
		
		clearing = true;//clearing began
			
			try {
				fileReader = new Scanner(homeFile);
				
				//read from the file
				
				if(fileReader.hasNextLine()){//if there is something to clear
					
					int decision = JOptionPane.showConfirmDialog(null, "Are you sure?");
					
					if(JOptionPane.OK_OPTION == decision){//if yes, clear
						try {
						fileWriter = new FileWriter(homeFile);
					
						printWriter = new PrintWriter(fileWriter);
				
						printWriter.print("");
						printWriter.close();
						fileWriter.close();
						
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}else{//inform user there isn't a high score
					JOptionPane.showMessageDialog(null, "No High Score to Clear");
				}
				
				fileReader.close();
				
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "No File Found");
				System.exit(0);
			}
			
		clearing = false;//clearing done
	}
	
	public int getHighScore(){
		File homeFile = new File("C:/HighScore.txt");

		Scanner fileReader;
		
		String line = "";//line of high score
		
		StringTokenizer tokenizer;
		
		try {
			fileReader = new Scanner(homeFile);
			
			//read from the file
			
			if(fileReader.hasNextLine()){
				line = fileReader.nextLine();
				
				tokenizer = new StringTokenizer(line," ");
				
				highScore = Integer.parseInt( tokenizer.nextToken() );
			}
			fileReader.close();
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "No File Found");
			System.exit(0);
		}
		
		return highScore;
	}

	public void recordHighScore(int i){//records a high-score if there is one
		File homeFile = new File("C:/HighScore.txt");
		Scanner fileReader;
		FileWriter fileWriter;
		PrintWriter printWriter;
		
		String line = "";//line of high score
		StringTokenizer scoreTokenizer;
		
		int currentScore = -1;
		
		try {
			fileReader = new Scanner(homeFile);
			
			//read from the file
			
			if(fileReader.hasNextLine()){
				
					line = fileReader.nextLine();
					
					scoreTokenizer = new StringTokenizer(line," ");
					
					currentScore = Integer.parseInt( scoreTokenizer.nextToken() );

			}else{
				try {
					fileWriter = new FileWriter(homeFile);
					printWriter = new PrintWriter(fileWriter);
					
					printWriter.print("No High Score");
					
					printWriter.close();
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			fileReader.close();
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		if( i >  currentScore){
			try {
				fileWriter = new FileWriter(homeFile);
			
				printWriter = new PrintWriter(fileWriter);
		
			//if the new score is higher, replace old one
				printWriter.print(i + " - " + highScoreName);
				printWriter.close();
				fileWriter.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void select(){
		if(currentChoice == 0){//if start
			mainMenuOpen = false;
			bird = new Bird("FlappyBirdAni1.png", background1.getWidth()/4, background1.getHeight()/2);
			cardLayout.show(cards, gameCard);
		}
		if(currentChoice == 1){//if clear high score
			clearHighScore();
		}
		if(currentChoice == 2){//if quit
			System.exit(0);
		}
	}

	public void keyTyped(KeyEvent ke) {}

	public void keyPressed(KeyEvent ke) {
		
		if(ke.getKeyCode() == KeyEvent.VK_UP){
			if(mainMenuOpen){
				if(!clearing){//scrolls up on the options
					currentChoice --;
					
					if(currentChoice == -1){
						currentChoice = 2;
					}
					repaint();
				}
			}
		}
	}

	public void keyReleased(KeyEvent ke) {
		
		if(ke.getKeyCode() == KeyEvent.VK_ENTER){
			
			if(!mainMenuOpen){//starts the game
				if(startGame == false){
						gravity = true;
						startGameText = "";//removes the startGameText
						startGame = true;
				}	
			}
			
			if(mainMenuOpen){
				if(!startGame){
					if(!clearing){//picking an option
						select();
					}
				}
			}
		}
		
		if(ke.getKeyCode() == KeyEvent.VK_UP){
			
			if(startGame){
				if(running){//flying mechanic
					
					if(degrees > 0) degrees = 0;//if the bird is falling, reset it's rotation
					
					if(degrees <= 0 && degrees > -20){//boundaries for degrees
						degrees -= 20;
					}
					
					bird.setY(bird.getY() - 25);
					bird.setFalling(0.15);
				}
			}
		}
		
		if(ke.getKeyCode() == KeyEvent.VK_DOWN){//scrolls down on the options
			if(mainMenuOpen){
				if(!clearing){
					currentChoice ++;
				
					if(currentChoice == 3){
						currentChoice = 0;
					}
					repaint();
				}
			}
		}
	}
	
	class MainPanel extends JPanel{
		
		private String line = "";
		
		public String readScore(){
			File homeFile = new File("C:/HighScore.txt");
			
			Scanner fileReader;
			
			try {
				fileReader = new Scanner(homeFile);
				
				if( fileReader.hasNextLine() ){
					
					line = fileReader.nextLine();//returns the high-score and name

				}else{//resets the line to blank if clearing
					line = "";
				}
				
				fileReader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(0);
			}
			
			return line;
			
		}

		public void paintComponent(Graphics g){
			
			super.paintComponent(g);
			
			Graphics2D g2 = (Graphics2D)g;

			background1.draw(g2);
			background2.draw(g2);
			foreground1.draw(g2);
			foreground2.draw(g2);
			
			bird.draw(g2);
			
			if(background1.getX() < -background1.getWidth()){//repaints the background to the other side
				background1.setX(background1.getWidth() - 1);
				repaint();
			}
			if(background2.getX() < -background1.getWidth()){//repaints the background to the other side
				background2.setX(background1.getWidth() - 1);
				repaint();
			}
			
			g.setColor(Color.YELLOW);
			g.setFont(titleFont);
			g.drawString(title, background1.getWidth() * 3/11, background1.getHeight() / 8);
			
			g.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
			g.setColor(Color.BLACK);
			g.drawString(readScore(), background1.getWidth() / 10, background1.getHeight() / 3);
			
			g.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
			g.setColor(Color.YELLOW);
			g.drawString("High Score:", background1.getWidth() / 10, background1.getHeight() * 2/7);
			
			for(int i = 0; i < options.length; i ++){
				if( i == currentChoice ){
					g.setColor(Color.RED);
				}else{
					g.setColor(Color.BLACK);
				}
					g.setFont(optionFont);
					g.drawString(options[i], background1.getWidth() * 2/5, (background1.getHeight() / 2) + (30 * i));
			}
			
			this.requestFocus();
		}
	}
	
	class GamePanel extends JPanel{
		
		public void paintComponent(Graphics g){
		
			super.paintComponent(g);
			
			int displacementY = bird.getY();
			int displacementX = bird.getX();
					
			Graphics2D g2 = (Graphics2D) g;
		
			background1.draw(g2);
			background2.draw(g2);
			
			foreground1.draw(g2);
			foreground2.draw(g2);
			
			obstacle1.draw(g2);
			obstacle2.draw(g2);
			obstacle3.draw(g2);
			obstacle4.draw(g2);
			
			g2.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
			g2.drawString(""+score, background1.getWidth()/2, background1.getHeight()/10);//score string
			
			g2.setFont(new Font("", Font.BOLD, 36));
			g2.drawString(startGameText, background1.getWidth()/5, background1.getHeight()/2);
			
			if(running == false){
				g2.drawString("THUMP", bird.getX(), bird.getY());
			}
			
			g2.rotate(Math.toRadians(degrees), bird.getWidth()/2 + displacementX, bird.getHeight()/2 + displacementY);
			
			bird.draw(g2);
			
			this.requestFocus();
		}
	}
}