package json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import json.restrictions.Restriction;
import json.restrictions.RestrictionPointer;
import json.restrictions.RestrictionType;
import json.restrictions.SuperRestriction;

public class RestrictedJson<R extends RestrictionPointer> implements JsonEntity
{
	private static final String TYPE = "type";
	
	RestrictionType subType = null;
	
	public RestrictionType getSubType() {
		return subType;
	}

	HashMap<Restriction, JsonEntity> contents = new HashMap<Restriction, JsonEntity>();
	
	public RestrictedJson(JsonObject json, Class<R> restrictionPointerType)
	{
		this.iteratePointers(json, restrictionPointerType);
	}
	
	private void iteratePointers(JsonObject json, Class<? extends RestrictionPointer> restrictionPointerType)
	{
		for (RestrictionPointer restrictionPointer : restrictionPointerType.getEnumConstants())
		{
			try
			{
				Restriction restriction = restrictionPointer.getRestriction();
				String restrictionName = restriction.getName();
				
				if (restriction.getOptional() && json.get(restrictionName) == null)
					continue;
				this.loadJson(json, restrictionPointer);
				if (restrictionPointer instanceof SuperRestriction)
				{
					RestrictionType[] possibleSubRestrictions = ((SuperRestriction) restrictionPointer).getSubRestrictions();
					
					String typeString = json.getString(TYPE);	
					if (typeString == null)
						throw new Exception("Can't find type for " + restrictionPointer);
					
					RestrictionType restrictionType = RestrictionType.valueOf(typeString.toUpperCase());
					if (restrictionType == null || !this.subrestrictionArrayContains(possibleSubRestrictions, restrictionType))
						throw new Exception("Can't find valid type for " + restrictionPointer);
					
					this.subType = restrictionType;
					this.iteratePointers(json, restrictionType.getClazz());
				
				}
			}
			catch(Exception e)
			{
				new Exception("Error loading " + restrictionPointer + " for " + restrictionPointerType.getName(), e).printStackTrace();;
			}
		}
	}
	
	private boolean subrestrictionArrayContains(RestrictionType[] possibleSubRestrictions, RestrictionType restrictionType)
	{
		for (RestrictionType subRestriction : possibleSubRestrictions)
		{
			if (restrictionType == subRestriction)
				return true;
		}
		return false;
	}
	
	
	private void loadJson(JsonObject json, RestrictionPointer restrictionPointer)
	{
		JsonEntity jsonEntity = null;
		Restriction restriction = restrictionPointer.getRestriction();
		String restrictionName = restriction.getName();
		JsonType jsonType = restriction.getJsonType();
		if (restriction.getJsonDim() == JsonDim.MAP && jsonType instanceof RestrictionType)
		{
			HashMap<String, RestrictedJson<? extends RestrictionPointer>> entityMap = new HashMap<String, RestrictedJson<? extends RestrictionPointer>>();
			JsonObject jsonObject = json.getJsonObject(restrictionName);
			for (Entry<String, JsonValue> entry : jsonObject.entrySet())
			{
				String key = entry.getKey();
				RestrictionType restrictionType = (RestrictionType) jsonType;
				entityMap.put(key, new RestrictedJson((JsonObject) entry.getValue(), restrictionType.getClazz()));
			}	
			jsonEntity = new JsonEntityMap<RestrictedJson<? extends RestrictionPointer>>(entityMap);
		}
		else if (restriction.getJsonDim() == JsonDim.MAP && jsonType == CoreJsonType.STRING)
		{
			HashMap<String, JsonEntityString> stringMap = new HashMap<String, JsonEntityString>();
			JsonObject jsonObject = json.getJsonObject(restrictionName);
			for (Entry<String, JsonValue> entry : jsonObject.entrySet())
			{
				String key = entry.getKey();
				JsonString jsonString = (JsonString) entry.getValue();
				stringMap.put(key, new JsonEntityString(jsonString.getString()));
			}	
			jsonEntity = new JsonEntityMap<JsonEntityString>(stringMap);
		}
		else if (restriction.getJsonDim() == JsonDim.MAP && jsonType == CoreJsonType.NUMBER)
		{
			HashMap<String, JsonEntityNumber> numberMap = new HashMap<String, JsonEntityNumber>();
			JsonObject jsonObject = json.getJsonObject(restrictionName);
			for (Entry<String, JsonValue> entry : jsonObject.entrySet())
			{
				String key = entry.getKey();
				JsonNumber jsonNumber = (JsonNumber) entry.getValue();
				numberMap.put(key, new JsonEntityNumber(jsonNumber.intValue()));
			}	
			jsonEntity = new JsonEntityMap<JsonEntityNumber>(numberMap);
		}
		else if (restriction.getJsonDim() == JsonDim.MAP && jsonType == CoreJsonType.BOOLEAN)
		{
			HashMap<String, JsonEntityBoolean> booleanMap = new HashMap<String, JsonEntityBoolean>();
			JsonObject jsonObject = json.getJsonObject(restrictionName);
			for (Entry<String, JsonValue> entry : jsonObject.entrySet())
			{
				String key = entry.getKey();
				JsonValue jsonValue = (JsonValue) entry.getValue();
				booleanMap.put(key, new JsonEntityBoolean(jsonValue.equals(JsonValue.TRUE)));
			}	
			jsonEntity = new JsonEntityMap<JsonEntityBoolean>(booleanMap);
		}
		else if (restriction.getJsonDim() == JsonDim.ARRAY && jsonType instanceof RestrictionType)
		{
			ArrayList<RestrictedJson<? extends RestrictionPointer>> entityList = new ArrayList<RestrictedJson<? extends RestrictionPointer>>();
			JsonArray jsonArray = json.getJsonArray(restrictionName);
			for (int i = 0; i < jsonArray.size(); i++)
			{
				RestrictionType restrictionType = (RestrictionType) jsonType;
				entityList.add(new RestrictedJson(jsonArray.getJsonObject(i), restrictionType.getClazz()));
			}	
			jsonEntity = new JsonEntityArray<RestrictedJson<? extends RestrictionPointer>>(entityList);
		}
		else if (restriction.getJsonDim() == JsonDim.ARRAY && jsonType == CoreJsonType.STRING)
		{
			JsonArray jsonArray = json.getJsonArray(restrictionName);
			ArrayList<JsonEntityString> entityList = new ArrayList<JsonEntityString>();
			for (int i = 0; i < jsonArray.size(); i++)
			{
				entityList.add(new JsonEntityString(jsonArray.getString(i)));

			}
			jsonEntity = new JsonEntityArray<JsonEntityString>(entityList);
		}
		else if (restriction.getJsonDim() == JsonDim.ARRAY && jsonType == CoreJsonType.NUMBER)
		{
			JsonArray jsonArray = json.getJsonArray(restrictionName);
			ArrayList<JsonEntityNumber> entityList = new ArrayList<JsonEntityNumber>();
			for (int i = 0; i < jsonArray.size(); i++)
			{
				entityList.add(new JsonEntityNumber(jsonArray.getInt(i)));

			}
			jsonEntity = new JsonEntityArray<JsonEntityNumber>(entityList);
		}
		else if (restriction.getJsonDim() == JsonDim.ARRAY && jsonType == CoreJsonType.BOOLEAN)
		{
			JsonArray jsonArray = json.getJsonArray(restrictionName);
			ArrayList<JsonEntityBoolean> entityList = new ArrayList<JsonEntityBoolean>();
			for (int i = 0; i < jsonArray.size(); i++)
			{
				entityList.add(new JsonEntityBoolean(jsonArray.getBoolean(i)));

			}
			jsonEntity = new JsonEntityArray<JsonEntityBoolean>(entityList);
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
		else if (jsonType == CoreJsonType.BOOLEAN)
		{
			jsonEntity = new JsonEntityBoolean(json.containsKey(restrictionName) && json.getBoolean(restrictionName));
		}
		
		this.contents.put(restriction, jsonEntity);
	}
	
	public <J extends RestrictionPointer> RestrictedJson<J> getRestrictedJson(R r, Class<J> j)
	{
		return (RestrictedJson<J>) this.contents.get(r.getRestriction());
	}
	
	public <J extends RestrictionPointer> JsonEntityMap<RestrictedJson<J>> getRestrictedJsonMap(R r, Class<J> j)
	{
		return (JsonEntityMap<RestrictedJson<J>>) this.contents.get(r.getRestriction());
	}
	
	public <J extends RestrictionPointer> RestrictedJson<J> getRestrictedJsonFromMap(R r, Class<J> j, String key)
	{
		return ((JsonEntityMap<RestrictedJson<J>>) this.contents.get(r.getRestriction())).getMemberBy(key);
	}
	
	public <J extends RestrictionPointer> JsonEntityArray<RestrictedJson<J>> getRestrictedJsonArray(R r, Class<J> j)
	{
		return (JsonEntityArray<RestrictedJson<J>>) this.contents.get(r.getRestriction());
	}
	
	public JsonEntityMap<JsonEntityString> getStringMap(R r)
	{
		return (JsonEntityMap<JsonEntityString>) this.contents.get(r.getRestriction());
	}
	
	public JsonEntityArray<JsonEntityString> getStringArray(R r)
	{
		return (JsonEntityArray<JsonEntityString>) this.contents.get(r.getRestriction());
	}
	
	public String getString(R r)
	{
		JsonEntityString jsonEntityString = (JsonEntityString) this.contents.get(r.getRestriction());
		return jsonEntityString.renderAsString();
	}
	
	public boolean getBoolean(R r)
	{
		JsonEntityBoolean jsonEntityBoolean = (JsonEntityBoolean) this.contents.get(r.getRestriction());
		return jsonEntityBoolean.isValue();
	}
	
	public JsonEntityNumber getJsonEntityNumber(R r)
	{
		return (JsonEntityNumber) this.contents.get(r.getRestriction());
	}

	public Integer getNumber(R r)
	{
		JsonEntityNumber jen = (JsonEntityNumber) this.contents.get(r.getRestriction());
		if (jen == null)
			return null;
		return jen.getValue();
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
