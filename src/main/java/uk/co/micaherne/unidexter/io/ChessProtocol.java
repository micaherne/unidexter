package uk.co.micaherne.unidexter.io;


public interface ChessProtocol {

	public abstract void doInput(String input) throws UCIException;

	public abstract void sendBestMove(int bestMove);

}