import java.awt.geom.Point2D;


public class Body {

	// Force
	private Point2D.Double[] f;
	
	// Position and velocity
	private Point2D.Double p, v;
	
	// Mass
	private double m;
	
	Body(Point2D.Double p, Point2D.Double v, Point2D.Double[] f, double m)
	{
		this.p = p;
		this.v = v;
		this.f = f;
		this.m = m;
	}
	/**
	 * 
	 * @param otherBody
	 * @return Distance from this body to otherBody
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
	public void setF(int wnr, Point2D.Double force)
	{
		f[wnr] = force;
	}
	public void setFx(int wnr, double fx)
	{
		f[wnr].x = fx;
	}
	public void setFy(int wnr, double fy)
	{
		f[wnr].y = fy;
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
	public Point2D.Double getF(int wnr)
	{
		return f[wnr];
	}
	public double getFx(int wnr)
	{
		return f[wnr].x;
	}
	public double getFy(int wnr)
	{
		return f[wnr].y;
	}
	public void resetF()
	{
		for (int i = 0; i < f.length; i++)
		{
			f[i].x = 0;
			f[i].y = 0;
		}
	}
	public double getM()
	{
		return m;
	}
}
