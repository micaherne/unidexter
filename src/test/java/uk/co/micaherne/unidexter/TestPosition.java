package uk.co.micaherne.unidexter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.unidexter.perft.Perft;

public class TestPosition {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFromFEN() throws NotationException {
		// Position position = Position.fromFEN(Chess.START_POS_FEN);
	}
	
	@Test
	public void testMove() throws NotationException {
		Position position = Position.fromFEN(Chess.START_POS_FEN);
		int move = MoveUtils.create(Chess.Square.E2, Chess.Square.E4);
		position.move(move);
		assertFalse(position.whiteToMove);
		int move2 = MoveUtils.create(Chess.Square.E7, Chess.Square.E5);
		position.move(move2);
		
		// Make sure taking a rook removes the right to castle
		position = Position.fromFEN("r3k2r/p1ppqNb1/bn2pnp1/3P4/4P3/1pN2Q1p/PPPBBPPP/R3K2R w KQkq - 0 2");
		move = MoveUtils.create(Chess.Square.F7, Chess.Square.H8);
		position.move(move);
		assertFalse(position.castling[Chess.Colour.BLACK][1]);
	}
	
	@Test
	public void testMove2() throws NotationException {
		Position position = Position.fromFEN("rnb1kbnr/pp1pp2p/8/4N2Q/4R3/3B4/2PB1PPP/5RK1 b kq - 0 18");
		int move = MoveUtils.create(Chess.Square.E8, Chess.Square.E7);
		assertTrue(position.move(move));
		assertEquals(1, Perft.perft(position, 1).moveCount);
	}
	
	@Test
	public void testEPMove() throws NotationException {
		// EP square is cleared on moving / taking
		Position position = Position.fromFEN("r4rk1/p1ppqpb1/bn2pnp1/3PN3/Pp2P3/2N2Q1p/1PP1BPPP/R1B1K2R b KQ a3 0 2");
		assertEquals(1L << 16, position.epSquare);
		int move = MoveUtils.create(25, 16);
		position.move(move);
		assertEquals(0L, position.epSquare);
		
		Position position1a = Position.fromFEN("r4rk1/p1ppqpb1/bn2pnp1/3PN3/Pp2P3/2N2Q1p/1PP1BPPP/R1B1K2R b KQ a3 0 2");
		assertEquals(1L << 16, position1a.epSquare);
		int move1a = MoveUtils.create(25, 17);
		position1a.move(move1a);
		assertEquals(0L, position1a.epSquare);
		
		// EP square is set on moving pawn 2 squares
		Position position2 = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
		int move2 = MoveUtils.create(8, 24);
		position2.move(move2);
		assertEquals(1L << 16, position2.epSquare);
	}
	
	@Test
	public void testUnmakeEP() throws NotationException {
		// EP square is set on moving pawn 2 squares
		Position position = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
		int move = MoveUtils.create(8, 24);
		position.move(move);
		assertEquals(1L << 16, position.epSquare);
		
		int[] board = position.board.clone();
		
		int move2 = MoveUtils.create(25, 17);
		position.move(move2);
		assertEquals(0L, position.epSquare);
		
		MoveUndo undoData = position.undoData.peek();
		assertEquals(1L << 16, undoData.epSquare);
		
		position.unmakeMove();
		assertEquals(1L << 16, position.epSquare);
		
		for (int i = 0; i < 64; i++) {
			assertEquals(board[i], position.board[i]);
		}
		
		Position position2 = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
		MoveGenerator m = new MoveGenerator(position2);
		int[] mBefore = m.generateMoves();
		int move3 = MoveUtils.create(11, 2);
		position2.move(move3);
		position2.unmakeMove();
		int[] mAfter = m.generateMoves();
		assertArrayEquals(mBefore, mAfter);
		
		// Make sure unmake works for black too
		Position position3 = Position.fromFEN("4k3/2p5/8/3P4/8/8/8/4K3 b - - 0 1");
		position3.move(MoveUtils.create(Chess.Square.C7, Chess.Square.C5));
		position3.move(MoveUtils.create(Chess.Square.D5, Chess.Square.C6, false, true));
		position3.unmakeMove();
		position3.unmakeMove();
		assertEquals(Chess.Piece.EMPTY, position3.board[Chess.Square.C6]);
	}
	
	@Test
	public void testUnmakeMove() throws NotationException {
		Position position = Position.fromFEN(Chess.START_POS_FEN);
		int move = MoveUtils.create(Chess.Square.E2, Chess.Square.E4);
		position.move(move);
		position.unmakeMove();
		assertEquals(Chess.Piece.White.PAWN, position.board[Chess.Square.E2]);
		assertEquals(Chess.Piece.EMPTY, position.board[Chess.Square.E4]);
		assertTrue(position.whiteToMove);
	}
	
	@Test
	public void testUnmakeMove2() throws NotationException {
		Position position = Position.fromFEN("4k3/2p5/8/3P4/8/8/8/4K3 b - - 0 1");
		assertTrue(position.move(MoveUtils.create(Chess.Square.C7, Chess.Square.C5)));
		assertTrue(position.move(MoveUtils.create(Chess.Square.D5, Chess.Square.C6)));
		position.unmakeMove();
		position.unmakeMove();
		assertEquals(0, position.board[Chess.Square.C6]);
	}
	
	@Test
	public void testMakeCastlingMove() throws NotationException {
		Position position = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
		int move = MoveUtils.create(4, 6);
		assertTrue(position.castling[Chess.Colour.WHITE][0]);
		assertTrue(position.castling[Chess.Colour.WHITE][1]);
		position.move(move);
		assertFalse(position.castling[Chess.Colour.WHITE][0]);
		assertFalse(position.castling[Chess.Colour.WHITE][1]);
		MoveUndo undo = position.undoData.peek();
		assertTrue(undo.castling[Chess.Colour.WHITE][0]);
		assertTrue(undo.castling[Chess.Colour.WHITE][1]);
		position.unmakeMove();
		assertTrue(position.castling[Chess.Colour.WHITE][0]);
		assertTrue(position.castling[Chess.Colour.WHITE][1]);
		
	}
		
	/**
	 * @throws NotationException
	 */
	@Test
	public void testInitialisePieceBitboards() throws NotationException {
		Position position = Position.fromFEN(Chess.START_POS_FEN);
		position.initialisePieceBitboards();
		assertEquals(2, Long.numberOfLeadingZeros(position.pieceBitboards[Chess.Piece.BISHOP]));
	}
	
	@Test
	public void testInCheck() throws NotationException {
		Position position = Position.fromFEN("4k3/2p5/b7/3P4/8/8/8/4K3 b - - 0 1");
		assertFalse(position.inCheck(Chess.Colour.BLACK));
	}

}
