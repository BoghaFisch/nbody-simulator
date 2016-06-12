
public class ForceCalcThread extends Thread {

	// Worker number and number of threads
	private int wnr, numThreads;
	
	// The array of bodies
	private Body[] bodies;
	
	// The root-quadtree
	private QuadTree qt;
	
	// Counterbarrier used to signal when done
	private CounterBarrier cb;
	
	ForceCalcThread(int wnr, Body[] bodies, QuadTree qt, int numThreads, CounterBarrier cb)
	{
		this.wnr = wnr;
		this.bodies = bodies;
		this.qt = qt;
		this.numThreads = numThreads;
		this.cb = cb;
	}
	public void run()
	{
		// For every %numThreads% body, calculate force
		for (int i = wnr; i < bodies.length; i+=numThreads)
		{
			bodies[i].setF(qt.getForceOnBody(bodies[i]));
		}
		// This thread is done => increment counterbarrier
		cb.increment();
	}
}
