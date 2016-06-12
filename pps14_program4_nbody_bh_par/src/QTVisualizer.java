import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFrame;

public class QTVisualizer extends JFrame {

	private QGCanvas jc;
	private QuadTree qt;
	
	QTVisualizer(QuadTree qt, Body[] bodies)
	{
		setSize(1500, 1500);
		jc = new QGCanvas(qt, bodies);
		jc.setBackground(Color.WHITE);
		add(jc);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.qt = qt;
	}
	public void setQT(QuadTree qt)
	{
		this.qt = qt;
		jc.setQT(qt);
	}
	public void repaint()
	{
		jc.repaint();
	}
}
class QGCanvas extends Canvas
{
	private static boolean printQuadrants = true;
	private Body[] bodies;
	private QuadTree qt;
	
	QGCanvas(QuadTree qt, Body[] bodies)
	{
		super();
		this.qt = qt;
		this.bodies = bodies;
	}
	public void setQT(QuadTree qt)
	{
		this.qt = qt;
	}
	public void paint(Graphics g)
	{
		Point2D.Double position;
		int nodeDiameter, x, y, width, height;
		QuadTree current;
		QuadTree[] children;
		LinkedList<QuadTree> qtq = new LinkedList<QuadTree>();
		
		for (int i = 0; i < bodies.length; i++)
		{
			position = bodies[i].getP();
			nodeDiameter = (int) Math.round(bodies[i].getM());
			
			x = ((int) Math.round(position.x));
			y = ((int) Math.round(position.y));
			
			g.fillOval(x, y, nodeDiameter, nodeDiameter);
			
		}
		if (printQuadrants)
		{
			qtq.add(qt);
			while (qtq.size() != 0)
			{
				// Fetch current quadtree and read rectangle
				current = qtq.poll();
				x = (int) Math.round(current.getMinX());
				y = (int) Math.round(current.getMinY());
				width = (int) Math.round(current.getWidth());
				height = (int) Math.round(current.getHeight());
				
				// Draw it
				g.drawRect(x, y, width, height);
				
	//			// Draw center of mass for all inner nodes
	//			if (current.getMassCenter() != null && current.getChildren() != null)
	//			{
	//				x = (int) Math.round(current.getMassCenter().x);
	//				y = (int) Math.round(current.getMassCenter().y);
	//				g.setColor(Color.RED);
	//				g.fillOval(x, y, 4, 4);
	//				g.setColor(Color.BLACK);
	//			}
				// Fetch children
				children = current.getChildren();
				
				// Add children to queue (if there are any)
				if (children != null)
				{
					for (int i = 0; i < children.length; i++)
					{
						qtq.add(children[i]);
					}
				}
			}
		}
	}
}
