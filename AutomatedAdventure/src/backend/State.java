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
	
	public void changeStateValue(String newStateValue)
	{
		JsonEntityArray<JsonEntityString> stringArray = this.stateJson.getStringArray(StateRestriction.VARIATIONS);
		for (int i = 0; i < stringArray.getLength(); i++)
		{
			String stateMember = stringArray.getMemberAt(i).renderAsString();
			if (newStateValue.equals(stateMember))
			{
				this.currentIndex = i;
				return;
			}
		}
		new Exception("State value " + newStateValue + " not recognised.").printStackTrace();
	}
}
