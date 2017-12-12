
public class InsertionThread extends  Thread
{
	// All bodies
	private Body[] bodies;
	
	// The quadtree assigned to this InsertionThread
	private QuadTree qt;
	
	// Counterbarrier to tell when done
	private CounterBarrier cb;
	
	// Worker number
	private int wnr;
	
	InsertionThread(int wnr, Body[] bodies, QuadTree qt, CounterBarrier cb)
	{
		this.bodies = bodies;
		this.qt = qt;
		this.cb = cb;
		this.wnr = wnr;
	}
	public void run()
	{
		for (int i = 0; i < bodies.length; i++)
		{
			qt.insert(bodies[i]);
		}
		
		qt.initalizeMassCenters();
		cb.increment();
	}
}
