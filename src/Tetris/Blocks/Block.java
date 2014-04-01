package Tetris.Blocks;

public interface Block {
	public abstract void insert();
	public abstract void fall();
	public abstract void drop();
	public abstract void rotate();
	public abstract void shiftRight();
	public abstract void shiftLeft();
}
