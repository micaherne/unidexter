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
		assertEquals(Zobrist.hashForPosition(position), position.zobristHash);
		
		position.move(MoveUtils.create(Chess.Square.B4, Chess.Square.A2));
		assertEquals(Zobrist.hashForPosition(position), position.zobristHash);
		
		// Another position
		position = Position.fromFEN("r1bqkbnr/pppppppp/8/8/1n6/5N2/PPPPPPPP/RNBQKB1R b KQkq - 0 1");
		assertEquals(Zobrist.hashForPosition(position), position.zobristHash);
		
		position.move(MoveUtils.create(Chess.Square.A8, Chess.Square.B8));
		assertEquals(Zobrist.hashForPosition(position), position.zobristHash);
		
		position.unmakeMove();
		assertEquals(Zobrist.hashForPosition(position), position.zobristHash);
		
	}

}
