package frontEnd;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GlassPanel extends JPanel
{
	private ArrayList<GlassImage> glassImages = new ArrayList<GlassImage>();
	
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for (GlassImage glassImage : this.glassImages)
		{
			g.drawImage(glassImage.image, glassImage.x, glassImage.y, this);
		}
		this.glassImages = new ArrayList<GlassImage>();
	}
	
	public void addGlassImage(BufferedImage image, int x, int y)
	{
		GlassImage glassImage = new GlassImage(image, x, y);
		this.glassImages.add(glassImage);
	}
	
	private class GlassImage
	{
		public BufferedImage image;
		public int x;
		public int y;
		
		public GlassImage(BufferedImage image, int x, int y)
		{
			this.image = image;
			this.x = x;
			this.y = y;
		}
	}
}
