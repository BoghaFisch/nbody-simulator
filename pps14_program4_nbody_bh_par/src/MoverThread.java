import java.awt.geom.Point2D;


public class MoverThread extends Thread 
{
	// Worker number, number of bodies and number of workers
	private int w, gnumBodies, numWorkers;
	
	// Array of all bodies
	private Body[] bodies;
	
	// Gravitational constant
	private double g;
	
	// Counterbarrier to signal when done
	private CounterBarrier cb;
	
	MoverThread(Body[] bodies, int w, int gnumBodies, int numWorkers, double g, CounterBarrier cb)
	{
		this.bodies = bodies;
		this.w = w;
		this.gnumBodies = gnumBodies;
		this.g = g;
		this.numWorkers = numWorkers;
		this.cb = cb;
	}
	public void run()
	{
		// Move the bodies
		moveBodies();
		// Done, increment counterbarrier
		cb.increment();
	}
	public void moveBodies()
	{
		Point2D.Double deltaV, deltaP;
		Point2D.Double force = new Point2D.Double(0, 0);
		for (int i = w; i < gnumBodies; i+=numWorkers)
		{
			force.x = bodies[i].getFx();
			force.y = bodies[i].getFy();
			
			// Calculate differences
			deltaV = new Point2D.Double(force.x/bodies[i].getM(), force.y/bodies[i].getM());
			deltaP = new Point2D.Double(bodies[i].getVx()+deltaV.x/2, bodies[i].getVy() + deltaV.y/2);
			
			// Update velocity
			bodies[i].setVx(bodies[i].getVx() + deltaV.x);
			bodies[i].setVy(bodies[i].getVy() + deltaV.y);
			
			// Update position
			bodies[i].setPx(bodies[i].getPx() + deltaP.x);
			bodies[i].setPy(bodies[i].getPy() + deltaP.y);
		}
	}
}
