package uk.co.micaherne.unidexter;

public class MoveUndo {
	
	public int move;

	// Is castling affected for each side (some moves can affect both)
	public boolean[] affectsCastling = new boolean[] {false, false};
	public boolean isEnPassent = false;
	
	public int movedPiece = 0;
	public int capturedPiece = 0;
	
	public boolean[][] castling = new boolean[][]{{false, false}, {false, false}};
	public long epSquare = 0L;
	
	public long zobristHash = 0L;
	
	public MoveUndo(int move) {
		this.move = move;
	}
}
