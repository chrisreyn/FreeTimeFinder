package gui;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.joda.time.DateTime;

/**
 * Class that contains dialog box that requests fields necessary to create an event
 * @author roie
 *
 */
public class CreateEventDialog{

	private JFrame _parent;
	private JPanel _calendar;
	private JPanel _calPane;
	private JLabel _monthAndYear;
	// text field holding the event name
	private JTextField _eventName;

	private JDialog _dialog;
	private EventPanel _eventPanel;
	//Information needed to build calendar
	private DateTime _today;
	private DateTime _firstOfMonth;
	// Array representing selected dates
	private ArrayList<DateTime> _selectedDates = new ArrayList<DateTime>();

	//Exit buttons
	private JButton _okButton;
	private JButton _cancelButton;

	// Fields for selected start and end hour
	private JComboBox _startHour;
	private JComboBox _endHour;
	private ImageIcon _kairosIcon = new ImageIcon(getClass().getResource("KairosIcon.png"));
	public static enum DaysOfWeek {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday};

	public CreateEventDialog(EventPanel eventPanel){
		_today = new DateTime();
		_calendar = new JPanel();
		_eventPanel = eventPanel;

		_calPane = new JPanel();
		_firstOfMonth = _today.minusDays(_today.getDayOfMonth()-1);
		_dialog = new JDialog();
		_parent = new JFrame();

		_monthAndYear = new JLabel(_firstOfMonth.monthOfYear().getAsText() + " " + _firstOfMonth.getYear());

		_calendar.setLayout(new GridLayout(0, 7, 0, 0));
		buildCalendar();

		JButton prev = new JButton("<");
		prev.setActionCommand("<");
		JButton next = new JButton(">");
		next.setActionCommand(">");
		NextPrevListener npl = new NextPrevListener();
		prev.addActionListener(npl);
		next.addActionListener(npl);

		JPanel monthYearPane = new JPanel();
		monthYearPane.add(_monthAndYear);

		_calPane.add(prev, BorderLayout.EAST);
		_calPane.add(_calendar, BorderLayout.CENTER);
		_calPane.add(next, BorderLayout.WEST);
		_calPane.revalidate();

		_eventName = new JTextField();
		_eventName.setText("New Event Name");
		_eventName.addFocusListener(new myFocusListener());
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new GridLayout (0,3));
		namePanel.add(new JPanel());
		namePanel.add(_eventName);

		// Build selection options for start and end times
		String[] strings = new String[25];
		for (int i=0; i<=24; i++){
			if (i==0) {
				strings[i] = "midnight";
			}
			else if (i ==12){
				strings[i]="noon";
			}
			else if (i ==24) {
				strings[i] = "midnight ";
			}
			else {
				strings[i] = new String (Integer.toString(i) + ":00");
			}
		}

		_startHour = new JComboBox(strings);
		_startHour.setSelectedIndex(10);

		_endHour = new JComboBox(strings);
		_endHour.setSelectedIndex(20);

		JPanel times = new JPanel();
		times.setLayout(new GridLayout(2,2));
		times.add(new JLabel("From:"));
		times.add(_startHour);
		times.add(new JLabel("To:"));
		times.add(_endHour);

		_dialog = new JDialog(_parent, "Choose Dates");
		_dialog.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		_okButton = new JButton ("OK");
		_okButton.setActionCommand("OK");
		_cancelButton = new JButton ("Cancel");
		_cancelButton.setActionCommand ("Cancel");
		ExitListener el = new ExitListener();
		_okButton.addActionListener(el);
		_cancelButton.addActionListener(el);
		JPanel submitPanel = new JPanel();
		submitPanel.add(_okButton);
		submitPanel.add(_cancelButton);

		//Layout information

		c.insets = new Insets(10, 0, 5, 0);

		c.weighty = 0.0;
		c.weightx = 1.0;	
		c.gridx = 0;
		c.gridy = 0;
		_dialog.add(new JLabel("What is the name of the event you are planning?"), c);

		c.weighty = 0.0;
		c.weightx = 1.0;	
		c.gridx = 0;
		c.gridy = 1;
		_dialog.add(namePanel, c);

		c.weighty = 0.0;
		c.weightx = 1.0;	
		c.gridx = 0;
		c.gridy = 2;
		c.ipady = 0;
		_dialog.add(new JLabel ("What dates might work?"), c);

		c.weighty = 0.0;
		c.weightx = 1.0;	
		c.gridx = 0;
		c.gridy = 3;
		c.ipady = 0;
		_dialog.add(monthYearPane, c);

		c.weighty = 0.0;
		c.weightx = 1.0;	
		c.gridx = 0;
		c.gridy = 4;
		_dialog.add(_calPane, c);

		c.weighty = 0.0;
		c.weightx = 1.0;	
		c.gridx = 0;
		c.gridy = 5;
		c.ipady = 0;
		_dialog.add(new JLabel ("What Times?"), c);		

		c.weighty = 0.0;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 6;
		c.ipady=0;
		_dialog.add(times, c);

		c.weighty = 0.0;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 7;
		_dialog.add(submitPanel, c);

		_dialog.pack();
		_dialog.setLocationRelativeTo(null);
		_dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		_dialog.setResizable(false);
		_dialog.setVisible(true);
	}

	/**
	 * Method that builds the mini-calendar representation inside the create event dialog box
	 */
	private void buildCalendar(){
		_calendar.removeAll();
		_monthAndYear.setText(_firstOfMonth.monthOfYear().getAsText() + " " + _firstOfMonth.getYear());

		// Adds day of week labels
		for (DaysOfWeek d : DaysOfWeek.values()){
			JPanel p = new JPanel();
			p.add(new JLabel(d.name()));
			_calendar.add(p);
		}

		// Adds spacing panels
		for (int i=1; i<_firstOfMonth.getDayOfWeek(); i++){
			_calendar.add(new JPanel());
		}

		int maxDay = _firstOfMonth.dayOfMonth().getMaximumValue();
		// Adds days of the month
		for (int i = 0; i<maxDay; i++){

			DateButton button = new DateButton(Integer.toString(_firstOfMonth.plusDays(i).getDayOfMonth()),
					_firstOfMonth.plusDays(i));
			button.addActionListener(new DateListener());
			if (button.getDate().isBefore(_today)){
				button.setEnabled(false);
			}
			for (DateTime date: _selectedDates){
				if (button.getDate().equals(date))
					button.setSelected(true);
			}
			_calendar.add(button);
		}

		_dialog.invalidate();
		_dialog.validate();
		_dialog.pack();
		_calendar.revalidate();
	}

	/**
	 * Listens to clicks on days of the month
	 * @author roie
	 *
	 */
	private class DateListener implements ActionListener{

		// On click toggle whether date is in the selected dates array
		@Override
		public void actionPerformed(ActionEvent e) {

			if (((DateButton) e.getSource()).isSelected()==true)
				_selectedDates.add(((DateButton) e.getSource()).getDate());
			else {
				_selectedDates.remove(((DateButton) e.getSource()).getDate());
			}
		}

	}

	// Listens to nextmonth/ previousmonth button
	private class NextPrevListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("<")){
				_firstOfMonth = _firstOfMonth.minusMonths(1);
				_firstOfMonth = _firstOfMonth.minusDays(_firstOfMonth.getDayOfMonth()-1);
				buildCalendar();
			}
			else if (e.getActionCommand().equals(">")){
				_firstOfMonth = _firstOfMonth.plusMonths(1);
				_firstOfMonth = _firstOfMonth.plusDays(_firstOfMonth.getDayOfMonth()-1);
				buildCalendar();
			}
		}
	}	

	/**
	 * Focus listener for name box
	 * If focus is lost and field has not been filled, fill with "New Event Name"
	 * If focus is gained and field is "New Event Name", set to blank
	 * @author roie
	 *
	 */

	private class myFocusListener implements FocusListener{

		@Override
		public void focusGained(FocusEvent e) {
			if (((JTextField) e.getSource()).getText().equals("New Event Name")){
				((JTextField) e.getSource()).setText("");
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			if (((JTextField) e.getSource()).getText().equals("")){
				((JTextField) e.getSource()).setText("New Event Name");
			}
		}
	}	

	/**
	 * Listener for exit buttons
	 * @author roie
	 *
	 */
	private class ExitListener implements ActionListener{

		/**
		 * On click check that all fields have been filled out correctly, and create event using filled out fields.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("OK")){
				if (_eventName.getText().equals("New Event Name")){
					JOptionPane.showMessageDialog(new JFrame(), "Please choose a name for your event.", "", JOptionPane.ERROR_MESSAGE, _kairosIcon);
				} else if (_selectedDates.size() == 0){
					JOptionPane.showMessageDialog(new JFrame(), "Please choose dates for your event.", "", JOptionPane.ERROR_MESSAGE, _kairosIcon);
				} else if (_startHour.getSelectedIndex() >= _endHour.getSelectedIndex()){
					JOptionPane.showMessageDialog(new JFrame(), "Please choose appropriate times for your event.", "", JOptionPane.ERROR_MESSAGE, _kairosIcon);
				} else {
					_dialog.dispose();
					Collections.sort(_selectedDates);

					_eventPanel.createEvent(_eventName.getText(), _selectedDates, _startHour.getSelectedIndex(), _endHour.getSelectedIndex());
				}
			}
			else if (e.getActionCommand().equals("Cancel")){
				_dialog.dispose();
			}
		}
	}


	private class DateButton extends JToggleButton{

		private DateTime _date;

		public DateButton (String s, DateTime date){
			super(s);
			_date = date;
		}

		public DateTime getDate(){
			return _date;
		}
	}


}
