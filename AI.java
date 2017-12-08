public class AI
{
	public static long computeBlackMove(long blackBoard, long whiteBoard)
	{
		long legalMoves = Board.getBlackLegalMoves(blackBoard, whiteBoard);
		double bestValue = Double.NEGATIVE_INFINITY;
		long bestMove = -1L;
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		double value;
		long move, flipPieces, newBlackBoard, newWhiteBoard;
		while(legalMoves != 0L)
		{
			move = ~(legalMoves - 1) & legalMoves;
			flipPieces = Board.makeBlackMove(move, blackBoard, whiteBoard);
			newBlackBoard = blackBoard ^ flipPieces | move;
			newWhiteBoard = whiteBoard ^ flipPieces;
			value = AI.valueStateWhite(10, newBlackBoard, newWhiteBoard, alpha, beta);
			if(value > bestValue)
			{
				alpha = value;
				bestValue = value;
				bestMove = move;
			}
			if(beta <= alpha)
				break;
			legalMoves &= legalMoves - 1;
		}
		return bestMove;
	}
	public static long computeWhiteMove(long blackBoard, long whiteBoard)
	{
		long legalMoves = Board.getWhiteLegalMoves(blackBoard, whiteBoard);
		double bestValue = Double.POSITIVE_INFINITY;
		long bestMove = -1L;
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		double value;
		long move, flipPieces, newBlackBoard, newWhiteBoard;
		while(legalMoves != 0L)
		{
			move = ~(legalMoves - 1) & legalMoves;
			flipPieces = Board.makeWhiteMove(move, blackBoard, whiteBoard);
			newBlackBoard = blackBoard ^ flipPieces;
			newWhiteBoard = whiteBoard ^ flipPieces | move;
			value = AI.valueStateBlack(10, newBlackBoard, newWhiteBoard, alpha, beta);
			if(value < bestValue)
			{
				beta = value;
				bestValue = value;
				bestMove = move;
			}
			if(beta <= alpha)
				break;
			legalMoves &= legalMoves - 1;
		}
		return bestMove;
	}
	private static double valueStateBlack(int evaluationDepth, long blackBoard, long whiteBoard, double alpha, double beta)
	{
		if(evaluationDepth == 0)
			return AI.terminalEvaluation(blackBoard, whiteBoard);
		long legalMoves = Board.getBlackLegalMoves(blackBoard, whiteBoard);
		if(legalMoves == 0L)
		{
			if(Board.getWhiteLegalMoves(blackBoard, whiteBoard) == 0L)
				return AI.terminalEvaluation(blackBoard, whiteBoard);
			return valueStateWhite(evaluationDepth, blackBoard, whiteBoard, alpha, beta);
		}
		double value = Double.NEGATIVE_INFINITY;
		long move, flipPieces, newBlackBoard, newWhiteBoard;
		while(legalMoves != 0L)
		{
			move = ~(legalMoves - 1) & legalMoves;
			flipPieces = Board.makeBlackMove(move, blackBoard, whiteBoard);
			newBlackBoard = blackBoard ^ flipPieces | move;
			newWhiteBoard = whiteBoard ^ flipPieces;
			value = Math.max(value, AI.valueStateWhite(evaluationDepth - 1, newBlackBoard, newWhiteBoard, alpha, beta));
			alpha = Math.max(alpha, value);
			if(beta <= alpha)
				break;
			legalMoves &= legalMoves - 1;
		}
		return value;
	}
	private static double valueStateWhite(int evaluationDepth, long blackBoard, long whiteBoard, double alpha, double beta)
	{
		if(evaluationDepth == 0)
			return AI.terminalEvaluation(blackBoard, whiteBoard);
		long legalMoves = Board.getWhiteLegalMoves(blackBoard, whiteBoard);
		if(legalMoves == 0L)
		{
			if(Board.getBlackLegalMoves(blackBoard, whiteBoard) == 0L)
				return AI.terminalEvaluation(blackBoard, whiteBoard);
			return valueStateBlack(evaluationDepth, blackBoard, whiteBoard, alpha, beta);
		}
		double value = Double.POSITIVE_INFINITY;
		long move, flipPieces, newBlackBoard, newWhiteBoard;
		while(legalMoves != 0L)
		{
			move = ~(legalMoves - 1) & legalMoves;
			flipPieces = Board.makeWhiteMove(move, blackBoard, whiteBoard);
			newBlackBoard = blackBoard ^ flipPieces;
			newWhiteBoard = whiteBoard ^ flipPieces | move;
			value = Math.min(value, AI.valueStateBlack(evaluationDepth - 1, newBlackBoard, newWhiteBoard, alpha, beta));
			beta = Math.min(beta, value);
			if(beta <= alpha)
				break;
			legalMoves &= legalMoves - 1;
		}
		return value;
	}
	private static double terminalEvaluation(long blackBoard, long whiteBoard)
	{
		if(blackBoard == 0L)
			return -64.0;
		else if(whiteBoard == 0L)
			return 64.0;
		double count = 0;
		while(blackBoard != 0L)
		{
			blackBoard &= blackBoard - 1;
			++count;
		}
		while(whiteBoard != 0L)
		{
			whiteBoard &= whiteBoard - 1;
			--count;
		}
		return count;
	}
}