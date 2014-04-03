package Tetris;

import java.awt.*;
import java.util.Random;

public class Block {
	// General static data about tetris shapes and Random object
	private static final Random generator = new Random();
	
	private Tetromino block;
	private Point location;
	private boolean rotated;
	public Block() {
		// Get a random shape
		block = Tetromino.getTetromino(generator.nextInt(Tetromino.NUM_SHAPES));
		rotated = false;
	}
	
	private void draw() {
		
	}
	
	public void insert() {
		
	}
	public void fall() {
		
	}
	public void drop() {
		
	}
	public void rotate() {
		rotated = !rotated;
		draw();
	}
	
	public void shiftRight() {
		// TODO: replace 100 with an accessor for the board width
		if (location.x + block.getConfiguration(rotated).getMaxX() < 100) {
			
			location.setLocation(location.x + 1, location.y);
			draw();
		}
	}
	public void shiftLeft() {
		if (location.x - block.getConfiguration(rotated).getMinX() >= 0) {
			location.setLocation(location.x - 1, location.y);
			draw();
		}
	}
}
