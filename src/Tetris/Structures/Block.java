package Tetris.Structures;

import Tetris.Game;

import java.awt.*;
import java.util.Random;

public class Block {
	private static final Random generator = new Random();
	
	private Tetromino block;
	private Grid pane;
	private Point location;
	private boolean rotated;
	private int minY;
	public Block(Grid g) {
		block = Tetromino.getTetromino(generator.nextInt(Tetromino.NUM_SHAPES));
		insert(g);
	}
	
	// The value of minY needs to be accessed enough it's justified to update a member with this method call
	private void calcMinY() { minY = block.getConfiguration(rotated).getMinY(location.x); }
	
	// In a Tetromino the lowest y-coordinate is -1, so we can be sure minY - 1 will cover all possible collapsible rows
	private void sink() { Game.sink(minY - 1); }
	
	public void insert(Grid g) {
		free();
		pane = g;
		location = g.insertAt();
		rotated = false;
		calcMinY();
	}
	
	private void draw() {
		if (pane != null) {
			// This requires two separate loops because a falling block may overlay its shadow partially before it is fully in place
			for (Point p : block.getConfiguration(rotated).getCoords()) {
				pane.cell(minY - p.y, location.x - p.x).Shadow();
			}
			for (Point p : block.getConfiguration(rotated).getCoords()) {
				pane.cell(location.y - p.y, location.x - p.x).Block(block);
			}
		}
	}
	private void free() {
		if (pane != null) {
			for (Point p : block.getConfiguration(rotated).getCoords()) {
				pane.cell(location.y - p.y, location.x - p.x).Clear();
				pane.cell(minY - p.y, location.x - p.x).Clear();
			}
		}
	}
	
	// It is assumed that all remaining methods will not be called unless this block is the active one in the game
	public void fall() {
		if (location.y > minY) {
			free();
			
			location.translate(0, -1);
			draw();
		}
		else {
			sink();
		}
	}
	public void drop() {
		free();
		location.setLocation(location.x, minY);
		draw();
		sink();
	}
	public void rotate() {
		boolean legal = true;
		
		// Make sure that by rotating we don't rotate into another block
		for (Point p : block.getConfiguration(!rotated).getCoords()) {
			if (p.y + location.y > 0 && p.x + location.x >= 0 && p.x + location.x < pane.getCols()) {
				if (pane.cell(p.y + location.y, p.x + location.x).getState() == Grid.GridState.BLOCK) {
					legal = false;
					break;
				}
			}
		}
		
		if (legal) {
			free();
			
			rotated = !rotated;
			calcMinY();
			
			// Check if by rotating we've gone out of bounds, and make corrections
			while (location.x + block.getConfiguration(rotated).getMinX() < 0) {
				location.translate(1, 0);
			}
			while (location.x + block.getConfiguration(rotated).getMaxX() > pane.getCols()) {
				location.translate(-1, 0);
			}
			
			draw();
		}
	}
	
	public void shiftRight() {
		if (location.x + block.getConfiguration(rotated).getMaxX() < pane.getCols()) {
			free();
			
			location.translate(1, 0);
			calcMinY();
			draw();
		}
	}
	public void shiftLeft() {
		if (location.x + block.getConfiguration(rotated).getMinX() >= 0) {
			free();
			
			location.translate(-1, 0);
			calcMinY();
			draw();
		}
	}
}
