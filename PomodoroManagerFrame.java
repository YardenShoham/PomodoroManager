// Pomodoro Manager
// Yarden Shoham 2018

// sound imports
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

// GUI imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PomodoroManagerFrame extends JFrame
{
	private JButton playPauseButton;
	private JButton resetButton;

	private JButton[] modeButtons;

	private JLabel countdownLabel;

	private enum modes { POMODORO, REST };
	private modes[] modesArray = modes.values();
	private modes currentMode;

	private boolean counting;
	private int remainingTime;

	private static final int[] times = { 25 * 60, 5 * 60 }; // minutes * seconds

	private String modeNames[] = { "Pomodoro", "Rest" };

	private Clip finishedSound;

	/**
	* This frame will be what the user will use to work with the program.
	* @param title the title of the frame
	*/
	public PomodoroManagerFrame(String title)
	{
		super(title);

		// setting default time and mode
		remainingTime = times[0];
		currentMode = modesArray[0];

		// constructing
		resetButton = new JButton("RESET");
		playPauseButton = new JButton();

		modeButtons = new JButton[modesArray.length];
		for (int i = 0; i < modeButtons.length; i++)
		{
			modeButtons[i] = new JButton(modeNames[i]);
		}
		countdownLabel = new JLabel(formatTimeString(), SwingConstants.CENTER);

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
		for (int i = 0; i < modeButtons.length; i++)
		{
			modesPanel.add(modeButtons[i]);
		}
		northPanel.add(modesPanel);

		add(northPanel, BorderLayout.NORTH);
		add(countdownLabel);
		add(southPanel, BorderLayout.SOUTH);

		handleHandlers();

		// sound initialization
		try
		{
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource("finishedSound.wav"));
			finishedSound = AudioSystem.getClip();
			finishedSound.open(audioIn);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
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
						updateFrameText();

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
					updateFrameText();

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
	*/
	private void updateFrameText()
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
		String formattedTime = formatTimeString();

		countdownLabel.setText(formattedTime);
		setTitle(formattedTime + " - " + mode);
	}

	/**
	* When the countdown is finished this method will be called.
	*/
	private void finished()
	{
		try
		{
			finishedSound.start();
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
					remainingTime = times[i];
					break;
				}
			}
			updateFrameText();
		}
	}

}