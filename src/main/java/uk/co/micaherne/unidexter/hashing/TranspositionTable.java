package uk.co.micaherne.unidexter.hashing;

public class TranspositionTable {
	
	public int size;
	public TranspositionTableEntry[] entries;
	
	public TranspositionTable(int size) {
		this.size = size;
		entries = new TranspositionTableEntry[size];
	}
	
	public void add(TranspositionTableEntry entry) {
		entries[(int) (Math.abs(entry.key) % size)] = entry;
	}
	
	public TranspositionTableEntry get(long fullKey) {
		return entries[(int) (Math.abs(fullKey) % size)];
	}
	
}
