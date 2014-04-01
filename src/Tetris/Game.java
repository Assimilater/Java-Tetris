package Tetris;

import Tetris.Blocks.Block;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;

public class Game extends JPanel implements ActionListener {
	private static final int FALL_RATE = 500;
	
	// boolean flag "isAccelerated" in case the time updates from leveling while the user is holding down
	// boolean flag "holdUsed" in case the user tries to stall by switching between hold back and forth
	private boolean holdUsed, isAccelerated;
	
	// int flag "counter" to be used by gameTimer and show the user a countdown
	private int level, linesToLevel;
	
	private Block holdBlock, activeBlock;
	private ArrayDeque<Block> blocksInQueue;
	private Timer fallTimer;
	
	// This exists because there will only be a single instance that can be tracked statically
	private static Game game;
	public static boolean isActive() { return game != null; }
	
	public Game() {
		game = this;
		MainFrame.setGamePanel(this);
		
		fallTimer = new Timer(1000, this);
		
		holdUsed = false;
		isAccelerated = false;
		
		level = 0;
		linesToLevel = 0;
		updateGameLevel();
		
		holdBlock = null;
		activeBlock = Block.getRandom();
		blocksInQueue = new ArrayDeque<Block>(4);
		
	}
	
	private void redraw() {
		
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
		
		game.fallTimer.setDelay((int)(1000 * Math.pow(.5, factor)));
	}
	
	// updateGameLevel - Determine if the number of lines to level up has been reached, if so update the game
	private static void updateGameLevel() {
		if (game.linesToLevel <= 0) {
			++game.level;
			game.linesToLevel = game.level * 5 + 15;
			
			updateFallRate();
		}
	}
	
	public static void getNext() {
		game.fallTimer.stop();
		game.holdUsed = false;
		
		game.activeBlock = game.blocksInQueue.remove();
		game.blocksInQueue.add(Block.getRandom());
		
		game.redraw();
		
		game.fallTimer.start();
	}
	
	// Called when a block "sinks into place"
	public static void sink() {
		updateGameLevel();
		
		if (false) { // Check if the grid has overflow
			game.fallTimer.stop();
			JOptionPane.showMessageDialog(MainFrame.getThis(),
				"Congratulations!\n" +
				"You made it to:\n" +
				"Level " + game.level,
				"Game Over",
				JOptionPane.INFORMATION_MESSAGE
			);
			
			// Stop the game
			game = null;
		}
	}
	
	public static void hold() {
		if (game.holdUsed) {
			// Alert the user they can't switch again
			return;
		}
		
		game.fallTimer.stop();
		
		if (game.holdBlock != null) {
			Block temp = game.activeBlock;
			game.activeBlock = game.holdBlock;
			game.holdBlock = temp;
		}
		else {
			game.holdBlock = game.activeBlock;
			getNext();
		}
		
		game.holdUsed = true;
		game.fallTimer.start();
	}
	
	// Let block objects handle the manipulative commands
	public static void drop() { game.activeBlock.drop(); }
	public static void rotate() { game.activeBlock.rotate(); }
	public static void shiftRight() { game.activeBlock.shiftRight(); }
	public static void shiftLeft() { game.activeBlock.shiftLeft(); }
	
	// Adjust the fallTimer when the user hold the down arrow 
	public static void accelerate() { game.isAccelerated = true; updateFallRate(); }
	public static void decelerate() { game.isAccelerated = false; updateFallRate(); }
	
	// ActionListener
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == fallTimer) {
			game.activeBlock.fall();
		}
	}
}
