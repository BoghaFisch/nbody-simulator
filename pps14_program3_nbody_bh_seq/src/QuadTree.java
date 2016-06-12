import java.awt.geom.Point2D;
import java.util.ArrayList;


public class QuadTree 
{
	public static double g = Main.G;
	public static double far = Main.far;
	
	// Number of approximations and number of precise calculations
	public static long timesApprox = 0;
	public static long timesPrecise = 0;
	
	// Quadrants northwest, northeast, southeast and southwest
	public static int NW = 0;
	public static int NE = 1;
	public static int SE = 2; 
	public static int SW = 3;
	
	private QuadTree parent; // Parent, or null if root
	
	// Min and max values for x and y, representing the borders for this quadrant
	private double minX, maxX, minY, maxY;
	
	// Quadrant width and height
	private double width, height;
	
	// True if this is an inner node, false otherwise
	private boolean isInner;
	
	// The children of this QuadTree
	private QuadTree[] children = null;
	
	// The body currently occupying this quadrant (only for leaf-nodes)
	private Body currentInhabitant = null;
	
	// The center of mass for this quadrant
	private MassCenter massCenter;
	
	QuadTree(QuadTree parent, double minX, double maxX, double minY, double maxY)
	{
		this.parent = parent;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		width = maxX-minX;
		height = maxY-minY;
		
		// Not inner when initialized (no body inserted yet)
		isInner = false;
		
		// Initialize masscenter
		massCenter = new MassCenter();
	}
	/**
	 * 
	 * @param b
	 * @return The force working on the body by this tree
	 */
	public Point2D.Double getForceOnBody(Body b)
	{
		Point2D.Double forceOnPoint = new Point2D.Double(0, 0);
		Point2D.Double direction;
		double magnitude;
		double distance = massCenter.distanceTo(b);
		double approxQuotient = width/distance;
		
		// If approxQuotient < far => approximate force my mass-center
		if (approxQuotient < far)
		{
			if (distance < 10)
			{
				distance = 10;
			}
			magnitude = (g*b.getM()*massCenter.getM())/Math.pow(distance, 2);
			direction = new Point2D.Double(massCenter.getPx()-b.getPx(), massCenter.getPy()-b.getPy());
			
			forceOnPoint.x = magnitude*direction.x/distance;
			forceOnPoint.y = magnitude*direction.y/distance;
			
			timesApprox++;
		}
		// If we are below the approx-threshold and have children, go down a level in the tree
		else if (children != null && approxQuotient > far)
		{
			Point2D.Double force;
			for (int i = 0; i < children.length; i++)
			{
				force = children[i].getForceOnBody(b);
				forceOnPoint.x += force.x;
				forceOnPoint.y += force.y;
			}
		}
		// If we are below the threshold and have no children => compute the actual force
		else if (approxQuotient > far && children == null)
		{
			if (distance < 10)
			{
				distance = 10;
			}
			magnitude = (g*b.getM()*massCenter.getM())/Math.pow(distance, 2);
			direction = new Point2D.Double(massCenter.getPx()-b.getPx(), massCenter.getPy()-b.getPy());
			
			forceOnPoint.x = magnitude*direction.x/distance;
			forceOnPoint.y = magnitude*direction.y/distance;
			timesPrecise++;
		}
		return forceOnPoint;
	}
	public MassCenter initalizeMassCenters()
	{
		if (!isInner && currentInhabitant != null)
		{
			// This is a leaf node => update masscenter with the current body in the quadrant
			massCenter.update(currentInhabitant);
		}
		else if (isInner)
		{
			// If it is an inner node, initialize masscenters for children and update this with the child-masscenters
			MassCenter childMassCenter;
			for (int i = 0; i < children.length; i++)
			{
				childMassCenter = children[i].initalizeMassCenters();
				
				// If the child has a mass, update masscenter
				if (childMassCenter.getM() > 0)
				{
					massCenter.update(childMassCenter);
				}
			}
		}
		return massCenter;
	}
	public boolean insert(Body b)
	{
		// If not within this quadrant, return false (we could not insert)
		if (!isWithinBoundries(b))
		{
			return false;
		}
		// Leaf node -> insert
		else if (!isInner && currentInhabitant == null)
		{
			currentInhabitant = b;
			b.setQuadrant(this);
			return true;
		}
		else if (children == null && currentInhabitant != null)
		{
			// Split and create children
			subdivide();
			children[getQuadrant(currentInhabitant)].insert(currentInhabitant);
			currentInhabitant = null;
		}
		children[getQuadrant(b)].insert(b);
		
		return true;
	}
	/**
	 * Creates four child-quadrants
	 */
	public void subdivide()
	{
		// Initialize child-array
		children = new QuadTree[4];
		
		// Calculate border-coordinates
		double xMid = minX + (maxX-minX)/2;
		double yMid = minY + (maxY-minY)/2;
		
		// Split up the quadrant and create children
		children[NW] = new QuadTree(this, minX, xMid, yMid, maxY);
		children[NE] = new QuadTree(this, xMid, maxX, yMid, maxY);
		children[SE] = new QuadTree(this, xMid, maxX, minY, yMid);
		children[SW] = new QuadTree(this, minX, xMid, minY, yMid);
		
		isInner = true;
	}
	/**
	 * 
	 * @param b
	 * @return The quadrant (if any) in which b belongs. If not within borders, returns -1.
	 */
	public int getQuadrant(Body b)
	{
		Point2D.Double point = b.getP();
		double xMid = minX + (maxX-minX)/2;
		double yMid = minY + (maxY-minY)/2;
		
		// Regular cases (x or y not on any border between two quadrants).
		if (point.x < xMid && point.y > yMid)
		{
			return NW;
		}
		else if (point.x > xMid && point.y > yMid)
		{
			return NE;
		}
		else if (point.x > xMid && point.y < yMid)
		{
			return SE;
		}
		else if (point.x < xMid && point.y < yMid)
		{
			return SW;
		}
		// Special cases. If the point is exactly on the border between two quadrants (not very likely with double coordinates)
		else if (point.x == xMid)
		{
			if (point.y < yMid)
			{
				return SW;
			}
			else return NW;
		}
		else if (point.y == yMid)
		{
			if (point.x < xMid)
			{
				return NW;
			}
			else return NE;
		}
		else if (!isWithinBoundries(b))
		{
			return -1;
		}
		// Shouldn't happen, since we've covered all the cases. If it does, throw exception.
		else throw new IllegalArgumentException("Unsupported coordinates for point ["+point.x+","+point.y+"]."
				+" Could not split region.");
	}
	/**
	 * 
	 * @param b
	 * @return True if Body b is within the borders of this quadrant
	 */
	public boolean isWithinBoundries(Body b)
	{
		Point2D.Double point = b.getP();
		if (point.x > minX && point.x < maxX 
				&& point.y > minY && point.y < maxY)
		{
			return true;
		}
		else return false;
	}
	/**
	 * 
	 * @param x
	 * @param y
	 * @return True if coordinates x and y are within the borders
	 */
	public boolean isWithinBoundries(double x, double y)
	{
		if (x > minX && x < maxX 
				&& y > minY && y < maxY)
		{
			return true;
		}
		else return false;
	}
	// Get and set methods below
	public QuadTree getParent()
	{
		return parent;
	}
	public double getMinX()
	{
		return minX;
	}
	public double getMaxX()
	{
		return maxX;
	}
	public double getMinY()
	{
		return minY;
	}
	public double getMaxY()
	{
		return maxY;
	}
	public double getWidth()
	{
		return width;
	}
	public double getHeight()
	{
		return height;
	}
	public QuadTree[] getChildren()
	{
		return children;
	}
	public MassCenter getMassCenter()
	{
		return massCenter;
	}
}
