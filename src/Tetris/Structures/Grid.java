package Tetris.Structures;

import Tetris.Assets;

import javax.swing.*;
import java.awt.*;

public class Grid extends JPanel {
	public static enum GridState { EMPTY, SHADOW, BLOCK }
	public class GridCell extends JPanel {
		private GridState state;
		private Shape.Tetromino block;
		private boolean isBig;
		
		public GridState getState() { return state; }
		public Shape.Tetromino getBlock() { return block; }
		@Override
		public void paintComponent(final Graphics g) {
			super.paintComponent(g);
			Assets.paint(block, isBig, this, g);
		}
		
		public GridCell (boolean big) { Clear(); isBig = big; }
		public void Shadow() {
			state = GridState.SHADOW;
			block = Shape.Tetromino.Shadow;
			this.repaint();
		}
		public void Clear() {
			state = GridState.EMPTY;
			block = Shape.Tetromino.Empty;
			this.repaint();
		}
		public void Block(Shape.Tetromino b) {
			state = GridState.BLOCK;
			block = b;
			this.repaint();
		}
	}
	
	private int visibleRows, rows, cols;
	private Point insertion;
	private GridCell[][] gridCells;
	public Grid(boolean isPlaceholder) {
		// Make default background transparent
		this.setOpaque(false);
		
		if (isPlaceholder) {
			visibleRows = 4; rows = 4; cols = 4;
			insertion = new Point(-1, -1);
		}
		else {
			visibleRows = 20; rows = 24; cols = 10;
			insertion = new Point(5, 20);
		}
		
		this.setLayout(new GridLayout(visibleRows, cols));
		
		gridCells = new GridCell[rows][cols];
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; ++j) {
				gridCells[i][j] = new GridCell(!isPlaceholder);
			}
		}
		
		// Second for loop because GridLayout inserts left to right top to bottom, and we want left to right bottom to top
		for (int i = rows - visibleRows; i < rows; ++i) {
			for (int j = 0; j < cols; ++j) {
				this.add(gridCells[rows - 1 - i][j]);
			}
		}
	}
	
	// Accessors
	public Point insertAt() { return insertion; }
	public int getCols() { return cols; }
	public int getRows() { return rows; }
	public GridCell cell(int row, int col) {
		if (row < rows && col < cols) {
			if (row < 0 || col < 0) {
				return null;
			}
			return gridCells[row][col];
		}
		return null;
	}
	
	public int collapseAbove(int row) {
		if (row >= rows) { return 0; }
		
		// Loop through the row to check if it's empty, full, or partially full
		boolean full = true, empty = true;
		for (int j = 0; j < cols; ++j) {
			if (gridCells[row][j].getState() != GridState.BLOCK) {
				full = false;
			}
			else {
				empty = false;
			}
		}
		
		// Base case 1: The row is full, shift all rows above down 1
		if (full) {
			int above = collapseAbove(row + 1);
			
			empty = false;
			for (int i = row; i < rows - 1 && !empty; ++i) {
				empty = true;
				for (int j = 0; j < cols; ++j) {
					if (gridCells[i + 1][j].getState() == GridState.BLOCK) {
						gridCells[i][j].Block(gridCells[i + 1][j].getBlock());
						empty = false;
					}
					else {
						gridCells[i][j].Clear();
					}
				}
			}
			
			return ++above;
		}
		
		// Base case 2: The row is empty, halt recursive calls 
		if(empty) {
			return 0;
		}
		
		// Base case 3: The row is partially full, continue recursive search
		return collapseAbove(row + 1);
	}
	
	public boolean checkIfAllEmpty() {
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; ++j) {
				if (gridCells[i][j].getState() != GridState.EMPTY) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void wipe() {
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; ++j) {
				gridCells[i][j].Clear();
			}
		}
	}
	
	public void repaintGrid() {
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; ++j) {
				gridCells[i][j].repaint();
			}
		}
	}
}
