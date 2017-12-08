import javax.swing.JApplet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class OthelloApplet extends JApplet
{
	public OthelloApplet()
	{
		setLayout(new BorderLayout());
		OthelloPanel p = new OthelloPanel();
		getContentPane().add(p, BorderLayout.CENTER);
		Button b = new Button("New Game");
		b.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					p.reset();
				}
			});
		getContentPane().add(b, BorderLayout.NORTH);
		setVisible(true);
	}
}