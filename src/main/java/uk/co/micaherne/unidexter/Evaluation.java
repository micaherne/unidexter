package uk.co.micaherne.unidexter;

import uk.co.micaherne.unidexter.Chess.Colour;

public class Evaluation {
	
	public Position position;
	
	public static final long CENTRE2X2 = 0x1818000000L;
	public static final long CENTRE4X4 = 0x3c3c3c3c0000L;

	public Evaluation(Position position) {
		this.position = position;
	}
	
	/**
	 * Return an evaluation in centipawns of the position from the point
	 * of view of White.
	 * 
	 * @param position
	 * @return
	 */
	public int evaluate() {
		int result = 0;
		
		result += 100 * materialDifference(Chess.Piece.PAWN);
		result += 300 * materialDifference(Chess.Piece.KNIGHT);
		result += 300 * materialDifference(Chess.Piece.BISHOP);
		result += 500 * materialDifference(Chess.Piece.ROOK);
		result += 900 * materialDifference(Chess.Piece.QUEEN);
		
		// Slight plus for central pawns
		result += 5 * (centralPawns(Chess.Colour.WHITE) - centralPawns(Chess.Colour.BLACK));
		
		return result;
	}
	
	/**
	 * Evaluate a position for which there are no legal moves (i.e. a checkmate or stalemate)
	 * 
	 * @param position2
	 * @param depth
	 * @return
	 */
	public int evaluateTerminal(Position position, int depth) {
		if (position.whiteToMove && position.inCheck(Colour.WHITE)) {
			return (Integer.MIN_VALUE / 2 - depth);
		} else if (!position.whiteToMove && position.inCheck(Colour.BLACK)) {
			return (Integer.MAX_VALUE / 2 + depth);
		} else {
			return 0; // stalemate
		}
	}
	
	public int materialDifference(int pieceType) {
		return Long.bitCount(position.pieceBitboards[pieceType] & position.colourBitboards[Chess.Colour.WHITE]) 
				- Long.bitCount(position.pieceBitboards[pieceType] & position.colourBitboards[Chess.Colour.BLACK]);
	}
	
	public long centralPawns(int colour) {
		return Long.bitCount(CENTRE2X2 & position.pieceBitboards[Chess.Piece.PAWN] & position.colourBitboards[colour]);
	}
	
	public void setPosition(Position position) {
		this.position = position;
	}

	

}
