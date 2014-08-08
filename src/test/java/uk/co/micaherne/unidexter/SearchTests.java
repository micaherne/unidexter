package uk.co.micaherne.unidexter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.unidexter.search.Search;

public class SearchTests {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testBestMove() throws NotationException, InterruptedException {
		Position position = Position.fromFEN("rnb1kbnr/pp1pp2p/8/4N2Q/4R3/3B4/2PB1PPP/5RK1 b kq - 0 18");
		Search search = new Search(position);
		int move = search.bestMove(1);
		assertEquals(MoveUtils.create(Chess.Square.E8, Chess.Square.D8), move);
		move = search.bestMove(3);
		assertEquals(MoveUtils.create(Chess.Square.E8, Chess.Square.D8), move);
	}

}
