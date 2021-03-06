package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.joda.time.DateTime;

import cal_master.Communicator;
import cal_master.Communicator.URLAlreadyExistsException;
import calendar.Event;
import calendar_importers.EventImporter.InvalidURLException;

/**
 * 
 * Represents the panel that holds all of the event names the user has stored in the program, as well as the
 * buttons they can use to add an existing event to the program, as well as the buttons to remove events from the program,
 * and links to actual w2ms of each event
 *
 */

public class EventPanel extends JPanel {

	private ArrayList<EventLabel> _eventLabels = new ArrayList<EventLabel>();
	private ArrayList<RemoveEventLabel> _removeLabels = new ArrayList<RemoveEventLabel>();
	private ArrayList<OpenEventInBrowserLabel> _openLabels = new ArrayList<OpenEventInBrowserLabel>();
	private Communicator _communicator;
	private JButton _addButton;
	private JButton _createButton;
	private CalendarGui _gui;
	private JLabel _titleLabel;
	private JScrollPane _eventsScrollPane;
	private JPanel _scrollPaneInner = new JPanel();
	private GroupLayout _layout= new GroupLayout(this), _spiLayout = new GroupLayout(_scrollPaneInner);
	private ImageIcon _kairosIcon = new ImageIcon(getClass().getResource("KairosIcon.png"));
	
	public EventPanel(Communicator communicator, CalendarGui gui){
		_communicator = communicator;
		_gui = gui;
		_addButton = new JButton("Add Event");
		_addButton.addActionListener(new AddEventListener());
		_addButton.setFont(new Font(GuiConstants.FONT_NAME, _addButton.getFont().getStyle(), _addButton.getFont().getSize()));
		
		_createButton = new JButton ("Create Event");
		_createButton.addActionListener(new CreateEventListener());
		_createButton.setFont(new Font(GuiConstants.FONT_NAME, _createButton.getFont().getStyle(), _createButton.getFont().getSize()));
		
		_titleLabel = new JLabel("My Events");
		Font newLabelFont=new Font(GuiConstants.FONT_NAME,Font.BOLD,
				_titleLabel.getFont().getSize());  

		_titleLabel.setFont(newLabelFont);
		
		GridLayout sPILayout = new GridLayout(10, 2);
		sPILayout.setVgap(0);
		
		_scrollPaneInner.setLayout(sPILayout);
		_eventsScrollPane = new JScrollPane(_scrollPaneInner);
		
		this.setUp();
	}
	
	/**
	 * Sets up display (clearing what's there and rebuilding the layouts)
	 */
	private void setUp() {
		_scrollPaneInner.removeAll();
		_scrollPaneInner.setLayout(_spiLayout);
		_spiLayout.setAutoCreateGaps(true);
		_spiLayout.setAutoCreateContainerGaps(true);
		
		SequentialGroup spiVSGrp = _spiLayout.createSequentialGroup();
		ParallelGroup spiHPGrp =  _layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		
		int i=0;
		if(_eventLabels.size() > 0)
			for(EventLabel label : _eventLabels){
				RemoveEventLabel rLabel = _removeLabels.get(i);
				OpenEventInBrowserLabel oLabel = _openLabels.get(i);
				spiVSGrp.addGroup(_spiLayout.createParallelGroup().addComponent(rLabel).addComponent(oLabel).addComponent(label));
				spiHPGrp.addGroup(_spiLayout.createSequentialGroup().addComponent(rLabel).addComponent(oLabel).addComponent(label));
				i++;
			}
		
		_spiLayout.setVerticalGroup(spiVSGrp);
		_spiLayout.setHorizontalGroup(spiHPGrp);
		
		this.setLayout(_layout);
		_layout.setAutoCreateGaps(true);
		_layout.setAutoCreateContainerGaps(true);
	
		
		SequentialGroup vertSeqGrp = _layout.createSequentialGroup();
		ParallelGroup horizParGrp = _layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		
		vertSeqGrp.addComponent(_titleLabel);
		horizParGrp.addComponent(_titleLabel);
	
		horizParGrp.addComponent(_eventsScrollPane);
		vertSeqGrp.addComponent(_eventsScrollPane);
		
		horizParGrp.addComponent(_addButton);
		vertSeqGrp.addComponent(_addButton);
		
		horizParGrp.addComponent(_createButton);
		vertSeqGrp.addComponent(_createButton);

		_layout.setHorizontalGroup(horizParGrp);
		_layout.setVerticalGroup(vertSeqGrp);
		
		this.revalidate();
		_gui.repaint();
	}
	
	
	public void addEvent(EventLabel label){
		_eventLabels.add(label);
		_removeLabels.add(new RemoveEventLabel(label.getID()));
		_openLabels.add(new OpenEventInBrowserLabel(label.getID()));
		setUp();
	}
	
	public void addEvent(Event newEvent){
		EventLabel newLabel = new EventLabel(newEvent.getName(), String.valueOf(newEvent.getID()), _communicator, _gui);
		_eventLabels.add(newLabel);
		_removeLabels.add(new RemoveEventLabel(newLabel.getID()));
		_openLabels.add(new OpenEventInBrowserLabel(newLabel.getID()));
		setUp();
	}
	
	public void createEvent(String name, ArrayList<DateTime> selectedDates, int startHour, int endHour){
		try {
			addEvent(_communicator.createEvent(name, selectedDates, startHour, endHour));
		} catch (URLAlreadyExistsException e) {
			System.out.println("You did something wrong");
		} catch (IOException e) {
			System.out.println("You did something wrong");
		} catch (InvalidURLException e) {
			System.out.println("You did something wrong");
		}
	}
	
	public void addEvents(ArrayList<EventLabel> events){
		_eventLabels.addAll(events);
		for(EventLabel label : events){
			_removeLabels.add(new RemoveEventLabel(label.getID()));
			_openLabels.add(new OpenEventInBrowserLabel(label.getID()));
		}
		setUp();
		_gui.repaint();
	}
	
	/**
	 * Remove event based on id. Called by RemoveEventLabel associated with this event
	 * @param idToRemove - id f event to remove
	 */
	public void removeEvent(String idToRemove){
		
		//search through list for the event with given id
		for(int i=0; i<_eventLabels.size(); i++){
			if(_eventLabels.get(i).getID() == idToRemove){
				_eventLabels.remove(i);
				_removeLabels.remove(i);
				_openLabels.remove(i);
				
				//If gui is displaying this event, set the gui's event to NULL
				if(_gui.getEvent() != null && String.valueOf(_gui.getEvent().getID()).equals(idToRemove)){
					//System.out.println("Event Panel Setting Event to Null");
					_gui.setEvent(null);
					_gui.repaint();
				}
				break;
			}
		}
		//System.out.println("NUM LABELS: " + _eventLabels.size());
		setUp();
	}
	
	public void openEventInBrowser(String idToOpen){

		Event toOpen = _communicator.getEventByID(idToOpen);
		
		if(toOpen != null && toOpen.getURL() != null){
			URI uri;
			try {
				uri = new URI(toOpen.getURL());
				java.awt.Desktop.getDesktop().browse(uri);
			} catch (URISyntaxException e) {
			} catch (IOException e) {
			}
		}
				
	}
	
	public void refresh() {
		for(EventLabel label : _eventLabels){
			label.refresh();
		}
	}
	
	public void setSelectedEvent(String eventID){
		for(EventLabel label : _eventLabels){
			if(label.getID().equals(eventID))
				label.setSelected(true);
			else
				label.setSelected(false);
		}
		this.repaint();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		int i=0;
		for(EventLabel label: _eventLabels){
			label.repaint();
			_removeLabels.get(i).repaint();
			_openLabels.get(i).repaint();
			i++;
		}
	}
	
	/**
	 * Click Listener for "Create Event" button
	 *
	 */
	private class CreateEventListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			URL url = null;
			try {
				url = new URL("http://www.google.com");
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			if (Communicator.webConnected(url)) {
				new CreateEventDialog(EventPanel.this);
			}
			else {
				JOptionPane.showMessageDialog(null, "You are not connected to the Internet.\nKairos cannot create a new event.", "Connection Error", JOptionPane.ERROR_MESSAGE, _kairosIcon);
			}
		}
		
	}
	
	/**
	 * ClickListener for the "Add Event" button
	 *
	 */
	private class AddEventListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			URL testURL = null;
			try {
				testURL = new URL("http://www.google.com");
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			if (Communicator.webConnected(testURL)) {
				String url = JOptionPane.showInputDialog("Please enter the URL of the When2Meet you would like to add");
				
				Event newEvent = null;
				boolean noEvent = true;
				while(noEvent){
					try {
						if(url == null)
							break;
						newEvent = _communicator.addEvent(url);
						noEvent = false;
					} catch (MalformedURLException e) {
						url = JOptionPane.showInputDialog("Invalid URL. Try Again.");
					} catch (URLAlreadyExistsException e) {
						url = JOptionPane.showInputDialog("That event is already stored. Try Again.");
					} catch (IOException e) {
						url = JOptionPane.showInputDialog("Invalid URL. Try Again.");
					} catch (InvalidURLException e) {
						url = JOptionPane.showInputDialog("Invalid URL. Try Again.");
					}
				}
				
				if(newEvent != null){
					if(newEvent.getName() == null){
						System.err.println("Event has no name, something has gone wrong.");
					}
					addEvent(newEvent);
				}
				
				
			}
			else {
				JOptionPane.showMessageDialog(null, "You are not connected to the Internet.\nKairos cannot add an event.", "Connection Error", JOptionPane.ERROR_MESSAGE,_kairosIcon);
			}
		}
		
		
	}
	
	/**
	 * Represents the "X" the user clicks to remove an event from the program
	 *
	 */
	private class RemoveEventLabel extends JLabel implements MouseListener{
		
		private String _eventID;
		
		public RemoveEventLabel(String id){
			super("X");
			_eventID = id;
			this.addMouseListener(this);
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			int selection = JOptionPane.showConfirmDialog(null,"Are you sure you want to remove this event?", "", 
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, _kairosIcon);
			if(selection == JOptionPane.YES_OPTION){
				_communicator.removeEvent(_eventID);
				removeEvent(_eventID);
			}
		}

		/*
		 * Color-change alerts user to mouse-over (whether or not the mouse is in a position to click on it)
		 */
		
		@Override
		public void mouseEntered(MouseEvent arg0) {
			this.setForeground(Color.RED);
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			this.setForeground(Color.BLACK);
		}

		@Override
		public void mousePressed(MouseEvent arg0) {}

		@Override
		public void mouseReleased(MouseEvent arg0) {}
		
	}
	
	private class OpenEventInBrowserLabel extends JLabel implements MouseListener {

		private String _eventID;
		
		
		public OpenEventInBrowserLabel(String eventID){
//			super("O");
			_eventID = eventID;
			this.addMouseListener(this);
			ImageIcon icon = new ImageIcon(getClass().getResource("open-in-new-window.png"));
			this.setIcon(icon);
			
		}


		@Override
		public void mouseClicked(MouseEvent arg0) {
			openEventInBrowser(_eventID);
		}


		@Override
		public void mouseEntered(MouseEvent arg0) {
			this.setForeground(Color.GREEN);
		}


		@Override
		public void mouseExited(MouseEvent arg0) {
			this.setForeground(Color.BLACK);
		}


		@Override
		public void mousePressed(MouseEvent arg0) { }


		@Override
		public void mouseReleased(MouseEvent arg0) { }
		
	}
}
