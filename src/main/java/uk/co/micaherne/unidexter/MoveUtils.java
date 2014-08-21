package uk.co.micaherne.unidexter;

/**
 * Utility class for dealing with moves, which are stored internally
 * as an int:
 * 
 * lowest 8 bits: to square
 * next 8 bits: from square
 * next 8 bits: promoted piece
 * next 1 bit: queening move?
 * next 1 bit: e.p. capture?
 * 
 * The promoted piece can be empty where the move is a generated prospective one and all 
 * promotions may be tried, whereas for specific moves (such as parsed from notation) the
 * promoted piece will be set.
 * 
 * @author Michael Aherne
 *
 */
public class MoveUtils {
	
	public static final int create(int from, int to) {
		// System.out.println("Creating move from " + from + " to " + to);
		return from << 8 | to;
	}
	
	/**
	 * Create a move with basic flags set.
	 * 
	 * This should only ever be called with queening = true when creating a basic move
	 * when generating all queening moves from a position.
	 * 
	 * @param from from square
	 * @param to to square
	 * @param queening is it a queening move?
	 * @param enPassentCapture is it an e.p. capture
	 * @return
	 */
	public static final int create(int from, int to, boolean queening, boolean enPassentCapture) {
		return from << 8 | to | (queening ? 1 << 24 : 0) | (enPassentCapture ? 1 << 25 : 0);
	}
	
	public static final int create(int from, int to, int promotedPiece) {
		// If promotedPiece is provided, it's a promotion and therefore 
		// also not an en passent capture
		return from << 8 | to | (1 << 24) | (promotedPiece << 16);
	}
	
	/**
	 * Add all the promotions for the given queening move to the moves array.
	 *  
	 * @param from
	 * @param to
	 * @param moves
	 * @param side
	 * @param moveCount TODO
	 * @return the number of moves added (always 4)
	 */
	public static final int addPromotions(int from, int to, int[] moves, int side, int moveCount) {
		int baseMove = create(from, to, true, false);
		for (int i = 0; i < Chess.Bitboard.promotedPieces[side].length; i++) {
			moves[++moveCount] = baseMove | (Chess.Bitboard.promotedPieces[side][i] << 16);
		}
		return Chess.Bitboard.promotedPieces[side].length;
	}
	
	public static final int fromSquare(int move) {
		return (int) (255 & (move >>> 8));
	}

	public static final int toSquare(int move) {
		return (int) (move & 255);
	}
	
	public static final boolean isQueening(int move) {
		return ((move >>> 24) & 1) == 1;
	}
	
	public static final boolean isEnPassentCapture(int move) {
		return ((move >>> 25) & 1) == 1;
	}

	public static final int promotedPiece(int move) {
		return (move >>> 16) & 255;
	}
	
	public static final int pieceType(int piece) {
		return piece & 7;
	}
	
}
