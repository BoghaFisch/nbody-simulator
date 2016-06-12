import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Main {

	static int gnumBodies = 120; // Number of bodies
	static int numSteps = 17000; // Number of timesteps in the simulation
	static int numWorkers = 8;	// Number of workers to use
	static double far = 2.1;		// Parameter for knowing when to approximate. Theta in the coursebook.
	static double G = 0.1;
	
	static long DELAY =4;
	static boolean RANDOMIZE_INPUT = false;
	static boolean DISPLAY_GRAPHICS = true;
	static boolean printTimes = false;
	static boolean printPercentageApproxed = false;
	/**
	 * Initialize parameters from args
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
					if (args.length > 3)
					{
						numWorkers = Integer.parseInt(args[3]);
						System.out.println("numWorkers: "+numWorkers);
					}
				}
			}
		}
	}
	/**
	 * 
	 * @return A Simulation-instance generated from current parameters
	 */
	public static Simulation readInput()
	{
		Simulation sim = null;
		if (RANDOMIZE_INPUT)
		{
			// Randomize simulation
			InputRandomizer ir = new InputRandomizer();
			sim = ir.getRandomInstance(gnumBodies, numSteps, G);
		}
		else
		{
			// Read from file
			try 
			{
				// Initialize Body[]
				Body[] bodies = new Body[gnumBodies];
				
				// Initialize buffered filereader
				BufferedReader br = new BufferedReader(new FileReader("input.txt"));
				
				// Position, velocity, force
				Point2D.Double p, v, f;
				
				// mass
				double m;
				
				// Read from file and create bodies. file[0] = p.x, file[1] = p.y, file[2] = m
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
				
				// Return a simulation-object with the bodies read from the file
				return new Simulation(bodies, G, numSteps, far, numWorkers);
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
		// Initialize parameters from args
		readArgs(args);

		// Get a simulation from current parameters
		Simulation sim = readInput();
		
		// Run the simulation
		sim.runSimulation();
	}

}
