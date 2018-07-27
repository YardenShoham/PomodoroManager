import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PomodoroManager
{
	public static void main(String[] args)
	{
		PomodoroManagerFrame frame = new PomodoroManagerFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setVisible(true);
	}
}