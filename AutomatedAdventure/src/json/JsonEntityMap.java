package json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class JsonEntityMap<E extends JsonEntity> implements JsonEntity
{
	HashMap<String, E> entityMap;
	
	public JsonEntityMap(HashMap<String, E> entityMap)
	{
		this.entityMap = entityMap;
	}
	
	public E getMemberBy(String key)
	{
		return this.entityMap.get(key);
	}
	
	@Override
	public String renderAsString()
	{
		boolean first = true;
		StringBuilder stringBuilder = new StringBuilder("[");
		for(Entry<String, E> entry : this.entityMap.entrySet())
		{
			if (first)
			{
				first = false;
			}
			else
			{
				stringBuilder.append(", ");
			}
			stringBuilder.append(entry.getKey());
			stringBuilder.append(" : ");
			stringBuilder.append(entry.getValue().renderAsString());
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

}
