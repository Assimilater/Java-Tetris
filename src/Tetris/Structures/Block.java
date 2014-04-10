package Tetris.Structures;

import Tetris.Game;

import java.awt.*;

public class Block {
	
	private Shape block;
	private Grid pane;
	private Point location;
	private int minY;
	public Block(Grid g) {
		block = Shape.nextShape();
		location = new Point();
		insert(g);
	}
	
	// The value of minY needs to be accessed enough it's justified to update a member with this method call
	private void calcMinY() { if (pane != null) minY = block.getMinY(pane, location.x); }
	
	// In a Tetromino the lowest y-coordinate is -1, so we can be sure minY - 1 will cover all possible collapsible rows
	private void sink() { Game.sink(minY - 1); }
	
	public void insert(Grid g) {
		free();
		pane = g;
		
		location.setLocation(g.insertAt());
		if (location.x == -1) {
			location.setLocation(-block.getMinX(), block.getMinY(g, -block.getMinX()));
		}
		
		calcMinY();
		draw();
	}
	
	private void free() {
		if (pane != null) {
			Point[] coords = block.getCoords();
			for (Point p : coords) {
				pane.cell(location.y + p.y, location.x + p.x).Clear();
				pane.cell(minY + p.y, location.x + p.x).Clear();
			}
		}
	}
	
	private void draw() {
		if (pane != null) {
			// This requires two separate loops because a falling block may overlay its shadow partially before it is fully in place
			Point[] coords = block.getCoords();
			if (pane.insertAt().x != -1) {
				for (Point p : coords) {
					pane.cell(minY + p.y, location.x + p.x).Shadow();
				}
			}
			for (Point p : coords) {
				pane.cell(location.y + p.y, location.x + p.x).Block(block);
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
		free();
		
		// Make sure that by rotating we don't rotate into another block
		Shape testShape = new Shape(block);
		Point testLoc = new Point(location);
		boolean legal = true;
		testShape.rotate();
		
		// Check if by rotating we've gone out of bounds, and make corrections
		while (testLoc.x + testShape.getMinX() < 0) {
			testLoc.translate(1, 0);
		}
		while (testLoc.x - 1 + testShape.getMaxX() >= pane.getCols()) {
			testLoc.translate(-1, 0);
		}
		
		// Check and make sure we're not rotating into any previously placed blocks
		Point[] coords = block.getCoords();
		Point validate = new Point();
		for (Point p : coords) {
			validate.setLocation(p.x + location.x, p.y + location.y);
			if (validate.y >= 0 && validate.x >= 0 && p.x + validate.x < pane.getCols()) {
				if (pane.cell(validate.y, validate.x).getState() == Grid.GridState.BLOCK) {
					legal = false;
					break;
				}
			}
		}
		
		if (legal) {
			block = testShape;
			location = testLoc;
			calcMinY();
		}
		
		draw();
	}
	
	public void shiftRight() {
		if (location.x + block.getMaxX() < pane.getCols() - 1) {
			free();
			if (pane.cell(location.y, location.x + 1 + block.getMaxX()).getState() != Grid.GridState.BLOCK) {
				location.translate(1, 0);
				calcMinY();
			}
			draw();
		}
	}
	public void shiftLeft() {
		if (location.x + block.getMinX() > 0) {
			free();
			if (pane.cell(location.y, location.x - 1 + block.getMaxX()).getState() != Grid.GridState.BLOCK) {
				location.translate(-1, 0);
				calcMinY();
			}
			draw();
		}
	}
}
