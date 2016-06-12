import java.awt.geom.Point2D;
import java.util.Random;


public class InputRandomizer {

	InputRandomizer()
	{
		
	}
	public Simulation getRandomInstance(int gnumBodies, int numSteps, int numWorkers, double g)
	{
		Simulation sim;
		Body[] bodies = new Body[gnumBodies];
		Point2D.Double p;
		Point2D.Double v;
		Point2D.Double[] f;
		double m;
		
		Random r = new Random();
		for (int i = 0; i < gnumBodies; i++)
		{
			p = new Point2D.Double(500*r.nextDouble(), 500*r.nextDouble());
			v = new Point2D.Double(0, 0);
			f = new Point2D.Double[numWorkers];
			for (int j = 0; j < numWorkers; j++)
			{
				f[j] = new Point2D.Double(0, 0);
			}
			m = 10*r.nextDouble();
			bodies[i] = new Body(p, v, f, m);
		}
		sim = new Simulation(bodies, g, numSteps, numWorkers);
		return sim;
	}
}
