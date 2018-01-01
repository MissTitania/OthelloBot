import java.io.PrintWriter;

public class TrainingLoop
{
	public static void main(String[] args)
	{
		TrainingMonkey[] monkeys = new TrainingMonkey[16];
		double[] sexiness = new double[monkeys.length];
		for(int k = 0; k < monkeys.length; ++k)
		{
			monkeys[k] = new TrainingMonkey();
			sexiness[k] = 0.0;
		}
		int generationNumber = 0;
		while(true)
		{
			int matchCounter = 0;
			for(int k = 0; k < monkeys.length; ++k)
				for(int j = k + 1; j < monkeys.length; ++j)
				{
					System.out.print("Hosting battle " + (matchCounter + 1) + "/" + (monkeys.length * (monkeys.length - 1) / 2));
					if(Math.random() < .5)
					{
						int[] battleResults = cageMatch(monkeys[k], monkeys[j]);
						sexiness[k] += battleResults[0];
						sexiness[j] += battleResults[1];
					}
					else
					{
						int[] battleResults = cageMatch(monkeys[j], monkeys[k]);
						sexiness[k] += battleResults[1];
						sexiness[j] += battleResults[0];
					}
					System.out.println();
					++matchCounter;
				}
			writeNeuralOutput(generationNumber, monkeys, sexiness);
			System.out.println("Selectively breeding generation " + (generationNumber + 1) + "...");
			TrainingMonkey[] temp = new TrainingMonkey[monkeys.length];
			for(int k = 0; k < temp.length; ++k)
			{
				int m1 = naturalSelection(sexiness);
				int m2 = naturalSelection(sexiness);
				temp[k] = monkeys[m1].breed(monkeys[m2]);
			}
			monkeys = temp;
			for(int k = 0; k < monkeys.length; ++k)
				sexiness[k] = 0.0;
			++generationNumber;
		}
	}
	private static int naturalSelection(double[] sexiness)
	{
		double totalSexiness = 0.0;
		for(int k = 0; k < sexiness.length; ++k)
			totalSexiness += sexiness[k];
		int index = 0;
		double randomDraw = Math.random();
		while(randomDraw >= sexiness[index] / totalSexiness)
		{
			randomDraw -= sexiness[index] / totalSexiness;
			++index;
		}
		return index;
	}
	private static int[] cageMatch(TrainingMonkey m1, TrainingMonkey m2)
	{
		long blackPieces = 68853694464L;
		long whitePieces = 34628173824L;
		boolean moved;
		do
		{
			moved = false;
			if(Board.getWhiteLegalMoves(blackPieces, whitePieces) != 0L)
			{
				long computedMove = m1.computeMove(whitePieces, blackPieces);
				long flipPieces = Board.makeWhiteMove(computedMove, blackPieces, whitePieces);
				whitePieces |= computedMove;
				blackPieces ^= flipPieces;
				whitePieces ^= flipPieces;
				moved = true;
				System.out.print(".");
			}
			if(Board.getBlackLegalMoves(blackPieces, whitePieces) != 0L)
			{
				long computedMove = m2.computeMove(blackPieces, whitePieces);
				long flipPieces = Board.makeBlackMove(computedMove, blackPieces, whitePieces);
				blackPieces |= computedMove;
				blackPieces ^= flipPieces;
				whitePieces ^= flipPieces;
				moved = true;
				System.out.print(".");
			}
		}
		while(moved);
		if(whitePieces == 0L)
			return new int[] {0, 64};
		else if(blackPieces == 0L)
			return new int[] {64, 0};
		int whiteCount = 0;
		int blackCount = 0;
		while(blackPieces != 0L)
		{
			blackPieces &= blackPieces - 1;
			++blackCount;
		}
		while(whitePieces != 0L)
		{
			whitePieces &= whitePieces - 1;
			++whiteCount;
		}
		return new int[] {whiteCount, blackCount};
	}
	private static void writeNeuralOutput(int generationNumber, TrainingMonkey[] monkeys, double[] sexiness)
	{
		System.out.println("Writing generation " + generationNumber + " statistics to disk...");
		for(int k = 0; k < monkeys.length; ++k)
			for(int j = k + 1; j < monkeys.length; ++j)
				if(sexiness[k] < sexiness[j])
				{
					TrainingMonkey temp1 = monkeys[j];
					monkeys[j] = monkeys[k];
					monkeys[k] = temp1;
					double temp2 = sexiness[j];
					sexiness[j] = sexiness[k];
					sexiness[k] = temp2;
				}
		try
		{
			PrintWriter writer = new PrintWriter("NeuralOutputs/Generation" + generationNumber + ".txt", "UTF-8");
			for(int k = 0; k < monkeys.length; ++k)
			{
				writer.println("Chromosome " + (k + 1) + " (Power " + ((int) (sexiness[k] + .5)) + "):");
				for(int j = 0; j < 8; ++j)
				{
					writer.print(monkeys[k].diagonalCoefficientsA[j] + ", ");
					writer.print(monkeys[k].diagonalCoefficientsL[j] + ", ");
				}
				for(int j = 0; j < 4; ++j)
				{
					writer.print(monkeys[k].rowCoefficientsA[j] + ", ");
					writer.print(monkeys[k].rowCoefficientsL[j] + ", ");
				}
				writer.println(monkeys[k].moveDifferentialCoefficient);
			}
			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}