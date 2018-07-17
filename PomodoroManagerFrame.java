// Pomodoro Manager
// Yarden Shoham 2018

// GUI imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PomodoroManagerFrame extends JFrame
{
	private JButton playPauseButton;
	private JButton resetButton;
	private JLabel countdownLabel;

	private boolean counting;
	private enum modes { POMODORO, REST };
	private modes currentMode = modes.POMODORO;
	private int remainingTime;


	private static final int POMODORO_TIME = 25 * 60; // 25 minutes * 60 seconds
	private static final int REST_TIME = 5 * 60;

	/**
	* This frame will be what the user will use to work with the program.
	* @param title the title of the frame
	*/
	public PomodoroManagerFrame(String title)
	{
		super(title);

		// setting time starting with pomodoro
		remainingTime = POMODORO_TIME;

		// Placing everything in its place

		// constructing
		resetButton = new JButton("RESET");
		playPauseButton = new JButton();
		countdownLabel = new JLabel(formatTimeString(), SwingConstants.CENTER);

		// customizing
		countdownLabel.setFont(new Font("Serif", Font.PLAIN, 50));
		playPauseButton.setForeground(Color.WHITE);
		setButtonState(counting);


		JPanel southPanel = new JPanel();
		southPanel.add(playPauseButton);
		southPanel.add(resetButton);

		add(countdownLabel);
		add(southPanel, BorderLayout.SOUTH);

		handleHandlers();
	}

	/**
	* This method updates the START/PAUSE button's text and color according to its state.
	* @param state false if the counter is paused and true if the counter is running.
	*/
	private void setButtonState(boolean state)
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
						setFrameText();

						if (remainingTime == 0)
						{
							setButtonState(false);
							//finished(); TODO
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
					setButtonState(!counting);
					if (remainingTime <= 0)
					{
						switch (currentMode)
						{
							case POMODORO: remainingTime = POMODORO_TIME; break;
							case REST: remainingTime = REST_TIME; break;
						}
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
					setButtonState(false);
					switch (currentMode)
					{
						case POMODORO: remainingTime = POMODORO_TIME; break;
						case REST: remainingTime = REST_TIME; break;
					}
					setFrameText();

				}
			}
			);

	}

	/**
	* Updates the text of the frame, both the label's text and the title's.
	*/
	private void setFrameText()
	{
		String mode;
		switch (currentMode)
		{
			case POMODORO: mode = "Pomodoro"; break;
			case REST: mode = "Rest"; break;
			default: mode = "";
		}
		String formattedTime = formatTimeString();

		countdownLabel.setText(formattedTime);
		setTitle(formattedTime + " - " + mode);
	}
}