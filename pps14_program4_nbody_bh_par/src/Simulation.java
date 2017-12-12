import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Simulation {

	private Body[] bodies;
	private int gnumBodies, numSteps;
	private double g;
	private double far;
	private int numWorkers, numSplits;
	private QTVisualizer vis;
	private QuadTree qt;
	private ExecutorService e;
	
	Simulation(Body[] bodies, double g, int numSteps, double far, int numWorkers)
	{
		this.bodies = bodies;
		this.g = g;
		this.numSteps = numSteps;
		this.gnumBodies = bodies.length;
		qt = new QuadTree(null, -3000, 3000, -3000, 3000);
		this.far = far;
		this.numWorkers = numWorkers;
		numSplits = calcNumSplits();
		e = Executors.newFixedThreadPool(numWorkers);
		
		if (Main.DISPLAY_GRAPHICS)
		{
			// Display graphics
			vis = new QTVisualizer(qt, bodies);
		}
	}
	public void runSimulation()
	{
		long s = System.nanoTime();
		for (int i = 0; i < numSteps; i++)
		{
			calculateForces();
			moveBodies();
		}
		
		e.shutdown();
		System.out.println("Nanoseconds taken for simulation: "+(System.nanoTime()-s));
	}
	public void calculateForces()
	{
		constructQuadTree();
		displayGraphics();
		
		CounterBarrier cb = new CounterBarrier(numWorkers);
		for (int i = 0; i < numWorkers; i++)
		{
			e.submit(new ForceCalcThread(i, bodies, qt, numWorkers, cb));
		}
		// Main-thread waits until all force-calculations are done
		while (!cb.isDone())
		{
			Thread.yield();
		}
	}
	public void moveBodies()
	{
		CounterBarrier cb = new CounterBarrier(numWorkers);
		for (int i = 0; i < numWorkers; i++)
		{
			// Submit numWorkers mover-tasks to the threadpool
			e.submit(new MoverThread(bodies, i, gnumBodies, numWorkers, g, cb));
		}
		// Wait until all bodies have been moved
		while (!cb.isDone())
		{
			Thread.yield();
		}
	}
	public void constructQuadTree()
	{
		if (qt.getChildren() != null)
		{
			// If qt has been initalized before, use the masscenter as middle-point this iteration to get a more equal distribution on level 1
			MassCenter center = qt.getMassCenter();
			qt = new QuadTree(null, center.getPx() - 3000, center.getPx() + 3000, center.getPy() - 3000, center.getPy() + 3000);
		}
		else
		{
			// If this is the first timestep, initialize the quadtree root to reach from [x,y]=-[3000,-3000] to [x,y]=[3000,3000]
			qt = new QuadTree(null, -3000, 3000, -3000, 3000);
		}
		// Insert bodies to the quadtree
		insertBodies();
	}
	public void insertBodies()
	{
		LinkedList<QuadTree> workerQuadrants = getWorkerQuadrants();
		CounterBarrier cb = new CounterBarrier(workerQuadrants.size());
		int counter = 0;
		
		for (QuadTree wq : workerQuadrants)
		{
			// Create an InsertionThread for each quadrant
			e.submit(new InsertionThread(counter, bodies, wq, cb));
			counter++;
		}
		// Wait until all bodies have been inserted
		while (!cb.isDone())
		{
			Thread.yield();
		}
		// Initialize MassCenters for the levels above the current quadrant
		qt.initializeMassCenters(numSplits-1);
	}
	/**
	 * Get the required amount of splits that has to be made in order for each
	 * worker to get at least one QuadTree each 
	 */
	public int calcNumSplits()
	{
		int splits = 0;
		while (Math.pow(4, splits) < numWorkers)
		{
			splits++;
		}
		return splits;
	}
	/**
	 * Split as many times as needed for each worker to have at least one inner node each.
	 * @return
	 */
	public LinkedList<QuadTree> getWorkerQuadrants()
	{
		int splits = 0;
		QuadTree root;
		LinkedList<QuadTree> qTrees = new LinkedList<QuadTree>();
		qTrees.add(qt);
		while (splits < numSplits)
		{
			for (int i = 0; i < Math.pow(4, splits); i++)
			{
				root = qTrees.poll();
				root.subdivide();
				for (int j = 0; j < root.getChildren().length; j++)
				{
					qTrees.add(root.getChildren()[j]);
				}
			}
			splits++;
		}
		return qTrees;
	}
	/**
	 * Displays graphics if the parameter DISPLAY_GRAPHICS in main is set to true. Else does nothing.
	 */
	public void displayGraphics()
	{
		if (Main.DISPLAY_GRAPHICS)
		{
			try 
			{
				Thread.sleep(Main.DELAY);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			vis.setQT(qt);
			vis.repaint();
		}
	}
}
