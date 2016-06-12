import java.awt.geom.Point2D;
import java.util.ArrayList;


public class QuadTree 
{
	public static double g = Main.G;
	public static double far = Main.far;
	
	// Northwest, northeast, southeast, southwest (used to refer to children
	public static int NW = 0;
	public static int NE = 1;
	public static int SE = 2; 
	public static int SW = 3;
	
	private QuadTree parent; // Parent, or null if root
	
	// The borders for this quadrant
	private double minX, maxX, minY, maxY;
	
	// Quadrant width and height
	private double width, height;
	
	// True if this is an inner node.
	private boolean isInner;
	
	// Children for this node. null if leaf
	private QuadTree[] children = null;
	
	// If this is a leafnode, currentInhabitant may hold a body. null if inner node or no bodies in this quadrant
	private Body currentInhabitant = null;
	
	// The masscenter of this quadrant
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
		
		// Leaf-node when initialized
		isInner = false;
		
		// Masscenter initialized to [0,0] with mass 0
		massCenter = new MassCenter();
	}
	/**
	 * 
	 * @param b
	 * @return The force working on Body b from this quadrant
	 */
	public Point2D.Double getForceOnBody(Body b)
	{
		Point2D.Double forceOnPoint = new Point2D.Double(0, 0);
		Point2D.Double direction;
		double magnitude;
		double distance = massCenter.distanceTo(b);
		double approxQuotient = width/distance;
		
		// If the approxQuotitient < far, approximate force by using the masscenter of this quadtree
		if (approxQuotient < far)
		{
			// Prevent bodies from being hurled away if getting to close to eachother my setting a minimum-distance to be used
			if (distance < 10)
			{
				distance = 10;
			}
			// Calculate magnitude and direction
			magnitude = (g*b.getM()*massCenter.getM())/Math.pow(distance, 2);
			direction = new Point2D.Double(massCenter.getPx()-b.getPx(), massCenter.getPy()-b.getPy());
			
			// Set new force on point
			forceOnPoint.x = magnitude*direction.x/distance;
			forceOnPoint.y = magnitude*direction.y/distance;
		}
		// If approxQuotitient > far and this is an inner node, call getForceOnBody on the children
		else if (children != null && approxQuotient > far)
		{
			Point2D.Double force;
			
			// Get the force working on Body b from each child
			for (int i = 0; i < children.length; i++)
			{
				force = children[i].getForceOnBody(b);
				forceOnPoint.x += force.x;
				forceOnPoint.y += force.y;
			}
		}
		// If approxQuotitient > far and this is a leaf-node, calculate actual force
		else if (approxQuotient > far && children == null)
		{
			// Prevent bodies from being hurled away if getting to close to eachother my setting a minimum-distance to be used
			if (distance < 10)
			{
				distance = 10;
			}
			// Calculate magnitude and direction
			magnitude = (g*b.getM()*massCenter.getM())/Math.pow(distance, 2);
			direction = new Point2D.Double(massCenter.getPx()-b.getPx(), massCenter.getPy()-b.getPy());
			
			// Update force on b
			forceOnPoint.x = magnitude*direction.x/distance;
			forceOnPoint.y = magnitude*direction.y/distance;
		}
		return forceOnPoint;
	}
	/**
	 * Initializes the masscenters of the nodes in this tree
	 * 
	 * @return The masscenter of this quadtree
	 */
	public MassCenter initalizeMassCenters()
	{
		// If this is a leafnode with a Body in it, masscenter is in that body
		if (!isInner && currentInhabitant != null)
		{
			massCenter.update(currentInhabitant);
		}
		// If this is an inner node, update with child-masscenters
		else if (isInner)
		{
			MassCenter childMassCenter;
			for (int i = 0; i < children.length; i++)
			{
				// Initialize masscenters for each child
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
	/**
	 * @param depth Go only this deep when initializing from child-masscenters
	 * 
	 */
	public void initializeMassCenters(int depth)
	{
		if (depth > 0)
		{
			initializeMassCenters(depth-1);
		}
		for (int i = 0; i < children.length; i++)
		{
			massCenter.update(children[i].getMassCenter());
		}
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
		// Init child-array
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
	 * @return True if Body b is within this quadrant. False if it is not.
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
	public boolean isWithinBoundries(double x, double y)
	{
		if (x > minX && x < maxX 
				&& y > minY && y < maxY)
		{
			return true;
		}
		else return false;
	}
	/**
	 * Update the masscenter of parent, by passing the masscenter of this quadtree
	 * @return
	 */
	public boolean updateParentMassCenter()
	{
		if (parent == null)
		{
			return false;
		}
		else
		{
			parent.getMassCenter().update(massCenter);
			return true;
		}
	}
	public boolean updateWithChildMassCenters()
	{
		if (children == null)
		{
			return false;
		}
		else
		{
			for (int i = 0; i < children.length; i++)
			{
				massCenter.update(children[i].getMassCenter());
			}
			return true;
		}
	}
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
	public void printDimensions()
	{
		System.out.println("minX "+minX+" maxX "+maxX+" minY "+minY+" maxY "+maxY);
	}
}
