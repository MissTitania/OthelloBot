public class SubgameEvaluationPrecomp
{
	public static void main(String[] args)
	{
		generateStartingPositions(0, (byte) 0, (byte) 0);
	}
	private static void generateStartingPositions(int lowestToManipulate, byte blackBoard, byte whiteBoard)
	{
		if(lowestToManipulate < 8)
		{
			generateStartingPositions(lowestToManipulate + 1, blackBoard, whiteBoard);
			generateStartingPositions(lowestToManipulate + 1, (byte) (blackBoard | (((byte) 1) << lowestToManipulate)), whiteBoard);
			generateStartingPositions(lowestToManipulate + 1, blackBoard, (byte) (whiteBoard | (((byte) 1) << lowestToManipulate)));
		}
		else
			System.out.println((((int) blackBoard) << 8) + whiteBoard + ", " + solvePositionBlack(blackBoard, whiteBoard));
	}
	private static byte solvePositionBlack(byte blackBoard, byte whiteBoard)
	{
		byte value = 0;
		for(int k = 0; k < 8; ++k)
		{
			if((blackBoard & (1 << k)) != 0)
				++value;
			else if((whiteBoard & (1 << k)) != 0)
				--value;
		}
		byte legalMoves = (byte) (~(blackBoard | whiteBoard));
		byte flipPieces, t, newBlackBoard, newWhiteBoard, move;
		while(legalMoves != 0L)
		{
			move = (byte) (~(legalMoves - 1) & legalMoves);
			flipPieces = (byte) 0;
			t = (byte) (whiteBoard & (move >>> 1));
			t |= whiteBoard & (t >>> 1);
			t |= whiteBoard & (t >>> 1);
			t |= whiteBoard & (t >>> 1);
			t |= whiteBoard & (t >>> 1);
			t |= whiteBoard & (t >>> 1);
			if((blackBoard & (t >>> 1)) != 0)
				flipPieces |= t;
			t = (byte) (whiteBoard & (move << 1));
			t |= whiteBoard & (t << 1);
			t |= whiteBoard & (t << 1);
			t |= whiteBoard & (t << 1);
			t |= whiteBoard & (t << 1);
			t |= whiteBoard & (t << 1);
			if((blackBoard & (t << 1)) != 0)
				flipPieces |= t;
			newBlackBoard = (byte) (blackBoard ^ flipPieces | move);
			newWhiteBoard = (byte) (whiteBoard ^ flipPieces);
			value = (byte) Math.max(value, Math.min(solvePositionBlack(newBlackBoard, newWhiteBoard), solvePositionWhite(newBlackBoard, newWhiteBoard)));
			legalMoves &= legalMoves - 1;
		}
		return value;
	}
	private static byte solvePositionWhite(byte blackBoard, byte whiteBoard)
	{
		byte value = 0;
		for(int k = 0; k < 8; ++k)
		{
			if((blackBoard & (1 << k)) != 0)
				++value;
			else if((whiteBoard & (1 << k)) != 0)
				--value;
		}
		byte legalMoves = (byte) (~(blackBoard | whiteBoard));
		byte flipPieces, t, newBlackBoard, newWhiteBoard, move;
		while(legalMoves != 0L)
		{
			move = (byte) (~(legalMoves - 1) & legalMoves);
			flipPieces = (byte) 0;
			t = (byte) (blackBoard & (move >>> 1));
			t |= blackBoard & (t >>> 1);
			t |= blackBoard & (t >>> 1);
			t |= blackBoard & (t >>> 1);
			t |= blackBoard & (t >>> 1);
			t |= blackBoard & (t >>> 1);
			if((whiteBoard & (t >>> 1)) != 0)
				flipPieces |= t;
			t = (byte) (blackBoard & (move << 1));
			t |= blackBoard & (t << 1);
			t |= blackBoard & (t << 1);
			t |= blackBoard & (t << 1);
			t |= blackBoard & (t << 1);
			t |= blackBoard & (t << 1);
			if((whiteBoard & (t << 1)) != 0)
				flipPieces |= t;
			newBlackBoard = (byte) (blackBoard ^ flipPieces);
			newWhiteBoard = (byte) (whiteBoard ^ flipPieces | move);
			value = (byte) Math.min(value, Math.max(solvePositionBlack(newBlackBoard, newWhiteBoard), solvePositionWhite(newBlackBoard, newWhiteBoard)));
			legalMoves &= legalMoves - 1;
		}
		return value;
	}
}