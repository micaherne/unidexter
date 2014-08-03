package uk.co.micaherne.unidexter.notation;

import uk.co.micaherne.unidexter.MoveUtils;
import uk.co.micaherne.unidexter.NotationException;

public class LongAlgebraicNotation extends AlgebraicNotation {
	
	public int parseMove(String representation) throws NotationException {
		if (representation.length() < 4 || representation.length() > 5) {
			throw new NotationException("Move must be 4 or 5 characters");
		}
		int move;
		if (representation.length() == 5) {
			move = MoveUtils.create(toSquare(representation.substring(0, 2)), 
					toSquare(representation.substring(2,  4)), toPiece("" + representation.charAt(4)));
		} else {
			move = MoveUtils.create(toSquare(representation.substring(0, 2)), 
					toSquare(representation.substring(2,  4)));
		}
		return move;
	}

	public String toString(int move) throws NotationException {
		int fromSquare = MoveUtils.fromSquare(move);
		int toSquare = MoveUtils.toSquare(move);
		assert fromSquare < 8;
		assert toSquare < 8;
		int promotedPiece = MoveUtils.promotedPiece(move);
		boolean isQueening = MoveUtils.isQueening(move);
		StringBuilder result = new StringBuilder();
		result.append((char)('a' + (fromSquare % 8)));
		result.append((fromSquare / 8) + 1);
		result.append((char)('a' + (toSquare % 8)));
		result.append((toSquare / 8) + 1);
		if (isQueening && (promotedPiece == 0)) {
			// The move is just a generated queening move without a specific piece
			throw new NotationException("Unable to determine promoted piece");
		}
		if (isQueening) {
			result.append(fromPiece(promotedPiece));
		}
		return result.toString();
	}

}
