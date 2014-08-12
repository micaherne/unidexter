package uk.co.micaherne.unidexter;

import uk.co.micaherne.unidexter.Chess.Colour;

public class MoveGenerator {
	
	public Position position;
	public long[][] bbPawnAttacks = new long[2][64]; // colour, square
	public long[][] bbRayAttacks = new long[8][64];
	public long[] bbRookAttacks = new long[64];
	public long[] bbKnightAttacks = new long[64];
	public long[] bbBishopAttacks = new long[64];
	public long[] bbQueenAttacks = new long[64];
	public long[] bbKingAttacks = new long[64];
	
	// Castling data
	public static final int[] ooFrom = {Chess.Square.E1, Chess.Square.E8};
	public static final int[] ooTo = {Chess.Square.G1, Chess.Square.G8};
	public static final int[] oooTo = {Chess.Square.C1, Chess.Square.C8};
	public static final int[] ooRook = {Chess.Square.H1, Chess.Square.H8};
	public static final int[] oooRook = {Chess.Square.A1, Chess.Square.A8};
	public static final int[][] ooSquares = {{Chess.Square.E1, Chess.Square.F1, Chess.Square.G1}, {Chess.Square.E8, Chess.Square.F8, Chess.Square.G8}};
	public static final int[][] oooSquares = {{Chess.Square.C1, Chess.Square.D1, Chess.Square.E1}, {Chess.Square.C8, Chess.Square.D8, Chess.Square.E8}};
	public static final long[] bbOO = {0x0000000000000060L, 0x6000000000000000L};
	public static final long[] bbOOO = {0x000000000000000EL, 0x0E00000000000000L};

	public MoveGenerator(Position position) {
		this.position = position;
		initialisePawnAttacks();
		initialiseRayAttacks();
		initialiseRookAttacks();
		initialiseKnightAttacks();
		initialiseBishopAttacks();
		initialiseQueenAttacks();
		initialiseKingAttacks();
	}
	
	private void initialisePawnAttacks() {
		for (int origin = 0; origin < 64; origin++) {
			// We generate pawn attacks for squares on the first rank, even though
			// these can't have pawns on them, as these are used in attacks()
			for (int j = 0; j < 2; j++) {
				int square = origin + Chess.Bitboard.bishopOffsets[j];
				
				if ((Math.abs((square % 8) - (origin % 8)) == 1) 
						&& (Math.abs((square / 8 - origin / 8)) == 1)
						&& square > -1) {
					bbPawnAttacks[Colour.BLACK][origin] |= (1L << square);
				}
			}

			for (int j = 2; j < 4; j++) {
				int square = origin + Chess.Bitboard.bishopOffsets[j];
				
				if ((Math.abs((square % 8) - (origin % 8)) == 1) 
						&& (Math.abs((square / 8 - origin / 8)) == 1)
						&& square < 64) {
					bbPawnAttacks[Colour.WHITE][origin] |= (1L << square);
				}
			}
		}
	}

	private void initialiseRayAttacks() {
		for (int origin = 0; origin < 64; origin++) {
			setRayAttack(origin, Chess.Bitboard.DirectionOffset.SW, Chess.Bitboard.DirectionIndex.SW, Chess.Bitboard.FILE_H | Chess.Bitboard.RANK_8);
			setRayAttack(origin, Chess.Bitboard.DirectionOffset.S,  Chess.Bitboard.DirectionIndex.S, Chess.Bitboard.RANK_8);
			setRayAttack(origin, Chess.Bitboard.DirectionOffset.SE, Chess.Bitboard.DirectionIndex.SE, Chess.Bitboard.FILE_A | Chess.Bitboard.RANK_8);
			setRayAttack(origin, Chess.Bitboard.DirectionOffset.W,  Chess.Bitboard.DirectionIndex.W, Chess.Bitboard.FILE_H);
			setRayAttack(origin, Chess.Bitboard.DirectionOffset.E,  Chess.Bitboard.DirectionIndex.E, Chess.Bitboard.FILE_A);
			setRayAttack(origin, Chess.Bitboard.DirectionOffset.NW, Chess.Bitboard.DirectionIndex.NW, Chess.Bitboard.FILE_H | Chess.Bitboard.RANK_1);
			setRayAttack(origin, Chess.Bitboard.DirectionOffset.N,  Chess.Bitboard.DirectionIndex.N, Chess.Bitboard.RANK_1);
			setRayAttack(origin, Chess.Bitboard.DirectionOffset.NE, Chess.Bitboard.DirectionIndex.NE, Chess.Bitboard.FILE_A | Chess.Bitboard.RANK_1);
		}
	}
	
	private void initialiseRookAttacks() {
		for (int origin = 0; origin < 64; origin++) {
			bbRookAttacks[origin] = bbRayAttacks[Chess.Bitboard.DirectionIndex.N][origin] | bbRayAttacks[Chess.Bitboard.DirectionIndex.E][origin]
					| bbRayAttacks[Chess.Bitboard.DirectionIndex.W][origin] | bbRayAttacks[Chess.Bitboard.DirectionIndex.S][origin];
		}
	}
	
	private void initialiseKnightAttacks() {
		for (int origin = 0; origin < 64; origin++) {
			// Generate masks to prevent wrapping
			long fileMask = 0L;
			long rankMask = 0L;
			int originFile = origin % 8;
			int originRank = origin / 8;
			for (int j = 0; j < 8; j++) {
				if (Math.abs(j - originFile) < 3) {
					fileMask |= Chess.Bitboard.files[j];
				}
				if (Math.abs(j - originRank) < 3) {
					rankMask |= Chess.Bitboard.ranks[j];
				}
			}
			
			bbKnightAttacks[origin] = 0;
			for (int i = 0; i < Chess.Bitboard.knightOffsets.length; i++) {
				
				int target = origin + Chess.Bitboard.knightOffsets[i];
				if (target >= 0 && target < 64) {
					bbKnightAttacks[origin] |= 1l << target;
				}
				
			}
			bbKnightAttacks[origin] &= fileMask;
			bbKnightAttacks[origin] &= rankMask;
		}
	}
	
	private void initialiseBishopAttacks() {
		for (int origin = 0; origin < 64; origin++) {
			bbBishopAttacks[origin] = bbRayAttacks[Chess.Bitboard.DirectionIndex.NW][origin] | bbRayAttacks[Chess.Bitboard.DirectionIndex.NE][origin]
					| bbRayAttacks[Chess.Bitboard.DirectionIndex.SW][origin] | bbRayAttacks[Chess.Bitboard.DirectionIndex.SE][origin];
		}
	}
	
	private void initialiseQueenAttacks() {
		for (int origin = 0; origin < 64; origin++) {
			bbQueenAttacks[origin] = bbRookAttacks[origin] | bbBishopAttacks[origin];
		}
	}
	
	private void initialiseKingAttacks() {
		for (int origin = 0; origin < 64; origin++) {
			// Generate masks to prevent wrapping
			long fileMask = 0L;
			long rankMask = 0L;
			int originFile = origin % 8;
			int originRank = origin / 8;
			for (int j = 0; j < 8; j++) {
				if (Math.abs(j - originFile) < 2) {
					fileMask |= Chess.Bitboard.files[j];
				}
				if (Math.abs(j - originRank) < 2) {
					rankMask |= Chess.Bitboard.ranks[j];
				}
			}
			
			bbKingAttacks[origin] = 0;
			for (int i = 0; i < Chess.Bitboard.kingOffsets.length; i++) {
				
				int target = origin + Chess.Bitboard.kingOffsets[i];
				if (target >= 0 && target < 64) {
					bbKingAttacks[origin] |= 1l << target;
				}
				
			}
			bbKingAttacks[origin] &= fileMask;
			bbKingAttacks[origin] &= rankMask;
		}
	}
	
	/**
	 * 
	 * @param origin
	 * @param directionOffset
	 * @param directionIndex
	 * @param terminalSquares if directionOffset takes us to one of these squares, stop
	 */
	private void setRayAttack(int origin, int directionOffset, int directionIndex, long terminalSquares) {
		bbRayAttacks[directionIndex][origin] = 0;
		int next = origin + directionOffset;
		while (next < 64 && next >= 0) {
			if (((1L << next) & terminalSquares) != 0) {
				break;
			}
			bbRayAttacks[directionIndex][origin] |= (1L << next);
			next += directionOffset;
		}
	}
	
	/**
	 * Takes a bitboard of destination squares and adds moves to each of them to the array
	 * of moves.
	 * 
	 * @param moves an array of moves
	 * @param destinations a bitboard of destination squares
	 * @param from the origin square
	 * @param moveCount the number of moves currently in the moves array
	 * @return the new number of moves in the moves array
	 */
	public int bitboardToMoves(int[] moves, long destinations, int from, int moveCount) {
		while (destinations != 0L) {
			int lowestMoveBit = Long.numberOfTrailingZeros(destinations);
			moves[++moveCount] = MoveUtils.create(from, lowestMoveBit);
			destinations ^= (1L << lowestMoveBit);
		}
		moves[0] = moveCount;
		return moveCount;
	}

	/**
	 * An array of moves. 0 index is number of actual moves.
	 * 
	 * @return
	 */
	public int[] generateMoves() {
		int moveCount = 0;
		int[] moves = new int[128];
		
		moveCount = generateCaptures(moves, moveCount);
		moveCount = generateNonCaptures(moves, moveCount);

		moves[0] = moveCount;
		
		return moves;
	}
	
	public int generateCaptures(int[] moves, int moveCount) {
		
		int colourToMove = position.whiteToMove ? Chess.Colour.WHITE : Chess.Colour.BLACK;
		
		moveCount = pieceMoves(moves, moveCount, colourToMove, true); // captures
		
		// Pawn captures
		long targets = (position.epSquare | position.pieceBitboards[Chess.Bitboard.OCCUPIED]) & ~position.colourBitboards[colourToMove];
		
		if (position.whiteToMove) {
			
			long pawns = position.pieceBitboards[Chess.Piece.PAWN] & position.colourBitboards[Chess.Colour.WHITE];
			long captureNW = targets & ((~Chess.Bitboard.FILE_A & pawns) << 7);
			while (captureNW != 0) {
				int lowestBit = Long.numberOfTrailingZeros(captureNW);
				boolean isEpCapture = (1L << lowestBit == position.epSquare);
				if (lowestBit >= 56) {
					moveCount += MoveUtils.addPromotions(lowestBit - Chess.Bitboard.DirectionOffset.NW, lowestBit, moves, colourToMove, moveCount);
				} else {
					moves[++moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.NW, lowestBit, false, isEpCapture);
				}
				captureNW ^= (1L << lowestBit);
			}
			
			long captureNE = targets & ((~Chess.Bitboard.FILE_H & pawns) << 9);
			while (captureNE != 0) {
				int lowestBit = Long.numberOfTrailingZeros(captureNE);
				boolean isEpCapture = (1L << lowestBit == position.epSquare);
				if (lowestBit >= 56) {
					moveCount += MoveUtils.addPromotions(lowestBit - Chess.Bitboard.DirectionOffset.NE, lowestBit, moves, colourToMove, moveCount);
				} else {
					moves[++moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.NE, lowestBit, false, isEpCapture);
				}
				captureNE ^= (1L << lowestBit);
			}
		} else {
			long pawns = position.pieceBitboards[Chess.Piece.PAWN] & position.colourBitboards[Chess.Colour.BLACK];
			long captureSW = targets & ((~Chess.Bitboard.FILE_A & pawns) >>> 9);
			while (captureSW != 0) {
				int lowestBit = Long.numberOfTrailingZeros(captureSW);
				boolean isEpCapture = (1L << lowestBit == position.epSquare);
				if (lowestBit < 8) {
					moveCount += MoveUtils.addPromotions(lowestBit - Chess.Bitboard.DirectionOffset.SW, lowestBit, moves, colourToMove, moveCount);
				} else {
					moves[++moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.SW, lowestBit, false, isEpCapture);
				}
				captureSW ^= (1L << lowestBit);
			}
			
			long captureSE = targets & ((~Chess.Bitboard.FILE_H & pawns) >>> 7);
			while (captureSE != 0) {
				int lowestBit = Long.numberOfTrailingZeros(captureSE);
				boolean isEpCapture = (1L << lowestBit == position.epSquare);
				if (lowestBit < 8) {
					moveCount += MoveUtils.addPromotions(lowestBit - Chess.Bitboard.DirectionOffset.SE, lowestBit, moves, colourToMove, moveCount);
				} else {
					moves[++moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.SE, lowestBit, false, isEpCapture);
				}
				captureSE ^= (1L << lowestBit);
			}
		}
		
		return moveCount;
	}

	public int generateNonCaptures(int[] moves, int moveCount) {
				
		int colourToMove = position.whiteToMove ? Chess.Colour.WHITE : Chess.Colour.BLACK;
		int oppositeColour = (colourToMove + 1) % 2;
		
		moveCount = pieceMoves(moves, moveCount, colourToMove, false); // non-captures

		if (position.castling[colourToMove][0] 
				&& ((bbOOO[colourToMove] & position.pieceBitboards[Chess.Bitboard.OCCUPIED]) == 0)
				&& !attacks(oooSquares[colourToMove][0], oppositeColour)
				&& !attacks(oooSquares[colourToMove][1], oppositeColour)
				&& !attacks(oooSquares[colourToMove][2], oppositeColour)) {
			moves[++moveCount] = MoveUtils.create(ooFrom[colourToMove], oooTo[colourToMove]);
		}
		if (position.castling[colourToMove][1] 
				&& ((bbOO[colourToMove] & position.pieceBitboards[Chess.Bitboard.OCCUPIED]) == 0)
				&& !attacks(ooSquares[colourToMove][0], oppositeColour)
				&& !attacks(ooSquares[colourToMove][1], oppositeColour)
				&& !attacks(ooSquares[colourToMove][2], oppositeColour)) {
			moves[++moveCount] = MoveUtils.create(ooFrom[colourToMove], ooTo[colourToMove]);
		}

		// Pawn moves
		if (position.whiteToMove) {
			
			long pawns = position.pieceBitboards[Chess.Piece.PAWN] & position.colourBitboards[Chess.Colour.WHITE];
			long oneSquareMoves = (pawns << 8) & ~position.pieceBitboards[Chess.Bitboard.OCCUPIED];
			long destinationSquares = oneSquareMoves;
			while (destinationSquares != 0L) {
				int lowestBit = Long.numberOfTrailingZeros(destinationSquares);
				if (lowestBit >= 56) {
					moveCount += MoveUtils.addPromotions(lowestBit - Chess.Bitboard.DirectionOffset.N, lowestBit, moves, colourToMove, moveCount);
				} else {
					moves[++moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.N, lowestBit);
				}
				destinationSquares ^= (1L << lowestBit);
			}
			long twoSquareMoves = ((oneSquareMoves & (Chess.Bitboard.RANK_3)) << 8) & ~position.pieceBitboards[Chess.Bitboard.OCCUPIED];
			destinationSquares = twoSquareMoves;
			while (destinationSquares != 0L) {
				int lowestBit = Long.numberOfTrailingZeros(destinationSquares);
				moves[++moveCount] = MoveUtils.create(lowestBit - 16, lowestBit);
				destinationSquares ^= (1L << lowestBit);
			}
			
			
		} else {
			
			long pawns = position.pieceBitboards[Chess.Piece.PAWN] & position.colourBitboards[Chess.Colour.BLACK];
			long oneSquareMoves = (pawns >>> 8) & ~position.pieceBitboards[Chess.Bitboard.OCCUPIED];
			long destinationSquares = oneSquareMoves;
			while (Long.bitCount(destinationSquares) != 0) {
				int lowestBit = Long.numberOfTrailingZeros(destinationSquares);
				if (lowestBit < 8) {
					moveCount += MoveUtils.addPromotions(lowestBit + Chess.Bitboard.DirectionOffset.N, lowestBit, moves, colourToMove, moveCount);
				} else {
					moves[++moveCount] = MoveUtils.create(lowestBit + Chess.Bitboard.DirectionOffset.N, lowestBit);
				}
				destinationSquares ^= (1L << lowestBit);
			}
			long twoSquareMoves = ((oneSquareMoves & (Chess.Bitboard.RANK_6)) >>> 8) & ~position.pieceBitboards[Chess.Bitboard.OCCUPIED];
			destinationSquares = twoSquareMoves;
			while (Long.bitCount(destinationSquares) != 0) {
				int lowestBit = Long.numberOfTrailingZeros(destinationSquares);
				moves[++moveCount] = MoveUtils.create(lowestBit + 16, lowestBit);
				destinationSquares ^= (1L << lowestBit);
			}
		}
		
		return moveCount;
		
	}
	/**
	 * Generate moves for all non-pawns.
	 * 
	 * Note that this will *either* return captures or non-captures.
	 * 
	 * @param moves array of moves
	 * @param moveCount current number of moves
	 * @param colourToMove the side to move
	 * 
	 * @param captures if true only captures will be returned, if false only non-captures
	 * 
	 * @return
	 */
	private int pieceMoves(int[] moves, int moveCount, int colourToMove, boolean captures) {
		
		int oppositeColour = (colourToMove + 1) % 2;
		
		// Determine mask to use to return moves
		long destinationMask =  ~position.pieceBitboards[Chess.Bitboard.OCCUPIED]; //~position.colourBitboards[colourToMove];
		if (captures) {
			destinationMask = position.colourBitboards[oppositeColour];
		}
		
		// Knight moves
		long knights = position.pieceBitboards[Chess.Piece.KNIGHT] & position.colourBitboards[colourToMove];
		while (knights != 0L) {
			int lowestBit = Long.numberOfTrailingZeros(knights);
			long destinations = bbKnightAttacks[lowestBit] & destinationMask;
			moveCount = bitboardToMoves(moves, destinations, lowestBit, moveCount);
			knights ^= (1L << lowestBit);
		}
		
		// Bishop moves (+ queen diagonals)
		long bishopsAndQueens = (position.pieceBitboards[Chess.Piece.BISHOP] | position.pieceBitboards[Chess.Piece.QUEEN]) & position.colourBitboards[colourToMove];
		while (bishopsAndQueens != 0L) {
			int lowestBit = Long.numberOfTrailingZeros(bishopsAndQueens);
			long destinations = bishopAttacks(lowestBit) & destinationMask;
			moveCount = bitboardToMoves(moves, destinations, lowestBit, moveCount);
			bishopsAndQueens ^= (1L << lowestBit);
		}
		
		// Rook moves (+ queen linear)
		long rooksAndQueens = (position.pieceBitboards[Chess.Piece.ROOK] | position.pieceBitboards[Chess.Piece.QUEEN]) & position.colourBitboards[colourToMove];
		while (rooksAndQueens != 0L) {
			int lowestBit = Long.numberOfTrailingZeros(rooksAndQueens);
			long destinations = rookAttacks(lowestBit) & destinationMask;
			moveCount = bitboardToMoves(moves, destinations, lowestBit, moveCount);
			rooksAndQueens ^= (1L << lowestBit);
		}
		
		// Queen moves are done.
		
		// King moves
		long kings = position.pieceBitboards[Chess.Piece.KING] & position.colourBitboards[colourToMove];
		while (kings != 0L) {
			int lowestBit = Long.numberOfTrailingZeros(kings);
			long destinations = bbKingAttacks[lowestBit] & destinationMask;
			moveCount = bitboardToMoves(moves, destinations, lowestBit, moveCount);
			kings ^= (1L << lowestBit);
		}
		return moveCount;
	}
	
	/**
	 * Get a bitboard of all the squares attacked by a piece on square origin
	 * in direction directionIndex. This contains the first blocker which may
	 * be the same colour as the moving piece.
	 * 
	 * The logic of this is from https://chessprogramming.wikispaces.com/Classical+Approach
	 * 
	 * @param directionIndex
	 * @param origin
	 * @return
	 */
	public long positiveRayAttacks(int directionIndex, int origin) {
		long attacks = bbRayAttacks[directionIndex][origin];
		long blocker = attacks & position.pieceBitboards[Chess.Bitboard.OCCUPIED];
		if (blocker != 0L) {
			int square = Long.numberOfTrailingZeros(blocker);
			attacks ^= bbRayAttacks[directionIndex][square];
		}
		return attacks;
	}
	
	public long negativeRayAttacks(int directionIndex, int origin) {
		long attacks = bbRayAttacks[directionIndex][origin];
		long blocker = attacks & position.pieceBitboards[Chess.Bitboard.OCCUPIED];
		if (blocker != 0L) {
			int square = 63 - Long.numberOfLeadingZeros(blocker);			
			attacks ^= bbRayAttacks[directionIndex][square];
		}
		return attacks;
	}
	
	/**
	 * Get a bitboard of all the squares actually attacked by the bishop on the given
	 * origin square in the position.
	 * 
	 * NB. There is no check that there *is* a bishop on the origin square. Calling code
	 * must do this.
	 * 
	 * TODO: This uses a potentially flaky correspondence between the values of DirectionIndex 
	 * and bishopOffsets, which should probably be improved.
	 * 
	 * @param origin
	 * @return
	 */
	public long bishopAttacks(int origin) {
		long result = 0L;
		for (int i = 0; i < 4; i++) {
			if (Chess.Bitboard.bishopOffsets[i] > 0) {
				result |= positiveRayAttacks(i, origin);
			} else {
				result |= negativeRayAttacks(i, origin);
			}
		}
		return result;
	}
	
	public long rookAttacks(int origin) {
		long result = 0L;
		for (int i = 0; i < 4; i++) {
			if (Chess.Bitboard.rookOffsets[i] > 0) {
				result |= positiveRayAttacks(i + 4, origin);
			} else {
				result |= negativeRayAttacks(i + 4, origin);
			}
		}
		return result;
	}
	
	// Attack logic (nicked from Crafty)
	public boolean attacks(int square, int side) {
		if ((bbPawnAttacks[oppositeColour(side)][square] & position.pieceBitboards[Chess.Piece.PAWN] & position.colourBitboards[side]) != 0) {
			return true;
		}
		if ((bbKnightAttacks[square] & position.pieceBitboards[Chess.Piece.KNIGHT] & position.colourBitboards[side]) != 0) {
			return true;
		}
		if (((bbBishopAttacks[square] & (position.pieceBitboards[Chess.Piece.BISHOP] | position.pieceBitboards[Chess.Piece.QUEEN]) & position.colourBitboards[side]) != 0) 
				&& (bishopAttacks(square) & (position.pieceBitboards[Chess.Piece.BISHOP] | position.pieceBitboards[Chess.Piece.QUEEN]) & position.colourBitboards[side]) != 0) {
			return true;
		}
		if (((bbRookAttacks[square] & (position.pieceBitboards[Chess.Piece.ROOK] | position.pieceBitboards[Chess.Piece.QUEEN]) & position.colourBitboards[side]) != 0) 
				&& (rookAttacks(square) & (position.pieceBitboards[Chess.Piece.ROOK] | position.pieceBitboards[Chess.Piece.QUEEN]) & position.colourBitboards[side]) != 0) {
			return true;
		}
		if ((bbKingAttacks[square] & position.pieceBitboards[Chess.Piece.KING] & position.colourBitboards[side]) != 0) {
			return true;
		}
		return false;
	}
	
	public static int oppositeColour(int colour) {
		return colour ^ 1;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

}
