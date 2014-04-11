package Tetris.Structures;

import Tetris.Game;

import java.awt.*;

public class Block {
	// Declared statically to reduce memory usage
	private static Point point = new Point();
	private static Shape shape;
	private static Point[] coords;
	private static Grid.GridCell cell;
	
	private Shape block;
	private Grid pane;
	private Point location, maxFall;
	public Block(Grid g) {
		block = Shape.nextShape();
		location = new Point();
		maxFall = new Point();
		insert(g);
	}
	
	// In a Tetromino the lowest y-coordinate is -1, so we can be sure maxFall - 1 will cover all possible collapsible rows
	private void sink() { Game.sink(location.y - 1); }
	
	public boolean insert(Grid g) {
		free();
		pane = g;
		
		location.setLocation(g.insertAt());
		if (location.x == -1) {
			location.setLocation(-block.getMinX(), -block.getMinY());
		}
		
		calcShadowDrop();
		if (maxFall.y > location.y) {
			return false;
		}
		
		draw();
		return true;
	}
	
	// Public because the game needs to be able to perform a successful swap on hold
	public void freeGrid() { free(); pane = null; }
	
	private void free() {
		if (pane != null) {
			coords = block.getCoords();
			for (Point p : coords) {
				if (pane.insertAt().x != -1) {
					pane.cell(maxFall.y + p.y, location.x + p.x).Clear();
				}
				pane.cell(location.y + p.y, location.x + p.x).Clear();
			}
		}
	}
	
	private void draw() {
		if (pane != null) {
			coords = block.getCoords();
			if (pane.insertAt().x != -1) {
				for (Point p : coords) {
					pane.cell(maxFall.y + p.y, location.x + p.x).Shadow();
				}
			}
			for (Point p : coords) {
				pane.cell(location.y + p.y, location.x + p.x).Block(block);
			}
		}
	}
	
	// It is assumed that all remaining methods will not be called unless this block is the active one in the game
	public void fall() {
		point.setLocation(location);
		point.translate(0, -1);
		if (assertLegal(pane, block, point, block, location)) {
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
		location.setLocation(location.x, maxFall.y);
		draw();
		sink();
	}
	
	private void calcShadowDrop() {
		if (pane != null) {
			maxFall.setLocation(location);
			maxFall.translate(0, 1);
			
			point.setLocation(location);
			do {
				if (!assertLegal(pane, block, point, block, null)) { break; }
				else { point.translate(0, -1); maxFall.translate(0, -1); }
			} while (true);
		}
	}
	
	public void rotate() {
		shape = block.rotate();
		point.setLocation(location);
		
		// Check if by rotating we've gone out of bounds, and make corrections
		while (point.x + shape.getMinX() < 0) { point.translate(1, 0); }
		while (point.x + shape.getMaxX() >= pane.getCols()) { point.translate(-1, 0); }
		
		// Check and make sure we're not rotating into any previously placed blocks
		if (assertLegal(pane, shape, point, block, location)) {
			free();
			
			block = block.rotate();
			location.setLocation(point);
			calcShadowDrop();
			
			draw();
		}
	}
	
	public void shiftRight() {
		point.setLocation(location);
		point.translate(1, 0);
		
		if (assertLegal(pane, block, point, block, location)) {
			free();
			
			location.translate(1, 0);
			calcShadowDrop();
			
			draw();
		}
	}
	public void shiftLeft() {
		point.setLocation(location);
		point.translate(-1, 0);
		
		if (assertLegal(pane, block, point, block, location)) {
			free();
			
			location.translate(-1, 0);
			calcShadowDrop();
			
			draw();
		}
	}
	
	private static boolean assertLegal(Grid g, Shape s, Point pos, Shape overShape, Point overPos) {
		if (g == null) { return false; }
		
		Point[] points = overShape.getCoords();
		boolean isOverlap = false;
		
		coords = s.getCoords();
		for (Point p : coords) {
			if (overPos != null) {
				isOverlap = false;
				for (Point p2 : points) {
					if (p2.x + overPos.x == p.x + pos.x && p2.y + overPos.y == p.y + pos.y) {
						isOverlap = true;
						break;
					}
				}
			}
			
			if (!isOverlap) {
				cell = g.cell(pos.y + p.y, pos.x + p.x);
				if (cell == null || cell.getState() == Grid.GridState.BLOCK) {
					return false;
				}
			}
		}
		return true;
	}
}
