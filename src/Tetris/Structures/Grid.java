package Tetris.Structures;

import javax.swing.*;
import java.awt.*;

public class Grid extends JPanel {
	
	public enum GridState { EMPTY, SHADOW, BLOCK }
	
	public class GridCell extends JPanel {
		private GridState state;
		private Shape block;
		private Image image;
		
		public GridState getState() { return state; }
		public Shape getBlock() { return block; }
		private void setImage(Image i) { image = i; this.repaint(); }
		@Override
		public void paintComponent(final Graphics g) {
			super.paintComponent(g);
			if (image != null) {
				g.drawImage(image, 0, 0, null);
			}
		}
		
		public GridCell () { Clear(); }
		public void Shadow() {
			this.setImage(Shape.SHADOW);
			state = GridState.SHADOW;
			block = null;
		}
		public void Clear() {
			this.setImage(Shape.EMPTY);
			state = GridState.EMPTY;
			block = null;
		}
		public void Block(Shape b) {
			block = b;
			this.setImage(block.getColor());
			state = GridState.BLOCK;
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
				gridCells[i][j] = new GridCell();
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
}
