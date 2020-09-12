package frontEnd;

import java.awt.Color;
import java.awt.Font;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

public class WoodTheme extends MetalTheme
{
	private static final FontUIResource mainFont = new FontUIResource(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
	private static final ColorUIResource menuBackgroundColor = new ColorUIResource(Color.WHITE);
	
	public WoodTheme()
	{
		super();	
	}
	
	@Override
	public FontUIResource getControlTextFont()
	{
		return WoodTheme.mainFont;
	}

	@Override
	public FontUIResource getMenuTextFont() {
		return WoodTheme.mainFont;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	protected ColorUIResource getPrimary1() {
		return null;
	}

	@Override
	protected ColorUIResource getPrimary2() {
		return null;
	}

	@Override
	protected ColorUIResource getPrimary3() {
		return null;
	}

	@Override
	protected ColorUIResource getSecondary1() {
		return null;
	}

	@Override
	protected ColorUIResource getSecondary2() {
		return null;
	}

	@Override
	protected ColorUIResource getSecondary3() {
		return null;
	}

	@Override
	public FontUIResource getSubTextFont() {
		return null;
	}

	@Override
	public FontUIResource getSystemTextFont() {
		return null;
	}

	@Override
	public FontUIResource getUserTextFont() {
		return null;
	}

	@Override
	public FontUIResource getWindowTitleFont() {
		return null;
	}
	
	public ColorUIResource getMenuSelectedBackground()
	{
		return WoodTheme.menuBackgroundColor;
	}
}
