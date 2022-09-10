package backend.pages;

import backend.Map;
import backend.Map.MapPosition;

public class PositionCounter implements Counter
{
	Map map;
	CounterSecondaryType positionCounterType;
	int x;
	int y;

	public PositionCounter(Map map, CounterSecondaryType positionCounterType)
	{
		this.map = map;
		this.positionCounterType = positionCounterType;
		this.init();
	}
	
	private void init()
	{
		switch(this.positionCounterType)
		{
		case ALL:
			this.x = 0;
			this.y = 0;
			break;
		}
	}
	
	public void increment()
	{		
		switch(this.positionCounterType)
		{
		case ALL:
			x++;
			if (x == map.getWidth())
			{
				x = 0;
				y++;
			}			
		break;
		}
	}
	
	public boolean isFinished()
	{
		switch(this.positionCounterType)
		{
		case ALL:
			return y >= this.map.getHeight();
		default:
			return true;
		}
	}
	
	public MapPosition getMapPosition()
	{
		return map.getMapPosition(this.x, this.y);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
