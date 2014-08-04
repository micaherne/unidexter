package uk.co.micaherne.unidexter;


public class CodeGen {
	
	public static void main(String[] args) {
		CodeGen codeGen = new CodeGen();
		codeGen.generateCentreSquares();
	}
	
	public void generateSquares() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				StringBuffer file = new StringBuffer("public static final int ");
				file.append((char)('A' + j));
				file.append(i + 1);
				file.append(" = ");
				file.append(i * 8 + j);
				file.append(";");
				System.out.println(file.toString());
			}
		}
	}
	
	public void generateLines() {
		long fileA = 1L;
		long rank1 = 1L;
		for (int i = 0; i < 7; i++) {
			fileA |= (fileA << 8);
			rank1 |= (rank1 << 1);
		}
		
		String fileOutput[] = new String[8];
		String rankOutput[] = new String[8];
		
		for (int i = 0; i < 8; i++) {
			long file = fileA << i;
			long rank = rank1 << (8 * i);
			char fileName = (char) ('A' + i);
			String rankName = String.valueOf(i + 1);
			fileOutput[i] = "public static final long FILE_" + fileName + " = " + file + "L;";
			rankOutput[i] = "public static final long RANK_" + rankName + " = " + rank + "L;";
		}
		
		for (int i = 0; i < 8; i++) {
			System.out.println(fileOutput[i]);
		}
		
		for (int i = 0; i < 8; i++) {
			System.out.println(rankOutput[i]);
		}
	}
	
	public void generateCentreSquares() {
		long centre2x2 = 1L << 27 | 1L << 28;
		centre2x2 |= centre2x2 << 8;
		long centre4x4 = 1L << 18 | 1L << 19 | 1L << 20 | 1L << 21;
		centre4x4 |= centre4x4 << 8;
		centre4x4 |= centre4x4 << 8;
		centre4x4 |= centre4x4 << 8;
		System.out.println("public static final long CENTRE2X2 = 0x" + Long.toHexString(centre2x2) + "L;");
		System.out.println("public static final long CENTRE4X4 = 0x" + Long.toHexString(centre4x4) + "L;");
	}

}
