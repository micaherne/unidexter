package uk.co.micaherne.unidexter.evaluation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.unidexter.Chess;
import uk.co.micaherne.unidexter.Position;
import uk.co.micaherne.unidexter.notation.NotationException;

public class EvaluationTests {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testEvaluate() throws NotationException {
		Position position = Position.fromFEN(Chess.START_POS_FEN);
		Evaluation evaluation = new Evaluation(position);
		assertEquals(0, evaluation.evaluate());
	}
	
	@Test
	public void testEvaluateTerminal() {
		Position position = Position.fromFEN("3K4/8/2k1q3/8/8/8/8/8 w - - 2 1");
		Evaluation evaluation = new Evaluation(position);
		assertEquals(0, evaluation.evaluateTerminal(0));
		
		position = Position.fromFEN("3K4/3q4/2k5/8/8/8/8/8 w - - 2 1");
		evaluation = new Evaluation(position);
		assertTrue(evaluation.evaluateTerminal(0) < -10000);
	}

}
