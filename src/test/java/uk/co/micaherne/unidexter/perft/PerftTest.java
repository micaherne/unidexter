package uk.co.micaherne.unidexter.perft;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.unidexter.Chess;
import uk.co.micaherne.unidexter.Position;
import uk.co.micaherne.unidexter.notation.NotationException;

public class PerftTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testInitPos() throws NotationException {
		Position position = Position.fromFEN(Chess.START_POS_FEN);
		PerftResult p = Perft.perft(position, 1);
		assertEquals(20, p.moveCount);
		PerftResult p2 = Perft.perft(position, 2);
		assertEquals(400, p2.moveCount);
		PerftResult p3 = Perft.perft(position, 3);
		assertEquals(8902, p3.moveCount);
		// PerftResult p4 = Perft.perft(position, 4);
		// assertEquals(197281, p4.moveCount);
		// PerftResult p5 = Perft.perft(position, 5);
		// assertEquals(4865609, p5.moveCount);
	}
	
	@Test
	public void testKiwiPete() throws NotationException {
		Position position = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
		PerftResult p = Perft.perft(position, 1);
		assertEquals(48, p.moveCount);
		PerftResult p2 = Perft.perft(position, 2);
		assertEquals(2039, p2.moveCount);
		PerftResult p3 = Perft.perft(position, 3);
		assertEquals(97862, p3.moveCount);
		// PerftResult p4 = Perft.perft(position, 4);
		// assertEquals(4085603, p4.moveCount);
	}
	
	@Test
	public void testPerft() throws NotationException {
		Position position = Position.fromFEN("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
		PerftResult p = Perft.perft(position, 1);
		assertEquals(14, p.moveCount);
		
		position = Position.fromFEN("rnbqkbnr/pp2pppp/2p5/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
		p = Perft.perft(position, 1);
		assertEquals(30, p.moveCount);
		
		position = Position.fromFEN("4k3/8/8/2pP4/8/8/8/4K3 w - c6 0 2");
		p = Perft.perft(position, 1);
		assertEquals(7, p.moveCount);
	}
	
	// This tests a position where a move was being missed out when searching more deeply
	// @Test
	public void testWeirdPosition() throws NotationException {
		Position position = Position.fromFEN("4k3/2p5/8/3P4/8/8/8/4K3 b - - 0 1");
		Map<String, Long> divide1 = Perft.divide(position, 1);
		Map<String, Long> divide2 = Perft.divide(position, 2);
		assertEquals(divide1.size(), divide2.size());
	}
	
	@Test
	public void testMoveFromCheck() throws NotationException {
		Position position = Position.fromFEN("rnb1kbnr/pp1pp2p/8/4N2Q/4R3/3B4/2PB1PPP/5RK1 b kq - 0 18");
		assertEquals(1, Perft.perft(position, 1).moveCount);
	}
	
	// @Test
	public void testDivide() throws NotationException {
		// Position position = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R b KQkq -");
		// Position position = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R4RK1 b kq - 1 1");
		// Position position = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/1B1PN3/1p2P3/P1N2Q2/1PPB1PpP/R3K2R b KQkq - 1 1");
		// Position position = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
		// Position position = Position.fromFEN("r1bqk2r/pppp1ppp/2n1p3/8/3PP1n1/2PB1N1P/P1P2PP1/R1BQK2R b KQkq - 0 7");
		Position position = Position.fromFEN("4k3/8/8/8/8/8/8/4K2R w K - 0 1");
		Map<String, Long> divide = Perft.divide(position, 1);
		long nodes = 0L;
		for (String key : divide.keySet()) {
			Long moveCount = divide.get(key);
			System.out.println(key + " " + moveCount);
			nodes += moveCount;
		}
		System.out.println("Moves: " + divide.size());
		System.out.println("Nodes: " + nodes);
	}
	
	@Test
	public void testPerft8() throws NotationException {
		Position position = Position.fromFEN("r3k2r/8/8/8/8/8/8/4K3 w kq - 0 1");
		PerftResult p = Perft.perft(position, 1);
		assertEquals(5, p.moveCount);
		
		p = Perft.perft(position, 2);
		assertEquals(130, p.moveCount);
		
		p = Perft.perft(position, 3);
		assertEquals(782, p.moveCount);
		
		p = Perft.perft(position, 4);
		assertEquals(22180, p.moveCount);
		
		/*p = Perft.perft(position, 5);
		assertEquals(118882, p.moveCount);
		
		p = Perft.perft(position, 6);
		assertEquals(3517770, p.moveCount);*/
		
	}

}
