import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.util.Timer;

public class OthelloPanel extends JPanel
{
	private OthelloPanel self;
	private long blackPieces;
	private long whitePieces;
	private long legalMoves;
	private long litSquare;
	private int state;

	public OthelloPanel()
	{
		self = this;
		blackPieces = 68853694464L;
		whitePieces = 34628173824L;
		state = 0;
		legalMoves = Board.getWhiteLegalMoves(blackPieces, whitePieces);
		litSquare = 0L;
		setPreferredSize(new Dimension(600, 630));
		addMouseMotionListener(new MouseMotionListener()
			{
				public void mouseMoved(MouseEvent e)
				{
					long location = 1L << (e.getX() / 75) << Math.min((e.getY() / 75 * 8), 56);
					if((location & legalMoves) != 0L && state == 0)
						litSquare = location;
					else
						litSquare = 0L;
					repaint();
				}
				public void mouseDragged(MouseEvent e)
				{
					long location = 1L << (e.getX() / 75) << Math.min((e.getY() / 75 * 8), 56);
					if((location & legalMoves) != 0L && state == 0)
						litSquare = location;
					else
						litSquare = 0L;
					repaint();
				}
			});
		addMouseListener(new MouseListener()
			{
				public void mouseClicked(MouseEvent e)
				{
				}
				public void mouseEntered(MouseEvent e)
				{
				}
				public void mouseExited(MouseEvent e)
				{
				}
				public void mousePressed(MouseEvent e)
				{
				}
				public void mouseReleased(MouseEvent e)
				{
					long location = 1L << (e.getX() / 75) << (e.getY() / 75 * 8);
					if((location & legalMoves) != 0L && state == 0)
					{
						whitePieces |= location;
						long flipPieces = Board.makeWhiteMove(location, blackPieces, whitePieces);
						blackPieces ^= flipPieces;
						whitePieces ^= flipPieces;
						litSquare = 0L;
						legalMoves = Board.getBlackLegalMoves(blackPieces, whitePieces);
						if(legalMoves == 0L)
						{
							legalMoves = Board.getWhiteLegalMoves(blackPieces, whitePieces);
							if(legalMoves == 0L)
								state = 2;
						}
						else
						{
							state = 1;
							new Timer().schedule(new BotMoveTask(self), 200L);
						}
						repaint();
					}
				}
			});
	}
	public void botMove()
	{
		if(state == 1)
		{
			long botMove = AI.computeBlackMove(blackPieces, whitePieces);
			blackPieces |= botMove;
			long flipPieces = Board.makeBlackMove(botMove, blackPieces, whitePieces);
			blackPieces ^= flipPieces;
			whitePieces ^= flipPieces;
			legalMoves = Board.getWhiteLegalMoves(blackPieces, whitePieces);
			if(legalMoves == 0L)
			{
				legalMoves = Board.getBlackLegalMoves(blackPieces, whitePieces);
				if(legalMoves == 0L)
					state = 2;
				else
					new Timer().schedule(new BotMoveTask(this), 200L);
			}
			else
				state = 0;
			repaint();
		}
	}
	public void reset()
	{
		blackPieces = 68853694464L;
		whitePieces = 34628173824L;
		state = 0;
		legalMoves = Board.getWhiteLegalMoves(blackPieces, whitePieces);
		litSquare = 0L;
		repaint();
	}
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(new Color(150, 150, 150));
		g.fillRect(0, 600, 600, 30);
		g.setColor(new Color(0, 0, 0));
		g.setFont(new Font("BoringFont", Font.BOLD, 20));
		FontMetrics fMetrics = g.getFontMetrics();
		int blackCount = 0;
		for(int k = 0; k < 64; ++k)
			if((blackPieces & (1L << k)) != 0L)
				++blackCount;
		int whiteCount = 0;
		for(int k = 0; k < 64; ++k)
			if((whitePieces & (1L << k)) != 0L)
				++whiteCount;
		int textHeight = 613 + fMetrics.getHeight() / 2;
		g.drawString("Black: " + blackCount, 10, textHeight);
		g.drawString("White: " + whiteCount, 590 - fMetrics.stringWidth("White: " + whiteCount), textHeight);
		if(state == 0)
			g.drawString("Your move", 300 - fMetrics.stringWidth("Your move") / 2, textHeight);
		else if(state == 1)
			g.drawString("Thinking...", 300 - fMetrics.stringWidth("Thinking...") / 2, textHeight);
		else if(state == 2 && blackCount > whiteCount)
			g.drawString("You lose!", 300 - fMetrics.stringWidth("You lose!") / 2, textHeight);
		else if(state == 2 && blackCount < whiteCount)
			g.drawString("You win!", 300 - fMetrics.stringWidth("You win!") / 2, textHeight);
		else if(state == 2 && blackCount == whiteCount)
			g.drawString("Tie game!", 300 - fMetrics.stringWidth("Tie game!") / 2, textHeight);
		g.setColor(new Color(0, 180, 0));
		g.fillRect(0, 0, 600, 600);
		g.setColor(new Color(100, 255, 100));
		if(litSquare != 0L)
			for(int y = 0; y < 8; ++y)
				for(int x = 0; x < 8; ++x)
					if((litSquare >>> (y * 8) >>> x) == 1L)
						g.fillRect(x * 75, y * 75, 75, 75);
		g.setColor(new Color(0, 0, 0));
		for(int k = 0; k < 9; ++k)
			g.fillRect(k * 75 - 1, 0, 3, 600);
		for(int k = 0; k < 9; ++k)
			g.fillRect(0, k * 75 - 1, 600, 3);
		for(int y = 0; y < 8; ++y)
			for(int x = 0; x < 8; ++x)
			{
				if((blackPieces & (1L << x << (y * 8))) != 0L)
					g.fillOval(75 * x + 6, 75 * y + 6, 63, 63);
			}
		g.setColor(new Color(255, 255, 255));
		for(int y = 0; y < 8; ++y)
			for(int x = 0; x < 8; ++x)
			{
				if((whitePieces & (1L << x << (y * 8))) != 0L)
					g.fillOval(75 * x + 6, 75 * y + 6, 63, 63);
			}
	}
}