package json;

import static json.CoreJsonType.NUMBER;
import static json.CoreJsonType.STRING;

public enum Restriction implements JsonType
{
	ELEMENT(new JsonPair("name", STRING), new JsonArrayPair("options", STRING)),
	INTERVAL(new JsonPair("time", NUMBER), new JsonPair("name", STRING)),
	RULE(new JsonPair("interval", STRING), new JsonPair("chance", NUMBER)),
	STATE(new JsonArrayPair("rules", RULE), new JsonPair("name", STRING)),
	ROOM(new JsonArrayPair("templates", STRING)),
	SCENARIO(new JsonArrayPair("rooms", ROOM), new JsonArrayPair("states", STATE), new JsonArrayPair("intervals", INTERVAL));
	
	private JsonPair[] pairs;
	
	public JsonPair[] getPairs()
	{
		return pairs;
	}

	Restriction(JsonPair... pairs)
	{
		this.pairs = pairs;
	}
}
