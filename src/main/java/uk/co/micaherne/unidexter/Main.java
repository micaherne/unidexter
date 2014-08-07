package uk.co.micaherne.unidexter;

import uk.co.micaherne.unidexter.io.Engine;

public class Main implements Runnable {

	Engine engine;
	
	public Main() {
		super();
		engine = new Engine();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main e = new Main();
		//new Thread("engine").start();
		e.run();
	}

	public void run() {
		engine.startInput();
	}

}
