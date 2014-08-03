package uk.co.micaherne.unidexter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MoveUtilsTests {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCreate() {
		int move = MoveUtils.create(Chess.Square.E2, Chess.Square.E4);
		assertEquals(Chess.Square.E2, MoveUtils.fromSquare(move));
		assertEquals(Chess.Square.E4, MoveUtils.toSquare(move));
		assertFalse(MoveUtils.isQueening(move));
	}
	
	@Test
	public void testQueening() {
		int move = MoveUtils.create(Chess.Square.E7, Chess.Square.E8, true, false);
		assertTrue(MoveUtils.isQueening(move));
		
		// with promoted piece
		int move2 = MoveUtils.create(Chess.Square.E7, Chess.Square.E8, Chess.Piece.QUEEN);
		assertTrue(MoveUtils.isQueening(move2));
		assertEquals(Chess.Piece.QUEEN, MoveUtils.promotedPiece(move2));
	}
	
	@Test
	public void tempTest() {
		/*int[] bim = new int[256];
		bim[5] = 12345;
		int[][] nim = new int[12][256];
		nim[0] = bim;
		System.out.println(nim[0][5]);
		
		long t = 1;
		System.out.println((t << 63) >> 62);*/
		int move = MoveUtils.create(0, 0, 255);
		System.out.println(Integer.toBinaryString(move));
	}


}
