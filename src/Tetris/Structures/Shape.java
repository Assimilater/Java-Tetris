package Tetris.Structures;

import java.awt.*;
import java.util.Random;

public class Shape {
	private static final Random generator = new Random();
	private static final int NUM_SHAPES = 7;
	
	private int config, state;
	
	// Default Constructor is private because getNext will pick a random state to instantiate
	private Shape(int i) { config = i; state = 0; }
	public Shape(Shape rhs) { config = rhs.config; state = rhs.state; }
	public static Shape nextShape() { return new Shape(generator.nextInt(NUM_SHAPES)); }
	
	// Manipulators
	public void rotate() { state = (state == configs[config].rotations - 1) ? 0 : state + 1; }
	public void derotate() { state = (state == 0) ? configs[config].rotations - 1 : state - 1; }
	
	// Accessors
	public Tetromino getName() { return configs[config].name; }
	public Color getColor() { return configs[config].color; }
	public Point[] getCoords() { return configs[config].states[state].coords; }
	
	// Boundary-checking properties
	public int getMinX() { return configs[config].states[state].minX; }
	public int getMaxX() { return configs[config].states[state].maxX; }
	public int getMinY(Grid g, int col) {
		int minY = 0;
		for (Point p : configs[config].states[state].coords) {
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
	
	// BELOW THIS POINT CONTAINS STRICTLY STATICALLY REFERENCED DATA OBJECTS
	public static enum Tetromino { SBlock, ZBlock, LineBlock, TBlock, SquareBlock, LBlock, MirroredLBlock }
	
	// TODO: Replace colors with nicer images :)
	public static final Color SHADOW = Color.GRAY, EMPTY = Color.BLACK;
	private static final Configuration[] configs = new Configuration[] {
		new Configuration(
			Tetromino.SBlock, Color.GREEN, 2,
			new Point[]{new Point( 0, -1), new Point(0,  0), new Point(-1, 0), new Point(-1, 1)}
		),
		new Configuration(
			Tetromino.ZBlock, Color.PINK, 2,
			new Point[]{new Point( 0, -1), new Point(0,  0), new Point( 1, 0), new Point( 1, 1)}
		),
		new Configuration(
			Tetromino.LineBlock, Color.CYAN, 2,
			new Point[]{new Point( 0, -1), new Point(0,  0), new Point( 0, 1), new Point( 0, 2)}
		),
		new Configuration(
			Tetromino.TBlock, Color.MAGENTA, 4,
			new Point[]{new Point(-1,  0), new Point(0,  0), new Point( 1, 0), new Point( 0, 1)}
		),
		new Configuration(
			Tetromino.SquareBlock, Color.YELLOW, 1,
			new Point[]{new Point( 0,  0), new Point(1,  0), new Point( 0, 1), new Point( 1, 1)}
		),
		new Configuration(
			Tetromino.LBlock, Color.BLUE, 4,
			new Point[]{new Point(-1, -1), new Point(0, -1), new Point( 0, 0), new Point( 0, 1)}
		),
		new Configuration(
			Tetromino.MirroredLBlock, Color.ORANGE, 4,
			new Point[]{new Point( 1, -1), new Point(0, -1), new Point( 0, 0), new Point( 0, 1)}
		)
	};
	
	private static class Configuration {
		private static class Rotation {
			public Point[] coords;
			public int minX, maxX;
			private Rotation(Point[] c, int r) {
				coords = new Point[4];
				
				for (int i = 0; i < 4; ++i) {
					coords[i] = new Point(c[i]);
				}
				
				// Rotate an appropriate number of times
				for (int i = 0; i < r; ++i) {
					for (Point p : coords) {
						p.setLocation(p.y, -p.x);
					}
				}
				
				// Cache minX and maxX
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
		}
		
		public Tetromino name;
		public Color color;
		public int rotations;
		public Point[] coords;
		public Rotation[] states;
		
		private Configuration(Tetromino tName, Color tColor, int tRotations, Point[] tCoords) {
			name = tName;
			color = tColor;
			rotations = tRotations;
			coords = tCoords;
			
			states = new Rotation[rotations];
			for (int i = 0; i < rotations; ++i) {
				states[i] = new Rotation(coords, i);
			}
		}
	}
}
