package json;

import java.util.ArrayList;

import json.restrictions.RestrictionPointer;
import main.Main;

public class JsonEntityArray<E extends JsonEntity> implements JsonEntity
{
	ArrayList<E> entityList;
	
	public JsonEntityArray(ArrayList<E> entityList)
	{
		this.entityList = entityList;
	}
	
	public int getLength()
	{
		return this.entityList.size();
	}
	
	public E getRandomMember()
	{
		int rndm = Main.getRndm(this.entityList.size());
		return this.entityList.get(rndm);
	}
	
	public int getRandomIndex()
	{
		int rndm = Main.getRndm(this.entityList.size());
		return rndm;
	}
	
	public <J extends RestrictionPointer> RestrictedJson<J> getRestrictedJson(int index, Class<J> j)
	{
		return (RestrictedJson<J>) this.entityList.get(index);
	}
	
	public JsonEntityString getJsonEntityString(int index)
	{
		return (JsonEntityString) this.entityList.get(index);
	}

	@Override
	public String renderAsString()
	{
		boolean first = true;
		StringBuilder stringBuilder = new StringBuilder("[");
		for(JsonEntity jsonEntity : this.entityList)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				stringBuilder.append(", ");
			}
			stringBuilder.append(jsonEntity.renderAsString());
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}
}
