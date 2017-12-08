public class Board
{
	private static long[] directionMasks = {0x7F7F7F7F7F7F7F7FL, 0x00FEFEFEFEFEFEFEL, 0x00FFFFFFFFFFFFFFL, 0x007F7F7F7F7F7F7FL};
	private static long[] directionMasks2 = {0xFEFEFEFEFEFEFEFEL, 0x7F7F7F7F7F7F7F00L, 0xFFFFFFFFFFFFFF00L, 0xFEFEFEFEFEFEFE00L};
	private static byte[] directionShifts = {1, 7, 8, 9};

	public static long getBlackLegalMoves(long blackBoard, long whiteBoard)
	{
		long moves = 0L;
		long emptySquares = ~(blackBoard | whiteBoard);
		for(int direction = 0; direction < 4; ++direction)
		{
			byte shift = directionShifts[direction];
			long w, wReverse, t, tReverse;
			w = whiteBoard & directionMasks[direction];
			wReverse = whiteBoard & directionMasks2[direction];
			t = w & (blackBoard >>> shift);
			tReverse = wReverse & (blackBoard << shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			tReverse |= wReverse & (tReverse << shift);
			tReverse |= wReverse & (tReverse << shift);
			tReverse |= wReverse & (tReverse << shift);
			tReverse |= wReverse & (tReverse << shift);
			tReverse |= wReverse & (tReverse << shift);
			moves |= emptySquares & directionMasks[direction] & (t >>> shift);
			moves |= emptySquares & directionMasks2[direction] & (tReverse << shift);
		}
		return moves;
	}
	public static long getWhiteLegalMoves(long blackBoard, long whiteBoard)
	{
		long moves = 0L;
		long emptySquares = ~(blackBoard | whiteBoard);
		for(int direction = 0; direction < 4; ++direction)
		{
			byte shift = directionShifts[direction];
			long w, wReverse, t, tReverse;
			w = blackBoard & directionMasks[direction];
			wReverse = blackBoard & directionMasks2[direction];
			t = w & (whiteBoard >>> shift);
			tReverse = wReverse & (whiteBoard << shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			tReverse |= wReverse & (tReverse << shift);
			tReverse |= wReverse & (tReverse << shift);
			tReverse |= wReverse & (tReverse << shift);
			tReverse |= wReverse & (tReverse << shift);
			tReverse |= wReverse & (tReverse << shift);
			moves |= emptySquares & directionMasks[direction] & (t >>> shift);
			moves |= emptySquares & directionMasks2[direction] & (tReverse << shift);
		}
		return moves;
	}
	public static long makeBlackMove(long move, long blackBoard, long whiteBoard)
	{
		long piecesToFlip = 0L;
		long t, w;
		for(int direction = 0; direction < 4; ++direction)
		{
			byte shift = directionShifts[direction];
			w = whiteBoard & directionMasks[direction];
			t = w & (move >>> shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			if((blackBoard & directionMasks[direction] & (t >>> shift)) != 0L)
				piecesToFlip |= t;
			w = whiteBoard & directionMasks2[direction];
			t = w & (move << shift);
			t |= w & (t << shift);
			t |= w & (t << shift);
			t |= w & (t << shift);
			t |= w & (t << shift);
			t |= w & (t << shift);
			if((blackBoard & directionMasks2[direction] & (t << shift)) != 0L)
				piecesToFlip |= t;
		}
		return piecesToFlip;
	}
	public static long makeWhiteMove(long move, long blackBoard, long whiteBoard)
	{
		long piecesToFlip = 0L;
		long t, w;
		for(int direction = 0; direction < 4; ++direction)
		{
			byte shift = directionShifts[direction];
			w = blackBoard & directionMasks[direction];
			t = w & (move >>> shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			t |= w & (t >>> shift);
			if((whiteBoard & directionMasks[direction] & (t >>> shift)) != 0L)
				piecesToFlip |= t;
			w = blackBoard & directionMasks2[direction];
			t = w & (move << shift);
			t |= w & (t << shift);
			t |= w & (t << shift);
			t |= w & (t << shift);
			t |= w & (t << shift);
			t |= w & (t << shift);
			if((whiteBoard & directionMasks2[direction] & (t << shift)) != 0L)
				piecesToFlip |= t;
		}
		return piecesToFlip;
	}
}