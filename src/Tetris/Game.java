package Tetris;

import Tetris.Forms.MainFrame;
import Tetris.Structures.Block;
import Tetris.Structures.Grid;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game extends JPanel implements ActionListener {
	private static final int FALL_RATE = 1000, QUEUE_SIZE = 4;
	
	// boolean flag "isAccelerated" in case the time updates from leveling while the user is holding down
	// boolean flag "holdUsed" in case the user tries to stall by switching between hold back and forth
	private boolean holdUsed, isAccelerated;
	
	// int flag "counter" to be used by gameTimer and show the user a countdown
	private int level, linesToLevel;
	
	private Block holdBlock, gameBlock;
	private Block[] inQueueBlock;
	private Timer fallTimer;
	
	private Grid gameGrid, holdGrid;
	private Grid[] inQueueGrid;
	
	// This exists because there will only be a single instance that can be tracked statically
	private static Game game;
	public static boolean isActive() { return game != null; }
	
	public Game() {
		game = this;
		MainFrame.setGamePanel(this);
		
		fallTimer = new Timer(FALL_RATE, this);
		
		holdUsed = false;
		isAccelerated = false;
		
		level = 0;
		linesToLevel = 0;
		updateGameLevel();
		
		holdGrid = new Grid(true);
		holdBlock = null;
		
		gameGrid = new Grid(false);
		gameBlock = new Block(gameGrid);
		
		inQueueGrid = new Grid[QUEUE_SIZE];
		inQueueBlock = new Block[QUEUE_SIZE];
		
		for (int i = 0; i < QUEUE_SIZE; ++i) {
			inQueueGrid[i] = new Grid(true);
			inQueueBlock[i] = new Block((inQueueGrid[i]));
		}
	}
	
	// Update the fallTimer's delay in milliseconds
	private static void updateFallRate() {
		// Level 1 fall rate is 1 second, ever level thereafter is half the time
		// If the user is holding the down key it should double the speed, so don't decrement
		// At level 1, you don't double the fall speed, so decrement otherwise
		int factor = game.level;
		if (!game.isAccelerated) {
			--factor;
		}
		
		game.fallTimer.setDelay((int)(FALL_RATE * Math.pow(.5, factor)));
	}
	
	// updateGameLevel - Determine if the number of lines to level up has been reached, if so update the game
	private static void updateGameLevel() {
		if (game.linesToLevel <= 0) {
			++game.level;
			game.linesToLevel += game.level * 5 + 15;
			
			updateFallRate();
		}
	}
	
	private static void getNext() {
		game.holdUsed = false;
		
		// Dequeue the head element
		game.gameBlock = game.inQueueBlock[0];
		game.gameBlock.insert(game.gameGrid);
		
		// Shuffle the queue forward
		// There's only four elements in this queue so it's not a noticeable performance hit
		// The benefit to this is it keeps the indices for each block lined up with the panels they're drawn in
		for (int i = 1; i < QUEUE_SIZE - 1; ++i) {
			game.inQueueBlock[i - 1] = game.inQueueBlock[i];
			game.inQueueBlock[i - 1].insert(game.inQueueGrid[i - 1]);
		}
		
		// Insert a new block at the tail of the queue
		game.inQueueBlock[QUEUE_SIZE - 1] = new Block(game.inQueueGrid[QUEUE_SIZE - 1]);
	}
	
	// Called when a block "sinks into place"
	public static void sink(int row) {
		game.fallTimer.stop();
		
		game.linesToLevel -= game.gameGrid.collapseAbove(row);
		updateGameLevel();
		
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
			getNext();
			
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
			
			getNext();
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
	public static void accelerate() { game.isAccelerated = true; updateFallRate(); }
	public static void decelerate() { game.isAccelerated = false; updateFallRate(); }
	
	// ActionListener
	public synchronized void actionPerformed(ActionEvent e) {
		if (e.getSource() == fallTimer) {
			game.gameBlock.fall();
		}
	}
}
