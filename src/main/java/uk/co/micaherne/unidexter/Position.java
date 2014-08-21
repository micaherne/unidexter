package uk.co.micaherne.unidexter;

import java.util.Stack;

import uk.co.micaherne.unidexter.hashing.Zobrist;
import uk.co.micaherne.unidexter.notation.AlgebraicNotation;
import uk.co.micaherne.unidexter.notation.LongAlgebraicNotation;
import uk.co.micaherne.unidexter.notation.NotationException;

public class Position {
	
	// TODO: This is bad - need to refactor to remove inCheck() dependency on
	// MoveGenerator for attacks()
	public MoveGenerator moveGenerator;
	
	public long[] pieceBitboards;
	public long[] colourBitboards;
	
	public int[] board;
	
	// colour, (q, k)
	public boolean castling[][];
	
	public int moves = 0;
	public int halfMoves = 0;
	
	public long epSquare = 0L; // en passent square as a bitboard
	
	public boolean whiteToMove = false;
	
	public long zobristHash = 0L;
	
	/**
	 * The stack of undo data for the moves that have been made so far.
	 * 
	 * This would use less memory if we were to replace it with a set list of 
	 * MoveUndo objects and a currentMove pointer. When making a move we would 
	 * get the object for the currentMove+1 position, reset and re-use it.
	 */
	public Stack<MoveUndo> undoData = new Stack<MoveUndo>();
	
	public Position() {
		board = new int[64];
		pieceBitboards = new long[7];
		colourBitboards = new long[2];
		castling = new boolean[][]{{false, false}, {false, false}};
		moveGenerator = new MoveGenerator(this);
	}
	
	public void initialisePieceBitboards() {
		colourBitboards[0] = 0L;
		colourBitboards[1] = 0L;
		for (int i = 0; i < 7; i++) {
			pieceBitboards[i] = 0L;
		}
		for (int i = 0; i < 64; i++) {
			if (board[i] == Chess.Piece.EMPTY) {
				continue;
			}
			pieceBitboards[MoveUtils.pieceType(board[i])] |= (1L << i);
			pieceBitboards[Chess.Bitboard.OCCUPIED] |= (1L << i);
			colourBitboards[(board[i] >> 3) & 1] |= (1L << i);
		}
	}
	
	public static Position fromFEN(String fen) throws NotationException {
		Position result = new Position();
		AlgebraicNotation notation = new LongAlgebraicNotation();
		String[] fenParts = fen.split(" ");
		String[] boardParts = fenParts[0].split("\\/");

		for (int i = 0; i < 8; i++) {
			String rank = null;
			try {
				rank = boardParts[7 - i];
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new FENException("Not enough ranks");
			}

			int file = 0;
			for (String c : rank.split("")) {
				if ("".equals(c)) {
					continue;
				}
				if (!c.matches("\\d")) {
					result.board[i * 8 + file] = notation.toPiece(c);
					file++;
				} else {
					int cint = Integer.parseInt(c);
					while (cint > 0) {
						result.board[i * 8 + file] = Chess.Piece.EMPTY;
						cint--;
						file++;
					}
				}
			}
			if (file != 8) {
				throw new FENException("Not enough files");
			}
		}

		result.whiteToMove = "w".equals(fenParts[1]);

		if (!"-".equals(fenParts[2])) {
			for (String c : fenParts[2].trim().split("")) {
				if ("".equals(c)) {
					continue;
				}
				int piece = notation.toPiece(c);
				result.castling[piece >> 3][(piece % 8) - 5] = true;
			}
		}
		if (!"-".equals(fenParts[3])) {
			result.epSquare = 1L << notation.toSquare(fenParts[3]);
		}

		if (fenParts.length > 4) {
			try {
				result.halfMoves = Integer.parseInt(fenParts[4]);
			} catch (NumberFormatException e) {
				throw new FENException("halfmove must be integer", e);
			}
		}

		if (fenParts.length > 5) {
			try {
				result.moves = Integer.parseInt(fenParts[5]);
			} catch (NumberFormatException e) {
				throw new FENException("fullmove must be integer", e);
			}
		}
		
		result.initialisePieceBitboards();
		
		result.zobristHash = Zobrist.hashForPosition(result);
		
		return result;
	}
	
	public String toFEN() {
		StringBuffer result = new StringBuffer();
		LongAlgebraicNotation notation = new LongAlgebraicNotation();
		
		for (int i = 7; i >= 0; i--) {
			int spaces = 0;
			for (int j = 0; j < 8; j++) {
				try {
					int piece = board[i * 8 + j];
					if (piece == Chess.Piece.EMPTY) {
						spaces++;
					} else {
						if (spaces > 0) {
							result.append(spaces);
							spaces = 0;
						}
						result.append(notation.fromPiece(piece));
					}
				} catch (NotationException e) {
					e.printStackTrace();
					return "ERROR: Invalid values";
				}
			}
			if (spaces > 0) {
				result.append(spaces);
				spaces = 0;
			}
			if (i > 0) {
				result.append("/");
			}
		}
		
		result.append(" ");
		result.append(whiteToMove ? "w" : "b");
		
		result.append(" ");
		StringBuffer castlingResult = new StringBuffer();
		for (int colour = 0; colour < 2; colour++) {
			for (int piece = 0; piece < 2; piece++) {
				if (castling[colour][piece]) {
					char c = 'K';
					c -= (piece * ('K' - 'Q') - (colour * ('a' - 'A')));
					castlingResult.append(c);
				}
			}
		}
		if (castlingResult.length() == 0) {
			result.append("-");
		} else {
			result.append(castlingResult.toString());
		}
		
		result.append(" ");
		if (epSquare == 0L) {
			result.append("-");
		} else {
			result.append(notation.squareToString(Long.numberOfTrailingZeros(epSquare)));
		}
		
		result.append(" ");
		result.append(halfMoves);
		result.append(" ");
		result.append(moves);
		
		return result.toString();
	}

	@Override
	public String toString() {
		LongAlgebraicNotation notation = new LongAlgebraicNotation();
		StringBuilder builder = new StringBuilder("+-+-+-+-+-+-+-+-+\n");
		for (int i = 7; i >= 0; i--) {
			builder.append("|");
			for (int j = 0; j < 8; j++) {
				try {
					builder.append(notation.fromPiece(board[i * 8 + j]));
					builder.append("|");
				} catch (NotationException e) {
					e.printStackTrace();
					return "ERROR: Invalid values";
				}
			}
			builder.append("\n+-+-+-+-+-+-+-+-+\n");
		}
		return builder.toString();
	}
	
	/**
	 * Make the given move, updating it from the position beforehand if required.
	 * 
	 * This is intended for moves parsed from, for example, a UCI position command, where
	 * only the from square and to square are set. The normal move method expects the following
	 * extra data to be set, which must be determined from the position:
	 * 
	 * * captured piece
	 * * colour of promoted piece
	 * 
	 * There is no real need to try to make this method perform really well, as it's only intended
	 * for parsed input, so won't be called very often.
	 * 
	 * @param move
	 * @param updateFromPosition
	 * @return
	 */
	public boolean move(int move, boolean updateFromPosition) {
		if (!updateFromPosition) {
			// This should never be called like this
			return move(move);
		}
		int fromSquare = MoveUtils.fromSquare(move);
		int toSquare = MoveUtils.toSquare(move);
		if ((epSquare & (1L << toSquare)) != 0 && (MoveUtils.pieceType(board[fromSquare]) == Chess.Piece.PAWN)) {
			move |= (1 << 25);
		}
		if (MoveUtils.isQueening(move)) {
			// make sure promotedPiece is the same colour as moved piece
			int movedPiece = board[fromSquare];
			if ((movedPiece & 8) != (MoveUtils.promotedPiece(move) & 8)) {
				int actualPromotedPiece = (movedPiece & 8) | MoveUtils.pieceType(MoveUtils.promotedPiece(move));
				move = MoveUtils.create(fromSquare, toSquare, actualPromotedPiece);
			}
			
		}
		return move(move);
	}

	/**
	 * Make the given pseudolegal move.
	 * 
	 * @param move
	 * @return true if move legal, false if not
	 */
	public boolean move(int move) {
		MoveUndo undo = new MoveUndo(move);
		int fromSquare = MoveUtils.fromSquare(move);
		int toSquare = MoveUtils.toSquare(move);
		
		undo.zobristHash = zobristHash;

		int sideMoving = whiteToMove ? Chess.Colour.WHITE : Chess.Colour.BLACK;
		int opposingSide = whiteToMove ? Chess.Colour.BLACK : Chess.Colour.WHITE;

		undo.movedPiece = board[fromSquare];
		
		undo.capturedPiece = movePiece(fromSquare, toSquare); // always want this even if empty
		
		// Save the en passent square if necessary
		if (epSquare != 0L) {
			undo.epSquare = epSquare;
			zobristHash ^= Zobrist.epFile[Long.numberOfTrailingZeros(epSquare) % 8];
		}

		// Unset ep square (may be reset if pawn moves)
		epSquare = 0L;
		
		// Unset castling rights for rook captures
		if (castling[opposingSide][0] && (MoveUtils.pieceType(undo.capturedPiece) == Chess.Piece.ROOK) && (toSquare == MoveGenerator.oooRook[opposingSide])) {
			undo.affectsCastling[opposingSide] = true;
			undo.castling[opposingSide] = new boolean[] {castling[opposingSide][0], castling[opposingSide][1]};
			castling[opposingSide][0] = false;
			zobristHash ^= Zobrist.castling[opposingSide][0];
		}
		if (castling[opposingSide][1] && (MoveUtils.pieceType(undo.capturedPiece) == Chess.Piece.ROOK) && (toSquare == MoveGenerator.ooRook[opposingSide])) {
			undo.affectsCastling[opposingSide] = true;
			undo.castling[opposingSide] = new boolean[] {castling[opposingSide][0], castling[opposingSide][1]};
			castling[opposingSide][1] = false;
			zobristHash ^= Zobrist.castling[opposingSide][1];
		}
		
		switch (MoveUtils.pieceType(undo.movedPiece)) {
		
			case Chess.Piece.KING:
				// Castling
				if (castling[sideMoving][0] || castling[sideMoving][1]) {
					undo.affectsCastling[sideMoving] = true;
					undo.castling[sideMoving] = new boolean[] {castling[sideMoving][0], castling[sideMoving][1]};
					castling[sideMoving][0] = false;
					castling[sideMoving][1] = false;
					
					if (castling[0][0] != undo.castling[0][0]) {
						zobristHash ^= Zobrist.castling[0][0];
					}
					if (castling[0][1] != undo.castling[0][1]) {
						zobristHash ^= Zobrist.castling[0][1];
					}
								
					// Move rook
					if (Math.abs(toSquare - fromSquare) == 2) {
						if (toSquare == MoveGenerator.oooTo[sideMoving]) {
							movePiece(MoveGenerator.oooRook[sideMoving], toSquare - 1);
						} else if (toSquare == MoveGenerator.ooTo[sideMoving]) {
							movePiece(MoveGenerator.ooRook[sideMoving], toSquare + 1);
						}
					}
				}
				
				break;
			case Chess.Piece.QUEEN:
				break;
			case Chess.Piece.ROOK:
				if (castling[sideMoving][0] && fromSquare == MoveGenerator.oooRook[sideMoving]) {
					undo.affectsCastling[sideMoving] = true;
					undo.castling[sideMoving] = new boolean[] {castling[sideMoving][0], castling[sideMoving][1]};
					castling[sideMoving][0] = false;
					zobristHash ^= Zobrist.castling[sideMoving][0];
				}
				if (castling[sideMoving][1] && fromSquare == MoveGenerator.ooRook[sideMoving]) {
					undo.affectsCastling[sideMoving] = true;
					undo.castling[sideMoving] = new boolean[] {castling[sideMoving][0], castling[sideMoving][1]};
					castling[sideMoving][1] = false;
					zobristHash ^= Zobrist.castling[sideMoving][1];
				}
				break;
			case Chess.Piece.BISHOP:
				break;
			case Chess.Piece.KNIGHT:
				break;
			case Chess.Piece.PAWN:
				if (MoveUtils.isQueening(move)) {
					int promotedPiece = MoveUtils.promotedPiece(move);
					promotePawn(toSquare, promotedPiece);
				}
				
				// Do en passent captures
				if (MoveUtils.isEnPassentCapture(move)) {
					undo.isEnPassent = true;

					if (toSquare > fromSquare) {
						zobristToggle(toSquare - 8);
						board[toSquare - 8] = Chess.Piece.EMPTY;
						undo.capturedPiece = Chess.Piece.Black.PAWN;
					} else {
						zobristToggle(toSquare + 8);
						board[toSquare + 8] = Chess.Piece.EMPTY;
						undo.capturedPiece = Chess.Piece.White.PAWN;
					}
				}
				
				// Set the en passent square if necessary
				if (Math.abs(toSquare - fromSquare) == 16) {
					epSquare = 1L << (fromSquare + ((toSquare - fromSquare) / 2));
					zobristHash ^= Zobrist.epFile[Long.numberOfTrailingZeros(epSquare) % 8];
				}
				break;
		}

		undoData.push(undo);
		
		zobristHash ^= Zobrist.whiteToMove;
		
		whiteToMove = !whiteToMove;
		
		initialisePieceBitboards();
		
		if (inCheck(sideMoving)) {
			unmakeMove();
			return false;
		}
		

		/*if (zobristHash != Zobrist.hashForPosition(this)) {
			LongAlgebraicNotation notation = new LongAlgebraicNotation();
			System.out.println(notation.toString(move));
			System.out.println(toFEN());
			System.exit(1);
		}*/
				
		// TODO: Half-moves and moves
		
		return true;
	}
	
	// TODO: Fails if king not found (index out of bounds 64)
	public boolean inCheck(int side) {
		int kingPosition = Long.numberOfTrailingZeros(pieceBitboards[Chess.Piece.KING] & colourBitboards[side]);
		return moveGenerator.attacks(kingPosition, MoveGenerator.oppositeColour(side));
	}

	public void unmakeMove() {
		MoveUndo undo = undoData.pop();
		
		int fromSquare = MoveUtils.fromSquare(undo.move);
		int toSquare = MoveUtils.toSquare(undo.move);
		
		zobristHash ^= Zobrist.pieceSquare[board[fromSquare]][fromSquare];
		zobristHash ^= Zobrist.pieceSquare[board[toSquare]][toSquare];
		
		board[fromSquare] = undo.movedPiece;
		board[toSquare] = undo.capturedPiece;
		
		zobristHash ^= Zobrist.pieceSquare[board[fromSquare]][fromSquare];
		zobristHash ^= Zobrist.pieceSquare[board[toSquare]][toSquare];
		
		if (epSquare != 0L) {
			zobristHash ^= Zobrist.epFile[Long.numberOfTrailingZeros(epSquare) % 8];
		}
		
		epSquare = undo.epSquare;
		
		if (undo.epSquare != 0L) {
			zobristHash ^= Zobrist.epFile[Long.numberOfTrailingZeros(undo.epSquare) % 8];
		}
		
		// Undo castling permissions. TODO: Could be for loop but is it worth it?
		if (undo.affectsCastling[0]) {
			if (castling[0][0] != undo.castling[0][0]) {
				zobristHash ^= Zobrist.castling[0][0];
			}
			if (castling[0][1] != undo.castling[0][1]) {
				zobristHash ^= Zobrist.castling[0][1];
			}
			castling[0] = undo.castling[0];
		}
		if (undo.affectsCastling[1]) {
			if (castling[1][0] != undo.castling[1][0]) {
				zobristHash ^= Zobrist.castling[1][0];
			}
			if (castling[1][1] != undo.castling[1][1]) {
				zobristHash ^= Zobrist.castling[1][1];
			}
			castling[1] = undo.castling[1];
		}
		
		// Reset castled rook
		if ((MoveUtils.pieceType(undo.movedPiece) == Chess.Piece.KING) && (Math.abs(fromSquare - toSquare) == 2)) {
			int rookSquare = fromSquare + ((toSquare - fromSquare) / 2);
			int rook = board[rookSquare];
			if (toSquare > fromSquare) {
				zobristHash ^= Zobrist.pieceSquare[board[rookSquare + 2]][rookSquare + 2];
				board[rookSquare + 2] = rook;
			} else {
				zobristHash ^= Zobrist.pieceSquare[board[rookSquare - 3]][rookSquare - 3];
				board[rookSquare - 3] = rook;
			}
			
			zobristHash ^= Zobrist.pieceSquare[board[rookSquare]][rookSquare];
			board[rookSquare] = Chess.Piece.EMPTY;
		}
		
		if (undo.isEnPassent) {
			board[toSquare] = Chess.Piece.EMPTY;
			if (toSquare > fromSquare) {
				board[toSquare -  8] = undo.capturedPiece;
			} else {
				board[toSquare + 8] = undo.capturedPiece;
			}
		}
		
		zobristHash ^= Zobrist.whiteToMove;
		whiteToMove = !whiteToMove;
		
		initialisePieceBitboards();
		
		/*if (zobristHash != Zobrist.hashForPosition(this)) {
			LongAlgebraicNotation notation = new LongAlgebraicNotation();
			System.out.println("Hash problem");
			System.out.println("Move: " + notation.toString(undo.move));
			System.out.println(this);
		}*/
		
		// TODO: Half-moves and moves
	}
	
	/**
	 * Move the piece from one square to another, returning captured piece
	 * 
	 * @param fromSquare
	 * @param toSquare
	 * @return captured piece (which may be 0 for empty)
	 */
	public int movePiece(int fromSquare, int toSquare) {
		int result = board[toSquare];
		zobristToggle(toSquare);
		zobristToggle(fromSquare);
		board[toSquare] = board[fromSquare];
		board[fromSquare] = Chess.Piece.EMPTY;
		zobristToggle(toSquare);
		zobristToggle(fromSquare);
		return result;
	}
	
	public void promotePawn(int square, int promotedPiece) {
		zobristToggle(square);
		board[square] = promotedPiece;
		zobristToggle(square);
	}
	
	/**
	 * Add or remove the value for the piece currently on the square from/to the hash
	 * 
	 * @param square
	 */
	private void zobristToggle(int square) {
		zobristHash ^= Zobrist.pieceSquare[board[square]][square];
	}

}
