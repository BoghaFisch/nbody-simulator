import java.awt.geom.Point2D;


public class Simulation {

	private Body[] bodies;
	private int gnumBodies, numSteps;
	private double g;
	private Visualizer vis = null;
	
	Simulation(Body[] bodies, double g, int numSteps)
	{
		this.bodies = bodies;
		this.g = g;
		this.numSteps = numSteps;
		this.gnumBodies = bodies.length;
		
		// If graphics requested, create a visualizer object used for showing graphics
		if (Main.DISPLAY_GRAPHICS)
		{
			vis = new Visualizer(bodies);
		}
	}
	public void runSimulation()
	{
		long startTime = System.nanoTime();
		for (int i = 0; i < numSteps; i++)
		{
			// Calculate the forces on each body
			calculateForces();
			
			// Move the bodies
			moveBodies();
			
			// Display graphics (if requested)
			if (Main.DISPLAY_GRAPHICS)
			{
				vis.repaint();
				try 
				{
					// Delay, to prevent graphics from going to fast
					Thread.sleep(Main.DELAY);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
		// Print the time taken for the simulation
		System.out.println("Nanoseconds taken for simulation: "+(System.nanoTime()-startTime));
	}
	public void calculateForces()
	{
		double distance, magnitude;
		Point2D.Double direction;
		
		// For each body to each body, calculate the force impact they have on each other
		for (int i = 0; i < gnumBodies; i++)
		{
			for (int j = i+1; j < gnumBodies; j++)
			{
				
				distance = bodies[i].distanceTo(bodies[j]);
				
				// Set so that the bodies wont be "hurled away" when getting too close
				if (distance < 10)
				{
					distance = 10;
				}
				// Calculate magnitude
				magnitude = (g*bodies[i].getM()*bodies[j].getM())/Math.pow(distance, 2);
				
				// Calculate force direction
				direction = new Point2D.Double(bodies[j].getP().x-bodies[i].getP().x, bodies[j].getP().y-bodies[i].getP().y);
				
				// Update forces
				bodies[i].setFx(bodies[i].getFx() + magnitude*direction.x/distance);
				bodies[j].setFx(bodies[j].getFx() - magnitude*direction.x/distance);
				bodies[i].setFy(bodies[i].getFy() + magnitude*direction.y/distance);
				bodies[j].setFy(bodies[j].getFy() - magnitude*direction.y/distance);
			}
		}
	}
	public void moveBodies()
	{
		// Differences in speed and position for this "tick"
		Point2D.Double deltaV, deltaP;
		
		// For each body
		for (int i = 0; i < gnumBodies; i++)
		{
			// Calculate differences in speed and position
			deltaV = new Point2D.Double(bodies[i].getFx()/bodies[i].getM(), bodies[i].getFy()/bodies[i].getM());
			deltaP = new Point2D.Double(bodies[i].getVx()+deltaV.x/2, bodies[i].getVy()+deltaV.y/2);
			
			// Set new speed
			bodies[i].setVx(bodies[i].getVx()+deltaV.x);
			bodies[i].setVy(bodies[i].getVy()+deltaV.y);

			// Set new position
			bodies[i].setPx(bodies[i].getPx()+deltaP.x);
			bodies[i].setPy(bodies[i].getPy()+deltaP.y);
			
			// Reset force-vector
			bodies[i].resetF();		
		}
	}
}
