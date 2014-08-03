package uk.co.micaherne.unidexter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.unidexter.Chess.Colour;
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
	public void testMoveGenerator() throws NotationException {
		
		// System.out.println(BitboardUtils.toString(moveGenerator.bbKnightAttacks[Chess.Square.E4]));
		// System.out.println(BitboardUtils.toString(moveGenerator.bbKnightAttacks[Chess.Square.H5]));
	}

	@Test
	public void testGenerateMoves() throws NotationException {
		
		int[] moves = moveGenerator.generateMoves();
		assertEquals(20, moves[0]);
		
		position.whiteToMove = false;
		int[] bmoves = moveGenerator.generateMoves();
		/*for (int i = 1; i <= bmoves[0]; i++) {
			System.out.println(notation.toString(bmoves[i]));
		}*/
		assertEquals(20, bmoves[0]);
	}
	
	@Test
	public void testGenerateMoves2() throws NotationException {
		Position position2 = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
		MoveGenerator moveGenerator2 = new MoveGenerator(position2);
		int[] moves = moveGenerator2.generateMoves();
		for (int i = 1; i <= moves[0]; i++) {
			System.out.println(notation.toString(moves[i]));
		}
		// System.out.println(BitboardUtils.toString(moveGenerator2.bbRookAttacks[Chess.Square.A1]));
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
	public void testGenerateMoves5() throws NotationException {
		Position position5 = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/1B1PN3/1p2P3/P1N2Q2/1PPB1PpP/R3K2R b KQkq - 1 1");
		assertFalse(position5.whiteToMove);
		MoveGenerator moveGenerator5 = new MoveGenerator(position5);
		// System.out.println(BitboardUtils.toString(position5.pieceBitboards[Chess.Piece.PAWN]));
		int[] moves = moveGenerator5.generateMoves();
		for (int i = 1; i <= moves[0]; i++) {
			System.out.println(notation.toString(moves[i]));
		}
	}
	
	@Test
	public void testTemp() throws NotationException {
		Position position2 = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
		MoveGenerator moveGenerator2 = new MoveGenerator(position2);
		// System.out.println(BitboardUtils.toString(moveGenerator2.bishopAttacks(Chess.Square.F1)));
		/* assertTrue(moveGenerator2.attacks(Chess.Square.C3, Chess.Colour.BLACK));
		assertTrue(moveGenerator2.attacks(Chess.Square.C4, Chess.Colour.BLACK));
		assertFalse(moveGenerator2.attacks(Chess.Square.B3, Chess.Colour.BLACK));
		assertTrue(moveGenerator2.attacks(Chess.Square.H3, Chess.Colour.WHITE));*/
		//System.out.println(BitboardUtils.toString(MoveGenerator.ooIntermediateSquares[Chess.Colour.WHITE][0]));
		/*//System.out.println(BitboardUtils.toString(moveGenerator.bbKingAttacks[Chess.Square.A8]));
		Position position2 = Position.fromFEN("3kQ3/8/8/8/8/8/8/8 b - -");
		MoveGenerator moveGenerator2 = new MoveGenerator(position2);
		long start = System.nanoTime();
		for (int i = 0; i < 1000000; i++) {
			int[] moves = moveGenerator.generateMoves();
		}
		System.out.println("Time taken: " + (System.nanoTime() - start) + " nanoseconds");
		//assertEquals(5, moves[0]);
*/	}
	
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
