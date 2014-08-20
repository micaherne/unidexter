package uk.co.micaherne.unidexter.hashing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.unidexter.Chess;
import uk.co.micaherne.unidexter.MoveUtils;
import uk.co.micaherne.unidexter.Position;

public class ZobristTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		Zobrist.init();
		Position position = Position.fromFEN(Chess.START_POS_FEN);
		
		long hash = Zobrist.hashForPosition(position);
		assertEquals(hash, position.zobristHash);
		
		position.move(MoveUtils.create(Chess.Square.E2, Chess.Square.E4));
		long hash2 = Zobrist.hashForPosition(position);
		
		assertEquals(hash2, position.zobristHash);
		
		position.unmakeMove();
		assertEquals(hash, position.zobristHash);
		
		// Another position
		position = Position.fromFEN("r1bqkbnr/pppppppp/8/8/1n6/5N2/PPPPPPPP/RNBQKB1R w KQkq - 2 1");
		zz(position);
		
		position.move(MoveUtils.create(Chess.Square.B4, Chess.Square.A2));
		zz(position);
		
		// Another position
		position = Position.fromFEN("r1bqkbnr/pppppppp/8/8/1n6/5N2/PPPPPPPP/RNBQKB1R b KQkq - 0 1");
		zz(position);
		
		position.move(MoveUtils.create(Chess.Square.A8, Chess.Square.B8));
		zz(position);
		
		position.unmakeMove();
		zz(position);
		
		// Another position
		position = Position.fromFEN("rnbqkbnr/pppp1ppp/8/8/3Pp3/N4N2/PPP1PPPP/R1BQKB1R b KQkq - 0 1");
		long hash3 = Zobrist.hashForPosition(position);
		assertEquals(hash3, position.zobristHash);
		
		position.move(MoveUtils.create(Chess.Square.E4, Chess.Square.F3));
		zz(position);
		
		position.move(MoveUtils.create(Chess.Square.G2, Chess.Square.F3));
		zz(position);
		
		position.unmakeMove();
		position.unmakeMove();
		assertEquals(hash3, position.zobristHash);
		
		// Castling
		/*position = Position.fromFEN("4k3/8/8/8/8/8/8/4K2R w K - 0 1");
		zz(position);
		position.move(MoveUtils.create(Chess.Square.E1, Chess.Square.G1));
		zz(position);
		position.unmakeMove();
		zz(position);*/
	}

	/**
	 * Check that position's stored hash is the same as the calculated hash
	 * 
	 * @param position
	 */
	private void zz(Position position) {
		assertEquals(Zobrist.hashForPosition(position), position.zobristHash);
	}
	
}
