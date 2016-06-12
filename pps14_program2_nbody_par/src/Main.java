import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Main {

	static int gnumBodies = 120; // Number of bodies
	static int numSteps = 17000; // Number of timesteps in the simulation
	static int numWorkers = 2;	// Number of workers to be used
	
	static double G = 0.1;		// Gravitational constant
	
	static long DELAY = 3;
	static boolean RANDOMIZE_INPUT = false;
	static boolean DISPLAY_GRAPHICS = false;
	static boolean PRINT_TIME = false;
	
	/**
	 * Reads the arguments, and initializes simulation parameters accordingly
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
					numWorkers = Integer.parseInt(args[2]);
					System.out.println("numWorkers: "+numWorkers);
				}
			}
		}
	}
	/**
	 * 
	 * @return A Simulation-object with requested parameters
	 */
	public static Simulation readInput()
	{
		Simulation sim = null;
		if (RANDOMIZE_INPUT)
		{
			// Randomize a simulation-instance
			InputRandomizer ir = new InputRandomizer();
			sim = ir.getRandomInstance(gnumBodies, numSteps, numWorkers, G);
		}
		else
		{
			// Read from file
			try 
			{
				// Initialize Body-array
				Body[] bodies = new Body[gnumBodies];
				
				// Initialize buffered reader from file
				BufferedReader br = new BufferedReader(new FileReader("input.txt"));
				
				// Position and velocity
				Point2D.Double p, v;
				
				// Force. An array in which each worker updates the position matching its worker-id
				Point2D.Double[] f;
				
				// Mass
				double m;
				
				String[] components;
				String line;
				// Create bodies by reading from file. file[0]=p.x, file[1]=p.y, file[2] = m
				for (int i = 0; i < gnumBodies; i++)
				{
					f = new Point2D.Double[numWorkers];
					line = br.readLine();
					components = line.split(" ");
					p = new Point2D.Double(Double.parseDouble(components[0]), Double.parseDouble(components[1]));
					m = Double.parseDouble(components[2]);					
					v = new Point2D.Double();
					for (int j = 0; j < numWorkers; j++)
						f[j] = new Point2D.Double();
					bodies[i] = new Body(p, v, f, m);
				}
				// Done reading from file => close reader
				br.close();
				
				// Return a new simulation instance
				return new Simulation(bodies, G, numSteps, numWorkers);
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
		// Read args (initialize parameters)
		readArgs(args);

		// Get a simulation object with current parameters
		Simulation sim = readInput();
		
		// Run simulation
		sim.runSimulation();
	}

}
