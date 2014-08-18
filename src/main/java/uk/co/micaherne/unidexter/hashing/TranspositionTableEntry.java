package uk.co.micaherne.unidexter.hashing;

public class TranspositionTableEntry {
	
	public static final byte EXACT = 0;
	public static final byte UPPER = 1;
	public static final byte LOWER = 2;
	
	public long key;
	public long move;
	public int depth;
	public int score;
	public byte type;
	public int age;
	
	public TranspositionTableEntry(long key, long move, int depth,
			int score, byte type, int age) {
		super();
		this.key = key;
		this.move = move;
		this.depth = depth;
		this.score = score;
		this.type = type;
		this.age = age;
	}
	
	

}
