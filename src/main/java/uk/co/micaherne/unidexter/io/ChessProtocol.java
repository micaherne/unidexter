package uk.co.micaherne.unidexter.io;

import uk.co.micaherne.unidexter.Position;

public interface ChessProtocol {

	public abstract void doInput(String input) throws UCIException;

	public abstract Position getCurrentPosition();

}