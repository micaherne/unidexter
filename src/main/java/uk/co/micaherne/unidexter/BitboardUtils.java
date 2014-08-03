package uk.co.micaherne.unidexter;

public class BitboardUtils {
	
	public static String toString(long bitboard) {
		StringBuffer result = new StringBuffer();
		for (int i = 56; i >= 0; i -= 8) {
			result.append(new StringBuilder(String.format("%08d", Integer.parseInt(Integer.toBinaryString((int) ((bitboard >> i) & 255))))).reverse());
			result.append("\n");
		}
		return result.toString();
	}

}
