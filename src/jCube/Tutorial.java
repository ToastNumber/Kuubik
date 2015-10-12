package jCube;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author Kelsey McKenna
 */
public class Tutorial {

	/**
	 * This represents the cube displayed on screen. It can be used to generate
	 * a solution for the current state of the cube.
	 */
	private Cube cube;
	/**
	 * This variable allows certain methods to be accessed to perform general
	 * operations on the cube.
	 */
	private SolveMaster solveMaster;
	/**
	 * This variable allows a solution for the cross to be generated.
	 */
	private CrossSolver crossSolver;
	/**
	 * This variable allows a solution for the first-layer corners to be
	 * generated.
	 */
	private CornerSolver cornerSolver;
	/**
	 * This variable allows a solution for the middle-layer edges to be
	 * generated.
	 */
	private EdgeSolver edgeSolver;
	/**
	 * This variable allows a solution for the orientation of the last layer to
	 * be generated.
	 */
	private OrientationSolver orientationSolver;
	/**
	 * This variable allows a solution for the permutation of the last layer to
	 * be generated.
	 */
	private PermutationSolver permutationSolver;

	/**
	 * ¬The ith element of this array stores the scramble for the ith
	 * sub-tutorial.
	 */
	private String[] scrambles;
	/**
	 * The ith element of this array stores the description for the ith
	 * sub-tutorial.
	 */
	private String[] descriptions;
	/**
	 * The ith element of this array stores the pieces that are expected to be
	 * solved for the ith sub-tutorial.
	 */
	private String[][] expectedSolvedPieces;
	/**
	 * The ith element of this array stores the hints for the ith sub-tutorial.
	 */
	private String[][] hints;
	/**
	 * The ith element of this array stores the optimal solutions for the ith
	 * sub-tutorial.
	 */
	private String[][] optimalSolutions;
	/**
	 * The ith element of this array stores the solution for the ith
	 * sub-tutorial.
	 */
	private String[] explanations;
	/**
	 * This variable is used to open the file containing the tutorial.
	 */
	private TextFile currentFile;
	/**
	 * This stores the index of the next hint to be shown.
	 */
	private int hintIndex = -1;
	/**
	 * This stores the index of the current sub-tutorial.
	 */
	private int subTutorialIndex = 0;
	/**
	 * Each element of this array stores a single line in the text file being
	 * read.
	 */
	private String[] fileData;
	/**
	 * This stores the number of sub-tutorials.
	 */
	private int numSubTutorials;
	/**
	 * If true, then a tutorial has been loaded.
	 */
	private boolean tutorialLoaded;
	/**
	 * If the ith element of the array is true, then during the ith tutorial the
	 * user is granted permission to perform moves.
	 */
	private boolean[] tutorialsRequiringUserAction;
	/**
	 * If the ith element of the array is true, then during the ith tutorial the
	 * users actions will be checked to see if they fulfil certain criteria.
	 */
	private boolean[] tutorialsRequiringUserSolution;
	/**
	 * This stores the string “cross”. Used for readability.
	 */
	private static final String CROSS_SOLVED = "cross";
	/**
	 * This stores the string “corners”. Used for readability.
	 */
	private static final String CORNERS_SOLVED = "corners";
	/**
	 * This stores the string “edges”. Used for readability.
	 */
	private static final String EDGES_SOLVED = "edges";
	/**
	 * This stores the string “edge orientation”. Used for readability.
	 */
	private static final String EDGE_ORIENTATION_SOLVED = "edge orientation";
	/**
	 * This stores the string “corner orientation”. Used for readability.
	 */
	private static final String CORNER_ORIENTATION_SOLVED = "corner orientation";
	/**
	 * This stores the string “oll”. Used for readability.
	 */
	private static final String ORIENTATION_SOLVED = "oll";
	/**
	 * This stores the string “corner permutation”. Used for readability.
	 */
	private static final String CORNER_PERMUTATION_SOLVED = "corner permutation";
	/**
	 * This stores the string “edge permutation”. Used for readability.
	 */
	private static final String EDGE_PERMUTATION_SOLVED = "edge permutation";
	/**
	 * This stores the string “pll”. Used for readability.
	 */
	private static final String PERMUTATION_SOLVED = "pll";

	/**
	 * Constructor - sets up fields
	 * 
	 * @param cube
	 *            the cube to be used during tutorials.
	 */
	public Tutorial(Cube cube) {
		this.cube = cube;
		solveMaster = new SolveMaster(cube);
		crossSolver = new CrossSolver(cube);
		cornerSolver = new CornerSolver(cube);
		edgeSolver = new EdgeSolver(cube);
		orientationSolver = new OrientationSolver(cube);
		permutationSolver = new PermutationSolver(cube);

		scrambles = new String[1];
		descriptions = new String[1];
		expectedSolvedPieces = new String[1][1];
		hints = new String[1][1];
		optimalSolutions = new String[1][1];
		explanations = new String[1];
		tutorialsRequiringUserAction = new boolean[1];
	}

	/**
	 * Loads the tutorial stored in the file at the specified file path. The
	 * fields store the data from the file.
	 * 
	 * @param filePath
	 *            the path of the file which contains the tutorial
	 * @throws Exception
	 *             if the file cannot be accessed properly or is not formatted
	 *             correctly
	 */
	public void loadTutorial(String filePath) throws Exception {
		// Stores the number of lines in the the file.
		int numLines;
		// Stores the index of the current line in the file being processed.
		int currentIndex = 0;
		// Stores the index of the next heading in the file.
		int headingIndex;
		hintIndex = -1;
		subTutorialIndex = 0;

		currentFile = new TextFile();
		currentFile.setFilePath(filePath);
		currentFile.setIO(TextFile.READ);

		fileData = currentFile.readAllLines();
		numLines = fileData.length;
		currentFile.close();

		numSubTutorials = getNumSubTutorials();
		scrambles = new String[numSubTutorials];
		descriptions = new String[numSubTutorials];
		expectedSolvedPieces = new String[numSubTutorials][20];
		hints = new String[numSubTutorials][20];
		optimalSolutions = new String[numSubTutorials][20];
		explanations = new String[numSubTutorials];
		tutorialsRequiringUserAction = new boolean[numSubTutorials];
		tutorialsRequiringUserSolution = new boolean[numSubTutorials];

		for (int i = 0; i < numSubTutorials; ++i) {
			if (fileData[currentIndex].contains("ENABLE"))
				tutorialsRequiringUserAction[i] = true;
			else
				tutorialsRequiringUserAction[i] = false;

			currentIndex += 2;
			scrambles[i] = fileData[currentIndex];

			currentIndex += 2;

			/*
			 * This finds the next section in the sub-tutorial
			 */
			descriptions[i] = "";
			while ((!fileData[currentIndex].equals("EXPECTED FINAL STATE:")) && (!fileData[currentIndex].equals("*"))) {
				descriptions[i] += fileData[currentIndex] + "\n";
				++currentIndex;
			}

			descriptions[i] = descriptions[i].substring(0, descriptions[i].lastIndexOf("\n"));

			if (fileData[currentIndex].equals("EXPECTED FINAL STATE:")) {
				tutorialsRequiringUserSolution[i] = true;
				++currentIndex;

				/*
				 * Finds and stores the pieces that are expected to be solved.
				 * It does this by finding the index of the next heading and
				 * then storing each line before that heading.
				 */
				headingIndex = LinearSearch.linearSearchStartsWith(
						Arrays.copyOfRange(fileData, currentIndex, numLines), "HINT:");
				expectedSolvedPieces[i] = new String[headingIndex];
				for (int j = 0; j < headingIndex; ++j) {
					expectedSolvedPieces[i][j] = fileData[currentIndex];
					++currentIndex;
				}
				++currentIndex;

				/*
				 * Finds and stores the hints. It does this by finding the index
				 * of the next heading and then storing each line before that
				 * heading.
				 */
				headingIndex = LinearSearch.linearSearchStartsWith(
						Arrays.copyOfRange(fileData, currentIndex, numLines), "OPTIMAL SOLUTION:");
				hints[i] = new String[headingIndex];
				for (int j = 0; j < headingIndex; ++j) {
					hints[i][j] = fileData[currentIndex];
					++currentIndex;
				}
				++currentIndex;

				/*
				 * Finds and stores the optimal solutions. It does this by
				 * finding the index of the next heading and then storing each
				 * line before that heading.
				 */
				headingIndex = LinearSearch.linearSearchStartsWith(
						Arrays.copyOfRange(fileData, currentIndex, numLines), "EXPLANATION:");
				optimalSolutions[i] = new String[headingIndex];
				for (int j = 0; j < headingIndex; ++j) {
					optimalSolutions[i][j] = fileData[currentIndex];
					++currentIndex;
				}
				++currentIndex;

				/*
				 * Finds and stores the explanation. It does this by finding the
				 * index of the next heading and then appending each line before
				 * the heading to explanations[i].
				 */
				headingIndex = LinearSearch.linearSearchStartsWith(
						Arrays.copyOfRange(fileData, currentIndex, numLines), "*");
				explanations[i] = "";
				for (int j = 0; j < headingIndex; ++j) {
					explanations[i] += fileData[currentIndex] + "\n";
					++currentIndex;
				}
				++currentIndex;

				explanations[i] = explanations[i].substring(0, explanations[i].lastIndexOf("\n"));
			} else {
				++currentIndex;
			}
		}

		tutorialLoaded = true;
	}

	/**
	 * @return the number of sub-tutorials in the tutorial
	 */
	private int getNumSubTutorials() {
		int length = fileData.length;
		int num = 0;

		for (int i = 0; i < length; ++i) {
			if (fileData[i].startsWith("SCRAMBLE:"))
				++num;
		}

		return num;
	}

	/**
	 * Loads the next sub-tutorial, i.e. the sub-tutorial index is incremented
	 * and other indices are reset.
	 */
	public void loadNextSubTutorial() {
		if (subTutorialIndex < numSubTutorials - 1) {
			++subTutorialIndex;
			hintIndex = -1;
		}
	}

	/**
	 * Loads the previous sub-tutorial, i.e. the sub-tutorial index is
	 * decremented and other indices are reset.
	 */
	public void loadPreviousSubTutorial() {
		if (subTutorialIndex > 0) {
			--subTutorialIndex;
			hintIndex = -1;
		}
	}

	/**
	 * @return the scramble for the current sub-tutorial
	 */
	public String getScramble() {
		return scrambles[subTutorialIndex];
	}

	/**
	 * @return the description for the current sub-tutorial
	 */
	public String getDescription() {
		return String.format("(%d/%d): %n%s", subTutorialIndex + 1, scrambles.length, descriptions[subTutorialIndex]);
	}

	/**
	 * Loads the next hint for the current sub-tutorial, i.e. the hint index is
	 * incremented. The hint index will follow 0, 1, 2, ..., n, 0, 1, 2, ...
	 */
	public void loadNextHint() {
		hintIndex = (hintIndex + 1) % hints[subTutorialIndex].length;
	}

	/**
	 * Loads the previous hint for the current sub-tutorial, i.e. the hint index
	 * is decremented. The hint index will follow ..., 2, 1, 0, n, n - 1, ...,
	 */
	public void loadPreviousHint() {
		hintIndex = (hintIndex - 1 + hints.length) % hints[subTutorialIndex].length;
	}

	/**
	 * @return the current hint for the current sub-tutorial
	 */
	public String getHint() {
		return String.format("Hint %d: %s", hintIndex + 1, hints[subTutorialIndex][hintIndex]);
	}

	/**
	 * @return the explanation for the current sub-tutorial
	 */
	public String getExplanation() {
		return explanations[subTutorialIndex];
	}

	/**
	 * @return the optimal solutions for the current sub-tutorial
	 */
	public String[] getOptimalSolutions() {
		return optimalSolutions[subTutorialIndex];
	}

	/**
	 * @return <b>true</b> if the critera for the current sub-tutorial has been
	 *         filled by the user; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean criteriaFilled() {
		Cubie currentCubie;
		String current;

		try {
			for (int i = 0; i < expectedSolvedPieces[subTutorialIndex].length; ++i) {
				current = expectedSolvedPieces[subTutorialIndex][i].toLowerCase();

				switch (current) {
				case CROSS_SOLVED:
					if (!isCrossSolved())
						return false;
					break;
				case CORNERS_SOLVED:
					if (!cornerSolver.firstLayerCornersSolved())
						return false;
					break;
				case EDGES_SOLVED:
					if (!edgeSolver.middleLayerEdgesSolved())
						return false;
					break;
				case EDGE_ORIENTATION_SOLVED:
					if (!orientationSolver.isEdgeOrientationSolved())
						return false;
					break;
				case CORNER_ORIENTATION_SOLVED:
					if (!orientationSolver.isCornerOrientationSolved())
						return false;
					break;
				case ORIENTATION_SOLVED:
					if (!orientationSolver.isOrientationSolved())
						return false;
					break;
				case CORNER_PERMUTATION_SOLVED:
					if (!isCornerPermutationSolved())
						return false;
					break;
				case EDGE_PERMUTATION_SOLVED:
					if (!isEdgePermutationSolved())
						return false;
					break;
				case PERMUTATION_SOLVED:
					if (!(isCornerPermutationSolved() && isEdgePermutationSolved()))
						return false;
					break;
				default:
					boolean solved = true;
					currentCubie = new Cubie(getStickers(expectedSolvedPieces[subTutorialIndex][i]));
					solveMaster.clearMoves();
					solveMaster.rotateToTop(Color.white);
					solved = solveMaster.newPieceSolved(currentCubie);

					cube.performAbsoluteMoves(SolveMaster.getReverseStringMoves(solveMaster.getCatalogMoves()));

					return solved;
				}
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * This returns an array of Colors containing all colours denoted in the
	 * <code>stickerFormat</code> parameter. <br>
	 * For example, Input: <code>"r-g"</code> will return
	 * <code>{Color.red, Color.green}</code>
	 * 
	 * @param stickerFormat
	 *            denotes the stickers with letters separated by '-' characters.
	 * @return an array of Colors containing all colours denoted in the
	 *         <code>stickerFormat</code> parameter.
	 */
	private Color[] getStickers(String stickerFormat) {
		Color[] stickers = new Color[(stickerFormat.length() + 1) / 2];

		for (int i = 0; i < stickers.length; ++i) {
			stickers[i] = Cubie.getWordToColor(stickerFormat.substring(i * 2, (i * 2) + 1));
		}

		return stickers;
	}

	/**
	 * @return <b>true</b> if the current sub-tutorial requires/allows the user
	 *         to perform moves, i.e. moves are not locked; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean requiresUserAction() {
		return (tutorialsRequiringUserAction[subTutorialIndex]);
	}

	/**
	 * @return <b>true</b> if the current sub-tutorial requires the user to
	 *         solve a problem; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean requiresUserSolution() {
		return (tutorialsRequiringUserSolution[subTutorialIndex]);
	}

	/**
	 * @return the number of moves in the first optimal solution.
	 */
	public int getOptimalSolutionLength() {
		return getNumMovesWithoutRotations(optimalSolutions[subTutorialIndex][0]);
	}

	/**
	 * @param moves
	 *            the moves to be analysed
	 * @return the number of moves in <code>moves</code> but not counting
	 *         rotations
	 */
	public static int getNumMovesWithoutRotations(String moves) {
		moves = moves.toUpperCase();
		int numMoves = 0;

		for (int i = 0; i < moves.length(); ++i) {
			if ("UDRLFBM".contains(moves.substring(i, i + 1)))
				++numMoves;
		}

		return numMoves;
	}

	/**
	 * @param moves
	 *            the moves to be analysed
	 * @return the number of moves in <code>moves</code> but not counting
	 *         rotations
	 */
	public static int getNumMovesWithoutRotations(LinkedList<String> moves) {
		String stringMoves = "";
		int size = moves.size();

		for (int i = 0; i < size; ++i) {
			stringMoves += moves.get(i) + " ";
		}

		return getNumMovesWithoutRotations(stringMoves);
	}

	/**
	 * @return <b>true</b> if the tutorial is loaded; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean isLoaded() {
		return tutorialLoaded;
	}

	/**
	 * @return <b>true</b> if the cross is solved; <br>
	 *         <b>false</b> otherwise
	 */
	private boolean isCrossSolved() {
		Edge[] crossEdges = new Edge[4];

		for (int i = 0; i < 4; ++i)
			crossEdges[i] = new Edge(Edge.getInitialStickers(i));

		for (int i = 0; i < 4; ++i) {
			if (!crossSolver.isPieceSolved(crossEdges[i]))
				return false;
		}

		return true;
	}

	/**
	 * @return <b>true</b> if the permutation of the corners is solved; <br>
	 *         <b>false</b> otherwise
	 */
	private boolean isCornerPermutationSolved() {
		Corner[] corners = new Corner[4];

		for (int i = 4; i < 8; ++i)
			corners[i - 4] = new Corner(Corner.getInitialStickers(i));

		for (int i = 0; i < 4; ++i)
			if (!permutationSolver.isPieceSolved(corners[i]))
				return false;

		return true;
	}

	/**
	 * @return <b>true</b> if the permutation of the edges is solved; <br>
	 *         <b>false</b> otherwise
	 */
	private boolean isEdgePermutationSolved() {
		Edge[] edges = new Edge[4];

		for (int i = 8; i < 12; ++i)
			edges[i - 8] = new Edge(Edge.getInitialStickers(i));

		for (int i = 0; i < 4; ++i)
			if (!permutationSolver.isPieceSolved(edges[i]))
				return false;

		return true;
	}

	/**
	 * @return the index of the current sub-tutorial
	 */
	public int getSubTutorialIndex() {
		return subTutorialIndex;
	}

	/**
	 * @return <b>true</b> if the current sub-tutorial is the first
	 *         sub-tutorial; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean isFirstSubTutorial() {
		return subTutorialIndex == 0;
	}

	/**
	 * @return <b>true</b> if the current sub-tutorial is the last sub-tutorial; <br>
	 *         <b>false</b> otherwise
	 */
	public boolean isLastSubTutorial() {
		return subTutorialIndex == (numSubTutorials - 1);
	}

	/*
	 * public static void main(String[] args) { Cube cube = new Cube(); Tutorial
	 * t = new Tutorial(cube); t.loadTutorial("CrossTutorial.txt");
	 * 
	 * System.out.println("Description: " + t.getDescription());
	 * 
	 * if (t.requiresUserAction()) { System.out.println("Hint: " +
	 * t.getNextHint()); System.out.println("Optimal Solution: " +
	 * t.getOptimalSolutions()[0]); System.out.println("Explanation:\n" +
	 * t.getExplanation()); System.out.println("Optimal Solution Length: " +
	 * t.getOptimalSolutionLength());
	 * 
	 * if (t.criteriaFilled()) System.out.println("All Criteria Filled!");
	 * 
	 * else System.out.println("Not all criteria met"); } }
	 */
}
