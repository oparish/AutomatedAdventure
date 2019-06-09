package json.restrictions;

import static json.CoreJsonType.NUMBER;
import static json.CoreJsonType.STRING;

import json.JsonDim;
import json.JsonType;

public enum Restriction
{	
	NAME("name", STRING, JsonDim.MONO),
	ELEMENT_DATA("elementData", RestrictionType.ELEMENTDATA, JsonDim.ARRAY),
	OPTIONS("options", STRING, JsonDim.ARRAY),
	ELEMENTS("elements", RestrictionType.ELEMENT, JsonDim.ARRAY),
	TIME("time", NUMBER, JsonDim.MONO),
	INTERVAL_NAME("interval", STRING, JsonDim.MONO),
	CHANCE("chance", NUMBER, JsonDim.MONO),
	VARIATIONS("variations", STRING, JsonDim.ARRAY),
	TEMPLATES("templates", RestrictionType.TEMPLATE, JsonDim.ARRAY),
	ROOMS("rooms", RestrictionType.ROOM, JsonDim.ARRAY),
	STATES("states", RestrictionType.STATE, JsonDim.ARRAY),
	INTERVALS("intervals", RestrictionType.INTERVAL, JsonDim.ARRAY),
	CHECKTIME("checktime", NUMBER, JsonDim.MONO),
	INSTANCE_NUMBER("instanceNumber", NUMBER, JsonDim.MONO),
	PERCENTAGE("percentage", NUMBER, JsonDim.MONO),
	CHANCES("chances", RestrictionType.CHANCE, JsonDim.ARRAY),
	CHANCE_NAME("chanceName", STRING, JsonDim.MONO),
	PRIORITY("priority", NUMBER, JsonDim.MONO),
	CONDITIONS("conditions", RestrictionType.CONDITION, JsonDim.ARRAY),
	TYPE("type", STRING, JsonDim.MONO),
	VALUE("value", STRING, JsonDim.MONO),
	MODE("mode", STRING, JsonDim.MONO),
	CONTENT("content", STRING, JsonDim.MONO),
	ACTIONS("actions", RestrictionType.ACTION, JsonDim.ARRAY),
	ACTION_TYPES("actionTypes", RestrictionType.ACTION_TYPE, JsonDim.ARRAY),
	SHOWN_NAME("shownName", STRING, JsonDim.MONO),
	KEY_NAME("keyName", STRING, JsonDim.MONO),
	ROOM_TYPE("type", STRING, JsonDim.MONO),
	ACTION_NAME("actionName", STRING, JsonDim.MONO),
	COMPONENT_STATES("componentStates", RestrictionType.COMPONENT_STATE, JsonDim.ARRAY),
	COMPONENT_CHANGES("componentChanges", RestrictionType.COMPONENT_CHANGE, JsonDim.ARRAY),
	COMPONENT_NAME("componentName", STRING, JsonDim.MONO),
	OLD_COMPONENT_STATE_NAME("oldComponentStateName", STRING, JsonDim.MONO),
	NEW_COMPONENT_STATE_NAME("newComponentStateName", STRING, JsonDim.MONO),
	INITIAL_COMPONENT_STATE_NAME("initialComponentStateName", STRING, JsonDim.MONO),
	TRANSITION_TEXT("transitionText", STRING, JsonDim.MONO),
	DESCRIPTION("description", STRING, JsonDim.MONO),
	TRIGGER("trigger", RestrictionType.TRIGGER, JsonDim.MONO);
	
	
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

	private Restriction(String name, JsonType jsonType, JsonDim jsonDim)
	{
		this.name = name;
		this.jsonType = jsonType;
		this.jsonDim = jsonDim;
	}
}
