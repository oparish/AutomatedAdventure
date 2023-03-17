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

import backend.ImageData;
import backend.ImageDataKey;
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
	private static HashMap<ImageData, ImageIcon> combinedIcons = new HashMap<ImageData, ImageIcon>();
	private static HashMap<ImageData, ImageIcon> combinedDisabledIcons = new HashMap<ImageData, ImageIcon>();
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
	
	private static ImageIcon loadCombinedImageIcon(HashMap<ImageData, ImageIcon> iconMap, ImageData imageData, boolean disabled) throws Exception
	{
		for (ImageData key : iconMap.keySet())
		{
			if (key.isEqualTo(imageData))
			{
				return iconMap.get(key);
			}
		}
		
		BufferedImage baseImg = Main.produceCombinedImageIcon(imageData);
		
		if (disabled)
			Main.applyDisabledEffect(baseImg);
			
		ImageIcon imageIcon = new ImageIcon(baseImg);	
		iconMap.put(imageData, imageIcon);

	    return imageIcon;
	} 
	
	private static BufferedImage produceCombinedImageIcon(ImageData characterData) throws Exception
	{
		String baseFilename = characterData.get(ImageDataKey.BACKGROUND);
		String leftImageName = characterData.get(ImageDataKey.LEFT_CHARACTER);
		String rightImageName = characterData.get(ImageDataKey.RIGHT_CHARACTER);
		String centreImageName = characterData.get(ImageDataKey.CENTRE_CHARACTER);
		String effectFilename = characterData.get(ImageDataKey.EFFECT);
		
		BufferedImage baseImg = Main.loadImageFromFile(baseFilename);
		Graphics2D graphics = baseImg.createGraphics();
		
		if (leftImageName != null)
		{
			BufferedImage leftCharacterImg = Main.loadImageFromFile(leftImageName);
			int segmentWidth = leftCharacterImg.getWidth() * 5 / 24;
			graphics.drawImage(leftCharacterImg, 0 - segmentWidth, 0, null);
		}
		
		if (rightImageName != null)
		{
			BufferedImage rightCharacterImg = Main.loadImageFromFile(rightImageName);
			int segmentWidth = rightCharacterImg.getWidth() * 5 / 24;
			graphics.drawImage(rightCharacterImg, 0 + segmentWidth, 0, null);
		}
		
		if (centreImageName != null)
		{
			BufferedImage centreCharacterImg = Main.loadImageFromFile(centreImageName);
			graphics.drawImage(centreCharacterImg, 0, 0, null);
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
	
	public static ImageIcon loadCombinedImageIcon(ImageData imageData) throws Exception
	{		
		return Main.loadCombinedImageIcon(Main.combinedIcons, imageData, false);
	}
	
	public static ImageIcon loadDisableCombinedImageIcon(ImageData imageData) throws Exception
	{		
		return Main.loadCombinedImageIcon(Main.combinedDisabledIcons, imageData, true);
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
