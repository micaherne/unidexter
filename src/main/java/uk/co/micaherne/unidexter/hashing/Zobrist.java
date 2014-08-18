package uk.co.micaherne.unidexter.hashing;

import java.util.Random;

import uk.co.micaherne.unidexter.Position;

public class Zobrist {
	
	public static long[][] pieceSquare = new long[15][64];
	
	public static long whiteToMove;
	
	public static long[][] castling = new long[2][2];
	
	public static long[] epFile = new long[8];

	public static void init() {
		Random random = new Random(1234);
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 64; j++) {
				pieceSquare[i][j] = random.nextLong();
			}
		}
		
		whiteToMove = random.nextLong();
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				castling[i][j] = random.nextLong();
			}
		}
		
		for (int i = 0; i < 8; i++) {
			epFile[i] = random.nextLong();
		}
	}
	
	public static long hashForPosition(Position position) {
		long result = 0L;
		for (int i = 0; i < 64; i++) {
			if (position.board[i] != 0) {
				result ^= pieceSquare[position.board[i]][i];
			}
		}
		if (position.whiteToMove) {
			result ^= whiteToMove;
		}
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if (position.castling[i][j]) {
					result ^= castling[i][j];
				}
			}
		}
		
		if (position.epSquare != 0L) {
			result ^= epFile[Long.numberOfTrailingZeros(position.epSquare) % 8];
		}
		
		return result;
	}

}
