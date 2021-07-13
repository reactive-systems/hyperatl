package hyperatl.util;

public class Stopwatch {

	private long totalTime = 0;
	
	private long startTime = 0;
	private long stopTime = 0;
	
	
	public void start() {
		this.startTime = System.currentTimeMillis();
	}
	
	
	public void stop() {
		this.stopTime = System.currentTimeMillis();
		totalTime += stopTime-startTime;
	}
	
	public void reset() {
		totalTime = 0;
	}
	
	
	  //elaspsed time in milliseconds
	public long getElapsedTime() {
		return totalTime;
	}
} 
