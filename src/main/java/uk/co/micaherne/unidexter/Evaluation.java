package uk.co.micaherne.unidexter;

public class Evaluation {
	
	public Position position;

	public Evaluation(Position position) {
		this.position = position;
	}
	
	/**
	 * Return an evaluation in micropawns of the position from the point
	 * of view of White.
	 * 
	 * @param position
	 * @return
	 */
	public int evaluate() {
		int result = 0;
		
		result += materialDifference(Chess.Piece.PAWN);
		result += 3 * materialDifference(Chess.Piece.KNIGHT);
		result += 3 * materialDifference(Chess.Piece.BISHOP);
		result += 5 * materialDifference(Chess.Piece.ROOK);
		result += 9 * materialDifference(Chess.Piece.QUEEN);
		
		return result;
	}
	
	public int materialDifference(int pieceType) {
		return Long.bitCount(position.pieceBitboards[pieceType] & position.colourBitboards[Chess.Colour.WHITE]) 
				- Long.bitCount(position.pieceBitboards[pieceType] & position.colourBitboards[Chess.Colour.BLACK]);
	}

}
