import java.awt.geom.Point2D;


public class Body {

	// Position-, velocity- and force-states of this body
	private Point2D.Double p, v, f;
	
	// The mass of this body
	private double m;
	
	Body(Point2D.Double p, Point2D.Double v, Point2D.Double f, double m)
	{
		this.p = p;
		this.v = v;
		this.f = f;
		this.m = m;
	}
	/**
	 * 
	 * @param otherBody - The body to calculate the distance to
	 * @return The distance from this point to otherBody
	 */
	public double distanceTo(Body otherBody)
	{
		return (p.distance(otherBody.getP()));
	}
	public void setP(Point2D.Double p)
	{
		this.p = p;
	}
	public void setPx(double px)
	{
		p.x = px;
	}
	public void setPy(double py)
	{
		p.y = py;
	}
	public void setV(Point2D.Double v)
	{
		this.v = v;
	}
	public void setVx(double vx)
	{
		v.x = vx;
	}
	public void setVy(double vy)
	{
		v.y = vy;
	}
	public void setF(Point2D.Double f)
	{
		this.f = f;
	}
	public void setFx(double fx)
	{
		f.x = fx;
	}
	public void setFy(double fy)
	{
		f.y = fy;
	}
	public void setM(double m)
	{
		this.m = m;
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
	public Point2D.Double getV()
	{
		return v;
	}
	public double getVx()
	{
		return v.x;
	}
	public double getVy()
	{
		return v.y;
	}
	public Point2D.Double getF()
	{
		return f;
	}
	public double getFx()
	{
		return f.x;
	}
	public double getFy()
	{
		return f.y;
	}
	public void resetF()
	{
		f = new Point2D.Double(0, 0);
	}
	public double getM()
	{
		return m;
	}
}
