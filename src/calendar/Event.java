package calendar;

import static gui.GuiConstants.SLOT_COLOR;
import static gui.GuiConstants.MAX_SLOT_OPACITY;
import gui.DayPanel;
import gui.GuiConstants;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;

public class Event extends CalendarGroup<CalendarSlots> {
	private ArrayList<java.awt.Color> _colors = new ArrayList<java.awt.Color>(); 
	private ArrayList<EventUpdate> _updates = new ArrayList<EventUpdate>();
	public enum PaintMethod {Bars, HeatMap};
	private PaintMethod _paintMethod;
	private CalendarSlots _userResponse = null;
	private boolean _userHasSubmitted = false;
	private boolean _hasUpdates = false;
	//protected int _minInSlot = 15;
	protected String _name, _url;
	protected int _id;
	
	
	
	public Event(DateTime start, DateTime end, CalGroupType type){
		super(start, end, type);
		//System.out.println("Here " + _minInSlot);
		initColors();
	}
	
	public Event(DateTime start, DateTime end, Collection<CalendarSlots> cals, CalGroupType type){
		super(start, end, cals, type);
		//System.out.println("There " + _minInSlot);
		initColors();
	}
	
	public int getMinInSlot(){
		return -1;
	}
	
	public ArrayList<String> getCalOwnerNames() {
		ArrayList<String> names = new ArrayList<String>();
		if(_userHasSubmitted)
			names.add(_userResponse.getOwner().getName());
		
		for(CalendarSlots cal : this.getCalendars())
			names.add(cal.getOwner().getName());
		
		return names;
	}
	public void setID(int id) { _id = id; };
	public void setURL(String url) { _url = url; }
	public void setName(String name) {_name = name; }
	
	
	public void setUserResponse(CalendarSlots cal) { 
		_userResponse = cal; 
		this.removeCalendar(cal);
		String names = "";
		for(CalendarSlots c : this.getCalendars()){
			names += c.getOwner().getName() + ", ";
		}
	}
	public void setUserSubmitted(boolean b) {_userHasSubmitted = b; }
	
	public class CalByThatNameNotFoundException extends Exception {
		
	}
	
	public CalendarSlots getCalByName(String name) throws CalByThatNameNotFoundException {
		CalendarSlots cal = null;
		
		for(CalendarSlots thisCal : this.getCalendars()) {
			if (thisCal.getOwner().getName().equalsIgnoreCase(name))
				return thisCal;
		}
		
		if(_userResponse.getOwner().getName().equalsIgnoreCase(name)) {
			System.out.println("Getting User Response Cal");
			return _userResponse;
		}
		
		/*
		for(int i = 0; i < this.getCalendars().size(); i++) { 
			if(this.getCalendars().get(i).getOwner().getName().equalsIgnoreCase(name)) {
				return this.getCalendars().get(i);
			}
		}*/
		throw new CalByThatNameNotFoundException();
	}
	
	private void initColors() {
		_colors.add(GuiConstants.PALE_YELLOW);
		_colors.add(GuiConstants.AQUAMARINE);
		_colors.add(GuiConstants.PALE_GREEN);
		_colors.add(GuiConstants.BRIGHT_ORANGE);
		_colors.add(GuiConstants.BABY_BLUE);
		//_colors.add(GuiConstants.BLUEBERRY);
		//_colors.add(GuiConstants.DARK_BROWN);
		_colors.add(GuiConstants.PEACH);
		
		
		ArrayList<CalendarSlots> calendars = this.getCalendars();
		
		for(int i=0; i< calendars.size(); i++){
			calendars.get(i).setColor(_colors.get(i % _colors.size()));
		}
		
	}
	
	public void init(){
		initColors();
		//TODO: is this what we really want?
	}
	
	public String getURL(){ return _url; }
	public int getID(){ return _id; }
	public String getName(){ return _name; }
	public CalendarSlots getUserResponse() { return _userResponse; }
	
	
	public boolean hasUpdates() { return _hasUpdates; }
	public ArrayList<EventUpdate> getUpdates() { return _updates; }
	public void addUpdates(ArrayList<EventUpdate> newUpdates) {
		assert newUpdates != null;
		assert _updates != null;
		
		if(newUpdates.size() == 0)
			return;
		_hasUpdates = true;
		_updates.addAll(0,  newUpdates);
	}
	
	public void updatesViewed() { _hasUpdates = false;}
	
	
	public boolean userHasSubmitted(){
		return _userHasSubmitted;
	}

	// 10 pts
	// 0 - 9
	// 0 -> 0
	// 9 -> height
	// index / (numPts - 1) * height
	
	private int endPtToHeight(int index, int numPts, int height) {
		double indDbl = (double) index;
		double numPtsDbl = (double) numPts;
		double heightDbl = (double) height;
		return (int) (indDbl * heightDbl / (numPtsDbl - 1.0));
		//return (int) ((1.0 * index) / (1.0 * numPts - 1.0) * height);
	}
	
	private int slotHeight(int numPts, int height) {
		return height / numPts;
	}
	
	private int availWidth(int totalAvail, int width) {
		return width / totalAvail;
	}

	private void paintBars(ArrayList<CalendarSlots> cals, Graphics2D brush, DayPanel d, int day) {
		int numSlotsInDay = cals.get(0).getSlotsInDay();
		
		// 0 1 2 3
		// goes from left, right edge left, left edge right, right edge right
		int[][] endpts = new int[numSlotsInDay*2+1][4];
		for(int i = 0; i < endpts.length; i++) {
			endpts[i][0] = 0;
			endpts[i][1] = 0;
			endpts[i][2] = 0;
			endpts[i][3] = 0;
		}
		
		ArrayList<CalendarSlots> visibleCals = new ArrayList<CalendarSlots>();
		for(CalendarSlots cal: cals) {
			if(cal.isVisible())
				visibleCals.add(cal);
		}
		
		// TODO if not displaying user on right hand side can get rid of
		/*
		 if(this.userHasSubmitted() && this.getUserResponse().isVisible()) {
		 
			visibleCals.add(this.getUserResponse());
		}
*/	
		for(int i = 0; i < visibleCals.size(); i++) {
			// Left is 0, right is 1
			int outside = 0;
			int inside = 1;
			if(i % 2 == 1) {
				outside = 3;
				inside = 2;
			}

			CalendarSlots cal = visibleCals.get(i);
			
			// set color of cal for use in friend bar
			// Color color = _colors.get(i % _colors.size());
			// cal.setColor(color);
			
			if(cal.isVisible()){
				brush.setColor(cal.getColor());
				// Fill in availabilities
				for(int slot = 0; slot < numSlotsInDay; slot++) {
					int s = slot*2+1;
					if(cal.getAvail(day, slot) == Availability.free)
						endpts[s][outside] = endpts[s][inside] + 1;
					else
						endpts[s][outside] = endpts[s][inside];
				}
	
				// Connect the dots
				for(int endPtSlot = 2; endPtSlot < endpts.length - 1; endPtSlot = endPtSlot + 2) {
					int above = endpts[endPtSlot - 1][outside];
					int below = endpts[endPtSlot + 1][outside];
					if(above == below)
						endpts[endPtSlot][outside] = above;
					else if(above < below)
						endpts[endPtSlot][outside] = above;
					else
						endpts[endPtSlot][outside] = below;
				}
	
				// Draw this calendar
				int pHeight = d.getHeight();
				int numEndPts = endpts.length;
				int availWidth = availWidth(visibleCals.size(), d.getWidth());
				for(int slot = 0; slot < numSlotsInDay; slot++) {
					Polygon poly = new Polygon();
					int endPtAbove = slot * 2;
					int endPtHere = slot * 2 + 1;
					int endPtBelow = slot * 2 + 2;
					int topY = endPtToHeight(endPtAbove, numEndPts, pHeight);
					int bottomY = endPtToHeight(endPtBelow, numEndPts, pHeight);
					int centerX = d.getWidth() / 2;
					int centerY = (topY + bottomY) / 2;
	
					// If we're on the left, want to subtract from center
					// Otherwise want to add to center
					int dir = -1;
					if(outside == 3)
						dir = 1;
	
					int outsideXMiddle = centerX + endpts[endPtHere][outside] * dir * availWidth;
					int insideXMiddle = centerX + endpts[endPtHere][inside] * dir * availWidth;
					int outsideXTop = centerX + endpts[endPtAbove][outside] * dir * availWidth;
					int insideXTop = centerX + endpts[endPtAbove][inside] * dir * availWidth;
					int outsideXBot = centerX + endpts[endPtBelow][outside] * dir * availWidth;
					int insideXBot = centerX + endpts[endPtBelow][inside] * dir * availWidth; 
	
					// Don't want to add top points
					if(! (outsideXTop == insideXTop && outsideXMiddle == insideXMiddle)) {
						poly.addPoint(outsideXTop, topY);
						poly.addPoint(insideXTop, topY);
					}
					
					if(! (outsideXMiddle == insideXMiddle))
						poly.addPoint(insideXMiddle, centerY);
					
					if(!(outsideXBot == insideXBot && outsideXMiddle == insideXMiddle)) {
						poly.addPoint(insideXBot, bottomY);
						poly.addPoint(outsideXBot, bottomY);		
					}
					if(! (outsideXMiddle == insideXMiddle))
						poly.addPoint(outsideXMiddle, centerY);
	
					brush.draw(poly);	
					brush.fill(poly);
				}
	
	
				// Move over endpts
				for(int j = 0; j < endpts.length; j++)
					endpts[j][inside] = endpts[j][outside];
			
			}
		}
		
	}
	
	public void setPaintMethod(PaintMethod method) {
		_paintMethod = method;
	}
	
	private void paintHeatMap(ArrayList<CalendarSlots> cals, Graphics2D brush, DayPanel d, int day) {
		Rectangle2D.Double rect;

		int numSlotsInDay = cals.get(0).getSlotsInDay();
		
		ArrayList<CalendarSlots> visibleCals = new ArrayList<CalendarSlots>();
		for(CalendarSlots cal : cals) {
			if(cal.isVisible())
				visibleCals.add(cal);
		}
		
		int numPeeps = visibleCals.size();
//		int opacity = MAX_SLOT_OPACITY / (numPeeps*numPeeps/2);
//		java.awt.Color adjOpacityColor = new java.awt.Color(SLOT_COLOR.getRed(), 
//				SLOT_COLOR.getGreen(), SLOT_COLOR.getBlue(), opacity);
		
		int[] numAvail = new int[numSlotsInDay];
		
		for(int i=0; i<numSlotsInDay; i++)
			numAvail[i] = 0;
		
		for(CalendarSlots cal : cals) {
			if(cal.isVisible()){
				Availability[][] avail = cal.getAvail();
				for (int i=0; i< numSlotsInDay; i++){
					if(avail[day][i]==Availability.free)
						numAvail[i] += 1;
				}
				
			}
		}
		
		
	
		for (int i=0; i< numSlotsInDay; i++){
			double iDbl = (double) i;
			double hDbl = (double) d.getHeight();
			double sDbl = (double) numSlotsInDay;

			if(numAvail[i] > 0){
				rect = new Rectangle2D.Double();
				double startY = iDbl * hDbl / sDbl; //(hrsDbl*4.0);
				rect.setFrame(0, startY, d.getWidth(), (double) (hDbl/ sDbl)); //(hrsDbl*4.0));
				
				int multiplier = 150/numPeeps;
				
				Color slotColor = //new Color(39 + numAvail[i], 255 - 8*numAvail[i], 39 + numAvail[i]);
									new Color(39 - 39*numPeeps/(numPeeps+2)*numAvail[i]/numPeeps, 255 - 255*numPeeps/(numPeeps+2)*numAvail[i]/numPeeps,39 - 39*numPeeps/(numPeeps+2)*numAvail[i]/numPeeps, 255*numPeeps/(numPeeps + 2));
				brush.setColor(slotColor);
				brush.fill(rect);
			}
		}
			

	}
	
	public void paint(Graphics2D brush, DayPanel d, int day){
		//_colors.clear();
		//initColors();

		ArrayList<CalendarSlots> cals = this.getCalendars();
		if(!cals.isEmpty()){
			if(_paintMethod == PaintMethod.Bars) {
				paintBars(cals, brush, d, day);
			} else if(_paintMethod == PaintMethod.HeatMap) {
				paintHeatMap(cals, brush, d, day);
			}
		}
	}
	
	public void printUpdates() {
		System.out.println("=== " + this.getName() + " ===");
		for(EventUpdate eventUpdate : this.getUpdates()){
			System.out.println(eventUpdate.getMessage());
		}
		System.out.println("=================");
	}
	

	
}
