package uk.co.micaherne.unidexter.search;

import uk.co.micaherne.unidexter.MoveGenerator;
import uk.co.micaherne.unidexter.Position;
import uk.co.micaherne.unidexter.evaluation.Evaluation;
import uk.co.micaherne.unidexter.io.ChessProtocol;

public class Search implements Runnable {
	
	public Position position;
	public MoveGenerator moveGenerator;
	public Evaluation evaluation;
	
	// For threading use
	public int bestMove;
	public int[] pv;
	private int depth;
	private ChessProtocol protocol;
	
	public Search(Position position) {
		this.position = position;
		this.moveGenerator = new MoveGenerator(position);
		this.evaluation = new Evaluation(position);
	}
	
	public int bestMove(int depth) {
		return bestMoveNegamax(depth);
	}
	
	public int bestMoveNegamax(int depth) {
		bestMove = 0;
		pv = new int[depth];
		int max = Integer.MIN_VALUE;
		int[] moves = moveGenerator.generateMoves();
		for (int i = 1; i <= moves[0]; i++) {
			if (position.move(moves[i])) {
				
				// capture the first valid move in case interrupted
				if (bestMove == 0) {
					bestMove = moves[i];
				}
				
				int score = -negamax(depth - 1);
				if (score > max) {
					bestMove = moves[i];
					pv[depth - 1] = moves[i];
					if (protocol != null) {
						if (position.whiteToMove) {
							// protocol.sendPrincipalVariation(pv, -score, depth);
						} else {
							// protocol.sendPrincipalVariation(pv, score, depth);
						}
					}
					max = score;
				}
				
				position.unmakeMove();
			}
		}
		
		// If no moves have worked, it's checkmate or stalemate
		if (max == Integer.MIN_VALUE) {
			if (position.whiteToMove) {
				return evaluation.evaluateTerminal(position, depth);
			} else {
				return -evaluation.evaluateTerminal(position, depth);
			}
		}
		
		return bestMove;
	}
	
	public int negamax(int depth) {
		if (Thread.interrupted()) {
			throw new SearchInterruptException();
		}
		int max = Integer.MIN_VALUE;
		if (depth == 0) {
			if (position.whiteToMove) {
				return evaluation.evaluate();
			} else {
				return -evaluation.evaluate();
			}
		}
		int[] moves = moveGenerator.generateMoves();
		for (int i = 1; i <= moves[0]; i++) {
			if (position.move(moves[i])) {
				int score = -negamax(depth - 1);
				if (score > max) {
					pv[depth - 1] = moves[i];
					max = score;
				}
				position.unmakeMove();
			}
		}
		
		// If no moves have worked, it's checkmate or stalemate
		if (max == Integer.MIN_VALUE) {
			if (position.whiteToMove) {
				return evaluation.evaluateTerminal(position, depth);
			} else {
				return -evaluation.evaluateTerminal(position, depth);
			}
			
		}
		return max;
	}

	public void setPosition(Position position) {
		this.position = position;
		moveGenerator.setPosition(position);
		evaluation.setPosition(position);
	}

	@Override
	public void run() {
		try {
			bestMove = bestMove(depth);
		} catch (SearchInterruptException e) {
			// just let it send the best move as normal
		}
		this.protocol.sendBestMove(bestMove);
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void setProtocol(ChessProtocol protocol) {
		this.protocol = protocol;
	}


}
