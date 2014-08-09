package uk.co.micaherne.unidexter.io;

import uk.co.micaherne.unidexter.search.Line;


public interface ChessProtocol {

	public abstract void doInput(String input) throws UCIException;

	public abstract void sendBestMove(int bestMove);
	
	public abstract void sendPrincipalVariation(Line line, int score, int depth);

}