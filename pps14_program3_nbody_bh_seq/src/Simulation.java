import java.awt.geom.Point2D;
import java.util.ArrayList;


public class Simulation {

	private Body[] bodies;
	private int gnumBodies, numSteps;
	private double g;
	private double far;
	private QTVisualizer vis;
	private QuadTree qt;
	
	Simulation(Body[] bodies, double g, int numSteps, double far)
	{
		this.bodies = bodies;
		this.g = g;
		this.numSteps = numSteps;
		this.gnumBodies = bodies.length;
		qt = new QuadTree(null, -3000, 3000, -3000, 3000);
		this.far = far;
		
		if (Main.DISPLAY_GRAPHICS)
		{
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
		// Print percentage of times approximated
//		System.out.println("PERCENTAGE APPROXIMATED "+(1.0*QuadTree.timesApprox/(1.0*QuadTree.timesApprox+1.0*QuadTree.timesPrecise)));
		
		// Print the time taken for the simulation
		System.out.println("Nanoseconds taken for simulation: "+(System.nanoTime()-s));
	}
	public void calculateForces()
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
		if (qt.getChildren() != null)
		{
			// If qt has been initalized before, use the masscenter as middle-point this iteration to get a more equal distribution on level 1
			MassCenter center = qt.getMassCenter();
			qt = new QuadTree(null, center.getPx() - 3000, center.getPx() + 3000, center.getPy() - 3000, center.getPy() + 3000);
		}
		else
		{
			// If qt-children is null, this is the first time. Initialize A QuadTree from x between -3000 to 3000, and y from -3000 to 3000
			qt = new QuadTree(null, -3000, 3000, -3000, 3000);
		}
		
		for (int i = 0; i < gnumBodies; i++)
		{
			qt.insert(bodies[i]);
		}
		qt.initalizeMassCenters();
		
		for (int i = 0; i < bodies.length; i++)
		{
			bodies[i].setF(qt.getForceOnBody(bodies[i]));
		}
	}
	public void moveBodies()
	{
		Point2D.Double deltaV, deltaP;
		for (int i = 0; i < gnumBodies; i++)
		{
			// Calculate differences in speed and position
			deltaV = new Point2D.Double(bodies[i].getFx()/bodies[i].getM(), bodies[i].getFy()/bodies[i].getM());
			deltaP = new Point2D.Double(bodies[i].getVx() + deltaV.x/2, bodies[i].getVy() + deltaV.y/2);
			
			// Set new speed
			bodies[i].setVx(bodies[i].getVx() + deltaV.x);
			bodies[i].setVy(bodies[i].getVy() + deltaV.y);

			// Set new position
			bodies[i].setPx(bodies[i].getPx() + deltaP.x);
			bodies[i].setPy(bodies[i].getPy() + deltaP.y);
			
			// Reset force-vector
			bodies[i].setFx(0);
			bodies[i].setFy(0);
		}
	}
}
