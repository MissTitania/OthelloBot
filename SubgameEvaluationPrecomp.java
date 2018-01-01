import java.io.PrintWriter;

public class SubgameEvaluationPrecomp
{
	public static void main(String[] args)
	{
		try
		{
			PrintWriter writer = new PrintWriter("SubgamePrecomps.txt", "UTF-8");
			for(int k = 1; k <= 8; ++k)
			{
				byte[] allMoveComps = SubgameEvaluationPrecomp.generateRowPrecompAllMoves(k);
				byte[] legalMoveComps = SubgameEvaluationPrecomp.generateRowPrecompLegalMoves(k);
				for(int j = 0; j < allMoveComps.length; ++j)
					writer.print(allMoveComps[j] + ",");
				writer.println();
				for(int j = 0; j < legalMoveComps.length; ++j)
					writer.print(legalMoveComps[j] + ",");
				writer.println();
			}
			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static byte[] generateRowPrecompAllMoves(int length)
	{
		byte[] results = new byte[(int) (Math.pow(3, length) + .5)];
		for(int k = 0; k < results.length; ++k)
			results[k] = translatePositionAllMoves(length, k);
		return results;
	}
	public static byte[] generateRowPrecompLegalMoves(int length)
	{
		byte[] results = new byte[(int) (Math.pow(3, length) + .5)];
		for(int k = 0; k < results.length; ++k)
			results[k] = translatePositionLegalMoves(length, k);
		return results;
	}
	private static byte translatePositionAllMoves(int length, int position)
	{
		int blackBoard = 0;
		int whiteBoard = 0;
		int significantBit = 1;
		while(position > 0)
		{
			if(position % 3 == 1)
				blackBoard |= significantBit;
			else if(position % 3 == 2)
				whiteBoard |= significantBit;
			position /= 3;
			significantBit = significantBit << 1;
		}
		return solvePositionWhiteAll(length, blackBoard, whiteBoard);
	}
	private static byte translatePositionLegalMoves(int length, int position)
	{
		int blackBoard = 0;
		int whiteBoard = 0;
		int significantBit = 1;
		while(position > 0)
		{
			if(position % 3 == 1)
				blackBoard |= significantBit;
			else if(position % 3 == 2)
				whiteBoard |= significantBit;
			position /= 3;
			significantBit = significantBit << 1;
		}
		return solvePositionWhiteLegal(length, blackBoard, whiteBoard);
	}
	private static byte solvePositionBlackAll(int length, int blackBoard, int whiteBoard)
	{
		int mask = (1 << length) - 1;
		byte value = 0;
		for(int k = 0; k < 8; ++k)
		{
			if((blackBoard & (1 << k)) != 0)
				++value;
			else if((whiteBoard & (1 << k)) != 0)
				--value;
		}
		int legalMoves = (~(blackBoard | whiteBoard)) & mask;
		int flipPieces, t, newBlackBoard, newWhiteBoard, move;
		while(legalMoves != 0L)
		{
			move = ~(legalMoves - 1) & legalMoves;
			flipPieces = 0;
			t = whiteBoard & (move >>> 1);
			t |= whiteBoard & (t >>> 1);
			t |= whiteBoard & (t >>> 1);
			t |= whiteBoard & (t >>> 1);
			t |= whiteBoard & (t >>> 1);
			t |= whiteBoard & (t >>> 1);
			if((blackBoard & (t >>> 1)) != 0)
				flipPieces |= t;
			t = whiteBoard & (move << 1);
			t |= whiteBoard & (t << 1);
			t |= whiteBoard & (t << 1);
			t |= whiteBoard & (t << 1);
			t |= whiteBoard & (t << 1);
			t |= whiteBoard & (t << 1);
			if((blackBoard & (t << 1)) != 0)
				flipPieces |= t;
			newBlackBoard = blackBoard ^ flipPieces | move;
			newWhiteBoard = whiteBoard ^ flipPieces;
			value = (byte) Math.max(value, Math.min(solvePositionBlackAll(length, newBlackBoard, newWhiteBoard), solvePositionWhiteAll(length, newBlackBoard, newWhiteBoard)));
			legalMoves &= legalMoves - 1;
		}
		return value;
	}
	private static byte solvePositionWhiteAll(int length, int blackBoard, int whiteBoard)
	{
		int mask = (1 << length) - 1;
		byte value = 0;
		for(int k = 0; k < 8; ++k)
		{
			if((blackBoard & (1 << k)) != 0)
				++value;
			else if((whiteBoard & (1 << k)) != 0)
				--value;
		}
		int legalMoves = (~(blackBoard | whiteBoard)) & mask;
		int flipPieces, t, newBlackBoard, newWhiteBoard, move;
		while(legalMoves != 0L)
		{
			move = ~(legalMoves - 1) & legalMoves;
			flipPieces = 0;
			t = blackBoard & (move >>> 1);
			t |= blackBoard & (t >>> 1);
			t |= blackBoard & (t >>> 1);
			t |= blackBoard & (t >>> 1);
			t |= blackBoard & (t >>> 1);
			t |= blackBoard & (t >>> 1);
			if((whiteBoard & (t >>> 1)) != 0)
				flipPieces |= t;
			t = blackBoard & (move << 1);
			t |= blackBoard & (t << 1);
			t |= blackBoard & (t << 1);
			t |= blackBoard & (t << 1);
			t |= blackBoard & (t << 1);
			t |= blackBoard & (t << 1);
			if((whiteBoard & (t << 1)) != 0)
				flipPieces |= t;
			newBlackBoard = blackBoard ^ flipPieces;
			newWhiteBoard = whiteBoard ^ flipPieces | move;
			value = (byte) Math.min(value, Math.max(solvePositionBlackAll(length, newBlackBoard, newWhiteBoard), solvePositionWhiteAll(length, newBlackBoard, newWhiteBoard)));
			legalMoves &= legalMoves - 1;
		}
		return value;
	}
	private static int getBlackLegalMoves(int blackBoard, int whiteBoard)
	{
		int moves = 0;
		int emptySquares = ~(blackBoard | whiteBoard);
		int t = whiteBoard & (blackBoard >>> 1);
		int tReverse = whiteBoard & (blackBoard << 1);
		t |= whiteBoard & (t >>> 1);
		t |= whiteBoard & (t >>> 1);
		t |= whiteBoard & (t >>> 1);
		t |= whiteBoard & (t >>> 1);
		t |= whiteBoard & (t >>> 1);
		tReverse |= whiteBoard & (tReverse << 1);
		tReverse |= whiteBoard & (tReverse << 1);
		tReverse |= whiteBoard & (tReverse << 1);
		tReverse |= whiteBoard & (tReverse << 1);
		tReverse |= whiteBoard & (tReverse << 1);
		moves |= emptySquares & (t >>> 1);
		moves |= emptySquares & (tReverse << 1);
		return moves;
	}
	private static int getWhiteLegalMoves(int blackBoard, int whiteBoard)
	{
		int moves = 0;
		int emptySquares = ~(blackBoard | whiteBoard);
		int t = blackBoard & (whiteBoard >>> 1);
		int tReverse = blackBoard & (whiteBoard << 1);
		t |= blackBoard & (t >>> 1);
		t |= blackBoard & (t >>> 1);
		t |= blackBoard & (t >>> 1);
		t |= blackBoard & (t >>> 1);
		t |= blackBoard & (t >>> 1);
		tReverse |= blackBoard & (tReverse << 1);
		tReverse |= blackBoard & (tReverse << 1);
		tReverse |= blackBoard & (tReverse << 1);
		tReverse |= blackBoard & (tReverse << 1);
		tReverse |= blackBoard & (tReverse << 1);
		moves |= emptySquares & (t >>> 1);
		moves |= emptySquares & (tReverse << 1);
		return moves;
	}
	private static byte solvePositionBlackLegal(int length, int blackBoard, int whiteBoard)
	{
		int mask = (1 << length) - 1;
		byte value = 0;
		for(int k = 0; k < 8; ++k)
		{
			if((blackBoard & (1 << k)) != 0)
				++value;
			else if((whiteBoard & (1 << k)) != 0)
				--value;
		}
		int legalMoves = getBlackLegalMoves(blackBoard, whiteBoard) & mask;
		int flipPieces, t, newBlackBoard, newWhiteBoard, move;
		while(legalMoves != 0L)
		{
			move = ~(legalMoves - 1) & legalMoves;
			flipPieces = 0;
			t = whiteBoard & (move >>> 1);
			t |= whiteBoard & (t >>> 1);
			t |= whiteBoard & (t >>> 1);
			t |= whiteBoard & (t >>> 1);
			t |= whiteBoard & (t >>> 1);
			t |= whiteBoard & (t >>> 1);
			if((blackBoard & (t >>> 1)) != 0)
				flipPieces |= t;
			t = whiteBoard & (move << 1);
			t |= whiteBoard & (t << 1);
			t |= whiteBoard & (t << 1);
			t |= whiteBoard & (t << 1);
			t |= whiteBoard & (t << 1);
			t |= whiteBoard & (t << 1);
			if((blackBoard & (t << 1)) != 0)
				flipPieces |= t;
			newBlackBoard = blackBoard ^ flipPieces | move;
			newWhiteBoard = whiteBoard ^ flipPieces;
			value = (byte) Math.max(value, Math.min(solvePositionBlackLegal(length, newBlackBoard, newWhiteBoard), solvePositionWhiteLegal(length, newBlackBoard, newWhiteBoard)));
			legalMoves &= legalMoves - 1;
		}
		return value;
	}
	private static byte solvePositionWhiteLegal(int length, int blackBoard, int whiteBoard)
	{
		int mask = (1 << length) - 1;
		byte value = 0;
		for(int k = 0; k < 8; ++k)
		{
			if((blackBoard & (1 << k)) != 0)
				++value;
			else if((whiteBoard & (1 << k)) != 0)
				--value;
		}
		int legalMoves = getWhiteLegalMoves(blackBoard, whiteBoard) & mask;
		int flipPieces, t, newBlackBoard, newWhiteBoard, move;
		while(legalMoves != 0L)
		{
			move = ~(legalMoves - 1) & legalMoves;
			flipPieces = 0;
			t = blackBoard & (move >>> 1);
			t |= blackBoard & (t >>> 1);
			t |= blackBoard & (t >>> 1);
			t |= blackBoard & (t >>> 1);
			t |= blackBoard & (t >>> 1);
			t |= blackBoard & (t >>> 1);
			if((whiteBoard & (t >>> 1)) != 0)
				flipPieces |= t;
			t = blackBoard & (move << 1);
			t |= blackBoard & (t << 1);
			t |= blackBoard & (t << 1);
			t |= blackBoard & (t << 1);
			t |= blackBoard & (t << 1);
			t |= blackBoard & (t << 1);
			if((whiteBoard & (t << 1)) != 0)
				flipPieces |= t;
			newBlackBoard = blackBoard ^ flipPieces;
			newWhiteBoard = whiteBoard ^ flipPieces | move;
			value = (byte) Math.min(value, Math.max(solvePositionBlackLegal(length, newBlackBoard, newWhiteBoard), solvePositionWhiteLegal(length, newBlackBoard, newWhiteBoard)));
			legalMoves &= legalMoves - 1;
		}
		return value;
	}
}