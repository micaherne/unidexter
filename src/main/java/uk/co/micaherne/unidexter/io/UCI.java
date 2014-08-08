package uk.co.micaherne.unidexter.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import uk.co.micaherne.unidexter.Chess;
import uk.co.micaherne.unidexter.FENException;
import uk.co.micaherne.unidexter.NotationException;
import uk.co.micaherne.unidexter.Position;
import uk.co.micaherne.unidexter.notation.LongAlgebraicNotation;
import uk.co.micaherne.unidexter.search.Search;

public class UCI implements ChessProtocol {

	/*
	 * Input commands
	 * 
	 * * uci debug [ on | off ] 
	 * isready 
	 * setoption name <id> [value <x>] 
	 * register
	 * ucinewgame 
	 * position [fen <fenstring> | startpos ] moves <move1> ....
	 * <movei> 
	 * go 
	 * stop 
	 * ponderhit
	 * quit
	 */
	
	BufferedReader in;
	private Position currentPosition;
	private LongAlgebraicNotation notation;
	public Search search;
	protected Thread searchThread;


	public UCI() {
		super();
		in = new BufferedReader(new InputStreamReader(System.in));
		notation = new LongAlgebraicNotation();
		search = new Search(currentPosition);
		search.setProtocol(this);
	}


	public void startInput() {
		try {
			String input = in.readLine();
			while(!"quit".equals(input)) {
				if(input != null) {
					doInput(input);
				}
				Thread.sleep(50);
				input = in.readLine();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UCIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see uk.co.micaherne.unidexter.io.ChessProtocol#doInput(java.lang.String)
	 */
	@Override
	public void doInput(String input) throws UCIException {
		String[] parts = input.split("\\s+", 2);
		String keyword = parts[0];
		
		if("uci".equals(keyword)) {
			commandUci(input);
			return;
		}
		if("debug".equals(keyword)) {
			doOutput("info string debug not implemented");
		}
		if("isready".equals(keyword)) {
			commandIsReady(input);
		}
		if("setoption".equals(keyword)) {
			doOutput("info string setoption not implemented");
		}
		if("register".equals(keyword)) {
			doOutput("info string register not implemented");
		}
		if("ucinewgame".equals(keyword)) {
			commandUciNewGame(input);
		}
		if("position".equals(keyword)) {
			// TODO: Proper error handling
				try {
					commandPosition(input);
				} catch (NotationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if("go".equals(keyword)) {
			try {
				commandGo(input);
			} catch (NotationException e) {
				doOutput("info error searching for move " + e.getMessage());
			}
		}
		if("stop".equals(keyword)) {
			// interrupt the search thread if it's running
			if (searchThread != null && searchThread.isAlive()) {
				searchThread.interrupt();
			}
		}
		if("ponderhit".equals(keyword)) {
			doOutput("info string ponderhit not implemented");
		}
		if("quit".equals(keyword)) {
			System.exit(0);
		}
		
		// System.out.println(input);
	}
	
	private void doOutput(Object output) {
		System.out.println(output);
	}
	
	private void commandUci(String input) {
		doOutput("id name unidexter");
		doOutput("id author Michael Aherne");
		
		// TODO: send options here
		
		doOutput("uciok");
	}
	
	private void commandIsReady(String input) {
		doOutput("readyok");
	}
	
	private void commandUciNewGame(String input) {
		// TODO: implement
	}
	
	/*position [fen <fenstring> | startpos ]  moves <move1> .... <movei>*/
	private void commandPosition(String input) throws UCIException, NotationException {
		String[] tokens = input.split("\\s+");
		if(!"position".equals(tokens[0])){
			throw new UCIException();
		}
		if("startpos".equals(tokens[1])) {
			try {
				currentPosition = Position.fromFEN(Chess.START_POS_FEN);
				if(tokens.length > 3 && "moves".equals(tokens[2])){
					for(int i = 3; i < tokens.length; i++) {
						int move = notation.parseMove(tokens[i]);
						currentPosition.move(move);
					}
				}
			} catch (FENException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if("fen".equals(tokens[1])) {
			/* Think we're looking for everything after fen 
			 * until the word "move" or end of line
			 */
		} else {
			throw new UCIException("Position must be startpos or fen");
		}
	}
	
    private void commandGo(String input) {
    	searchThread = new Thread(search);
    	search.setDepth(5);
    	search.setPosition(currentPosition);
    	searchThread.start();
    	// Thread calls sendBestMove() when finished or interrupted
	}
    
    @Override
	public void sendBestMove(int bestMove) {
    	doOutput("bestmove " + notation.toString(bestMove));
    }

	public Position getCurrentPosition() {
		return currentPosition;
	}

}
