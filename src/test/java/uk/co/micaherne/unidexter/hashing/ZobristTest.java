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
	}

}
