package uk.co.micaherne.unidexter.search;

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

	public int moveCount = 0;
	public int[] moves = new int[MAX_LINE_LENGTH];
	
}
