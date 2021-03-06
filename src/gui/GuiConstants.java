package gui;

import java.awt.Color;

/**
 * Class containing graphical constants used throughout project
 * @author roie
 *
 */
public class GuiConstants {

	public static final int DEFAULT_START_HOUR = 9;
	public static final int DEFAULT_END_HOUR = 17;
	
	public static final int FRAME_HEIGHT = 700;
	public static final int FRAME_WIDTH = 1300;
	public static final int RESPONSE_SPACING = 0;	
	public static final int LINE_SPACING = 1;	
	public static final int DAY_SPACING = 8;
	public static final int MAX_DAY_WIDTH = 225;	
	public static final int RESPONSE_NAME_SPACING = 6;	
	public static final int RESPONSE_CONFLICT_SPACING = 30;
	public static final float INTERLINE_SPACING = 1.0f;
	public static final int HOUR_LABEL_SPACING = 5;
	public static final int MAX_PEEPS_FOR_INTUITIVE_HEATMAP = 20;

	public static final Color OPTIMAL_COLOR = new Color(245, 207, 126, 170); 
	public static final Color RESPONSE_COLOR = new Color(160, 50, 80, 255); //170);
//	public static Color SLOT_COLOR = new Color(34, 139, 34, 50);
	public static Color SLOT_COLOR = new Color(0, 139, 69, 100);
	public static int MAX_SLOT_OPACITY = 255;
	//public static final Color GRAY_OUT_COLOR = new Color(100, 100, 100, 75);
	public static final Color GRAY_OUT_COLOR = new Color(208, 207, 205);
	public static final Color BG_COLOR = new Color(251, 252, 254); //new Color(253, 236, 228); //new Color(240, 225, 225);
	
	public static final Color SHALE = new Color(136, 141, 148);
	
	public static final Color LABEL_COLOR = BG_COLOR; //SHALE; //new Color(200, 180, 180);
	public static final Color LINE_COLOR = new Color(0, 0, 0, 50);
	public static final Color RESPONSE_NAME_COLOR = new Color(0, 0, 0, 100);
	
	
	public static final Color PALE_YELLOW = new Color(245, 207, 126);
	public static final Color PALE_GREEN = new Color(171, 199, 151);
	public static final Color BLUEBERRY = new Color(62, 57, 77);
	public static final Color BABY_BLUE = new Color(38, 111, 128);
	public static final Color AQUAMARINE = new Color(97, 163, 149);
	public static final Color PEACH = new Color(253, 190, 139);
	public static final Color BRIGHT_ORANGE = new Color(238, 105, 64);
	public static final Color DARK_BROWN = new Color(49, 21, 0);
	
	public static final String FONT_NAME = "Monospaced";

}
