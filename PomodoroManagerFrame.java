// Pomodoro Manager
// Yarden Shoham 2018

// sound imports
import javax.sound.sampled.*;

// GUI imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// hashtable pomodoro counter imports
import java.util.Hashtable;
import java.io.*;

/**
* A program to use while working with the Pomodoro technique. It counts how many pomodoros were done every day and logs that information.
* This class presents the GUI aspect of the program. There is a countdown timer, a rest mode, a start\pause button as well as a rest button.
* One can use this program to track how many hours were used to work\study.
* @author Yarden Shoham
*/
public class PomodoroManagerFrame extends JFrame
{
	private String title = "Pomodoro Manager";

	private JButton playPauseButton;
	private JButton resetButton;

	private JButton[] modeButtons;

	private JLabel countdownLabel;

	private JLabel pomodoroCounter;
	private String pomodoroCounterBaseText = "Amount of Pomodoros done today: ";

	private enum modes { POMODORO, REST };
	private modes[] modesArray = modes.values();
	private modes currentMode;

	private boolean counting;
	private int remainingTime;

	private static final int[] times = { 25 * 60, 5 * 60 }; // minutes * seconds

	private String modeNames[] = { "Pomodoro", "Rest" };

	private Hashtable<SpecificDate, Integer> pomodoroAmountTable;
	private SpecificDate currentDate = new SpecificDate();
	private String logTableLocation = "table.bin";

	/**
	* This frame will be what the user will use to work with the program.
	*/
	public PomodoroManagerFrame()
	{
		setTitle(title);

		// getting number of pomodoros
		loadHashtable();

		// setting default time and mode
		remainingTime = times[0];
		currentMode = modesArray[0];

		// constructing
		resetButton = new JButton("RESET");
		playPauseButton = new JButton();

		modeButtons = new JButton[modesArray.length];
		for (int i = 0; i < modeButtons.length; i++) modeButtons[i] = new JButton(modeNames[i]);

		countdownLabel = new JLabel(formatTimeString(), SwingConstants.CENTER);
		pomodoroCounter = new JLabel(pomodoroCounterBaseText + pomodoroAmountTable.get(currentDate));

		// customizing
		countdownLabel.setFont(new Font("Serif", Font.PLAIN, 50));
		playPauseButton.setForeground(Color.WHITE);
		setState(counting);

		// placing everything in its place
		JPanel southPanel = new JPanel();
		southPanel.add(playPauseButton);
		southPanel.add(resetButton);

		JPanel northPanel = new JPanel(new GridLayout(2, 1));
		JPanel modesPanel = new JPanel();
		for (int i = 0; i < modeButtons.length; i++) modesPanel.add(modeButtons[i]);
		northPanel.add(modesPanel);
		northPanel.add(pomodoroCounter);

		add(northPanel, BorderLayout.NORTH);
		add(countdownLabel);
		add(southPanel, BorderLayout.SOUTH);

		handleHandlers();
	}
		

	/**
	* This method updates the START/PAUSE button's text and color according to its state as well as the program's state.
	* @param state false if the counter is paused and true if the counter is running.
	*/
	private void setState(boolean state)
	{
		counting = state;
		if (state)
		{
			playPauseButton.setText("PAUSE");
			playPauseButton.setBackground(Color.RED);
		}
		else
		{
			playPauseButton.setText("START");
			playPauseButton.setBackground(Color.GREEN);
		}
	}

	/**
	* Creating a time-formatted string.
	* @return a String MM:SS
	*/
	private String formatTimeString()
	{
		int seconds = remainingTime % 60;
		int minutes = remainingTime / 60;

		return (((minutes < 10) ? "0" + minutes : minutes) + ":" + ((seconds < 10) ? "0" + seconds : seconds));
	}

	/**
	* To initialize the handlers.
	*/
	private void handleHandlers()
	{
		Timer countingTimer = new Timer(1000, 
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					if (counting)
					{
						remainingTime--;
						updateFrameText(true);

						if (remainingTime == 0)
						{
							setState(false);
							finished();
						}
					}
				}
			}
			);
		countingTimer.start();

		playPauseButton.addActionListener(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					if (remainingTime != 0)
					{
						setState(!counting);
					}
				}
			}
			);

		resetButton.addActionListener(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					setState(false);
					for (int i = 0; i < modesArray.length; i++)
					{
						if (currentMode == modesArray[i])
						{
							remainingTime = times[i];
							break;
						}
					}
					updateFrameText(true);

				}
			}
			);

		ModeButtonsHandler modeButtonsHandler = new ModeButtonsHandler();
		for (int i = 0; i < modeButtons.length; i++)
		{
			modeButtons[i].addActionListener(modeButtonsHandler);
		}
	}

	/**
	* Updates the text of the frame, both the label's text and the title's.
	* @param changeFrameTitle true to update the frame's title with the remaining time and current mode; false to do nothing to the frame's title
	*/
	private void updateFrameText(boolean changeFrameTitle)
	{
		String formattedTime = formatTimeString();
		countdownLabel.setText(formattedTime);

		if (changeFrameTitle)
		{
			String mode = null;
			for (int i = 0; i < modesArray.length; i++)
			{
				if (currentMode == modesArray[i])
				{
					mode = modeNames[i];
					break;
				}
			}
			setTitle(formattedTime + " - " + mode);
		}
	}

	/**
	* When the countdown is finished this method will be called.
	*/
	private void finished()
	{
		try
		{
			// playing the beeping sound
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource("finishedSound.wav"));
			Clip finishedSound = AudioSystem.getClip();
			finishedSound.open(audioIn);
			finishedSound.start();

			if (currentMode == modes.POMODORO)
			{
				// update count
				Integer count = pomodoroAmountTable.get(currentDate);
				pomodoroAmountTable.put(currentDate, ++count);
				pomodoroCounter.setText(pomodoroCounterBaseText + count);
				
				// write updated table to file
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(logTableLocation));
				oos.writeObject(pomodoroAmountTable);
				oos.close();
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
		}


	}

	/**
	* Handles the loading of the hashtable.
	*/
	@SuppressWarnings("unchecked") // there is a cast inside, it is safe
	private void loadHashtable()
	{
		try
		{
			File temp = new File(logTableLocation);
			if (temp.exists() && !temp.isDirectory())
			{
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(logTableLocation));
				pomodoroAmountTable = (Hashtable<SpecificDate, Integer>) ois.readObject();
				if (!pomodoroAmountTable.containsKey(currentDate)) pomodoroAmountTable.put(currentDate, new Integer(0));
				ois.close();
			}
			else
			{
				pomodoroAmountTable = new Hashtable<>();
				pomodoroAmountTable.put(currentDate, new Integer(0));
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	/**
	* A handler for the mode buttons. Sets the program to its appropriate state.
	*/
	private class ModeButtonsHandler implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			setState(false);

			JButton source = (JButton) event.getSource();
			for (int i = 0; i < modeButtons.length; i++)
			{
				if (source == modeButtons[i])
				{
					currentMode = modesArray[i];
					remainingTime = times[i];
					break;
				}
			}
			updateFrameText(false);
			setTitle(title);
		}
	}

}