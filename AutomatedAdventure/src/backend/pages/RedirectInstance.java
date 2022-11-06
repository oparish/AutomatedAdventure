package backend.pages;

import backend.Element.ElementInstance;
import backend.ElementGroup;
import backend.Map.MapPosition;
import backend.Element;
import backend.Scenario;
import json.JsonEntityArray;
import json.RestrictedJson;
import json.restrictions.AdjustmentDataRestriction;
import json.restrictions.ContextChangeRestriction;
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
			if (element == null)
				selectedInstance = null;
			else if (element.getUnique())
				selectedInstance = element.getUniqueInstance();
			else
				selectedInstance = pageContext.getElementInstance(element);
			if (!ContextConditionRestriction.checkCondition(this.scenario, contextConditionData, selectedInstance, 
					this.getSelectedElementGroup()))
			{
				check = false;
				break;
			}		
		}
		return check;
	}
	
	private void processContextChanges() throws Exception
	{
		JsonEntityArray<RestrictedJson<ContextChangeRestriction>> changeArray = 
				this.redirectJson.getRestrictedJsonArray(RedirectRestriction.CONTEXT_CHANGES, ContextChangeRestriction.class);
		if (changeArray != null)
		{
			for (int i = 0; i < changeArray.getLength(); i++)
			{
				RestrictedJson<ContextChangeRestriction> changeRestriction = changeArray.getMemberAt(i);
				this.processContextChange(changeRestriction);
			}
		}
	}
	
	private void processContextChange(RestrictedJson<ContextChangeRestriction> changeRestriction) throws Exception
	{
		String counterToGroup = changeRestriction.getString(ContextChangeRestriction.COUNTER_TO_GROUP);
		String addElementToContext = changeRestriction.getString(ContextChangeRestriction.ADD_ELEMENT_TO_CONTEXT);
		if (counterToGroup != null)
		{
			ElementGroup elementGroup = this.setupElementGroup(counterToGroup);
			this.pageContext.setSelectedElementGroup(elementGroup);
		}
		if (addElementToContext != null)
		{
			String[] stringArray = addElementToContext.split("\\:");
			Element element = this.scenario.getElement(stringArray[0]);
			ElementInstance elementInstance = element.getUniqueInstance(stringArray[1]);
			this.pageContext.addElementInstance(elementInstance);
		}
	}
		
	public void load(ElementInstance elementInstance, ElementChoice elementChoice) throws Exception
	{	
		RestrictedJson<AdjustmentDataRestriction> adjustmentData = 
				this.redirectJson.getRestrictedJson(RedirectRestriction.ADJUSTMENT_DATA, AdjustmentDataRestriction.class);
		this.processContextChanges();
		this.processAdjustmentData(adjustmentData);
				
		if (this.checkConditions())
		{
			String ifPageWord = redirectJson.getString(RedirectRestriction.FIRST);
			this.scenario.loadPage(ifPageWord, pageContext, elementInstance, elementChoice.elementGroup, elementChoice, null);
		}
		else
		{
			String elsePageWord = redirectJson.getString(RedirectRestriction.SECOND);
			this.scenario.loadPage(elsePageWord, pageContext, elementInstance, elementChoice.elementGroup, elementChoice, null);
		}
	}
}
