package calendar;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;

public class When2MeetEvent extends CalendarGroup<CalendarSlots> {
	
	private String _name, _url;
	private int _id;
	private CalendarSlots _userResponse = null;
	private boolean _userHasSubmitted = false;
	private ArrayList<Integer> _slotIndexToSlotID = new ArrayList<Integer>();

	
	
	public When2MeetEvent(DateTime st, DateTime et, String name, int id, String url,
			Collection<CalendarSlots> cals, ArrayList<Integer> slotIndToID){
		super(st, et, cals, CalGroupType.When2MeetEvent);
		_name = name;
		_id = id;
		_url = url;
		_slotIndexToSlotID = slotIndToID;
	}
	
	public void setID(int id) { _id = id; };
	public void setURL(String url) { _url = url; }
	public void setName(String name) {_name = name; }
	public void setUserResponse(CalendarSlots cal) { _userResponse = cal; }
	public void setUserSubmitted(boolean b) {_userHasSubmitted = b; }
	
	public String getURL(){
		return _url;
	}
	
	public int getID(){
		return _id;
	}
	
	public String getName(){
		return _name;
	}
	
	public CalendarSlots getUserResponse() {
		return _userResponse;
	}
	
	public boolean userHasSubmitted(){
		return _userHasSubmitted;
	}
	
	public CalendarSlots getCalByName(String name) {
		CalendarSlots cal = null;
		for(int i = 0; i < this.getCalendars().size(); i++) { 
			if(this.getCalendars().get(i).getOwner().getName().equalsIgnoreCase(name)) {
				cal = this.getCalendars().get(i);
				break;
			}
		}
		return cal;
	}
	
	public void removeCalByName(String name){
		this.getCalendars().remove(getCalByName(name));
	}
	
	public int getSlotID(int slotIndex) {
		try {
			return _slotIndexToSlotID.get(slotIndex);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Invalid slot index for getting ID");
			return -1;
		}
	}


}
