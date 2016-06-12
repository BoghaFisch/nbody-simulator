import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Main {

	static int gnumBodies = 120; // Number of bodies
	static int numSteps = 17000; // Number of timesteps in the simulation
	static double far = 2.1;		// Parameter far, used to know when to approximate force. Same as theta in the coursebook.
	static double G = 0.1;		// Gravitational constant
	
	static long DELAY =3;
	static boolean RANDOMIZE_INPUT = false;
	static boolean DISPLAY_GRAPHICS = false;
	static boolean printTimes = false;
	static boolean printPercentageApproxed = false;
	
	/**
	 * Reads args and initializes parameters accordingly
	 * @param args
	 */
	public static void readArgs(String[] args)
	{
		if (args.length > 0)
		{
			System.out.println("Arguments:");
			gnumBodies = Integer.parseInt(args[0]);
			System.out.println("gnumBodies: "+gnumBodies);
			if (args.length > 1)
			{
				numSteps = Integer.parseInt(args[1]);
				System.out.println("numSteps: "+numSteps);
				if (args.length > 2)
				{
					far = Double.parseDouble(args[2]);
					System.out.println("far: "+far);
				}
			}
		}
	}
	/**
	 * 
	 * @return A simulation object with requested parameters
	 */
	public static Simulation readInput()
	{
		Simulation sim = null;
		if (RANDOMIZE_INPUT)
		{
			// Randomize input
			InputRandomizer ir = new InputRandomizer();
			sim = ir.getRandomInstance(gnumBodies, numSteps, G);
		}
		else
		{
			try 
			{
				// Create bodies by reading from file
				Body[] bodies = new Body[gnumBodies];
				BufferedReader br = new BufferedReader(new FileReader("input.txt"));
				Point2D.Double p, v, f;
				double m;
				String[] components;
				String line;
				for (int i = 0; i < gnumBodies; i++)
				{
					line = br.readLine();
					components = line.split(" ");
					p = new Point2D.Double(Double.parseDouble(components[0]), Double.parseDouble(components[1]));
					m = Double.parseDouble(components[2]);					
					v = new Point2D.Double();
					f = new Point2D.Double();
					bodies[i] = new Body(p, v, f, m);
				}
				br.close();
				return new Simulation(bodies, G, numSteps, far);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return sim;
	}

	public static void main(String[] args) 
	{
		// Read arguments to initialize parameters
		readArgs(args);

		// Create simulation-instance
		Simulation sim = readInput();
		
		// Run the simulation
		sim.runSimulation();
	}

}
