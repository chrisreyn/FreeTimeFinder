package gui;

import static gui.GuiConstants.FRAME_HEIGHT;
import static gui.GuiConstants.FRAME_WIDTH;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.joda.time.DateTime;

import cal_master.Communicator;
import cal_master.NameIDPair;
import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.Event;
import calendar.When2MeetEvent;

public class CalendarGui {

	private CalendarGroup<CalendarResponses> _responseGroup = null;
	private Event _slotGroup = null;
	private int _startHour = 0;
	private int _numHours = 24;
	private JFrame _frame;
	private ReplyPanel _replyPanel;
	private JPanel _dayOfWeekLabels;
	private JPanel _hourOfDayLabels;
	private ArrayList<Integer> _hoursOfDay = new ArrayList<Integer>();
	private Communicator _communicator = new Communicator();
	private EventPanel _eventPanel = new EventPanel(_communicator, this);
	private UpdatesPanel _updatesPanel = new UpdatesPanel();
	private FriendBar _friendBar = new FriendBar(this);
	private JButton _submitButton = new JButton("Submit Response");
	private JButton _timeFindButton = new JButton("Find Best Times");
	private JButton _nextButton = new JButton("Next Week");
	private JButton _prevButton = new JButton("Previous Week");
	private JButton _refreshButton = new JButton("Refresh");
	public static enum DaysOfWeek {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday};

	public CalendarGui(){
		_communicator.startUp();

//		if(_communicator.hasEvent())  {
//
//			Event toReturn = _communicator.getW2M(_communicator.getFirstEventID());
//			_slotGroup = toReturn;
//			_slotGroup.init();
//			_startHour = _slotGroup.getStartTime().getHourOfDay();
//			_friendBar.setEvent(_slotGroup);
//		}
//		else {
			_startHour = 9;
			_friendBar.setEvent(null);
//		}

		if(_communicator.hasUserCal())
			_responseGroup=_communicator.getUserCal();
		
		_replyPanel = new ReplyPanel(_responseGroup, _slotGroup);

		ArrayList<NameIDPair> pairs = _communicator.getNameIDPairs();
		for(NameIDPair pair : pairs) {
			_eventPanel.addEvent(new EventLabel(pair.getName(), pair.getID(), _communicator, this));
		}

		
		_submitButton.addActionListener(new SubmitListener());
		_timeFindButton.addActionListener(new TimeFindListener());
		_nextButton.addActionListener(new NextListener());
		_prevButton.addActionListener(new PrevListener());
		
		_refreshButton.addActionListener(new RefreshListener());
		//_eventPanel.addEvent(new EventLabel("TESTING TESTING", "1234", _communicator, this));
		if(_slotGroup != null)
			_numHours = _slotGroup.getCalendars().get(0).getNumHours();
		else
			_numHours = 8;
//		makeDayLabels();
		makeHourLabels();
		buildFrame();
	}

	public Event getEvent(){
		return _slotGroup;
	}
	
	public void setEvent(Event event){
		_slotGroup= event;
		if(_slotGroup != null)
			_slotGroup.init();
		_responseGroup = _communicator.getUserCal();
		System.out.println("SLOT GROUP IN SET EVENT: " + _slotGroup);
		_replyPanel.setSlots(_slotGroup);
		System.out.println("Setting event for reply panel");
		_replyPanel.setResps(_responseGroup);
		_replyPanel.repaint();
		if(_slotGroup != null){
			_startHour = event.getStartTime().getHourOfDay();
			_numHours = event.getCalendars().get(0).getNumHours();
		}
		else{
			_startHour = 9;
			_numHours = 8;
		}
		_updatesPanel.setEvent(_slotGroup);
		_friendBar.setEvent(_slotGroup);
		updateHourLabels();
		_eventPanel.refresh();
		
	}

	
	public void setResponses(CalendarGroup<CalendarResponses> responseGroup){
		_responseGroup= responseGroup;
		_replyPanel.setResps(_responseGroup);
	}
//
//	public void updateDayLabels(){
//		_dayOfWeekLabels.removeAll();
//		_dayOfWeekLabels.setLayout(new GridLayout(1, 7, GuiConstants.LINE_SPACING, 0));
//
//		int counter=0;
//		for (DaysOfWeek d: DaysOfWeek.values()){
//			JPanel dayLabel = new JPanel();
//			dayLabel.add(new JLabel(d.name() +" "  + _thisMonday.plusDays(counter).monthOfYear().getAsShortText() + " " + _thisMonday.plusDays(counter).dayOfMonth().get(), SwingConstants.CENTER));
//			dayLabel.setBackground(GuiConstants.LABEL_COLOR);
//			_dayOfWeekLabels.add(dayLabel);
//			counter++;			
//		}
//		_dayOfWeekLabels.revalidate();
//		this.repaint();
//	}
//
//	public void makeDayLabels(){
//
//		_dayOfWeekLabels = new JPanel();
//		_dayOfWeekLabels.setBackground(GuiConstants.LINE_COLOR);
//		_dayOfWeekLabels.setLayout(new GridLayout(1, 7, GuiConstants.LINE_SPACING, 0));
//
//		int counter=0;
//		for (DaysOfWeek d: DaysOfWeek.values()){
//			System.out.println(d);
//			JPanel dayLabel = new JPanel();
//			dayLabel.add(new JLabel(d.name() +" "  + _thisMonday.plusDays(counter).monthOfYear().getAsShortText() + " " + _thisMonday.plusDays(counter).dayOfMonth().get(), SwingConstants.CENTER));
//			dayLabel.setBackground(GuiConstants.LABEL_COLOR);
//			_dayOfWeekLabels.add(dayLabel);
//			counter++;
//
//		}
//	}

	
	
	public void updateHourLabels(){
		_hourOfDayLabels.removeAll();
		_hourOfDayLabels.setLayout(new GridLayout(_numHours, 1, 0, 1));

		for (int i=_startHour; i<_startHour + _numHours; i++){
			JPanel hourLabel = new JPanel();
			hourLabel.add(new JLabel(i+ ":00", SwingConstants.CENTER), SwingConstants.CENTER);
			hourLabel.setBackground(GuiConstants.LABEL_COLOR);
			_hourOfDayLabels.add(hourLabel);
		}
		_hourOfDayLabels.revalidate();
		_hourOfDayLabels.repaint();
		this.repaint();
	}

	public void makeHourLabels(){
		_hourOfDayLabels = new JPanel();
		_hourOfDayLabels.setBackground(GuiConstants.LINE_COLOR);
		_hourOfDayLabels.setLayout(new GridLayout(_numHours, 1, 0, 1));
		_hourOfDayLabels.setBorder(new EmptyBorder(0,0,0,0));
		
		for (int i=_startHour; i<_startHour + _numHours; i++){
			JPanel hourLabel = new JPanel();
			hourLabel.add(new JLabel(i+ ":00", SwingConstants.CENTER), SwingConstants.CENTER);
			hourLabel.setBorder(new EmptyBorder(0,0,0,0));
			hourLabel.setBackground(GuiConstants.LABEL_COLOR);
			_hourOfDayLabels.add(hourLabel);
		}



	}


	private class InnerWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			//System.out.println("Window closing triggered");
			//_communicator.saveAll();
		}
	}

	public void buildFrame(){
		_frame = new JFrame("Kairos");
		_frame.addWindowListener(new InnerWindowListener());

		JPanel calPanel = new JPanel();
		GroupLayout calLayout = new GroupLayout(calPanel);
		calPanel.setLayout(calLayout);

		// TODO change to false
		_frame.setResizable(true);

		calLayout.setHorizontalGroup(
				calLayout.createSequentialGroup()
				.addComponent(_hourOfDayLabels, GroupLayout.PREFERRED_SIZE, _hourOfDayLabels.getPreferredSize().width,
						GroupLayout.PREFERRED_SIZE)
						.addComponent(_replyPanel, GroupLayout.PREFERRED_SIZE, (int) (FRAME_WIDTH*.75),
								GroupLayout.PREFERRED_SIZE));

		calLayout.setVerticalGroup(
				calLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(_replyPanel, GroupLayout.PREFERRED_SIZE, FRAME_HEIGHT - _replyPanel.getPreferredSize().height,
								GroupLayout.PREFERRED_SIZE)
								.addComponent(_hourOfDayLabels, GroupLayout.PREFERRED_SIZE, FRAME_HEIGHT - _replyPanel.getPreferredSize().height - _replyPanel.getWeekDayPanelHeight(),
										GroupLayout.PREFERRED_SIZE));

		_frame.add(calPanel, BorderLayout.CENTER);
		
		JPanel submitPanel = new JPanel();
		submitPanel.add(_submitButton);
		JPanel timeFindPanel = new JPanel();
		timeFindPanel.add(_timeFindButton);
		
		JPanel prevPanel = new JPanel();
		prevPanel.add(_prevButton);
		JPanel nextPanel = new JPanel();
		nextPanel.add(_nextButton);
		
		JPanel refreshPanel = new JPanel();
		refreshPanel.add(_refreshButton);
		
		
		JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
		buttonPanel.add(prevPanel);
		buttonPanel.add(nextPanel);
		buttonPanel.add(submitPanel);
		buttonPanel.add(timeFindPanel);
		buttonPanel.add(refreshPanel);
		
		JPanel northPanel = new JPanel(new GridLayout(2,1));
		northPanel.add(buttonPanel);
		northPanel.add(_friendBar);
		
		_frame.add(northPanel, BorderLayout.NORTH);

		JPanel eastPanel = new JPanel(new GridLayout(0,1));
		eastPanel.add(_eventPanel);
		eastPanel.add(_updatesPanel);
		eastPanel.setPreferredSize(new Dimension((int) (FRAME_WIDTH*.25 - _hourOfDayLabels.getPreferredSize().width), 700));
		_frame.add(eastPanel, BorderLayout.EAST);

		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		_frame.setVisible(true);
	}

	public void CreateEvent(String name, ArrayList<DateTime> dates, int startTime, int endTime){
		//TODO Finish this method
	}
	
	public void replyToEvent(){
		if(_slotGroup != null && _replyPanel.getClicks() != null)
			_communicator.submitResponse(Integer.toString(((When2MeetEvent) _slotGroup).getID()), _replyPanel.getClicks());
	}


	public void repaint(){
		if(_frame != null){
			_frame.invalidate();
			_frame.validate();
			_frame.repaint();
		}
	}

	public void setBestTimes(int duration){
		if(_slotGroup != null){
			CalendarResponses bestTimes = _communicator.getBestTimes(String.valueOf(_slotGroup.getID()), duration);
			_replyPanel.setBestTimes(bestTimes);
			repaint();
		}
	}
	
	private class SubmitListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int selection = JOptionPane.showConfirmDialog(null,"Are you sure you want to submit?", "", 
					JOptionPane.YES_NO_OPTION);
			if(selection == JOptionPane.YES_OPTION)
				replyToEvent();
			
		}
		
	}
	
	private class NextListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(_slotGroup != null)
				_replyPanel.nextWeek();
		}
		
	}
	private class PrevListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(_slotGroup != null)
				_replyPanel.prevWeek();
		}
		
	}
	
	private class TimeFindListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			SliderPane sliderPane = new SliderPane(_numHours, CalendarGui.this);
		}
		
	}
	
	private class RefreshListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			_communicator.refresh();
			// Retrieve this when2meet in case it has changed
			if(_slotGroup != null){
				setEvent(_communicator.getW2M(""+_slotGroup.getID()));
				System.out.println("After setting event in GUI");
				_slotGroup.printUpdates();
				System.out.println("=====");
			}
			
			repaint();
		}
		
		
		
	}

}
