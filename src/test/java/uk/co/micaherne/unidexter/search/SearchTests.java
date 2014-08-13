package uk.co.micaherne.unidexter.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.unidexter.Chess;
import uk.co.micaherne.unidexter.MoveGenerator;
import uk.co.micaherne.unidexter.MoveUtils;
import uk.co.micaherne.unidexter.Position;
import uk.co.micaherne.unidexter.io.UCI;
import uk.co.micaherne.unidexter.io.UCIException;
import uk.co.micaherne.unidexter.notation.LongAlgebraicNotation;
import uk.co.micaherne.unidexter.notation.NotationException;

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
	
	@Test
	public void testBroken2() throws UCIException {
		UCI uci = new UCI();
		uci.doInput("position startpos moves e2e4 b8c6 g1f3 g8f6 e4e5 f6e4 d2d3 e4c5 c1e3 c5e6 b1c3 d7d6 e5d6 c7d6 f3g5 e6g5 e3g5 d8a5 g5d2 c6d4 c3e4 a5d5 f2f3 c8f5 f1e2 f5e4 f3e4 d5b5 d2c3 d4e2 d1e2 e7e6 e1g1 a7a5 e2f3 b5b6 g1h1 f7f6 c3f6 g7f6 f3f6 h8g8 f6f7 e8d8 f7g8 d8c7 f1f7 c7c6 f7f8 b6b2 a1f1 a8f8 g8f8 b2a2 f8c8 c6b6 g2g4 a5a4 c8c3 e6e5 h1g2 a4a3 g2h3 a2b2 c3b2 a3b2 f1b1 h7h6 b1b2 b6a5 h3h4 b7b5 h4h5 b5b4 h5h6 a5a4 g4g5 a4a3 b2b1 a3a2 b1b4 a2a1 g5g6 d6d5 e4d5 e5e4 d3e4 a1a2 g6g7 a2a3 c2c3 a3a2 g7g8q");
		Position position = uci.getPosition();
		assertEquals(Chess.Piece.White.QUEEN, position.board[62]);
		System.out.println(position);
	}
	
	@Test
	public void testAlphaBeta() {
		Position position = Position.fromFEN(Chess.START_POS_FEN);
		Search search = new Search(position);
		Line pline = new Line();
		search.alphaBeta(4, Integer.MIN_VALUE + 100, Integer.MAX_VALUE - 100, pline);
	}
}
