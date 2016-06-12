import java.awt.geom.Point2D;


public class MassCenter implements Entity {

	private Point2D.Double p;
	private double m;
	
	MassCenter(Point2D.Double p, double m)
	{
		this.p = new Point2D.Double(p.x, p.y);
		this.m = m;
	}
	/**
	 * If initialized without arguments, position is [0,0] and mass is 0
	 */
	MassCenter()
	{
		p = new Point2D.Double();
		m = 0;
	}
	public void setP(Point2D.Double p)
	{
		this.p = new Point2D.Double(p.x, p.y);
	}
	public void setPx(double px)
	{
		p.x = px;
	}
	public void setPy(double py)
	{
		p.y = py;
	}
	public Point2D.Double getP()
	{
		return p;
	}
	public double getPx()
	{
		return p.x;
	}
	public double getPy()
	{
		return p.y;
	}
	public void setM(double m)
	{
		this.m = m;
	}
	public double getM()
	{
		return m;
	}
	public double distanceTo(Entity otherEntity)
	{
		return p.distance(otherEntity.getP());
	}
	/**
	 * Recalculates this masscenter when adding an entity e to the set
	 * @param e
	 */
	public void update(Entity e)
	{	
		// Update x and y coordinates
		p.x = (p.x*m + e.getPx()*e.getM())/(m+e.getM());
		p.y = (p.y*m + e.getPy()*e.getM())/(m+e.getM());
		
		// Update the mass
		m += e.getM();
	}
	public void reset()
	{
		p = new Point2D.Double();
		m = 0;
	}
}
