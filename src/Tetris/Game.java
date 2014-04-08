package Tetris;

import Tetris.Forms.MainFrame;
import Tetris.Structures.Block;
import Tetris.Structures.Grid;
import com.sun.prism.paint.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game extends JPanel implements ActionListener {
	private static final int FALL_RATE = 1000, QUEUE_SIZE = 4;
	
	// boolean flag "isAccelerated" in case the time updates from leveling while the user is holding down
	// boolean flag "holdUsed" in case the user tries to stall by switching between hold back and forth
	private boolean holdUsed, isAccelerated;
	
	// int flag "counter" to be used by gameTimer and show the user a countdown
	private Integer level, linesToLevel;
	
	private Block holdBlock, gameBlock;
	private Block[] inQueueBlock;
	private Timer fallTimer;
	
	private Grid gameGrid, holdGrid;
	private Grid[] inQueueGrid;
	private JLabel linesLabel, linesCountLabel;
	
	// This exists because there will only be a single instance that can be tracked statically
	private static Game game;
	public static boolean isActive() { return game != null; }
	
	public Game() {
		game = this;
		this.setLayout(null);
		
		fallTimer = new Timer(FALL_RATE, this);
		
		holdUsed = false;
		isAccelerated = false;
		
		linesLabel = new JLabel("Lines to Level: ");
		linesLabel.setFont(Program.displayFont);
		linesLabel.setBounds(5, 250, 150, 20);
		this.add(linesLabel);
		
		linesCountLabel = new JLabel("");
		linesCountLabel.setFont(new Font(Program.displayFont.getFontName(), Font.BOLD, 30));
		linesCountLabel.setForeground(Color.RED);
		linesCountLabel.setBounds(5, 275, 150, 50);
		this.add(linesCountLabel);
		
		level = 0;
		linesToLevel = 0;
		updateGameLevel();
		
		holdGrid = new Grid(true);
		holdGrid.setBounds(300, 5, 50, 50);
		this.add(holdGrid);
		
		holdBlock = null;
		
		gameGrid = new Grid(false);
		gameGrid.setBounds(375, 0, 350, 600);
		this.add(gameGrid);
		
		gameBlock = new Block(gameGrid);
		
		inQueueGrid = new Grid[QUEUE_SIZE];
		inQueueBlock = new Block[QUEUE_SIZE];
		
		for (int i = 0; i < QUEUE_SIZE; ++i) {
			inQueueGrid[i] = new Grid(true);
			inQueueGrid[i].setBounds(900, 5 + 50 * i, 50, 50);
			this.add(inQueueGrid[i]);
			
			inQueueBlock[i] = new Block((inQueueGrid[i]));
		}
		
		MainFrame.setGamePanel(this);
	}
	
	// Update the fallTimer's delay in milliseconds
	private void updateFallRate() {
		// Level 1 fall rate is 1 second, ever level thereafter is half the time
		// If the user is holding the down key it should double the speed, so don't decrement
		// At level 1, you don't double the fall speed, so decrement otherwise
		int factor = level;
		if (!isAccelerated) {
			--factor;
		}
		
		fallTimer.setDelay((int)(FALL_RATE * Math.pow(.5, factor)));
	}
	
	// updateGameLevel - Determine if the number of lines to level up has been reached, if so update the game
	private void updateGameLevel() {
		if (linesToLevel <= 0) {
			++level;
			linesToLevel += level * 5 + 15;
			
			updateFallRate();
		}
		updateLinesLabel();
	}
	
	private void updateLinesLabel() { linesCountLabel.setText(linesToLevel.toString()); }
	
	private void getNext() {
		holdUsed = false;
		
		// Dequeue the head element
		gameBlock = inQueueBlock[0];
		gameBlock.insert(gameGrid);
		
		// Shuffle the queue forward
		// There's only four elements in this queue so it's not a noticeable performance hit
		// The benefit to this is it keeps the indices for each block lined up with the panels they're drawn in
		for (int i = 1; i < QUEUE_SIZE - 1; ++i) {
			inQueueBlock[i - 1] = inQueueBlock[i];
			inQueueBlock[i - 1].insert(inQueueGrid[i - 1]);
		}
		
		// Insert a new block at the tail of the queue
		inQueueBlock[QUEUE_SIZE - 1] = new Block(inQueueGrid[QUEUE_SIZE - 1]);
	}
	
	// Called when a block "sinks into place"
	public static void sink(int row) {
		game.fallTimer.stop();
		
		//game.linesToLevel -= game.gameGrid.collapseAbove(row);
		//game.updateGameLevel();
		
		// TODO: replace false with method call fo overflow check
		if (false) {
			JOptionPane.showMessageDialog(MainFrame.getThis(),
				"Congratulations!\n" +
				"You made it to:\n" +
				"Level " + game.level,
				"Game Over",
				JOptionPane.INFORMATION_MESSAGE
			);
			
			// Stop the game simply by setting it to null
			game = null;
		}
		else {
			game.getNext();
			
			game.fallTimer.start();
		}
	}
	
	public static void hold() {
		if (game.holdUsed) {
			// Alert the user they can't switch again
			return;
		}
		
		game.fallTimer.stop();
		
		if (game.holdBlock != null) {
			Block temp = game.gameBlock;
			
			game.gameBlock = game.holdBlock;
			game.holdBlock = temp;
			
			game.gameBlock.insert(game.gameGrid);
			game.holdBlock.insert(game.holdGrid);
		}
		else {
			game.holdBlock = game.gameBlock;
			game.holdBlock.insert(game.holdGrid);
			
			game.getNext();
		}
		
		game.holdUsed = true;
		game.fallTimer.start();
	}
	
	// Let block objects handle the manipulative commands
	public static void drop() { game.gameBlock.drop(); }
	public static void rotate() { game.gameBlock.rotate(); }
	public static void shiftRight() { game.gameBlock.shiftRight(); }
	public static void shiftLeft() { game.gameBlock.shiftLeft(); }
	
	// Adjust the fallTimer when the user hold the down arrow 
	public static void accelerate() { game.isAccelerated = true; game.updateFallRate(); }
	public static void decelerate() { game.isAccelerated = false; game.updateFallRate(); }
	
	// ActionListener
	public synchronized void actionPerformed(ActionEvent e) {
		if (e.getSource() == fallTimer) {
			//game.gameBlock.fall();
		}
	}
}
