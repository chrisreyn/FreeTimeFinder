package calendar;

import static gui.GuiConstants.SLOT_COLOR;


import gui.DayPanel;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class CalendarSlots implements Calendar {
	private DateTime _startTime;
	private DateTime _endTime;
	private int _minInSlot;
	private int _numSlotsInDay;
	private int _numDays;
	private When2MeetOwner _owner;
	private Availability[][] _avail;
	private boolean _isVisible = true;

	public CalendarSlots(DateTime startTime, DateTime endTime, int minInSlot, Availability initAvail) {
		_startTime = startTime;
		_endTime = endTime;
		_numSlotsInDay = (lenDayInMinutes() + 1) / minInSlot;
		assert _numSlotsInDay % 4 == 0;
		_numDays = numDays();
		_minInSlot = minInSlot;		
		_avail = new Availability[_numDays][_numSlotsInDay];
		for(int day = 0; day < _numDays; day++)
			for(int slot = 0; slot < _numSlotsInDay; slot++)
				_avail[day][slot] = initAvail;
	}

	public int getDays() {
		return _numDays;
	}
	
	public CalendarSlots(DateTime startTime, DateTime endTime, When2MeetOwner owner, int minInSlot, Availability[][] availability){
		this(startTime, endTime, minInSlot, Availability.free);
		_owner = owner;
		assert availability.length == _numDays;
		_avail = availability;
	}

	// Need absolute value in case endtime is midnight
	public int lenDayInMinutes() {
		if(_endTime.getMinuteOfDay() == 0)
			return 24*60 - _startTime.getMinuteOfDay();
		else
			return _endTime.getMinuteOfDay() - _startTime.getMinuteOfDay();
	}

	public int numDays() {
		if(_endTime.getYear() == _startTime.getYear())
			return _endTime.getDayOfYear() - _startTime.getDayOfYear() + 1;
		else if(_endTime.getYear() == _startTime.getYear() + 1)
			return _endTime.getDayOfYear() + 366 - _startTime.getDayOfYear();
		System.err.println("err in numDays in CalendarSlotsImpl");
		System.exit(1);
		return -1;
	}
	
	public void setVisible(boolean b){
		_isVisible = b;
	}
	
	public boolean isVisible(){
		return _isVisible;
	}

	@Override
	public DateTime getStartTime() { return _startTime; }

	@Override
	public DateTime getEndTime() { return _endTime;	}

	public When2MeetOwner getOwner() { return _owner; }

	public int getSlotsInDay() { 
		assert _numSlotsInDay % 4 == 0;
		return _numSlotsInDay; }

	public int getTotalSlots() { 
		assert _numSlotsInDay % 4 == 0;
		return _numDays * _numSlotsInDay; }

	public Availability[][] getAvail() {
		return _avail;
	}

	public Availability getAvail(int day, int slot) {
		return _avail[day][slot];
	}

	public Availability getAvail(int slot) {
		int day = slot / _numSlotsInDay;
		int slotInDay = slot % _numSlotsInDay;
		return _avail[day][slotInDay];
	}
	public ArrayList<Integer> getSlotsForAvail(Availability avail){
		ArrayList<Integer> toReturn = new ArrayList<Integer>();

		for(int slot = 0; slot < getTotalSlots(); slot++) {
			if(getAvail(slot) == avail)
				toReturn.add(new Integer(slot));
		}
		return toReturn;
	}

	public void setAvail(int day, int slot, Availability avail) {
		_avail[day][slot] = avail;
		return;
	}

	public void setAvail(int slot, Availability avail) {
		int day = slot / _numSlotsInDay;
		int slotInDay = slot % _numSlotsInDay;
		// System.out.println("Day: " + day + " Slot in Day: " + slotInDay);
		_avail[day][slotInDay] = avail;
	}

	public void setOwner(When2MeetOwner o) { _owner = o; }

	public void print() {
		for(int slotInDay = 0; slotInDay < _numSlotsInDay; slotInDay++){
			if(slotInDay % 4 == 0)
				System.out.println("=========");
			for(int day = 0; day < _numDays; day++) {
				if(_avail[day][slotInDay] == Availability.busy)
					System.out.print("b");
				else
					System.out.print("f");
			}
			System.out.println();
		}
	}

	private int timeToSlot(DateTime time, boolean roundEarly) {
		assert time.compareTo(_startTime) >= 0 : "Time before start of calendar";
		assert time.compareTo(_endTime) <= 0 : "Time after end of calendar";

		assert _numSlotsInDay % 4 == 0;
		
		int daysOff = time.getDayOfYear() - _startTime.getDayOfYear();
		int minutesOff = time.getMinuteOfDay() - _startTime.getMinuteOfDay();
		if(roundEarly)
			return daysOff * _numSlotsInDay + minutesOff / _minInSlot;
		else
			return daysOff * _numSlotsInDay + minutesOff / _minInSlot + 1;
	}

	//TODO this may fail if endTime is the same as the end of the calendar

	public void setAvail(DateTime startTime, DateTime endTime, Availability avail) {
		int startSlot = timeToSlot(startTime, true);
		int endSlot = timeToSlot(endTime, false);

		assert startSlot >= 0 : "Negative start slot";
		assert startSlot < _numDays * _numSlotsInDay : "Start slot greater than number of slots in cal";
		assert endSlot >= 0 : "Negative end slot";
		assert endSlot < _numDays * _numSlotsInDay : "End slot greater than number of slots in cal";

		for(int slot = startSlot; slot < endSlot; slot++)
			setAvail(slot, avail);

	}


	public int getMinInSlot() {
		return _minInSlot;
	}
	
	public int getNumHours() {
		assert _numSlotsInDay % 4 == 0;
		return _minInSlot * _numSlotsInDay / 60;
	}
	
	public static int getDaysBetween(DateTime start, DateTime end){
		if(end.getYear() == start.getYear())
			return end.getDayOfYear() - start.getDayOfYear();
		else if(end.getYear() == start.getYear() + 1)
			return end.getDayOfYear() + 366 - start.getDayOfYear();
		return -1;
	}
	
	
	public void paint(Graphics2D brush, DayPanel d){
		Rectangle2D.Double rect;
		assert _numSlotsInDay % 4 == 0;

		if(_isVisible){
			int numDays = this.getDaysBetween(_startTime, d.getDay());
			if(numDays >= 0 && numDays < _numDays){
				for (int i=0; i< _numSlotsInDay; i++){
					double iDbl = (double) i;
					double hDbl = (double) d.getHeight();
					double hrsDbl = (double) this.getNumHours(); // d.getNumHours();
					double sDbl = (double) _numSlotsInDay;
					
					if (_avail[numDays][i]==Availability.free){
						rect = new Rectangle2D.Double();
						double startY = iDbl * hDbl / sDbl; //(hrsDbl*4.0);
						rect.setFrame(0, startY, d.getWidth(), hDbl/ sDbl); //(hrsDbl*4.0));
						brush.setColor(SLOT_COLOR);
						brush.draw(rect);
						brush.fill(rect);
					}
				}
			}
		}
	}


}
