package backend.pages;

import backend.Element.ElementInstance;
import backend.Map.MapPosition;

import java.util.HashMap;
import java.util.Map.Entry;

import backend.Scenario;
import json.JsonEntityArray;
import json.JsonEntityMap;
import json.JsonEntityNumber;
import json.RestrictedJson;
import json.restrictions.AdjustmentDataRestriction;
import json.restrictions.ElementAdjustmentRestriction;
import json.restrictions.PageRestriction;
import json.restrictions.PositionAdjustmentRestriction;
import json.restrictions.RandomRedirectRestriction;
import main.Main;

public class RandomRedirectInstance extends AbstractPageInstance
{
	RestrictedJson<RandomRedirectRestriction> randomRedirectJson;
	
	public RandomRedirectInstance(Scenario scenario, PageContext pageContext, RestrictedJson<RandomRedirectRestriction> randomRedirectJson, 
			MapPosition position)
	{
		super(scenario, pageContext, position);
		this.randomRedirectJson = randomRedirectJson;
	}
	
	public void load(ElementInstance elementInstance, ElementChoice elementChoice) throws Exception
	{
		RestrictedJson<AdjustmentDataRestriction> adjustmentData = 
				this.randomRedirectJson.getRestrictedJson(RandomRedirectRestriction.ADJUSTMENT_DATA, AdjustmentDataRestriction.class);
		this.processAdjustmentData(adjustmentData);
		
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
				this.scenario.loadPage(entry.getValue(), pageContext, elementInstance, elementChoice.elementGroup, elementChoice, null);
				return;
			}
		}
		
		throw new Exception("Random number out of range.");
	}
}
