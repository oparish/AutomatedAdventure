package backend.pages;

import backend.Element.ElementInstance;
import backend.Map.MapPosition;
import backend.Element;
import backend.Scenario;
import json.JsonEntityArray;
import json.RestrictedJson;
import json.restrictions.AdjustmentDataRestriction;
import json.restrictions.ContextConditionRestriction;
import json.restrictions.ElementAdjustmentRestriction;
import json.restrictions.RandomRedirectRestriction;
import json.restrictions.RedirectRestriction;

public class RedirectInstance extends AbstractPageInstance
{
	RestrictedJson<RedirectRestriction> redirectJson;
	
	public RedirectInstance(Scenario scenario, PageContext pageContext, RestrictedJson<RedirectRestriction> redirectJson, MapPosition position)
	{
		super(scenario, pageContext, position);
		this.redirectJson = redirectJson;
	}
	
	private boolean checkConditions() throws Exception
	{
		JsonEntityArray<RestrictedJson<ContextConditionRestriction>> contextConditionDataArray = 
				this.redirectJson.getRestrictedJsonArray(RedirectRestriction.CONTEXT_CONDITIONS, ContextConditionRestriction.class);
		
		if (contextConditionDataArray == null)
			return true;
		
		boolean check = true;
		
		for (int i = 0; i < contextConditionDataArray.getLength(); i++)
		{
			RestrictedJson<ContextConditionRestriction> contextConditionData = contextConditionDataArray.getMemberAt(i);
			String elementName = contextConditionData.getString(ContextConditionRestriction.ELEMENT_NAME);
			Element element = this.scenario.getElement(elementName);
			ElementInstance selectedInstance;
			if (element.getUnique())
				selectedInstance = element.getUniqueInstance();
			else
				selectedInstance = pageContext.getElementInstance(element);
			if (!ContextConditionRestriction.checkCondition(this.scenario, contextConditionData, selectedInstance))
			{
				check = false;
				break;
			}		
		}
		return check;
	}
		
	public void load(ElementInstance elementInstance, ElementChoice elementChoice) throws Exception
	{		
		RestrictedJson<AdjustmentDataRestriction> adjustmentData = 
				this.redirectJson.getRestrictedJson(RedirectRestriction.ADJUSTMENT_DATA, AdjustmentDataRestriction.class);
		this.processAdjustmentData(adjustmentData);
				
		if (this.checkConditions())
		{
			String ifPageWord = redirectJson.getString(RedirectRestriction.FIRST);
			this.scenario.loadPage(ifPageWord, pageContext, elementInstance, elementChoice, null);
		}
		else
		{
			String elsePageWord = redirectJson.getString(RedirectRestriction.SECOND);
			this.scenario.loadPage(elsePageWord, pageContext, elementInstance, elementChoice, null);
		}
	}
}
