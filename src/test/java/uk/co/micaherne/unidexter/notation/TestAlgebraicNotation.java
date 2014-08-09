package uk.co.micaherne.unidexter.notation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.unidexter.Chess;
import uk.co.micaherne.unidexter.MoveUtils;

public class TestAlgebraicNotation {
	
	private LongAlgebraicNotation notation;
	
	@Before
	public void setUp() throws Exception {
		notation = new LongAlgebraicNotation();
	}
	
	@Test(expected=NotationException.class)
	public void testToSquareWrongLength() throws NotationException {
		notation.toSquare("too long");
	}
	
	@Test
	public void testToSquare() throws NotationException {
		int square = notation.toSquare("a1");
		assertEquals(Chess.Square.A1, square);
		int square2 = notation.toSquare("h8");
		assertEquals(Chess.Square.H8, square2);
		int square3 = notation.toSquare("c6");
		assertEquals(Chess.Square.C6, square3);
	}
	
	@Test
	public void testFromPiece() throws NotationException {
		String piece = notation.fromPiece(Chess.Piece.KING);
		assertEquals("K", piece);
	}
	
	// @Test
	public void testToString() throws NotationException  {
		int move = MoveUtils.create(Chess.Square.E2, Chess.Square.E4);
		assertEquals("e2e4", notation.toString(move));
	}

}
