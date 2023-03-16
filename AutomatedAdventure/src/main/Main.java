package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import backend.MapElementType;
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
	private static HashMap<String, HashMap<String, HashMap<String, ImageIcon>>> combinedIcons = new HashMap<String, HashMap<String, HashMap<String, ImageIcon>>>();
	private static HashMap<String, HashMap<String, HashMap<String, ImageIcon>>> combinedDisabledIcons = new HashMap<String, HashMap<String, HashMap<String, ImageIcon>>>();
	private static final String NULL = "null";
	
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
	
	public static BufferedImage loadImageFromFile(String filename) throws Exception
	{
		try
		{
			BufferedImage rawImage = ImageIO.read(new File(filename));
			BufferedImage baseImage = new BufferedImage(rawImage.getWidth(), rawImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
			baseImage.getGraphics().drawImage(rawImage, 0, 0, null);
			return baseImage;
		}
		catch (IOException e)
		{
			throw new Exception("Can't load image with filename: " + filename, e);
		}
	}
	
	private static <K> K produceImageMap(HashMap<String, K> outerMap, K emptyMap, String filename)
	{
		K innerMap;
		if (filename != null)
		{
			if (outerMap.containsKey(filename))
			{
				innerMap = outerMap.get(filename);
			}
			else
			{
				innerMap = emptyMap;
				outerMap.put(filename, innerMap);
			}
		}
		else
		{
			if (outerMap.containsKey(NULL))
			{
				innerMap = outerMap.get(NULL);
			}
			else
			{
				innerMap = emptyMap;
				outerMap.put(NULL, innerMap);
			}
		}
		return innerMap;
	}
	
	private static ImageIcon loadCombinedImageIcon(HashMap<String, HashMap<String, HashMap<String, ImageIcon>>> iconMap, String baseFilename, 
			String characterFilename, String effectFilename, boolean disabled) throws Exception
	{		
		HashMap<String, HashMap<String, ImageIcon>> characterMap = Main.produceImageMap(iconMap, new HashMap<String, 
				HashMap<String, ImageIcon>>(), baseFilename);	
		HashMap<String, ImageIcon> effectMap = Main.produceImageMap(characterMap, new HashMap<String, ImageIcon>(), characterFilename);
		
		if (effectMap.containsKey(effectFilename))
		{
			return effectMap.get(effectFilename);
		}
		else if (effectFilename == null && effectMap.containsKey(NULL))
		{
			return effectMap.get(NULL);
		}
			
		BufferedImage baseImg = Main.produceCombinedImageIcon(baseFilename, characterFilename, effectFilename);
		
		if (disabled)
			Main.applyDisabledEffect(baseImg);
			
		ImageIcon imageIcon = new ImageIcon(baseImg);	
		if (effectFilename != null)	
			effectMap.put(effectFilename, imageIcon);
		else
			effectMap.put(NULL, imageIcon);

	    return imageIcon;
	} 
	
	private static BufferedImage produceCombinedImageIcon(String baseFilename, String characterFilename, String effectFilename) throws Exception
	{
		BufferedImage baseImg = Main.loadImageFromFile(baseFilename);
		Graphics2D graphics = baseImg.createGraphics();
		
		if (characterFilename != null)
		{
			BufferedImage characterImg = Main.loadImageFromFile(characterFilename);
			graphics.drawImage(characterImg, 0, 0, null);
		}
		
		if (effectFilename != null)
		{
			BufferedImage effectImg = Main.loadImageFromFile(effectFilename);
			graphics.drawImage(effectImg, 0, 0, null);
		}
		return baseImg;
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
	
	public static ImageIcon loadCombinedImageIcon(String baseFilename, String characterFilename, String effectFilename) throws Exception
	{		
		return Main.loadCombinedImageIcon(Main.combinedIcons, baseFilename, characterFilename, effectFilename, false);
	}
	
	public static ImageIcon loadDisableCombinedImageIcon(String baseFilename, String characterFilename, String effectFilename) throws Exception
	{		
		return Main.loadCombinedImageIcon(Main.combinedDisabledIcons, baseFilename, characterFilename, effectFilename, true);
	}
	
	public static ImageIcon loadImageIcon(String filename) throws Exception
	{
		return Main.loadImageIcon(Main.icons, filename, false);
	}
	
	public static ImageIcon loadDisabledImageIcon(String filename) throws Exception
	{
		return Main.loadImageIcon(Main.disabledIcons, filename, true);
	}
	
	public static GridBagConstraints setupConstraints(int x, int y, int width, int height)
	{
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = x;
		gridBagConstraints.gridy = y;
		gridBagConstraints.weightx = width;
		gridBagConstraints.weighty = height;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridwidth = width;
		gridBagConstraints.gridheight = height;
		return gridBagConstraints;
	}
	
}
