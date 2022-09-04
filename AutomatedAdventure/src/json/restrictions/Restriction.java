package json.restrictions;

import static json.CoreJsonType.NUMBER;
import static json.CoreJsonType.STRING;
import static json.CoreJsonType.BOOLEAN;

import json.JsonDim;
import json.JsonType;

public enum Restriction
{	
	NAME("name", STRING, JsonDim.MONO),
	CONNECTION_NAME("connectionName", STRING, JsonDim.MONO),
	UNIQUE("unique", BOOLEAN, JsonDim.MONO),
	ELEMENT_DATA("elementData", RestrictionType.ELEMENTDATA, JsonDim.MAP),
	ELEMENT_NUMBERS("elementNumbers", RestrictionType.ELEMENT_NUMBER, JsonDim.MAP),
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
	CHANCES("chances", RestrictionType.CHANCE, JsonDim.MAP),
	CHANCE_NAME("chanceName", STRING, JsonDim.MONO),
	PRIORITY("priority", NUMBER, JsonDim.MONO),
	CONDITIONS("conditions", RestrictionType.CONDITION, JsonDim.ARRAY),
	TOOLTIP("tooltip", RestrictionType.TOOLTIP, JsonDim.MONO),
	TOOLTIP_COMPONENTS("tooltipComponents", RestrictionType.TOOLTIP_COMPONENT, JsonDim.ARRAY),
	TOOLTIP_TEXT("tooltipText", STRING, JsonDim.MONO),
	TYPE("type", STRING, JsonDim.MONO),
	VALUE("value", STRING, JsonDim.MONO),
	NUMBER_VALUE("numberValue", NUMBER, JsonDim.MONO),
	SUM_NAME("sumName", STRING, JsonDim.MONO),
	NUMBER_REFERENCE("numberReference", STRING, JsonDim.MONO),
	SUM_SIGN("sumSign", STRING, JsonDim.MONO),
	COMPONENT_NUMBER("componentNumber", NUMBER, JsonDim.MONO),
	SUM_COMPONENTS("sumComponents", RestrictionType.SUM_COMPONENT, JsonDim.ARRAY),
	SUMS("sums", RestrictionType.SUM, JsonDim.MAP),
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
	TRIGGER("trigger", RestrictionType.TRIGGER, JsonDim.MONO),
	ENDINGS("endings", RestrictionType.ENDING, JsonDim.ARRAY),
	PAGES("pages", RestrictionType.PAGE, JsonDim.MAP),
	REDIRECTS("redirects", RestrictionType.REDIRECT, JsonDim.MAP),
	RANDOM_REDIRECTS("randomRedirects", RestrictionType.RANDOM_REDIRECT, JsonDim.MAP),
	CHOICES("choices", RestrictionType.CHOICE, JsonDim.ARRAY),
	RETURN_TO("returnTo", STRING, JsonDim.MONO),
	WITH_CONTEXT("withContext", BOOLEAN, JsonDim.MONO),
	CONTEXT_CHANGES("contextChanges", RestrictionType.CONTEXT_CHANGE, JsonDim.ARRAY),
	COUNTER_TO_GROUP("counterToGroup", STRING, JsonDim.MONO),
	FIRST("first", STRING, JsonDim.MONO),
	SECOND("second", STRING, JsonDim.MONO),
	MAX_VALUE("maxValue", NUMBER, JsonDim.MONO),
	MIN_VALUE("minValue", NUMBER, JsonDim.MONO),
	X("x", NUMBER, JsonDim.MONO),
	Y("y", NUMBER, JsonDim.MONO),
	GROUP_CONDITION_TYPE("groupConditionType", STRING, JsonDim.MONO),
	ADJUSTMENT_TYPE("adjustmentType", STRING, JsonDim.MONO),
	COUNTER_PRIMARY_TYPE("counterPrimaryType", STRING, JsonDim.MONO),
	COUNTER_SECONDARY_TYPE("counterSecondaryType", STRING, JsonDim.MONO),
	COUNTER_ADJUSTMENT_TYPE("counterAdjustmentType", STRING, JsonDim.MONO),
	COUNTER_CONDITION("counterCondition", STRING, JsonDim.MONO),
	IMAGE("image", RestrictionType.IMAGE, JsonDim.MONO),
	MAPS("maps", RestrictionType.MAP, JsonDim.MAP),
	MAP_ELEMENTS("mapElements", RestrictionType.MAP_ELEMENT, JsonDim.MAP),
	FILENAME("filename", STRING, JsonDim.MONO),
	WIDTH("width", NUMBER, JsonDim.MONO),
	HEIGHT("height", NUMBER, JsonDim.MONO),
	TILE_SIZE("tileSize", NUMBER, JsonDim.MONO),
	MAP_NAME("mapName", STRING, JsonDim.MONO),
	PANEL_NAME("panelName", STRING, JsonDim.MONO),
	ELEMENT_NAME("elementName", STRING, JsonDim.MONO),
	ELEMENT_QUALITY("elementQuality", STRING, JsonDim.MONO),
	ELEMENT_CONDITIONS("elementConditions", RestrictionType.ELEMENT_CONDITION, JsonDim.ARRAY),
	CONTEXT_CONDITIONS("contextConditions", RestrictionType.CONTEXT_CONDITION, JsonDim.ARRAY),
	ELEMENT_CHOICE("elementChoice", RestrictionType.ELEMENT_CHOICE, JsonDim.MONO),
	GROUP_CHOICE("groupChoice", RestrictionType.GROUP_CHOICE, JsonDim.MONO),
	INSTANCE_DETAILS("instanceDetails", RestrictionType.INSTANCE_DETAILS, JsonDim.MONO),
	CONNECTIONS("connections", RestrictionType.CONNECTION, JsonDim.MAP),
	ELEMENT_SETS("elementSets", RestrictionType.ELEMENT_SET, JsonDim.MAP),
	MEMBERS("members", RestrictionType.ELEMENT_SET_MEMBER, JsonDim.MAP),
	MAKE_ELEMENTS("makeElements", RestrictionType.MAKE_ELEMENT, JsonDim.ARRAY),
	MAKE_CONNECTIONS("makeConnections", RestrictionType.MAKE_CONNECTION, JsonDim.ARRAY),
	ADJUSTMENT_DATA("adjustmentData", RestrictionType.ADJUSTMENT_DATA, JsonDim.MONO),
	ELEMENT_ADJUSTMENTS("elementAdjustments", RestrictionType.ELEMENT_ADJUSTMENT, JsonDim.ARRAY),
	POSITION_ADJUSTMENTS("positionAdjustments", RestrictionType.POSITION_ADJUSTMENT, JsonDim.ARRAY),
	COUNTER_NAME("counterName", STRING, JsonDim.MONO),
	COUNTER_ADJUSTMENTS("counterAdjustments", RestrictionType.COUNTER_ADJUSTMENT, JsonDim.ARRAY),
	COUNTER_INITIALISATIONS("counterInitialisations", RestrictionType.COUNTER_INITIALISATION, JsonDim.ARRAY),
	POSITION_COUNTER_NAME("positionCounterName", STRING, JsonDim.MONO),
	NUMBER_MAP("numberMap", NUMBER, JsonDim.MAP),
	STRING_MAP("stringMap", STRING, JsonDim.MAP),
	SET_MAP("setMap", NUMBER, JsonDim.MAP),
	MAP_MAP("mapMap", RestrictionType.MAP_POSITION, JsonDim.MAP),
	PANELS("panels", RestrictionType.PANEL, JsonDim.MAP),
	MULTIPLIER("multiplier", NUMBER, JsonDim.MONO),
	RANGE_ATTRIBUTE("rangeAttribute", STRING, JsonDim.MONO),
	MAP_ELEMENT_TYPE("mapElementType", STRING, JsonDim.MONO);
	
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
