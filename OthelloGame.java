import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class OthelloGame
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("RACE WAR!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		OthelloPanel p = new OthelloPanel();
		frame.getContentPane().add(p, BorderLayout.CENTER);
		Button b = new Button("New Game");
		b.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					p.reset();
				}
			});
		frame.getContentPane().add(b, BorderLayout.NORTH);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
	}
}