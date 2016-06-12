import java.awt.geom.Point2D;


public class ForceCalcThread extends Thread {
	
	private int w, gnumBodies, numWorkers;
	private Body[] bodies;
	private double g;
	private CounterBarrier cb;
	
	ForceCalcThread(Body[] bodies, int w, int numWorkers, int gnumBodies, double g, CounterBarrier cb)
	{
		this.w = w;
		this.gnumBodies = gnumBodies;
		this.numWorkers = numWorkers;
		this.bodies = bodies;
		this.g = g;
		this.cb = cb;
	}
	public void run()
	{
		// Calculate forces
		calculateForces();
		
		// Increment CounterBarrier (this task is done)
		cb.increment();
	}
	public void calculateForces()
	{
		double distance, magnitude;
		Point2D.Double direction;
		
		// For every %numWorker%-body, calculate forces
		for (int i = w; i < gnumBodies; i+=numWorkers)
		{
			for (int j = i+1; j < gnumBodies; j++)
			{
				// Calculate distance between bodies i and j
				distance = bodies[i].distanceTo(bodies[j]);
				
				// Set so that the bodies wont be "hurled away" when getting too close
				if (distance < 10)
				{
					distance = 10;
				}
				// Calculate magnitude
				magnitude = (g*bodies[i].getM()*bodies[j].getM())/Math.pow(distance, 2);
				
				// Calculate force-direction
				direction = new Point2D.Double(bodies[j].getP().x-bodies[i].getP().x, bodies[j].getP().y-bodies[i].getP().y);
				
				// Update forces calculated by this thread (f[w])
				bodies[i].setFx(w, bodies[i].getFx(w) + magnitude*direction.x/distance);
				bodies[j].setFx(w, bodies[j].getFx(w) - magnitude*direction.x/distance);
				bodies[i].setFy(w, bodies[i].getFy(w) + magnitude*direction.y/distance);
				bodies[j].setFy(w, bodies[j].getFy(w) - magnitude*direction.y/distance);
			}
		}
	}

}
