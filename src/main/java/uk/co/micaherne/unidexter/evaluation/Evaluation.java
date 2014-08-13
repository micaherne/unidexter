package uk.co.micaherne.unidexter.evaluation;

import uk.co.micaherne.unidexter.Chess;
import uk.co.micaherne.unidexter.Chess.Colour;
import uk.co.micaherne.unidexter.Position;

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
		
		result += (100 * materialDifference(Chess.Piece.PAWN));
		result += (300 * materialDifference(Chess.Piece.KNIGHT));
		result += (310 * materialDifference(Chess.Piece.BISHOP));
		result += (500 * materialDifference(Chess.Piece.ROOK));
		result += (900 * materialDifference(Chess.Piece.QUEEN));
		
		// Add results from piece square tables
		result += (pieceSquare(Chess.Piece.PAWN, Chess.Colour.WHITE) - pieceSquare(Chess.Piece.PAWN, Chess.Colour.BLACK));
		result += (pieceSquare(Chess.Piece.KNIGHT, Chess.Colour.WHITE) - pieceSquare(Chess.Piece.KNIGHT, Chess.Colour.BLACK));
		result += (pieceSquare(Chess.Piece.BISHOP, Chess.Colour.WHITE) - pieceSquare(Chess.Piece.BISHOP, Chess.Colour.BLACK));
		result += (pieceSquare(Chess.Piece.QUEEN, Chess.Colour.WHITE) - pieceSquare(Chess.Piece.QUEEN, Chess.Colour.BLACK));

		return result;
	}
	
	/**
	 * Evaluate a position for which there are no legal moves (i.e. a checkmate or stalemate)
	 * 
	 * We pass depth to this to make "near" checkmates score worse than "far" ones.
	 * 
	 * @param depth
	 * @param position2
	 * 
	 * @return
	 */
	public int evaluateTerminal(int depth) {
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
	
	public int pieceSquare(int pieceType, int colour) {
		long pieces = position.pieceBitboards[pieceType] & position.colourBitboards[colour];
		int result = 0;
		while (pieces != 0) {
			int lowestBit = Long.numberOfTrailingZeros(pieces);
			switch (pieceType) {
			/* 1 - colour is because the piece square tables were nicked from crafty
			 * which has the colour values reversed! */
				case Chess.Piece.PAWN:
					result += PieceSquare.pval[0][1 - colour][lowestBit];
					break;
				case Chess.Piece.KNIGHT:
					result += PieceSquare.nval[0][1 - colour][lowestBit];
					break;
				case Chess.Piece.BISHOP:
					result += PieceSquare.bval[0][1 - colour][lowestBit];
					break;
				case Chess.Piece.QUEEN:
					result += PieceSquare.qval[0][1 - colour][lowestBit];
					break;
			}
			pieces ^= (1L << lowestBit);
		}
		return result;
	}
	
	public long centralPawns(int colour) {
		return Long.bitCount(CENTRE2X2 & position.pieceBitboards[Chess.Piece.PAWN] & position.colourBitboards[colour]);
	}
	
	public void setPosition(Position position) {
		this.position = position;
	}

}
