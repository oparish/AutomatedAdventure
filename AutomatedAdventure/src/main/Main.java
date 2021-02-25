package main;

import java.awt.Component;
import java.awt.Dimension;
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
	private static HashMap<String, HashMap<String, ImageIcon>> combinedIcons = new HashMap<String, HashMap<String, ImageIcon>>();
	
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
	
	public static ImageIcon loadImageIcon(String filename)
	{	
		if (Main.icons.containsKey(filename))
			return Main.icons.get(filename);
		
	    ImageIcon imageIcon = new ImageIcon(filename);
	    Main.icons.put(filename, imageIcon);
	    return imageIcon;
	}
	
	
	public static ImageIcon loadCombinedImageIcon(String baseFilename, String filename)
	{		
		if (Main.combinedIcons.containsKey(baseFilename))
		{
			HashMap<String, ImageIcon> innerIcons = Main.combinedIcons.get(baseFilename);
			if (innerIcons.containsKey(filename))
			{
				return innerIcons.get(filename);
			}
		}
		
		BufferedImage baseImg = null;
		BufferedImage img = null;
		try {
			baseImg = ImageIO.read(new File(baseFilename));
			img = ImageIO.read(new File(filename));
		} catch (IOException e) {
		}
		
		Graphics2D graphics = baseImg.createGraphics();
		graphics.drawImage(img, 0, 0, null);
		ImageIcon imageIcon = new ImageIcon(baseImg);
		
		if (!Main.combinedIcons.containsKey(baseFilename))
			Main.combinedIcons.put(baseFilename, new HashMap<String, ImageIcon>());
		
		HashMap<String, ImageIcon> innerIcons = Main.combinedIcons.get(baseFilename);
		innerIcons.put(filename, imageIcon);
		
	    return imageIcon;
	}
}
