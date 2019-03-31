package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.JFileChooser;

import frontEnd.RoomsWindow;

public class Main
{
	int checkInterval = 1000;
	
	public static Dimension findScreenCentre()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) screenSize.getWidth()/2;
		int height = (int) screenSize.getHeight()/2;
		return new Dimension(width, height);
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
	
	public static void main(String[] args)
	{
		Main main = new Main();
	}
	
	public Main()
	{
		RoomsWindow window = new RoomsWindow();
		window.updatePanel(0, "TEST");
		window.showWindow();
		this.startLoop();
	}
	
	private void mainLoop()
	{	

	}
	
	private void startLoop()
	{
		while(true)
		{
			this.mainLoop();
			try
			{
				Thread.sleep(this.checkInterval);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
