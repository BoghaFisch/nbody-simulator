import java.awt.geom.Point2D;

/**
 * Used to be able to compare masscenters with bodies
 * @author David
 *
 */
public interface Entity {

	public void setM(double m);
	public double getM();
	public void setP(Point2D.Double p);
	public void setPx(double px);
	public void setPy(double py);
	public Point2D.Double getP();
	public double getPx();
	public double getPy();
	public double distanceTo(Entity e);

}
