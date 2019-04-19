package backend;

import json.RestrictedJson;
import json.restrictions.ChanceRestriction;

public class Chance
{
	private RestrictedJson<ChanceRestriction> chanceJson;
	
	public Chance(RestrictedJson<ChanceRestriction> chanceJson)
	{
		this.chanceJson = chanceJson;
	}
	
	public String getName()
	{
		return this.chanceJson.getJsonEntityString(ChanceRestriction.NAME);
	}
	
	public int getPercentage()
	{
		return this.chanceJson.getJsonEntityNumber(ChanceRestriction.PERCENTAGE).getValue();
	}
	
	public int getPriority()
	{
		return this.chanceJson.getJsonEntityNumber(ChanceRestriction.PRIORITY).getValue();
	}
}
