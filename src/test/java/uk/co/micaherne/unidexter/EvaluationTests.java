package uk.co.micaherne.unidexter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

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
