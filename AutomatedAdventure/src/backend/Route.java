package backend;

import java.util.ArrayList;

import backend.Map.MapPosition;

public class Route {
	
	private ArrayList<MapPosition> positions;
	private int currentPos;
	private RouteType routeType;
	private RouteState routeState;
	
	public Route(ArrayList<MapPosition> positions, RouteType routeType)
	{
		this.positions = positions;
		this.routeType = routeType;
		this.currentPos = 0;
		this.routeState = RouteState.WALKING;
	}
	
	public MapPosition getPosition()
	{
		return this.positions.get(this.currentPos);
	}
	
	public boolean incrementPosition()
	{
		this.currentPos++;
		if (this.currentPos == this.positions.size() - 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean decrementPosition()
	{
		this.currentPos--;
		if (this.currentPos == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void resetPosition()
	{
		this.currentPos = 0;
	}
	
	public void addRoutePosition(MapPosition position)
	{
		this.positions.add(position);
	}
}
