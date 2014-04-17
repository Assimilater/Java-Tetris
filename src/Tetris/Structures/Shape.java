package Tetris.Structures;

import Tetris.Program;

import java.awt.*;
import java.util.Random;

public class Shape {
	private static final Random generator = new Random();
	private static final int NUM_SHAPES = 7;
	
	private int config, state;
	
	// Default Constructor is private because getNext will pick a random state to instantiate
	private Shape(int i, int s) { config = i; state = s; }
	public static Shape nextShape() { return new Shape(generator.nextInt(NUM_SHAPES), 0); }
	public Shape rotate() { return new Shape(config, (state == configs[config].states.length - 1) ? 0 : state + 1); }
	
	// Accessors
	public Tetromino getName() { return configs[config].name; }
	public Image getColor() { return configs[config].color; }
	public Point[] getCoords() { return configs[config].states[state].coords; }
	
	// Boundary-checking properties
	public int getMinX() { return configs[config].states[state].minX; }
	public int getMaxX() { return configs[config].states[state].maxX; }
	public int getMinY() { return configs[config].states[state].minY; }
	public int getMaxY() { return configs[config].states[state].maxY; }
	
	// BELOW THIS POINT CONTAINS STRICTLY STATICALLY REFERENCED DATA OBJECTS
	public static enum Tetromino { SBlock, ZBlock, LineBlock, TBlock, SquareBlock, LBlock, MirroredLBlock }
	
	public static final Image SHADOW = Program.getImage("Shadow"), EMPTY = Program.getImage("Empty");
	private static final Configuration[] configs = new Configuration[] {
		new Configuration(
			Tetromino.LineBlock, Program.getImage("Line"), 2,
			new Point[]{new Point( 0, -1), new Point(0,  0), new Point( 0, 1), new Point( 0, 2)}
		),
		new Configuration(
			Tetromino.SBlock, Program.getImage("S"), 2,
			new Point[]{new Point( 0, -1), new Point(0,  0), new Point(-1, 0), new Point(-1, 1)}
		),
		new Configuration(
			Tetromino.ZBlock, Program.getImage("Z"), 2,
			new Point[]{new Point( 0, -1), new Point(0,  0), new Point( 1, 0), new Point( 1, 1)}
		),
		new Configuration(
			Tetromino.TBlock, Program.getImage("T"), 4,
			new Point[]{new Point(-1,  0), new Point(0,  0), new Point( 1, 0), new Point( 0, 1)}
		),
		new Configuration(
			Tetromino.SquareBlock, Program.getImage("Square"), 1,
			new Point[]{new Point( 0,  0), new Point(1,  0), new Point( 0, 1), new Point( 1, 1)}
		),
		new Configuration(
			Tetromino.LBlock, Program.getImage("L"), 4,
			new Point[]{new Point(-1, -1), new Point(0, -1), new Point( 0, 0), new Point( 0, 1)}
		),
		new Configuration(
			Tetromino.MirroredLBlock, Program.getImage("MirroredL"), 4,
			new Point[]{new Point( 1, -1), new Point(0, -1), new Point( 0, 0), new Point( 0, 1)}
		)
	};
	
	private static class Configuration {
		private static class Rotation {
			public Point[] coords;
			public int minX, maxX, minY, maxY;
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
				
				// Cache minX, maxX, minY and maxY
				minX = 0; maxX = 0; minY = 0; maxY = 0;
				for (Point p : coords) {
					if (p.x < minX) { minX = p.x; }
					if (p.x > maxX) { maxX = p.x; }
					if (p.y < minY) { minY = p.y; }
					if (p.y > maxX) { maxY = p.y; }
				}
			}
		}
		
		public Tetromino name;
		public Image color;
		public Point[] coords;
		public Rotation[] states;
		
		private Configuration(Tetromino tName, Image tColor, int tRotations, Point[] tCoords) {
			name = tName;
			color = tColor;
			coords = tCoords;
			
			states = new Rotation[tRotations];
			for (int i = 0; i < tRotations; ++i) {
				states[i] = new Rotation(coords, i);
			}
		}
	}
}
