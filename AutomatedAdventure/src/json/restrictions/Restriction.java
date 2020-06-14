package json.restrictions;

import static json.CoreJsonType.NUMBER;
import static json.CoreJsonType.STRING;
import static json.CoreJsonType.BOOLEAN;

import json.JsonDim;
import json.JsonType;

public enum Restriction
{	
	NAME("name", STRING, JsonDim.MONO),
	UNIQUE("unique", BOOLEAN, JsonDim.MONO),
	ELEMENT_DATA("elementData", RestrictionType.ELEMENTDATA, JsonDim.ARRAY),
	ELEMENT_NUMBERS("elementNumbers", RestrictionType.ELEMENT_NUMBER, JsonDim.ARRAY, true),
	OPTIONS("options", STRING, JsonDim.ARRAY),
	ELEMENTS("elements", RestrictionType.ELEMENT, JsonDim.MAP),
	TIME("time", NUMBER, JsonDim.MONO),
	INTERVAL_NAME("interval", STRING, JsonDim.MONO),
	CHANCE("chance", NUMBER, JsonDim.MONO),
	VARIATIONS("variations", STRING, JsonDim.ARRAY),
	TEMPLATES("templates", RestrictionType.TEMPLATE, JsonDim.ARRAY),
	ROOMS("rooms", RestrictionType.ROOM, JsonDim.ARRAY),
	STATES("states", RestrictionType.STATE, JsonDim.ARRAY),
	INTERVALS("intervals", RestrictionType.INTERVAL, JsonDim.ARRAY),
	CHECKTIME("checktime", NUMBER, JsonDim.MONO),
	PERCENTAGE("percentage", NUMBER, JsonDim.MONO),
	CHANCES("chances", RestrictionType.CHANCE, JsonDim.ARRAY),
	CHANCE_NAME("chanceName", STRING, JsonDim.MONO),
	PRIORITY("priority", NUMBER, JsonDim.MONO),
	CONDITIONS("conditions", RestrictionType.CONDITION, JsonDim.ARRAY),
	TYPE("type", STRING, JsonDim.MONO),
	VALUE("value", STRING, JsonDim.MONO),
	NUMBER_VALUE("numberValue", NUMBER, JsonDim.MONO),
	MODE("mode", STRING, JsonDim.MONO),
	CONTENT("content", STRING, JsonDim.MONO),
	ACTIONS("actions", RestrictionType.ACTION, JsonDim.ARRAY),
	ACTION_TYPES("actionTypes", RestrictionType.ACTION_TYPE, JsonDim.ARRAY),
	SHOWN_NAME("shownName", STRING, JsonDim.MONO),
	KEY_NAME("keyName", STRING, JsonDim.MONO),
	ROOM_TYPE("type", STRING, JsonDim.MONO),
	ACTION_NAME("actionName", STRING, JsonDim.MONO),
	COMPONENT_STATES("componentStates", RestrictionType.COMPONENT_STATE, JsonDim.MAP),
	COMPONENT_CHANGES("componentChanges", RestrictionType.COMPONENT_CHANGE, JsonDim.ARRAY),
	COMPONENT_NAME("componentName", STRING, JsonDim.MONO),
	OLD_COMPONENT_STATE_NAME("oldComponentStateName", STRING, JsonDim.MONO),
	NEW_COMPONENT_STATE_NAME("newComponentStateName", STRING, JsonDim.MONO),
	COMPONENT_STATE_NAME("componentStateName", STRING, JsonDim.MONO),
	INITIAL_COMPONENT_STATE_NAME("initialComponentStateName", STRING, JsonDim.MONO),
	TRANSITION_TEXT("transitionText", STRING, JsonDim.MONO),
	DESCRIPTION("description", STRING, JsonDim.MONO),
	COMPONENTS("components", RestrictionType.COMPONENT, JsonDim.MAP),
	TRIGGER("trigger", RestrictionType.TRIGGER, JsonDim.MONO, true),
	ENDINGS("endings", RestrictionType.ENDING, JsonDim.ARRAY),
	PAGES("pages", RestrictionType.PAGE, JsonDim.MAP),
	CHOICES("choices", RestrictionType.CHOICE, JsonDim.ARRAY, true),
	WITH_CONTEXT("withContext", BOOLEAN, JsonDim.MONO),
	FIRST("first", STRING, JsonDim.MONO),
	SECOND("second", STRING, JsonDim.MONO),
	MAX_VALUE("maxValue", NUMBER, JsonDim.MONO),
	MIN_VALUE("minValue", NUMBER, JsonDim.MONO),
	ELEMENT_NAME("elementName", STRING, JsonDim.MONO, true),
	ELEMENT_QUALITY("elementQuality", STRING, JsonDim.MONO, true),
	ELEMENT_CONDITION("elementCondition", RestrictionType.ELEMENT_CONDITION, JsonDim.MONO, true),
	CONTEXT_CONDITION("contextCondition", RestrictionType.CONTEXT_CONDITION, JsonDim.MONO, true),
	ELEMENT_CHOICE("elementChoice", RestrictionType.ELEMENT_CHOICE, JsonDim.MONO, true),
	CONNECTIONS("connections", RestrictionType.CONNECTION, JsonDim.ARRAY),
	ELEMENT_SETS("elementSets", RestrictionType.ELEMENT_SET, JsonDim.ARRAY, true),
	MEMBERS("members", RestrictionType.ELEMENT_SET_MEMBER, JsonDim.ARRAY),
	MAKE_ELEMENTS("makeElements", RestrictionType.MAKE_ELEMENT, JsonDim.ARRAY, true),
	MAKE_CONNECTIONS("makeConnections", RestrictionType.MAKE_CONNECTION, JsonDim.ARRAY, true),
	EACH_ELEMENT_ADJUSTMENTS("eachElementAdjustments", RestrictionType.EACH_ELEMENT_ADJUSTMENT, JsonDim.ARRAY, true),
	MULTIPLIER("multiplier", NUMBER, JsonDim.MONO, true);
	
	private final boolean optional;
	private final JsonDim jsonDim;
	private final JsonType jsonType;
	public JsonType getJsonType() {
		return jsonType;
	}

	private final String name;
	
	public String getName() {
		return name;
	}

	public JsonDim getJsonDim()
	{
		return jsonDim;
	}
	
	public boolean getOptional()
	{
		return this.optional;
	}

	private Restriction(String name, JsonType jsonType, JsonDim jsonDim)
	{
		this(name, jsonType, jsonDim, false);
	}
	
	private Restriction(String name, JsonType jsonType, JsonDim jsonDim, boolean optional)
	{
		this.name = name;
		this.jsonType = jsonType;
		this.jsonDim = jsonDim;
		this.optional = optional;
	}
}
