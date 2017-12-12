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
		if (RANDOMIZE_INPUT)
		{
			InputRandomizer ir = new InputRandomizer();
			sim = ir.getRandomInstance(gnumBodies, numSteps, G);
		}
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
		readArgs(args);
		Simulation sim = readInput();
		sim.runSimulation();
	}

}
