import java.awt.geom.Point2D;


public class MoverThread extends Thread {
	private int w, gnumBodies, numWorkers;
	private Body[] bodies;
	private double g;
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
		moveBodies();
		cb.increment();
	}
	public void moveBodies()
	{
		Point2D.Double deltaV, deltaP;
		Point2D.Double force = new Point2D.Double(0, 0);
		
		for (int i = w; i < gnumBodies; i+=numWorkers)
		{
			// Sum forces from the workers
			for (int j = 0; j < numWorkers; j++)
			{
				// Add f-value
				force.x += bodies[i].getFx(j);
				force.y += bodies[i].getFy(j);
				
				// Reset worker j's value for i
				bodies[i].setFx(j, 0);
				bodies[i].setFy(j, 0);
			}
			
			// Calculate differences
			deltaV = new Point2D.Double(force.x/bodies[i].getM(), force.y/bodies[i].getM());
			deltaP = new Point2D.Double(bodies[i].getVx()+deltaV.x/2, bodies[i].getVy() + deltaV.y/2);
			
			// Update velocity
			bodies[i].setVx(bodies[i].getVx() + deltaV.x);
			bodies[i].setVy(bodies[i].getVy() + deltaV.y);
			
			// Update position
			bodies[i].setPx(bodies[i].getPx() + deltaP.x);
			bodies[i].setPy(bodies[i].getPy() + deltaP.y);
			
			// Reset force-vector
			force.x = 0;
			force.y = 0;
		}
	}
}
