package json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.json.JsonArray;
import javax.json.JsonObject;

import json.restrictions.Restriction;
import json.restrictions.RestrictionPointer;
import json.restrictions.RestrictionType;

public class RestrictedJson<R extends RestrictionPointer> implements JsonEntity
{
	HashMap<Restriction, JsonEntity> contents = new HashMap<Restriction, JsonEntity>();
	
	public RestrictedJson(JsonObject json, Class<R> restrictionPointerType)
	{
		for (RestrictionPointer restrictionPointer : restrictionPointerType.getEnumConstants())
		{
			JsonEntity jsonEntity = null;
			Restriction restriction = restrictionPointer.getRestriction();
			String restrictionName = restriction.getName();
			JsonType jsonType = restriction.getJsonType();
			
			if (restriction.getJsonDim() == JsonDim.ARRAY)
			{
				ArrayList<JsonEntity> entityList = new ArrayList<JsonEntity>();
				JsonArray jsonArray = json.getJsonArray(restrictionName);

				if (jsonType instanceof RestrictionType)
				{
					for (int i = 0; i < jsonArray.size(); i++)
					{
						RestrictionType restrictionType = (RestrictionType) jsonType;
						entityList.add(new RestrictedJson(jsonArray.getJsonObject(i), restrictionType.getClazz()));
					}
				}
				else if (jsonType == CoreJsonType.STRING)
				{
					for (int i = 0; i < jsonArray.size(); i++)
					{
						entityList.add(new JsonEntityString(jsonArray.getString(i)));
					}
				}
				else if (jsonType == CoreJsonType.NUMBER)
				{
					for (int i = 0; i < jsonArray.size(); i++)
					{
						entityList.add(new JsonEntityNumber(jsonArray.getInt(i)));
					}
				}
				
				jsonEntity = new JsonEntityArray(entityList);
			}
			else if (jsonType instanceof RestrictionType)
			{
				RestrictionType restrictionType = (RestrictionType) jsonType;
				jsonEntity = new RestrictedJson(json.getJsonObject(restrictionName), restrictionType.getClazz());
			}
			else if (jsonType == CoreJsonType.STRING)
			{
				jsonEntity = new JsonEntityString(json.getString(restrictionName));
			}
			else if (jsonType == CoreJsonType.NUMBER)
			{
				jsonEntity = new JsonEntityNumber(json.getInt(restrictionName));
			}					
			
			this.contents.put(restriction, jsonEntity);
		}
	}
	
	public JsonEntity getChild(R r)
	{
		return this.contents.get(r.getRestriction());
	}

	@Override
	public String renderAsString()
	{
		boolean first = true;
		StringBuilder stringBuilder = new StringBuilder("{");
		for (Entry<Restriction, JsonEntity> entry : this.contents.entrySet())
		{
			if (first)
			{
				first = false;
			}
			else
			{
				stringBuilder.append(", ");
			}
			stringBuilder.append(entry.getKey().getName());
			stringBuilder.append(":");
			stringBuilder.append(entry.getValue().renderAsString());
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
	}
}
