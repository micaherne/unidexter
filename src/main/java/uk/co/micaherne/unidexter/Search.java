package uk.co.micaherne.unidexter;

public class Search {
	
	public Position position;
	public MoveGenerator moveGenerator;
	public Evaluation evaluation;
	
	public Search(Position position) {
		this.position = position;
		this.moveGenerator = new MoveGenerator(position);
		this.evaluation = new Evaluation(position);
	}
	
	public int bestMove(int ply) {
		return negamax(ply);
	}
	
	public int negamax(int ply) {
		return 0; // TODO: IMplement
	}

}
