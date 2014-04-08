package Tetris.Structures;

import java.awt.*;
import java.util.HashMap;

public class Tetromino {
	// TODO: Replace colors with nicer images :)
	public static final Color
		SHADOW = Color.GRAY,
		EMPTY = Color.BLACK;
	
	public static final int
		NUM_SHAPES = 7,
		INFINITE_Y = 100; // A high value so there is no way for an empty column in a tetromino to interfere with the block's fall
	
	public enum tetroNames { ZBlock, SBlock, LineBlock, TBlock, SquareBlock, LBlock, MirroredLBlock }
	
	// Primitive data about each shape by order of tetroNames
	private final Point[][] tetroCoords = new Point[][]{
		{new Point( 0, -1), new Point(0,  0), new Point(-1, 0), new Point(-1, 1)},
		{new Point( 0, -1), new Point(0,  0), new Point( 1, 0), new Point( 1, 1)},
		{new Point( 0, -1), new Point(0,  0), new Point( 0, 1), new Point( 0, 2)},
		{new Point(-1,  0), new Point(0,  0), new Point( 1, 0), new Point( 0, 1)},
		{new Point( 0,  0), new Point(1,  0), new Point( 0, 1), new Point( 1, 1)},
		{new Point(-1, -1), new Point(0, -1), new Point( 0, 0), new Point( 0, 1)},
		{new Point( 1, -1), new Point(0, -1), new Point( 0, 0), new Point( 0, 1)}
	};
	private final Color[] tetroColors = new Color[] {
		Color.PINK,
		Color.GREEN,
		Color.CYAN,
		Color.MAGENTA,
		Color.YELLOW,
		Color.ORANGE,
		Color.BLUE
	}; // TODO: Replace colors with nicer images :)
	
	// Static list of all shape instances
	private static Tetromino[] shapes = new Tetromino[] {
		new Tetromino(tetroNames.ZBlock),
		new Tetromino(tetroNames.SBlock),
		new Tetromino(tetroNames.LineBlock),
		new Tetromino(tetroNames.TBlock),
		new Tetromino(tetroNames.SquareBlock),
		new Tetromino(tetroNames.LBlock),
		new Tetromino(tetroNames.MirroredLBlock),
	};
	
	public class Configuration {
		
		private Point[] coords;
		private int minX, maxX;
		private HashMap<Integer, Integer> minY;
		
		public Configuration(Point[] points) {
			coords = points;
			
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
			
			minY = new HashMap<Integer, Integer>();
			for (int i = minX; i <= maxX; ++i) {
				minY.put(i, INFINITE_Y); // There's no telling which coords are in a given column, so set a high value at first
				for (Point p : coords) {
					if (p.x == i && p.y < minY.get(i)) {
						minY.put(i, p.y);
					}
				}
			}
		}
		
		// MinX and MaxX are used to determine if you can shift left or right 
		public int getMinX() { return minX; }
		public int getMaxX() { return maxX; }
		
		// MinY is used to determine how low a piece can go based on each column it resides in
		public int getMinY(int col) {
			// TODO: Rewrite so it returns a y-coordinate relative to a parametrized game board
			if (minY.containsKey(col)) {
				return minY.get(col);
			}
			return INFINITE_Y;
		}
		
		// The Block class still needs to have access to the raw data when it "draws" to the board
		public Point[] getCoords() { return coords; }
	}
	
	// Even the constructor is private because this class maintains a static list of all shape instances
	private tetroNames name;
	private Color color;
	private Configuration defaultConfiguration, rotatedConfiguration;
	private Tetromino(tetroNames index) {
		name = index;
		color = tetroColors[index.ordinal()];
		
		Point[]
			defaultCoords = tetroCoords[index.ordinal()],
			rotatedCooords = new Point[4];
		
		// Calculate rotated coordinates
		for (int i = 0; i < 4; ++i) {
			if (index == tetroNames.SquareBlock) {
				rotatedCooords[i] = new Point(defaultCoords[i]);
			}
			else {
				rotatedCooords[i] = new Point(-defaultCoords[i].y, defaultCoords[i].x);
			}
		}
		
		// Create new Configuration instances based on cached coordinates
		defaultConfiguration = new Configuration(defaultCoords);
		rotatedConfiguration = new Configuration(rotatedCooords);
	}
	
	// We need accessors to be public, however
	public tetroNames getName() { return name; }
	public Color getColor() { return color; }
	public Configuration getConfiguration(boolean rotated) {
		if (rotated) {
			return rotatedConfiguration;
		}
		return defaultConfiguration;
	}
	
	// This is where external classes will get a particular shape
	public static Tetromino getTetromino(int i) {
		if (i > NUM_SHAPES || i < 0) {
			return null;
		}
		return shapes[i];
	}
}
