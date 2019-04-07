package json;

import java.util.ArrayList;

import main.Main;

public class JsonEntityArray<J extends JsonEntity> implements JsonEntity
{
	ArrayList<J> entityList;
	
	public JsonEntityArray(ArrayList<J> entityList)
	{
		this.entityList = entityList;
	}
	
	public JsonEntity getEntityAt(int index)
	{
		return this.entityList.get(index);
	}
	
	public int getLength()
	{
		return this.entityList.size();
	}
	
	public J getRandomMember()
	{
		int rndm = Main.getRndm(this.entityList.size());
		return this.entityList.get(rndm);
	}
	
	public int getRandomIndex()
	{
		int rndm = Main.getRndm(this.entityList.size());
		return rndm;
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
