package uk.co.micaherne.unidexter.search;

import java.util.Date;

import uk.co.micaherne.unidexter.MoveGenerator;
import uk.co.micaherne.unidexter.Position;
import uk.co.micaherne.unidexter.Chess.Colour;
import uk.co.micaherne.unidexter.evaluation.Evaluation;
import uk.co.micaherne.unidexter.io.ChessProtocol;

public class Search implements Runnable {

	public Position position;
	public MoveGenerator moveGenerator;
	public Evaluation evaluation;

	// For threading use
	public int bestMove;
	// public int[] pv;
	private int depth;
	private ChessProtocol protocol;
	private Line principalVariation;
	private long nodes;
	private Date searchStarted;
	
	public int mySide; // the colour the computer is playing (for pv info)

	public Search(Position position) {
		this.position = position;
		this.moveGenerator = new MoveGenerator(position);
		this.evaluation = new Evaluation(position);
	}

	public int bestMove(int depth) {
		principalVariation = new Line();
		// return bestMoveNegamax(depth, line);
		// NB: This method can't be interrupted, unlike the above one
		// int score = negamax(depth, principalVariation);
		
		mySide = position.whiteToMove ? Colour.WHITE : Colour.BLACK;
		
		// Set up info variables
		nodes = 0;
		searchStarted = new Date();
		
		// Using actual min and max value causes problems when negating
		int score = alphaBeta(depth, Integer.MIN_VALUE + 100, Integer.MAX_VALUE - 100, principalVariation);
		return principalVariation.moves[0];
	}

	public int bestMoveNegamax(int depth, Line pline) {
		Line line = new Line();
		bestMove = 0;
		int max = Integer.MIN_VALUE;
		int[] moves = moveGenerator.generateMoves();
		for (int i = 1; i <= moves[0]; i++) {
			if (position.move(moves[i])) {

				// capture the first valid move in case interrupted
				if (bestMove == 0) {
					bestMove = moves[i];
				}

				int score = -negamax(depth - 1, line);
				if (score > max) {
					max = score;
					bestMove = moves[i];
					pline.moves[0] = moves[i];
					System.arraycopy(line.moves, 0, pline.moves, 1,
							line.moveCount);
					pline.moveCount = line.moveCount + 1;

					if (protocol != null) {
						if (position.whiteToMove) {
							protocol.sendPrincipalVariation(this.principalVariation, -score,
									depth, nodes, searchStarted);
						} else {
							protocol.sendPrincipalVariation(this.principalVariation, score,
									depth, nodes, searchStarted);
						}
					}
				}

				position.unmakeMove();
			}
		}

		// If no moves have worked, it's checkmate or stalemate
		if (max == Integer.MIN_VALUE) {
			if (position.whiteToMove) {
				return evaluation.evaluateTerminal(depth);
			} else {
				return -evaluation.evaluateTerminal(depth);
			}
		}

		return bestMove;
	}

	public int negamax(int depth, Line pline) {
		Line line = new Line();

		if (Thread.interrupted()) {
			throw new SearchInterruptException();
		}
		
		int max = Integer.MIN_VALUE;
		if (depth == 0) {
			pline.moveCount = 0;
			if (position.whiteToMove) {
				return evaluation.evaluate();
			} else {
				return -evaluation.evaluate();
			}
		}
		int[] moves = moveGenerator.generateMoves();
		for (int i = 1; i <= moves[0]; i++) {
			if (position.move(moves[i])) {
				
				int score = -negamax(depth - 1, line);
				position.unmakeMove();
				
				if (score > max) {
					max = score;
					pline.moves[0] = moves[i];
					System.arraycopy(line.moves, 0, pline.moves, 1,
							line.moveCount);
					pline.moveCount = line.moveCount + 1;

					if (protocol != null
							&& pline == this.principalVariation) {
						if (position.whiteToMove) {
							protocol.sendPrincipalVariation(this.principalVariation, -score,
									depth, nodes, searchStarted);
						} else {
							protocol.sendPrincipalVariation(this.principalVariation, score,
									depth, nodes, searchStarted);
						}
					}
				}
				
			}
		}

		// If no moves have worked, it's checkmate or stalemate
		if (max == Integer.MIN_VALUE) {
			if (position.whiteToMove) {
				return evaluation.evaluateTerminal(depth);
			} else {
				return -evaluation.evaluateTerminal(depth);
			}

		}
		return max;
	}
	
	public int alphaBeta(int depth, int alpha, int beta, Line pline) {
		Line line = new Line();

		if (Thread.interrupted()) {
			throw new SearchInterruptException();
		}
		
		if (depth == 0) {
			nodes++; // just count evaluated nodes
			pline.moveCount = 0;
			if (position.whiteToMove) {
				return evaluation.evaluate();
			} else {
				return -evaluation.evaluate();
			}
		}
		
		int[] moves = moveGenerator.generateMoves();
		boolean validMoveFound = false;
		for (int i = 1; i <= moves[0]; i++) {
			if (position.move(moves[i])) {
				validMoveFound = true;
				int score = -alphaBeta(depth - 1, -beta, -alpha, line);
				position.unmakeMove();
				
				if (score >= beta) {
					return beta;
				}
				if (score > alpha) {
					alpha = score;
					
					pline.moves[0] = moves[i];
					System.arraycopy(line.moves, 0, pline.moves, 1,
							line.moveCount);
					pline.moveCount = line.moveCount + 1;

					if (protocol != null
							&& pline == this.principalVariation) {
						if (mySide == Colour.WHITE) {
							protocol.sendPrincipalVariation(this.principalVariation, score,
									depth, nodes, searchStarted);
						} else {
							protocol.sendPrincipalVariation(this.principalVariation, -score,
									depth, nodes, searchStarted);
						}
					}
				}
			}
		}
		
		// If no moves have worked, it's checkmate or stalemate
		if (!validMoveFound) {
			if (position.whiteToMove) {
				return evaluation.evaluateTerminal(depth);
			} else {
				return -evaluation.evaluateTerminal(depth);
			}

		}
		
		return alpha;
		
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
