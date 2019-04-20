package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.JFileChooser;

import backend.Mode;
import backend.Scenario;
import frontEnd.RoomsWindow;
import json.RestrictedJson;
import json.restrictions.ScenarioRestriction;

public class Main
{
	private boolean endingReached = false;
	public static Main main;
	private static Random random = new Random();
	
	private Scenario scenario;
	private int intervalCounter;
	public int getIntervalCounter() {
		return intervalCounter;
	}

	private long nextIntervalTime;
	private int checkTime;
	private RoomsWindow roomsWindow;
	
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
	
	public static void main(String[] args)
	{
		Main.main = new Main();
		Main.main.startLoop();
	}
	
	public Main()
	{
		JsonObject jsonObject = Main.openJsonFile(null);
		RestrictedJson<ScenarioRestriction> scenarioJson = 
				new RestrictedJson<ScenarioRestriction>(jsonObject.getJsonObject("scenario"), ScenarioRestriction.class);
		this.scenario = new Scenario(scenarioJson);
		this.checkTime = scenario.getCheckTime();
		this.intervalCounter = 0;
		this.setNextIntervalTime();
		this.roomsWindow = new RoomsWindow(scenario);
		this.roomsWindow.showWindow();
	}
	
	private void setNextIntervalTime()
	{
		this.nextIntervalTime = System.currentTimeMillis() + this.scenario.getIntervalTime(this.intervalCounter);
	}
	
	private void mainLoop()
	{	
		if (!this.endingReached && System.currentTimeMillis() >= this.nextIntervalTime)
		{
			this.intervalCounter++;
			if (this.intervalCounter >= this.scenario.getIntervalsLength())
			{
				if (this.scenario.getMode() == Mode.LOOP)
				{
					this.intervalCounter = 0;
				}
				else
				{
					this.endingReached = true;
					this.intervalCounter--;
				}
			}
			this.setNextIntervalTime();
		}
		this.roomsWindow.update(this.intervalCounter);
	}
	
	public void startLoop()
	{
		while(true)
		{
			this.mainLoop();
			try
			{
				Thread.sleep(this.checkTime);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
