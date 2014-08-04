package uk.co.micaherne.unidexter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.unidexter.notation.LongAlgebraicNotation;

public class MoveGeneratorTests {

	private Position position;
	private MoveGenerator moveGenerator;
	private LongAlgebraicNotation notation;

	@Before
	public void setUp() throws Exception {
		position = Position.fromFEN(Chess.START_POS_FEN);
		moveGenerator = new MoveGenerator(position);
		notation = new LongAlgebraicNotation();
	}

	@Test
	public void testGenerateMoves() throws NotationException {
		
		int[] moves = moveGenerator.generateMoves();
		assertEquals(20, moves[0]);
		
		position.whiteToMove = false;
		int[] bmoves = moveGenerator.generateMoves();
		assertEquals(20, bmoves[0]);
	}
	
	@Test
	public void testGenerateMoves2() throws NotationException {
		Position position2 = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
		MoveGenerator moveGenerator2 = new MoveGenerator(position2);
		int[] moves = moveGenerator2.generateMoves();
		assertEquals(48, moves[0]);
	}
	
	@Test
	public void testGenerateMoves3() throws NotationException {
		Position position3 = Position.fromFEN("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
		MoveGenerator moveGenerator3 = new MoveGenerator(position3);
		int[] moves = moveGenerator3.generateMoves();
		for (int i = 1; i <= moves[0]; i++) {
			System.out.println(notation.toString(moves[i]));
		}
		assertEquals(16, moves[0]); // 16 pseudolegal
		int legal = 0;
		for (int i = 1; i <= moves[0]; i++) {
			if (position3.move(moves[i])) {
				legal++;
				position3.unmakeMove();
			}
		}
		assertEquals(14, legal);
	}
	
	@Test
	public void testGenerateMoves4() throws NotationException {
		Position position = Position.fromFEN("4k3/8/8/2pP4/8/8/8/4K3 w - c6 0 2");
		assertEquals(1L << Chess.Square.C6, position.epSquare);
		MoveGenerator moveGenerator = new MoveGenerator(position);
		int[] moves = moveGenerator.generateMoves();
		assertEquals(7, moves[0]);
	}
	
	@Test
	public void testGenerateMovesFromCheck() throws NotationException {
		Position position = Position.fromFEN("rnb1kbnr/pp1pp2p/8/4N2Q/4R3/3B4/2PB1PPP/5RK1 b kq - 0 18");
		MoveGenerator moveGenerator = new MoveGenerator(position);
		int[] moves = moveGenerator.generateMoves();
		int legal = 0;
		for (int i = 1; i <= moves[0]; i++) {
			if (position.move(moves[i])) {
				legal++;
				position.unmakeMove();
			}
		}
		assertEquals(1, legal);
	}
	
	
	@Test
	public void testAttacks() throws NotationException {
		Position position2 = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
		MoveGenerator moveGenerator2 = new MoveGenerator(position2);
		assertTrue(moveGenerator2.attacks(Chess.Square.D3, Chess.Colour.BLACK));
		assertFalse(moveGenerator2.attacks(Chess.Square.F1, Chess.Colour.BLACK));
		
		Position position3 = Position.fromFEN("2k5/P7/8/8/8/8/8/3K4 w - - 0 1");
		MoveGenerator moveGenerator3 = new MoveGenerator(position3);
		assertTrue(moveGenerator3.attacks(Chess.Square.B8, Chess.Colour.WHITE));
		
		Position position4 = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPB1PPP/R2BK2R b KQkq - 1 1");
		MoveGenerator moveGenerator4 = new MoveGenerator(position4);
		assertTrue(moveGenerator4.attacks(Chess.Square.F1, Chess.Colour.BLACK));
		
		Position position5 = Position.fromFEN("4k3/2p5/8/3P4/8/8/8/4K3 b - - 0 1");
		MoveGenerator moveGenerator5 = new MoveGenerator(position5);
		assertFalse(moveGenerator5.attacks(Chess.Square.D7, Chess.Colour.WHITE));
		
	}

}
