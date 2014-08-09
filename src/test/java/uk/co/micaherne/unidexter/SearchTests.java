package uk.co.micaherne.unidexter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.unidexter.io.UCI;
import uk.co.micaherne.unidexter.io.UCIException;
import uk.co.micaherne.unidexter.notation.LongAlgebraicNotation;
import uk.co.micaherne.unidexter.notation.NotationException;
import uk.co.micaherne.unidexter.search.Search;

public class SearchTests {

	private LongAlgebraicNotation notation;

	@Before
	public void setUp() throws Exception {
		notation = new LongAlgebraicNotation();
	}

	@Test
	public void testBestMove() throws NotationException, InterruptedException {
		Position position = Position.fromFEN("rnb1kbnr/pp1pp2p/8/4N2Q/4R3/3B4/2PB1PPP/5RK1 b kq - 0 18");
		Search search = new Search(position);
		int move = search.bestMove(1);
		assertEquals(MoveUtils.create(Chess.Square.E8, Chess.Square.D8), move);
		move = search.bestMove(3);
		assertEquals(MoveUtils.create(Chess.Square.E8, Chess.Square.D8), move);
		
		 // Was giving Nf2 in this position:
        position = Position.fromFEN("r1bqk2r/pppp1ppp/2n1p3/8/3PP1n1/2PB1N1P/P1P2PP1/R1BQK2R b KQkq - 0 7");
        search = new Search(position);
        move = search.bestMove(4);
        
        assertNotEquals("g4f2", notation.toString(move));
        
        System.out.println(notation.toString(move));
	}

	@Test
	public void testBroken() throws UCIException {
		UCI uci = new UCI();
		uci.doInput("position startpos moves e2e4 b8c6 g1f3 g8f6 b1c3 e7e6 d2d4 f8b4 f1d3 b4c3 b2c3 f6g4 h2h3 g4f2 e1f2 d8f6 c1g5 f6g6 e4e5 f7f5 e5f6 g7f6 d3g6 h7g6 g5f6 h8f8 f6g5 d7d5 d1d3 a7a6 d3g6 f8f7 g6g8 f7f8 g8g6 f8f7 h1f1 c8d7 f2g1");
		Position position = uci.getPosition();
		
		// Position position = Position.fromFEN("r3k3/1ppb1r2/p1n1p1Q1/3p2B1/3P4/2P2N1P/P1P3P1/R4RK1 b q - 8 20");
		MoveGenerator m = new MoveGenerator(position);
		int[] moves = m.generateMoves();
		for (int i = 1; i <= moves[0]; i++) {
			System.out.println(notation.toString(moves[i]));
		}
		Search search = new Search(position);
        int move = search.bestMove(4);
        
        assertNotEquals("f5f4", notation.toString(move));
		/*System.out.println(position);
		System.out.println(BitboardUtils.toString(position.pieceBitboards[Chess.Piece.QUEEN]));
		System.out.println(BitboardUtils.toString(position.colourBitboards[Chess.Colour.BLACK]));*/
		
	}
}
