package uk.co.micaherne.unidexter.search;

import uk.co.micaherne.unidexter.notation.LongAlgebraicNotation;

/**
 * A representation of a line, i.e. a sequence of moves.
 * 
 * Mainly used for retrieving the principal variation of a search.
 * 
 * Taken from http://web.archive.org/web/20080214221948/http://www.seanet.com/~brucemo/topics/pv.htm
 * 
 * @author Michael Aherne
 *
 */
public class Line {
	
	public static final int MAX_LINE_LENGTH = 32;
	public static LongAlgebraicNotation notation;

	public int moveCount = 0;
	public int[] moves = new int[MAX_LINE_LENGTH];
	
	@Override
	public String toString() {
		if (notation == null) {
			notation = new LongAlgebraicNotation();
		}
		StringBuffer result = new StringBuffer("Line (").append(moveCount).append(" moves):");
		for (int i = 0; i < moveCount; i++) {
			result.append(" ").append(notation.toString(moves[i]));
		}
	
		return result.toString();
	}
	
	
	
}
