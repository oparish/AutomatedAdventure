package backend.pages;

import backend.Element.ElementInstance;

import java.util.HashMap;
import java.util.Map.Entry;

import backend.Scenario;
import json.JsonEntityArray;
import json.JsonEntityMap;
import json.JsonEntityNumber;
import json.RestrictedJson;
import json.restrictions.ElementAdjustmentRestriction;
import json.restrictions.RandomRedirectRestriction;
import main.Main;

public class RandomRedirectInstance extends AbstractPageInstance
{
	RestrictedJson<RandomRedirectRestriction> randomRedirectJson;
	
	public RandomRedirectInstance(Scenario scenario, PageContext pageContext, RestrictedJson<RandomRedirectRestriction> randomRedirectJson)
	{
		super(scenario, pageContext);
		this.randomRedirectJson = randomRedirectJson;
	}
	
	public void load(ElementInstance elementInstance, ElementChoice elementChoice) throws Exception
	{
		JsonEntityArray<RestrictedJson<ElementAdjustmentRestriction>> elementAdjustmentDataArray = 
				this.randomRedirectJson.getRestrictedJsonArray(RandomRedirectRestriction.ELEMENT_ADJUSTMENTS, ElementAdjustmentRestriction.class);
		if (elementAdjustmentDataArray!= null)
			this.makeElementAdjustments(elementAdjustmentDataArray);
		
		JsonEntityMap<JsonEntityNumber> jsonMap = randomRedirectJson.getNumberMap(RandomRedirectRestriction.NUMBER_MAP);
		HashMap<String, JsonEntityNumber> numberMap = jsonMap.getEntityMap();
		HashMap<Integer, String> dataMap = new HashMap<Integer, String>();
		int total = 0;

		for (Entry<String, JsonEntityNumber> entry : numberMap.entrySet())
		{
			JsonEntityNumber jsonEntityNumber = entry.getValue();
			int number = jsonEntityNumber.getValue();
			total += number;
			dataMap.put(new Integer(total), entry.getKey());
		}
		
		int value = Main.getRndm(total);
		
		for (Entry<Integer, String> entry : dataMap.entrySet())
		{
			if (value < entry.getKey())
			{
				this.scenario.loadPage(entry.getValue(), pageContext, elementInstance, elementChoice, null);
				return;
			}
		}
		
		throw new Exception("Random number out of range.");
	}
}
