package uk.co.micaherne.unidexter.notation;

import uk.co.micaherne.unidexter.Chess;

public abstract class AlgebraicNotation {
		
	public int toSquare(String representation) throws NotationException {
		if (representation.length() != 2) {
			throw new NotationException("Square must be 2 characters long");
		}
		char fileChar = representation.toLowerCase().charAt(0);
		char rankChar = representation.charAt(1);
		int file = fileChar - 'a';
		if (file < 0 || file > 7) {
			throw new NotationException("Invalid file letter");
		}
		int rank;
		try {
			rank = Integer.parseInt(String.valueOf(rankChar));
		} catch (NumberFormatException e) {
			throw new NotationException("Rank must be number");
		}
		
		rank--;
		if (rank < 0 || rank > 7) {
			throw new NotationException("Invalid rank number");
		}
		return rank * 8 + file;
	}
	
	public int toPiece(String representation, boolean ignoreColour) throws NotationException {
		int result = 0;

		switch (representation.toLowerCase()) {
			case "p":
				result = Chess.Piece.PAWN;
				break;
			case "r":
				result = Chess.Piece.ROOK;
				break;
			case "n":
				result = Chess.Piece.KNIGHT;
				break;
			case "b":
				result = Chess.Piece.BISHOP;
				break;
			case "q":
				result = Chess.Piece.QUEEN;
				break;
			case "k":
				result = Chess.Piece.KING;
				break;
			default:
				throw new NotationException("Invalid piece representation " + representation.toLowerCase());
		}
		if (!ignoreColour && representation.equals(representation.toLowerCase())) {
			result += 8;
		}
		return result;
	}
	
	public int toPiece(String representation) throws NotationException {
		return toPiece(representation, false);
	}
	
	public String fromPiece(int piece, boolean ignoreColour) throws NotationException {
		String result = null;

		switch (piece % 8) {
			case Chess.Piece.EMPTY:
				result = " ";
				break;
			case Chess.Piece.PAWN:
				result = "p";
				break;
			case Chess.Piece.ROOK:
				result = "r";
				break;
			case Chess.Piece.KNIGHT:
				result = "n";
				break;
			case Chess.Piece.BISHOP:
				result = "b";
				break;
			case Chess.Piece.QUEEN:
				result = "q";
				break;
			case Chess.Piece.KING:
				result = "k";
				break;
			default:
				throw new NotationException("Invalid piece number " + piece);
		}
		if (!ignoreColour && piece < 8) {
			result = result.toUpperCase();
		}
		return result;
	}
	
	public String fromPiece(int piece) throws NotationException {
		return fromPiece(piece, false);
	}
	
	public String squareToString(int square) {
		StringBuilder result = new StringBuilder();
		result.append((char) ('a' + (square % 8)));
		result.append((square / 8) + 1);
		return result.toString();
	}

}
