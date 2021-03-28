package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import backend.Mode;
import backend.Scenario;
import frontEnd.RoomsWindow;
import json.RestrictedJson;
import json.restrictions.ImageRestriction;
import json.restrictions.MapRestriction;
import json.restrictions.ScenarioRestriction;

public class Main
{
	public static Main main;
	private static Random random = new Random();
	private static HashMap<String, ImageIcon> icons = new HashMap<String, ImageIcon>();
	private static HashMap<String, ImageIcon> disabledIcons = new HashMap<String, ImageIcon>();
	private static HashMap<String, HashMap<String, ImageIcon>> combinedIcons = new HashMap<String, HashMap<String, ImageIcon>>();
	private static HashMap<String, HashMap<String, ImageIcon>> combinedDisabledIcons = new HashMap<String, HashMap<String, ImageIcon>>();
	
	public static Dimension findScreenCentre()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) screenSize.getWidth()/2;
		int height = (int) screenSize.getHeight()/2;
		return new Dimension(width, height);
	}
	
	public static int getRndm(int range)
	{
		return Main.random.nextInt(range);
	}
	
	public static JsonObject openJsonFile(Component parent)
	{
		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.showOpenDialog(parent);
		File file = fileChooser.getSelectedFile();
		try {
			FileReader fileReader = new FileReader(file);
			JsonReader jsonReader = Json.createReader(fileReader);
			return jsonReader.readObject();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private static ImageIcon loadImageIcon(HashMap<String, ImageIcon> iconMap, String filename, boolean disabled) throws Exception
	{	
		if (iconMap.containsKey(filename))
			return iconMap.get(filename);
		
		BufferedImage baseImg = Main.loadImageFromFile(filename);
		
		if (disabled)
			Main.applyDisabledEffect(baseImg);
		
	    ImageIcon imageIcon = new ImageIcon(baseImg);
	    iconMap.put(filename, imageIcon);
	    return imageIcon;
	}
	
	private static BufferedImage loadImageFromFile(String filename) throws Exception
	{
		try
		{
			return ImageIO.read(new File(filename));
		}
		catch (IOException e)
		{
			throw new Exception("Can't load image with filename: " + filename, e);
		}
	}
	
	private static ImageIcon loadCombinedImageIcon(HashMap<String, HashMap<String, ImageIcon>> iconMap, String baseFilename, String filename, 
			boolean disabled) throws Exception
	{		
		if (iconMap.containsKey(baseFilename))
		{
			HashMap<String, ImageIcon> innerIcons = iconMap.get(baseFilename);
			if (innerIcons.containsKey(filename))
			{
				return innerIcons.get(filename);
			}
		}
		
		BufferedImage baseImg = Main.loadImageFromFile(baseFilename);
		BufferedImage img = Main.loadImageFromFile(filename);
		
		Graphics2D graphics = baseImg.createGraphics();
		graphics.drawImage(img, 0, 0, null);
		
		if (disabled)
			Main.applyDisabledEffect(baseImg);
			
		ImageIcon imageIcon = new ImageIcon(baseImg);
		
		if (!iconMap.containsKey(baseFilename))
			iconMap.put(baseFilename, new HashMap<String, ImageIcon>());
		
		HashMap<String, ImageIcon> innerIcons = iconMap.get(baseFilename);
		innerIcons.put(filename, imageIcon);

	    return imageIcon;
	} 
	
	private static void applyDisabledEffect(BufferedImage baseImg)
	{
		for (int i = 0; i < baseImg.getWidth(); i += 4)
		{
			for (int j = 0; j < baseImg.getHeight(); j += 4)
			{
				baseImg.setRGB(i, j, 0);
				baseImg.setRGB(i + 1, j, 0);
				baseImg.setRGB(i, j + 1, 0);
				baseImg.setRGB(i + 1, j + 1, 0);
			}
		}
	}
	
	public static ImageIcon loadCombinedImageIcon(String baseFilename, String filename) throws Exception
	{		
		return Main.loadCombinedImageIcon(Main.combinedIcons, baseFilename, filename, false);
	}
	
	public static ImageIcon loadDisableCombinedImageIcon(String baseFilename, String filename) throws Exception
	{		
		return Main.loadCombinedImageIcon(Main.combinedDisabledIcons, baseFilename, filename, true);
	}
	
	public static ImageIcon loadImageIcon(String filename) throws Exception
	{
		return Main.loadImageIcon(Main.icons, filename, false);
	}
	
	public static ImageIcon loadDisabledImageIcon(String filename) throws Exception
	{
		return Main.loadImageIcon(Main.disabledIcons, filename, true);
	}
}
