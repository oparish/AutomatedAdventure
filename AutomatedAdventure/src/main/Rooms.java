package main;

import javax.json.JsonObject;

import backend.Mode;
import backend.Scenario;
import frontEnd.RoomsWindow;
import json.RestrictedJson;
import json.restrictions.ScenarioRestriction;

public class Rooms
{
	private boolean endingReached = false;
	private Scenario scenario;
	private int intervalCounter;
	public int getIntervalCounter() {
		return intervalCounter;
	}

	private long nextIntervalTime;
	private int checkTime;
	private RoomsWindow roomsWindow;
	public static Rooms rooms;
	
	public static void main(String[] args)
	{
		try
		{
			Rooms.rooms = new Rooms();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		Rooms.rooms.startLoop();
	}
	
	public static Rooms getMain()
	{
		return Rooms.rooms;
	}
	
	public Scenario getScenario()
	{
		return this.scenario;
	}
	
	public Rooms() throws Exception
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
