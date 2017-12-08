import java.util.TimerTask;

public class BotMoveTask extends TimerTask
{
	private OthelloPanel p;

	public BotMoveTask(OthelloPanel panel)
	{
		p = panel;
	}
	public void run()
	{
		p.botMove();
	}
}