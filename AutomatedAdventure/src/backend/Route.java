package backend;

import java.util.ArrayList;

import backend.Element.ElementInstance;
import backend.Map.MapPosition;

public class Route {
	
	private ArrayList<MapPosition> positions;
	private int currentPos;
	private RouteType routeType;
	private RouteState routeState;
	
	public Route(RouteType routeType)
	{
		this.positions = new ArrayList<MapPosition>();
		this.routeType = routeType;
		this.currentPos = 0;
		this.routeState = RouteState.WALKING;
	}
	
	public MapPosition getPosition()
	{
		return this.positions.get(this.currentPos);
	}
	
	private boolean checkNextStep(Faction faction, int nextPos)
	{
		MapPosition position = this.positions.get(nextPos);
		for (ElementInstance elementInstance : position.elementInstances)
		{
			if (faction != elementInstance.getFaction())
				return false;
		}
		
		return true;
	}
	
	public void incrementPosition(Faction faction)
	{
		this.changePosition(faction, 1);
	}
	
	private void changePosition(Faction faction, int change)
	{
		int actualChange;
		if (this.routeState == RouteState.REVERSING)
			actualChange = -change;
		else
			actualChange = change;
		
		if (!this.checkNextStep(faction, this.currentPos + actualChange))
		{
			if (this.routeType == RouteType.WAIT)
				this.routeState = RouteState.WAITING;
			else if (this.routeState == RouteState.REVERSING)
				this.routeState = RouteState.WALKING;
			else
				this.routeState = RouteState.REVERSING;
			return;
		}
		this.currentPos += actualChange;
		if (this.currentPos == this.positions.size() - 1 || this.currentPos == 0)
		{
			this.routeState = RouteState.COMPLETED;
		}
		else
		{
			this.routeState = RouteState.WALKING;
		}
	}
	
	public void decrementPosition(Faction faction)
	{
		this.changePosition(faction, -1);
	}
	
	public void resetPosition()
	{
		this.currentPos = 0;
	}
	
	public void addRoutePosition(MapPosition position)
	{
		this.positions.add(position);
	}
	
	public RouteState getRouteState()
	{
		return this.routeState;
	}
}
