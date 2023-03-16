package backend;

import java.util.HashMap;

public class ImageData extends HashMap<ImageDataKey, String>{
	
	public boolean isEqualTo(ImageData imageData)
	{
//		System.out.println("value1");
//		System.out.println(this.get(ImageDataKey.BACKGROUND));
//		System.out.println(this.get(ImageDataKey.CENTRE_CHARACTER));
//		System.out.println(this.get(ImageDataKey.LEFT_CHARACTER));
//		System.out.println(this.get(ImageDataKey.RIGHT_CHARACTER));
//		System.out.println(this.get(ImageDataKey.EFFECT));
//		
//		System.out.println("value2");
//		System.out.println(imageData.get(ImageDataKey.BACKGROUND));
//		System.out.println(imageData.get(ImageDataKey.CENTRE_CHARACTER));
//		System.out.println(imageData.get(ImageDataKey.LEFT_CHARACTER));
//		System.out.println(imageData.get(ImageDataKey.RIGHT_CHARACTER));
//		System.out.println(imageData.get(ImageDataKey.EFFECT));
		
		for (ImageDataKey imageDataKey: ImageDataKey.values())
		{
			String value1 = this.get(imageDataKey);
			String value2 = imageData.get(imageDataKey);
			if ((value1 == null && value2 != null) || (value1 != null && value2 == null))
			{
//				System.out.println("false");
				return false;
			}	
			else if (!(value1 == null && value2 == null) && !value1.equals(value2))
			{
				//System.out.println("false");
				return false;
			}
		}
	//	System.out.println("true");
		return true;
	}
}
