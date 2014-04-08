package Tetris.Structures;

import java.awt.*;

public class Configuration {
	private Point[] coords;
	private int minX, maxX;
	
	public Configuration(Point[] points) {
		coords = new Point[4];
		for (int i = 0; i < 4; ++i) {
			coords[i] = new Point(points[i]);
		}
		cache();
	}
	
	public Configuration(Configuration rhs) {
		coords = new Point[4];
		for (int i = 0; i < 4; ++i) {
			coords[i] = new Point(rhs.coords[i]);
		}
		cache();
	}
	
	public void rotate() {
		for (Point p : coords) {
			p.setLocation(p.y, -p.x);
		}
		cache();
	}
	
	private void cache() {
		minX = 0;
		for (Point p : coords) {
			if (p.x < minX) {
				minX = p.x;
			}
		}
		
		maxX = 0;
		for (Point p : coords) {
			if (p.x > maxX) {
				maxX = p.x;
			}
		}
	}
	
	// The Block class still needs to have access to the raw data when it "draws" to the board
	public Point[] getCoords() { return coords; }
	
	// MinX and MaxX are used to determine if you can shift left or right 
	public int getMinX() { return minX; }
	
	public int getMaxX() { return maxX; }
	
	// MinY is used to determine how low a piece can go
	public int getMinY(Grid g, int col) {
		int minY = 0;
		for (Point p : coords) {
			boolean sentinel = true;
			while (sentinel) {
				if (g.cell(p.y + minY, col + p.x) == null) {
					++minY;
				}
				else if (g.cell(p.y + minY, col + p.x).getState() == Grid.GridState.BLOCK) {
					++minY;
				}
				else {
					sentinel = false;
				}
				
				if (minY >= g.getRows()) {
					sentinel = false; // this will cause a return value out of bounds, indicating it can't go here 
				}
			}
		}
		return minY;
	}
}
