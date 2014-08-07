package uk.co.micaherne.unidexter;

public class Search {
	
	public Position position;
	public MoveGenerator moveGenerator;
	public Evaluation evaluation;
	
	public int[] bestMove;
	
	public Search(Position position) {
		this.position = position;
		this.moveGenerator = new MoveGenerator(position);
		this.evaluation = new Evaluation(position);
	}
	
	public int bestMove(int depth) {
		// bestMove = new int[depth + 1];
		// negamax(depth);
		// negaMaxAlphaBeta(Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
		// return bestMove[depth];
		return bestMoveNegamax(depth);
	}
	
	public int bestMoveNegamax(int depth) {
		int bestMove = 0;
		int max = Integer.MIN_VALUE;
		int[] moves = moveGenerator.generateMoves();
		for (int i = 1; i <= moves[0]; i++) {
			if (position.move(moves[i])) {
				int score = -negamax(depth - 1);
				if (score > max) {
					bestMove = moves[i];
					max = score;
				}
				position.unmakeMove();
			}
		}
		// If no moves have worked, it's checkmate or stalemate
		if (max == Integer.MIN_VALUE) {
			return evaluation.evaluateTerminal(position, depth);
		}
		
		return bestMove;
	}
	
	public int negamax(int depth) {
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
					max = score;
				}
				position.unmakeMove();
			}
		}
		
		// If no moves have worked, it's checkmate or stalemate
		if (max == Integer.MIN_VALUE) {
			return evaluation.evaluateTerminal(position, depth);
		}
		return max;
	}
	
	public int negaMaxAlphaBeta(int alpha, int beta, int depth) {
		if (depth == 0) {
			if (position.whiteToMove) {
				return evaluation.evaluate();
			} else {
				return -evaluation.evaluate();
			}
		}
		int[] moves = moveGenerator.generateMoves();
		for (int i = 1; i < moves[0]; i++) {
			if (position.move(moves[i])) {
				int score = -negaMaxAlphaBeta(-beta, -alpha, depth - 1);
				if (score >= beta) {
					position.unmakeMove();
					return beta;
				}
				if (score > alpha) {
					bestMove[depth] = moves[i];
					alpha = score;
				}
				position.unmakeMove();
			}
		}
		return alpha;
	}

	public void setPosition(Position position) {
		this.position = position;
		moveGenerator.setPosition(position);
		evaluation.setPosition(position);
	}


}
