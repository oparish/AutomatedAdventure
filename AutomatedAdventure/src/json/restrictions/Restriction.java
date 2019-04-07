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
	RULES("rules", RestrictionType.RULE, JsonDim.ARRAY),
	TEMPLATES("templates", STRING, JsonDim.ARRAY),
	ROOMS("rooms", RestrictionType.ROOM, JsonDim.ARRAY),
	STATES("states", RestrictionType.STATE, JsonDim.ARRAY),
	INTERVALS("intervals", RestrictionType.INTERVAL, JsonDim.ARRAY);
	
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
