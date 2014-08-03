package uk.co.micaherne.unidexter.perft;

import java.util.HashMap;
import java.util.Map;

import uk.co.micaherne.unidexter.MoveGenerator;
import uk.co.micaherne.unidexter.NotationException;
import uk.co.micaherne.unidexter.Position;
import uk.co.micaherne.unidexter.notation.LongAlgebraicNotation;

public class Perft {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static PerftResult perft(Position position, int depth) {
		MoveGenerator moveGenerator = new MoveGenerator(position);
		PerftResult result = new PerftResult();
		if (depth == 0) {
			result.moveCount++;
			return result;
		}
		int[] moves = moveGenerator.generateMoves();
		for (int i = 1; i <= moves[0]; i++) {
			if (position.move(moves[i])) {
				PerftResult subPerft = perft(position, depth - 1);
				position.unmakeMove();
				result.moveCount += subPerft.moveCount;
			}
		}
		return result;
	}
	
	public static Map<String, Long> divide(Position position, int depth) throws NotationException {
		LongAlgebraicNotation notation = new LongAlgebraicNotation();
		HashMap<String, Long> result = new HashMap<String, Long>();
		MoveGenerator moveGenerator = new MoveGenerator(position);
		int[] moves = moveGenerator.generateMoves();
		for (int i = 1; i <= moves[0]; i++) {
			if (position.move(moves[i])) {
				PerftResult subPerft = perft(position, depth - 1);
				position.unmakeMove();
				result.put(notation.toString(moves[i]), subPerft.moveCount);
			}
		}
		return result;
	}

}
