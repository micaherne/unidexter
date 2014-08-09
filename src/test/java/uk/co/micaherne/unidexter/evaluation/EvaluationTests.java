package uk.co.micaherne.unidexter.evaluation;

import static org.junit.Assert.assertEquals;

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

}
