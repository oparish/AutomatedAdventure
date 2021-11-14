package animation;

public class MapAnimation implements Runnable
{
	@Override
	public void run() {
		try {
			synchronized(this)
			{
				this.wait(3000);
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
	}

}
