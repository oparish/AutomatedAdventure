package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
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
import json.restrictions.ScenarioRestriction;

public class Main
{
	public static Main main;
	private static Random random = new Random();
	private static HashMap<String, ImageIcon> icons = new HashMap<String, ImageIcon>();
	
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
}
