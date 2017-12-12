import java.awt.geom.Point2D;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Simulation 
{
	private Body[] bodies;
	private int gnumBodies, numSteps, numWorkers;
	private double g;
	private Visualizer vis = null;
	
	Simulation(Body[] bodies, double g, int numSteps, int numWorkers)
	{
		this.bodies = bodies;
		this.g = g;
		this.numSteps = numSteps;
		this.gnumBodies = bodies.length;
		this.numWorkers = numWorkers;
		
		if (Main.DISPLAY_GRAPHICS)
		{
			vis = new Visualizer(bodies);
		}
	}
	public void runSimulation()
	{
		long s = System.nanoTime();
		ExecutorService e = Executors.newFixedThreadPool(numWorkers);
		
		for (int t = 0; t < numSteps; t++)
		{
			calculateForces(e);
			moveBodies(e);
			
			if (Main.DISPLAY_GRAPHICS)
			{
				vis.repaint();
				try 
				{
					Thread.sleep(Main.DELAY);
				} 
				catch (InterruptedException e1) 
				{
					e1.printStackTrace();
				}
			}
		}
		e.shutdown();
		System.out.println("Nanoseconds taken for simulation "+(System.nanoTime()-s));
	}
	/**
	 * Calculates the forces on all bodies
	 * @param e - Threadpool
	 */
	public void calculateForces(ExecutorService e)
	{
		CounterBarrier cb = new CounterBarrier(numWorkers);
		for (int i = 0; i < numWorkers; i++)
		{
			e.submit(new ForceCalcThread(bodies, i, numWorkers, gnumBodies, g, cb));
		}
		
		while (!cb.isDone())
		{
			Thread.yield();
		}
	}
	/**
	 * Moves the bodies by using the forces stored in them
	 * @param e - Threadpool
	 */
	public void moveBodies(ExecutorService e)
	{
		// CounterBarrier used for preventing main thread to continue
		CounterBarrier cb = new CounterBarrier(numWorkers);
		
		for (int i = 0; i < numWorkers; i++)
		{
			e.submit(new MoverThread(bodies, i, gnumBodies, numWorkers, g, cb));
		}
		
		while (!cb.isDone())
		{
			Thread.yield();
		}
	}
}
