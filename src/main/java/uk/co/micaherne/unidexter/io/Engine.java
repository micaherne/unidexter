package uk.co.micaherne.unidexter.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** 
 * The main class that reads the input stream and manages the other classes.
 * 
 * @author Michael Aherne
 *
 */
public class Engine {
	
	BufferedReader in;
	ChessProtocol protocol;

	public Engine() {
		in = new BufferedReader(new InputStreamReader(System.in));
		// protocol = new UCI();
	}
	
	public void startInput() {
		try {
			String input = in.readLine();
			while(!"quit".equals(input)) {
				if ("uci".equals(input)) {
					protocol = new UCI();
				}
				if ("xboard".equals(input)) {
					System.out.println("xboard protocol not supported");
					continue;
				}
				if(input != null) {
					protocol.doInput(input);
				}
				Thread.sleep(50);
				input = in.readLine();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UCIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
