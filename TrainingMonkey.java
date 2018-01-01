public class TrainingMonkey
{
	private static byte[] oneRow = SubgameEvaluationPrecomp.generateRowPrecompAllMoves(1);
	private static byte[] twoRow = SubgameEvaluationPrecomp.generateRowPrecompAllMoves(2);
	private static byte[] threeRowA = SubgameEvaluationPrecomp.generateRowPrecompAllMoves(3);
	private static byte[] threeRowL = SubgameEvaluationPrecomp.generateRowPrecompLegalMoves(3);
	private static byte[] fourRowA = SubgameEvaluationPrecomp.generateRowPrecompAllMoves(4);
	private static byte[] fourRowL = SubgameEvaluationPrecomp.generateRowPrecompLegalMoves(4);
	private static byte[] fiveRowA = SubgameEvaluationPrecomp.generateRowPrecompAllMoves(5);
	private static byte[] fiveRowL = SubgameEvaluationPrecomp.generateRowPrecompLegalMoves(5);
	private static byte[] sixRowA = SubgameEvaluationPrecomp.generateRowPrecompAllMoves(6);
	private static byte[] sixRowL = SubgameEvaluationPrecomp.generateRowPrecompLegalMoves(6);
	private static byte[] sevenRowA = SubgameEvaluationPrecomp.generateRowPrecompAllMoves(7);
	private static byte[] sevenRowL = SubgameEvaluationPrecomp.generateRowPrecompLegalMoves(7);
	private static byte[] eightRowA = SubgameEvaluationPrecomp.generateRowPrecompAllMoves(8);
	private static byte[] eightRowL = SubgameEvaluationPrecomp.generateRowPrecompLegalMoves(8);

	public double[] diagonalCoefficientsA;
	public double[] diagonalCoefficientsL;
	public double[] rowCoefficientsA;
	public double[] rowCoefficientsL;
	public double moveDifferentialCoefficient;

	public TrainingMonkey()
	{
		diagonalCoefficientsA = new double[8];
		diagonalCoefficientsL = new double[8];
		for(int k = 0; k < diagonalCoefficientsA.length; ++k)
		{
			diagonalCoefficientsA[k] = Math.random() * 2 - 1;
			diagonalCoefficientsL[k] = Math.random() * 2 - 1;
		}
		rowCoefficientsA = new double[4];
		rowCoefficientsL = new double[4];
		for(int k = 0; k < rowCoefficientsA.length; ++k)
		{
			rowCoefficientsA[k] = Math.random() * 2 - 1;
			rowCoefficientsL[k] = Math.random() * 2 - 1;
		}
		moveDifferentialCoefficient = Math.random() * 2 - 1;
	}
	public TrainingMonkey breed(TrainingMonkey partner)
	{
		TrainingMonkey baby = new TrainingMonkey();
		boolean myGenes = true;
		for(int k = 0; k < diagonalCoefficientsA.length; ++k)
		{
			if(Math.random() < .985)
			{
				if(myGenes)
					baby.diagonalCoefficientsA[k] = diagonalCoefficientsA[k];
				else
					baby.diagonalCoefficientsA[k] = partner.diagonalCoefficientsA[k];
			}
			if(Math.random() < .2)
				myGenes = !myGenes;
		}
		for(int k = 0; k < diagonalCoefficientsL.length; ++k)
		{
			if(Math.random() < .985)
			{
				if(myGenes)
					baby.diagonalCoefficientsL[k] = diagonalCoefficientsL[k];
				else
					baby.diagonalCoefficientsL[k] = partner.diagonalCoefficientsL[k];
			}
			if(Math.random() < .2)
				myGenes = !myGenes;
		}
		for(int k = 0; k < rowCoefficientsA.length; ++k)
		{
			if(Math.random() < .985)
			{
				if(myGenes)
					baby.rowCoefficientsA[k] = rowCoefficientsA[k];
				else
					baby.rowCoefficientsA[k] = partner.rowCoefficientsA[k];
			}
			if(Math.random() < .2)
				myGenes = !myGenes;
		}
		for(int k = 0; k < rowCoefficientsL.length; ++k)
		{
			if(Math.random() < .985)
			{
				if(myGenes)
					baby.rowCoefficientsL[k] = rowCoefficientsL[k];
				else
					baby.rowCoefficientsL[k] = partner.rowCoefficientsL[k];
			}
			if(Math.random() < .2)
				myGenes = !myGenes;
		}
		if(Math.random() < .985)
		{
			if(myGenes)
				baby.moveDifferentialCoefficient = moveDifferentialCoefficient;
			else
				baby.moveDifferentialCoefficient = partner.moveDifferentialCoefficient;
		}
		return baby;
	}
	public long computeMove(long blackBoard, long whiteBoard)
	{
		int count = 0;
		long blackBoardCopy = blackBoard;
		long whiteBoardCopy = whiteBoard;
		while(blackBoardCopy != 0L)
		{
			blackBoardCopy &= blackBoardCopy - 1;
			++count;
		}
		while(whiteBoardCopy != 0L)
		{
			whiteBoardCopy &= whiteBoardCopy - 1;
			++count;
		}
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
			if(count < 49)
				value = valueStateWhite(6, newBlackBoard, newWhiteBoard, alpha, beta, false);
			else
				value = valueStateWhite(14, newBlackBoard, newWhiteBoard, alpha, beta, true);
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
	private double valueStateBlack(int evaluationDepth, long blackBoard, long whiteBoard, double alpha, double beta, boolean terminal)
	{
		if(evaluationDepth == 0)
			return terminalEvaluation(blackBoard, whiteBoard, terminal);
		long legalMoves = Board.getBlackLegalMoves(blackBoard, whiteBoard);
		if(legalMoves == 0L)
		{
			if(Board.getWhiteLegalMoves(blackBoard, whiteBoard) == 0L)
				return terminalEvaluation(blackBoard, whiteBoard, true);
			return valueStateWhite(evaluationDepth, blackBoard, whiteBoard, alpha, beta, terminal);
		}
		double value = Double.NEGATIVE_INFINITY;
		long move, flipPieces, newBlackBoard, newWhiteBoard;
		while(legalMoves != 0L)
		{
			move = ~(legalMoves - 1) & legalMoves;
			flipPieces = Board.makeBlackMove(move, blackBoard, whiteBoard);
			newBlackBoard = blackBoard ^ flipPieces | move;
			newWhiteBoard = whiteBoard ^ flipPieces;
			value = Math.max(value, valueStateWhite(evaluationDepth - 1, newBlackBoard, newWhiteBoard, alpha, beta, terminal));
			alpha = Math.max(alpha, value);
			if(beta <= alpha)
				break;
			legalMoves &= legalMoves - 1;
		}
		return value;
	}
	private double valueStateWhite(int evaluationDepth, long blackBoard, long whiteBoard, double alpha, double beta, boolean terminal)
	{
		if(evaluationDepth == 0)
			return terminalEvaluation(blackBoard, whiteBoard, terminal);
		long legalMoves = Board.getWhiteLegalMoves(blackBoard, whiteBoard);
		if(legalMoves == 0L)
		{
			if(Board.getBlackLegalMoves(blackBoard, whiteBoard) == 0L)
				return terminalEvaluation(blackBoard, whiteBoard, true);
			return valueStateBlack(evaluationDepth, blackBoard, whiteBoard, alpha, beta, terminal);
		}
		double value = Double.POSITIVE_INFINITY;
		long move, flipPieces, newBlackBoard, newWhiteBoard;
		while(legalMoves != 0L)
		{
			move = ~(legalMoves - 1) & legalMoves;
			flipPieces = Board.makeWhiteMove(move, blackBoard, whiteBoard);
			newBlackBoard = blackBoard ^ flipPieces;
			newWhiteBoard = whiteBoard ^ flipPieces | move;
			value = Math.min(value, valueStateBlack(evaluationDepth - 1, newBlackBoard, newWhiteBoard, alpha, beta, terminal));
			beta = Math.min(beta, value);
			if(beta <= alpha)
				break;
			legalMoves &= legalMoves - 1;
		}
		return value;
	}
	private double terminalEvaluation(long blackBoard, long whiteBoard, boolean terminal)
	{
		if(terminal)
		{
			if(blackBoard == 0L)
				return -1064.0;
			else if(whiteBoard == 0L)
				return 1064.0;
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
			if(count > 0)
				return count + 1000.0;
			else if(count < 0)
				return count - 1000.0;
			else
				return 0.0;
		}

		double result = 0;
		//Move Differential Computation
		long blackLegalMoves = Board.getBlackLegalMoves(blackBoard, whiteBoard);
		long whiteLegalMoves = Board.getWhiteLegalMoves(blackBoard, whiteBoard);
		int count = 0;
		while(blackLegalMoves != 0L)
		{
			blackLegalMoves &= blackLegalMoves - 1;
			++count;
		}
		while(whiteLegalMoves != 0L)
		{
			whiteLegalMoves &= whiteLegalMoves - 1;
			--count;
		}
		result += moveDifferentialCoefficient * count;
		//Horizontal Row Precomps
		result += rowCoefficientsA[0] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {0, 1, 2, 3, 4, 5, 6, 7})];
		result += rowCoefficientsA[1] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {8, 9, 10, 11, 12, 13, 14, 15})];
		//result += rowCoefficientsA[2] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {16, 17, 18, 19, 20, 21, 22, 23})];
		//result += rowCoefficientsA[3] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {24, 25, 26, 27, 28, 29, 30, 31})];
		//result += rowCoefficientsA[3] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {32, 33, 34, 35, 36, 37, 38, 39})];
		//result += rowCoefficientsA[2] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {40, 41, 42, 43, 44, 45, 46, 47})];
		result += rowCoefficientsA[1] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {48, 49, 50, 51, 52, 53, 54, 55})];
		result += rowCoefficientsA[0] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {56, 57, 58, 59, 60, 61, 62, 63})];
		result += rowCoefficientsL[0] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {0, 1, 2, 3, 4, 5, 6, 7})];
		result += rowCoefficientsL[1] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {8, 9, 10, 11, 12, 13, 14, 15})];
		result += rowCoefficientsL[2] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {16, 17, 18, 19, 20, 21, 22, 23})];
		result += rowCoefficientsL[3] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {24, 25, 26, 27, 28, 29, 30, 31})];
		result += rowCoefficientsL[3] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {32, 33, 34, 35, 36, 37, 38, 39})];
		result += rowCoefficientsL[2] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {40, 41, 42, 43, 44, 45, 46, 47})];
		result += rowCoefficientsL[1] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {48, 49, 50, 51, 52, 53, 54, 55})];
		result += rowCoefficientsL[0] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {56, 57, 58, 59, 60, 61, 62, 63})];
		//Vertical Row Precomps
		result += rowCoefficientsA[0] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {0, 8, 16, 24, 32, 40, 48, 56})];
		result += rowCoefficientsA[1] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {1, 9, 17, 25, 33, 41, 49, 57})];
		//result += rowCoefficientsA[2] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {2, 10, 18, 26, 34, 42, 50, 58})];
		//result += rowCoefficientsA[3] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {3, 11, 19, 27, 35, 43, 51, 59})];
		//result += rowCoefficientsA[3] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {4, 12, 20, 28, 36, 44, 52, 60})];
		//result += rowCoefficientsA[2] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {5, 13, 21, 29, 37, 45, 53, 61})];
		result += rowCoefficientsA[1] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {6, 14, 22, 30, 38, 46, 54, 62})];
		result += rowCoefficientsA[0] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {7, 15, 23, 31, 39, 47, 55, 63})];
		result += rowCoefficientsL[0] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {0, 8, 16, 24, 32, 40, 48, 56})];
		result += rowCoefficientsL[1] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {1, 9, 17, 25, 33, 41, 49, 57})];
		result += rowCoefficientsL[2] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {2, 10, 18, 26, 34, 42, 50, 58})];
		result += rowCoefficientsL[3] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {3, 11, 19, 27, 35, 43, 51, 59})];
		result += rowCoefficientsL[3] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {4, 12, 20, 28, 36, 44, 52, 60})];
		result += rowCoefficientsL[2] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {5, 13, 21, 29, 37, 45, 53, 61})];
		result += rowCoefficientsL[1] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {6, 14, 22, 30, 38, 46, 54, 62})];
		result += rowCoefficientsL[0] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {7, 15, 23, 31, 39, 47, 55, 63})];
		//Diagonal Precomps
		result += 2 * diagonalCoefficientsA[0] * oneRow[extractIndex(blackBoard, whiteBoard, new int[] {7})];
		//result += 2 * diagonalCoefficientsA[1] * twoRow[extractIndex(blackBoard, whiteBoard, new int[] {6, 15})];
		result += diagonalCoefficientsA[2] * threeRowA[extractIndex(blackBoard, whiteBoard, new int[] {5, 14, 23})];
		result += diagonalCoefficientsL[2] * threeRowL[extractIndex(blackBoard, whiteBoard, new int[] {5, 14, 23})];
		result += diagonalCoefficientsA[3] * fourRowA[extractIndex(blackBoard, whiteBoard, new int[] {4, 13, 22, 31})];
		result += diagonalCoefficientsL[3] * fourRowL[extractIndex(blackBoard, whiteBoard, new int[] {4, 13, 22, 31})];
		result += diagonalCoefficientsA[4] * fiveRowA[extractIndex(blackBoard, whiteBoard, new int[] {3, 12, 21, 30, 39})];
		result += diagonalCoefficientsL[4] * fiveRowL[extractIndex(blackBoard, whiteBoard, new int[] {3, 12, 21, 30, 39})];
		result += diagonalCoefficientsA[5] * sixRowA[extractIndex(blackBoard, whiteBoard, new int[] {2, 11, 20, 29, 38, 47})];
		result += diagonalCoefficientsL[5] * sixRowL[extractIndex(blackBoard, whiteBoard, new int[] {2, 11, 20, 29, 38, 47})];
		//result += diagonalCoefficientsA[6] * sevenRowA[extractIndex(blackBoard, whiteBoard, new int[] {1, 10, 19, 28, 37, 46, 55})];
		//result += diagonalCoefficientsL[6] * sevenRowL[extractIndex(blackBoard, whiteBoard, new int[] {1, 10, 19, 28, 37, 46, 55})];
		//result += diagonalCoefficientsA[7] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {0, 9, 18, 27, 36, 45, 54, 63})];
		result += diagonalCoefficientsL[7] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {0, 9, 18, 27, 36, 45, 54, 63})];
		//result += diagonalCoefficientsA[6] * sevenRowA[extractIndex(blackBoard, whiteBoard, new int[] {8, 17, 26, 35, 44, 53, 62})];
		//result += diagonalCoefficientsL[6] * sevenRowL[extractIndex(blackBoard, whiteBoard, new int[] {8, 17, 26, 35, 44, 53, 62})];
		result += diagonalCoefficientsA[5] * sixRowA[extractIndex(blackBoard, whiteBoard, new int[] {16, 25, 34, 43, 52, 61})];
		result += diagonalCoefficientsL[5] * sixRowL[extractIndex(blackBoard, whiteBoard, new int[] {16, 25, 34, 43, 52, 61})];
		result += diagonalCoefficientsA[4] * fiveRowA[extractIndex(blackBoard, whiteBoard, new int[] {24, 33, 42, 51, 60})];
		result += diagonalCoefficientsL[4] * fiveRowL[extractIndex(blackBoard, whiteBoard, new int[] {24, 33, 42, 51, 60})];
		result += diagonalCoefficientsA[3] * fourRowA[extractIndex(blackBoard, whiteBoard, new int[] {32, 41, 50, 59})];
		result += diagonalCoefficientsL[3] * fourRowL[extractIndex(blackBoard, whiteBoard, new int[] {32, 41, 50, 59})];
		result += diagonalCoefficientsA[2] * threeRowA[extractIndex(blackBoard, whiteBoard, new int[] {40, 49, 58})];
		result += diagonalCoefficientsL[2] * threeRowL[extractIndex(blackBoard, whiteBoard, new int[] {40, 49, 58})];
		//result += 2 * diagonalCoefficientsA[1] * twoRow[extractIndex(blackBoard, whiteBoard, new int[] {48, 57})];
		result += 2 * diagonalCoefficientsA[0] * oneRow[extractIndex(blackBoard, whiteBoard, new int[] {56})];
		//Anti-Diagonal Precomps
		result += 2 * diagonalCoefficientsA[0] * oneRow[extractIndex(blackBoard, whiteBoard, new int[] {0})];
		//result += 2 * diagonalCoefficientsA[1] * twoRow[extractIndex(blackBoard, whiteBoard, new int[] {1, 8})];
		result += diagonalCoefficientsA[2] * threeRowA[extractIndex(blackBoard, whiteBoard, new int[] {2, 9, 16})];
		result += diagonalCoefficientsL[2] * threeRowL[extractIndex(blackBoard, whiteBoard, new int[] {2, 9, 16})];
		result += diagonalCoefficientsA[3] * fourRowA[extractIndex(blackBoard, whiteBoard, new int[] {3, 10, 17, 24})];
		result += diagonalCoefficientsL[3] * fourRowL[extractIndex(blackBoard, whiteBoard, new int[] {3, 10, 17, 24})];
		result += diagonalCoefficientsA[4] * fiveRowA[extractIndex(blackBoard, whiteBoard, new int[] {4, 11, 18, 25, 32})];
		result += diagonalCoefficientsL[4] * fiveRowL[extractIndex(blackBoard, whiteBoard, new int[] {4, 11, 18, 25, 32})];
		result += diagonalCoefficientsA[5] * sixRowA[extractIndex(blackBoard, whiteBoard, new int[] {5, 12, 19, 26, 33, 40})];
		result += diagonalCoefficientsL[5] * sixRowL[extractIndex(blackBoard, whiteBoard, new int[] {5, 12, 19, 26, 33, 40})];
		//result += diagonalCoefficientsA[6] * sevenRowA[extractIndex(blackBoard, whiteBoard, new int[] {6, 13, 20, 27, 34, 41, 48})];
		//result += diagonalCoefficientsL[6] * sevenRowL[extractIndex(blackBoard, whiteBoard, new int[] {6, 13, 20, 27, 34, 41, 48})];
		//result += diagonalCoefficientsA[7] * eightRowA[extractIndex(blackBoard, whiteBoard, new int[] {7, 14, 21, 28, 35, 42, 49, 56})];
		result += diagonalCoefficientsL[7] * eightRowL[extractIndex(blackBoard, whiteBoard, new int[] {7, 14, 21, 28, 35, 42, 49, 56})];
		//result += diagonalCoefficientsA[6] * sevenRowA[extractIndex(blackBoard, whiteBoard, new int[] {15, 22, 29, 36, 43, 50, 57})];
		//result += diagonalCoefficientsL[6] * sevenRowL[extractIndex(blackBoard, whiteBoard, new int[] {15, 22, 29, 36, 43, 50, 57})];
		result += diagonalCoefficientsA[5] * sixRowA[extractIndex(blackBoard, whiteBoard, new int[] {23, 30, 37, 44, 51, 58})];
		result += diagonalCoefficientsL[5] * sixRowL[extractIndex(blackBoard, whiteBoard, new int[] {23, 30, 37, 44, 51, 58})];
		result += diagonalCoefficientsA[4] * fiveRowA[extractIndex(blackBoard, whiteBoard, new int[] {31, 38, 45, 52, 59})];
		result += diagonalCoefficientsL[4] * fiveRowL[extractIndex(blackBoard, whiteBoard, new int[] {31, 38, 45, 52, 59})];
		result += diagonalCoefficientsA[3] * fourRowA[extractIndex(blackBoard, whiteBoard, new int[] {39, 46, 53, 60})];
		result += diagonalCoefficientsL[3] * fourRowL[extractIndex(blackBoard, whiteBoard, new int[] {39, 46, 53, 60})];
		result += diagonalCoefficientsA[2] * threeRowA[extractIndex(blackBoard, whiteBoard, new int[] {47, 54, 61})];
		result += diagonalCoefficientsL[2] * threeRowL[extractIndex(blackBoard, whiteBoard, new int[] {47, 54, 61})];
		//result += 2 * diagonalCoefficientsA[1] * twoRow[extractIndex(blackBoard, whiteBoard, new int[] {55, 62})];
		result += 2 * diagonalCoefficientsA[0] * oneRow[extractIndex(blackBoard, whiteBoard, new int[] {63})];
		return result;
	}
	private int extractIndex(long blackBoard, long whiteBoard, int[] positions)
	{
		int index = 0;
		for(int k = 0; k < positions.length; ++k)
		{
			index *= 3;
			if((blackBoard & (1L << positions[k])) > 0)
				++index;
			else if((whiteBoard & (1L << positions[k])) > 0)
				index += 2;
		}
		return index;
	}
}