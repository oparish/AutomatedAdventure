package json;

import java.util.ArrayList;
import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class RestrictedJson implements JsonEntity
{
	HashMap<String, JsonEntity> contents = new HashMap<String, JsonEntity>();
	
	protected RestrictedJson(JsonObject json, Restriction restriction)
	{
		for (JsonPair jsonPair : restriction.getPairs())
		{
			JsonEntity jsonEntity = null;
			
			if (jsonPair instanceof JsonArrayPair)
			{
				ArrayList<JsonEntity> entityList = new ArrayList<JsonEntity>();
				JsonArray jsonArray = json.getJsonArray(jsonPair.key);
				if (jsonPair.type instanceof Restriction)
				{
					for (int i = 0; i < jsonArray.size(); i++)
					{
						entityList.add(new RestrictedJson(jsonArray.getJsonObject(i), (Restriction) jsonPair.type));
					}
				}
				else if (jsonPair.type == CoreJsonType.STRING)
				{
					for (int i = 0; i < jsonArray.size(); i++)
					{
						entityList.add(new JsonEntityString(jsonArray.getString(i)));
					}
				}
				else if (jsonPair.type == CoreJsonType.NUMBER)
				{
					for (int i = 0; i < jsonArray.size(); i++)
					{
						entityList.add(new JsonEntityNumber(jsonArray.getInt(i)));
					}
				}
				
				jsonEntity = new JsonEntityArray(entityList);
			}
			else if (jsonPair.type instanceof Restriction)
			{
				jsonEntity = new RestrictedJson(json.getJsonObject(jsonPair.key), (Restriction) jsonPair.type);
			}
			else if (jsonPair.type == CoreJsonType.STRING)
			{
				jsonEntity = new JsonEntityString(json.getString(jsonPair.key));
			}
			else if (jsonPair.type == CoreJsonType.NUMBER)
			{
				jsonEntity = new JsonEntityNumber(json.getInt(jsonPair.key));
			}					
			
			this.contents.put(jsonPair.key, jsonEntity);
		}
	}
}
