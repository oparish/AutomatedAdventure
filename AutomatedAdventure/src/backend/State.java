package backend;

import json.JsonEntityArray;
import json.JsonEntityString;
import json.RestrictedJson;
import json.restrictions.StateRestriction;

public class State 
{
	RestrictedJson<StateRestriction> stateJson;
	int currentIndex;
	
	public int getCurrentIndex()
	{
		return currentIndex;
	}

	public State(RestrictedJson<StateRestriction> stateJson, int currentIndex)
	{
		this.stateJson = stateJson;
		this.currentIndex = currentIndex;
	}
	
	public String getValue()
	{
		JsonEntityArray<JsonEntityString> stringArray = this.stateJson.getStringArray(StateRestriction.VARIATIONS);
		JsonEntityString jsonEntityString = stringArray.getMemberAt(this.currentIndex);
		return jsonEntityString.renderAsString();
	}
}
