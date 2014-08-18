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
		Position position = Position.fromFEN(Chess.START_POS_FEN);
		Zobrist.init();
		long hash = Zobrist.hashForPosition(position);
		System.out.println(hash);
		position.move(MoveUtils.create(Chess.Square.E2, Chess.Square.E4));
		hash = Zobrist.hashForPosition(position);
		System.out.println(hash);
	}

}
