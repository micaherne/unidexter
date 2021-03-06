package uk.co.micaherne.unidexter.perft;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import uk.co.micaherne.unidexter.Position;
import uk.co.micaherne.unidexter.notation.NotationException;

/**
 * Compare perft results against the file supplied by roce
 * 
 * @author Michael Aherne
 *
 */
public class PerftCompare {

	public static void main(String[] args) throws IOException, NotationException {
		int maxDepth = 4;
		// FileReader fileReader = new FileReader("C:/Users/michael/git/micaherne/unidexter/roce39/perftsuite.epd");
		FileReader fileReader = new FileReader("C:/Users/vas07101/Desktop/temp/chess/roce39/perftsuite.epd");
		BufferedReader reader = new BufferedReader(fileReader);
		String line;
		int passes = 0;
		int fails = 0;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split(";");
			if (parts.length < 3) {
				continue;
			}
			String fen = parts[0].trim();
			for (int i = 1; i < parts.length; i++) {
				if (i > maxDepth) {
					break;
				}
				String entry = parts[i].trim();
				String[] entryParts = entry.split(" ");
				int perftResult = Integer.parseInt(entryParts[1]);
				Position position = Position.fromFEN(fen);
				PerftResult result = Perft.perft(position, i);
				if (perftResult == result.moveCount) {
					passes++;
					System.out.println("PASS: " + fen + ". Moves " + result.moveCount + ", depth " + i);
				} else {
					fails++;
					System.out.println("FAIL: " + fen + ". Moves " + result.moveCount + ", depth " + i);
					break;
				}
			}
		}
		
		reader.close();
		
		System.out.println("Passed: " + passes);
		System.out.println("Failed: " + fails);
	}

}
