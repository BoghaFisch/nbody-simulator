
import javax.swing.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Visualizer extends JFrame {

	private GCanvas jc;
	Visualizer(Body[] bodies)
	{
		setSize(1000, 1000);
		jc = new GCanvas(bodies);
		jc.setBackground(Color.WHITE);
		add(jc);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void repaint()
	{
		jc.repaint();
	}
}
class GCanvas extends Canvas
{
	private Body[] bodies;
	
	GCanvas(Body[] bodies)
	{
		super();
		this.bodies = bodies;
	}
	public void paint(Graphics g)
	{
		Point2D.Double position;
		int nodeDiameter;
		
		for (int i = 0; i < bodies.length; i++)
		{
			position = bodies[i].getP(); 
			nodeDiameter = (int) Math.round(bodies[i].getM());
			
			int x = ((int) Math.round(position.x));
			int y = ((int) Math.round(position.y));
			
			g.fillOval(x, y, nodeDiameter, nodeDiameter);
			
		}
	}
}