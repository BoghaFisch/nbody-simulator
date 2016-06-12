import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Main {

	static int gnumBodies = 120; // Number of bodies. Default value 120.
	static int numSteps = 17000; // Number of timesteps in the simulation. Default value 17000.
	
	static double G = 0.1;		// Gravitational constant
	
	static long DELAY = 3;		// Delay used when printing graphics
	
	static boolean RANDOMIZE_INPUT = false;
	static boolean DISPLAY_GRAPHICS = false;
	static boolean PRINT_TIME = false;
	
	/**
	 * Reads the args and initializes simulation constants
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
			}
		}
	}
	/**
	 * 
	 * @return - A Simulation object with requested parameters (by args)
	 */
	public static Simulation readInput()
	{
		Simulation sim = null;
		
		// If RANDOMIZE_INPUT => randomize input..
		if (RANDOMIZE_INPUT)
		{
			InputRandomizer ir = new InputRandomizer();
			sim = ir.getRandomInstance(gnumBodies, numSteps, G);
		}
		// Else we assume that we have a file to read from
		else
		{
			try 
			{
				// Initialize array of bodies
				Body[] bodies = new Body[gnumBodies];
				
				// Initialize buffered filereader
				BufferedReader br = new BufferedReader(new FileReader("input.txt"));
				
				// Position, velocity and force
				Point2D.Double p, v, f;
				
				// Mass
				double m;
				
				String[] components;
				String line;
				// Create bodies by reading from file. file[0]=p.x, file[1]=p.y, file[2] = m
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
				
				// Return a simulation for the bodies read
				return new Simulation(bodies, G, numSteps);
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
		// Read arguments to initialize simulation constants
		readArgs(args);

		// Read input and get a Simulation-object back
		Simulation sim = readInput();
		
		// Run the simulation
		sim.runSimulation();
	}

}
