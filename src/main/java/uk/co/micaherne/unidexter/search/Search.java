package uk.co.micaherne.unidexter.search;

import java.util.Date;

import uk.co.micaherne.unidexter.MemoryManagement;
import uk.co.micaherne.unidexter.MoveGenerator;
import uk.co.micaherne.unidexter.Position;
import uk.co.micaherne.unidexter.evaluation.Evaluation;
import uk.co.micaherne.unidexter.hashing.TranspositionTable;
import uk.co.micaherne.unidexter.hashing.TranspositionTableEntry;
import uk.co.micaherne.unidexter.hashing.Zobrist;
import uk.co.micaherne.unidexter.io.ChessProtocol;

public class Search implements Runnable {

	public Position position;
	public MoveGenerator moveGenerator;
	public Evaluation evaluation;
	public TranspositionTable transpositionTable;

	// For threading use
	public int bestMove;
	// public int[] pv;
	
	// the requested search depth
	private int depth;
	
	// the actual current ply
	public int ply;
	
	private ChessProtocol protocol;
	public Line principalVariation;
	private long nodes;
	private Date searchStarted;
	
	
	// Do we want to use the transposition table?
	private static final boolean USE_TT = true;
	private int tthit;
	
	public Search(Position position) {
		this.position = position;
		this.moveGenerator = new MoveGenerator(position);
		this.evaluation = new Evaluation(position);
		if (USE_TT) {
			this.transpositionTable = new TranspositionTable(MemoryManagement.getTranspositiontablesize());
			Zobrist.init();
		}
	}

	/**
	 * The root search method. Searches to at least given depth, but may search
	 * some nodes more deeply.
	 * 
	 * This will update bestMove and principalVariation as it searches.
	 * 
	 * @param depth
	 * @return
	 */
	public int search(int depth) {
		this.depth = depth;
		this.ply = 0;
		principalVariation = new Line();
		
		tthit = 0;
		// Set up info variables
		nodes = 0;
		searchStarted = new Date();
		
		// Using actual min and max value causes problems when negating
		search(depth, Integer.MIN_VALUE + 100, Integer.MAX_VALUE - 100, principalVariation);
		
		// System.out.println("info string " + tthit + " transposition table hits");
		return principalVariation.moves[0];
	}
	
	/**
	 * The actual minimax search method.
	 * 
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @param pline
	 * @return
	 */
	public int search(int depth, int alpha, int beta, Line pline) {
		Line line = new Line();

		if (Thread.interrupted()) {
			throw new SearchInterruptException();
		}
		
		long hashForCurrentPosition = position.zobristHash;
		if (USE_TT) {
			TranspositionTableEntry entry = transpositionTable.get(hashForCurrentPosition);
			if (entry != null) {
				if (hashForCurrentPosition == entry.key && entry.depth >= depth) {
					tthit++;
					switch (entry.type) {
						case TranspositionTableEntry.LOWER:
							if (entry.score >= beta) {
								return beta;
							}
							break;
						case TranspositionTableEntry.UPPER:
							if (entry.score < alpha) {
								return alpha;
							}
							break;
						case TranspositionTableEntry.EXACT:
							if (entry.score > alpha && entry.score < beta) {
								return entry.score;
							}
					}
				}
			}
		}
		
		if (depth == 0) {
			nodes++; // just count evaluated nodes
			pline.moveCount = 0;
			return evaluation.evaluate();
		}
		
		int[] moves = moveGenerator.generateMoves();
		boolean validMoveFound = false;
		for (int i = 1; i <= moves[0]; i++) {
			if (position.move(moves[i])) {
				validMoveFound = true;
				ply++;
				int score = -search(depth - 1, -beta, -alpha, line);
				position.unmakeMove();
				ply--;
				
				if (score >= beta) {
					if (USE_TT) {
						TranspositionTableEntry entry = new TranspositionTableEntry(hashForCurrentPosition, moves[i], depth, score, TranspositionTableEntry.LOWER, 0);
						transpositionTable.add(entry);
					}
					return beta;
				}
				if (score > alpha) {
					alpha = score;
					
					pline.moves[0] = moves[i];
					System.arraycopy(line.moves, 0, pline.moves, 1,
							line.moveCount);
					pline.moveCount = line.moveCount + 1;

					if (protocol != null
							&& ply == 0) {
							protocol.sendPrincipalVariation(this.principalVariation, score,
									depth, nodes, searchStarted);

					}
					
					if (USE_TT) {
						TranspositionTableEntry entry = new TranspositionTableEntry(hashForCurrentPosition, moves[i], depth, score, TranspositionTableEntry.EXACT, 0);
						transpositionTable.add(entry);
					}
				} else {
					if (USE_TT) {
						TranspositionTableEntry entry = new TranspositionTableEntry(hashForCurrentPosition, moves[i], depth, score, TranspositionTableEntry.UPPER, 0);
						transpositionTable.add(entry);
					}
				}
			}
		}
		
		// If no moves have worked, it's checkmate or stalemate
		if (!validMoveFound) {
			return evaluation.evaluateTerminal(depth);
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
			bestMove = search(depth);
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
