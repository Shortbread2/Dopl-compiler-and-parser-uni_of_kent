import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Parse a DOPL source file and report either that the file is ok
 * or it contains an error.
 * Parsing terminates on the first error.
 * 
 * @author
 * @version
 */
public class Parser {
	private final Tokenizer tokenizer;
	/**
	 * Create a parser.
	 * 
	 * @param filename The file to be translated.
	 * @throws IOException on any input issue.
	 */
	private ArrayList<String> input = new ArrayList<>();
	private HashMap<String, String> IDENTIFIER_LIST = new HashMap<>();
	private ArrayList<String> opList;
	private ArrayList<String> unaryOp;
	private ArrayList<String> logicalOp;
	private ArrayList<String> relationalOp;
	private ArrayList<String> term;

	public Parser(String filename)
			throws IOException {
		tokenizer = new Tokenizer(filename);
		input = tokenizer.getParserInput();
	}

	/**
	 * Parse the DOPL source file.
	 * 
	 * @throws IOException on any input issue.
	 */
	public void parse()
			throws IOException {
		term = new ArrayList<>();
		term.add("INTEGER_CONSTANT");
		term.add("CHARACTER_CONSTANT");
		term.add("IDENTIFIER");
		term.add("OPENING_BRACKET");
		term.add("CLOSING_BRACKET");

		opList = new ArrayList<>();
		opList.add(".plus.");
		opList.add(".minus.");
		opList.add(".mul.");
		opList.add(".div.");
		opList.add(".and.");
		opList.add(".or.");
		opList.add(".eq.");
		opList.add(".ne.");
		opList.add(".lt.");
		opList.add(".gt.");
		opList.add(".le.");
		opList.add(".ge.");

		relationalOp = new ArrayList<>();
		relationalOp.add(".eq.");
		relationalOp.add(".ne.");
		relationalOp.add(".lt.");
		relationalOp.add(".gt.");
		relationalOp.add(".le.");
		relationalOp.add(".ge.");

		logicalOp = new ArrayList<>();
		logicalOp.add(".and.");
		logicalOp.add(".or.");

		unaryOp = new ArrayList<>();
		unaryOp.add(".minus.");
		unaryOp.add(".not.");

		int closingBracketsNeeded = 0;
		if (input.get(0).split(" ")[2].equals("start") && input.get(input.size() - 1).split(" ")[2].equals("finish")) {

			for (int i = 0, count = 0; i < input.size(); i++)
				if (input.get(i).split(" ")[2].equals("start") || input.get(i).split(" ")[2].equals("finish")) {
					count++;
					if (count > 2) {
						System.out.println("error");
						System.exit(0);
					}
				}

			for (int i = 0; i < input.size(); i++) {
				// System.out.println(input.get(i));

				// checks brackets
				if (input.get(i).contains("("))
					closingBracketsNeeded++;

				if (input.get(i).contains(")"))
					closingBracketsNeeded--;

				if ((closingBracketsNeeded != 0 && input.get(i).contains(";")) || closingBracketsNeeded < 0) {
					System.out.println("error");
					System.exit(0);
				}
				if (input.get(i).contains("INTEGER_CONSTANT") || input.get(i).contains("CHARACTER_CONSTANT")
						|| input.get(i).contains("IDENTIFIER")) {
					if (input.get(i - 1).contains("INTEGER_CONSTANT") || input.get(i - 1).contains("CHARACTER_CONSTANT")
							|| input.get(i - 1).contains("IDENTIFIER")) {
						System.out.println("error");
						System.exit(0);
					} else if (input.get(i + 1).contains("INTEGER_CONSTANT")
							|| input.get(i + 1).contains("CHARACTER_CONSTANT")
							|| input.get(i + 1).contains("IDENTIFIER")) {
						System.out.println("error");
						System.exit(0);
					}
				}
			}

			for (int i = 0; i < input.size(); i++) {
				// System.out.println(input.get(i));

				// validate operators
				if (input.get(i).contains("."))
					if (!opList.contains(input.get(i).split(" ")[2]))
						if (!input.get(i).contains(".not.")) {
							System.out.println("error");
							System.exit(0);
						}

				// checks data types and also add identifiers into a list
				if (input.get(i).contains("integer") || input.get(i).contains("character")
						|| input.get(i).contains("logical")) {
					if (i + 2 >= input.size()) {
						System.out.println("error");
						System.exit(0);
					}
					if (input.get(i + 1).contains("IDENTIFIER")) {
						IDENTIFIER_LIST.put(input.get(i + 1).split(" ")[2], input.get(i).split(" ")[2]);
						for (int j = i + 2; j < input.size(); j += 2) {
							if (input.get(j).contains(";")) {
								// System.out.println(input.get(i) + ": " + "valid");
								break;
							} else if (input.get(j).contains(",")) {
								if (!input.get(j + 1).contains("IDENTIFIER")) {
									System.out.println("error");
									System.exit(0);
								} else {
									IDENTIFIER_LIST.put(input.get(j + 1).split(" ")[2], input.get(i).split(" ")[2]);
								}
							} else {
								System.out.println("error");
								System.exit(0);
							}
						}
					}
				}

				// validate statemets
				i = validateStatement(i);
			}
		}

		// checks bracket count
		if (closingBracketsNeeded > 0) {
			System.out.println("error");
			System.exit(0);
		}

		// checks if all identifiers are in the identifier list
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).contains("IDENTIFIER"))
				if (!IDENTIFIER_LIST.keySet().contains(input.get(i).split(" ")[2])) {
					System.out.println("error");
					System.exit(0);
				}
		}

		System.out.println("ok");
		// System.out.println(IDENTIFIER_LIST);
	}

	private int validateStatement(int i) {

		ArrayList<String> dataTypes = new ArrayList<>();
		// validate assignments (<-)
		if (input.get(i).contains("ASSIGN")) {
			if (input.get(i - 1).contains("IDENTIFIER")) {
				// System.out.println(IDENTIFIER_LIST.get(input.get(i - 1).split(" ")[2]));
				for (int j = i + 1; j < input.size(); j++) {
					// System.out.println(input.get(j));
					if (input.get(j).contains(";") && j != i + 1)
						return j;
					if (!term.contains(input.get(j).split(" ")[0])) {
						if (!unaryOp.contains(input.get(j).split(" ")[2]))
							if (!opList.contains(input.get(j).split(" ")[2])) {
								System.out.println("error");
								System.exit(0);
							}
						if (input.get(j).contains("KEYWORD") || j == input.size() - 1) {
							System.out.println("error");
							System.exit(0);
						}
					} else if (input.get(j).contains("IDENTIFIER")) {
						if (!IDENTIFIER_LIST.get(input.get(i - 1).split(" ")[2])
								.equals(IDENTIFIER_LIST.get(input.get(j).split(" ")[2]))) {
							System.out.println("error");
							System.exit(0);
						}
					}
					if (logicalOp.contains(input.get(j).split(" ")[2])
							|| relationalOp.contains(input.get(j).split(" ")[2])
							|| input.get(j).contains(".not.")) {
						dataTypes.add("logical");
					} else if (input.get(j).contains("CHARACTER_CONSTANT")) {
						dataTypes.add("character");
					} else if (input.get(j).contains("IDENTIFIER")) {
						dataTypes.add(IDENTIFIER_LIST.get(input.get(j).split(" ")[2]));
					} else {
						dataTypes.add("integer");
					}

					if (dataTypes.contains("logical")) {
						if (!(IDENTIFIER_LIST.get(input.get(i - 1).split(" ")[2]).equals("logical"))) {
							System.out.println("error");
							System.exit(0);
						}
					} else if (dataTypes.contains("character")) {
						if (!(IDENTIFIER_LIST.get(input.get(i - 1).split(" ")[2]).equals("character"))) {
							System.out.println("error");
							System.exit(0);
						}
					} else if (dataTypes.contains("integer")) {
						if (!(IDENTIFIER_LIST.get(input.get(i - 1).split(" ")[2]).equals("integer"))) {
							System.out.println("error");
							System.exit(0);
						}
					}
				}
			} else {
				System.out.println("error");
				System.exit(0);
			}
		}

		// validate print
		if (input.get(i).contains("print")) {
			for (int j = i + 1; j < input.size(); j++) {
				// System.out.println(input.get(j));
				if (input.get(j).contains(";") && j != i + 1)
					return j;
				if (!term.contains(input.get(j).split(" ")[0]))
					if (!unaryOp.contains(input.get(j).split(" ")[2]))
						if (!opList.contains(input.get(j).split(" ")[2])) {
							System.out.println("error");
							System.exit(0);
						}
				if (input.get(j).contains("KEYWORD") || j == input.size() - 1) {
					System.out.println("error");
					System.exit(0);
				}
			}
		}

		dataTypes.clear();
		// validate loop
		boolean repeat3 = false;
		if (input.get(i).contains("loopif")) {
			if (input.get(i + 1).contains("do")) {
				System.out.println("error");
				System.exit(0);
			}
			for (int j = i + 1; j < input.size(); j++) {
				// System.out.println(input.get(j));
				if ((input.get(j).contains("do") && input.get(j).contains("KEYWORDS")) || repeat3 == true) {
					repeat3 = true;
					int tmpNum = 0;

					if (!dataTypes.contains("logical")) {
						System.out.println("error");
						System.exit(0);
					}

					tmpNum = validateStatement(j + 1);
					if (tmpNum > j + 1) {
						j = tmpNum;
					}
					if (j + 2 < input.size()) {
						if (input.get(j + 2).contains("<-")) {
							tmpNum = validateStatement(j + 2);
						}
					}
					j++;
					if (input.get(j).contains("endloop"))
						if (input.get(j + 1).contains(";")) {
							repeat3 = false;
							return j + 1;
						}
				} else {
					if (logicalOp.contains(input.get(j).split(" ")[2])
							|| relationalOp.contains(input.get(j).split(" ")[2])
							|| input.get(j).contains(".not.")) {
						dataTypes.add("logical");
					} else if (input.get(j).contains("CHARACTER_CONSTANT")) {
						dataTypes.add("character");
					} else if (input.get(j).contains("IDENTIFIER")) {
						dataTypes.add(IDENTIFIER_LIST.get(input.get(j).split(" ")[2]));
					} else {
						dataTypes.add("integer");
					}
				}

				if (!term.contains(input.get(j).split(" ")[0]))
					if (!unaryOp.contains(input.get(j).split(" ")[2]))
						if (!opList.contains(input.get(j).split(" ")[2])) {
							System.out.println("error");
							System.exit(0);
						}
				if (j == input.size() - 1) {
					System.out.println("error");
					System.exit(0);
				}
			}
		}

		dataTypes.clear();
		// validate conditional
		boolean thenIsUsed = false;
		boolean repeat1 = false;
		boolean repeat2 = false;
		if (input.get(i).contains("if") && !input.get(i).contains("endif")) {
			for (int j = i + 1; j < input.size(); j++) {
				// System.out.println(input.get(j));
				if ((input.get(j).contains("then") && input.get(j).contains("KEYWORDS")) || repeat1 == true) {
					repeat1 = true;
					int tmpNum = 0;

					if (!dataTypes.contains("logical")) {
						System.out.println("error");
						System.exit(0);
					}

					tmpNum = validateStatement(j + 1);
					if (tmpNum > j + 1) {
						j = tmpNum;
					}
					if (j + 2 < input.size()) {
						if (input.get(j + 2).contains("<-")) {
							// System.out.println(input.get(j + 2));
							tmpNum = validateStatement(j + 2);
						}
					}
					j++;
					if (input.get(j).contains("endif"))
						if (input.get(j + 1).contains(";"))
							return j + 1;

					if (input.get(j).contains("else")) {
						repeat2 = true;
						thenIsUsed = true;
					}

				} else {
					if (logicalOp.contains(input.get(j).split(" ")[2])
							|| relationalOp.contains(input.get(j).split(" ")[2])
							|| input.get(j).contains(".not.")) {
						dataTypes.add("logical");
					} else if (input.get(j).contains("CHARACTER_CONSTANT")) {
						dataTypes.add("character");
					} else if (input.get(j).contains("IDENTIFIER")) {
						dataTypes.add(IDENTIFIER_LIST.get(input.get(j).split(" ")[2]));
					} else {
						dataTypes.add("integer");
					}
				}
				if ((input.get(j).contains("else") && input.get(j).contains("KEYWORDS") || repeat2 == true)
						&& thenIsUsed == true) {
					repeat1 = false;
					int tmpNum = 0;

					tmpNum = validateStatement(j + 1);
					if (tmpNum > j + 1) {
						j = tmpNum;
					}
					if (j + 2 < input.size()) {
						if (input.get(j + 2).contains("<-")) {
							// System.out.println(input.get(j + 2));
							tmpNum = validateStatement(j + 2);
						}
					}
					j++;
					if (input.get(j).contains("endif"))
						if (input.get(j + 1).contains(";"))
							return j + 1;

					if (input.get(j).contains("else"))
						thenIsUsed = true;

				}
				if (!term.contains(input.get(j).split(" ")[0]))
					if (!unaryOp.contains(input.get(j).split(" ")[2]))
						if (!opList.contains(input.get(j).split(" ")[2])) {
							System.out.println("error");
							System.exit(0);
						}
				if (j == input.size() - 1) {
					System.out.println("error");
					System.exit(0);
				}
			}
		}

		return i;
	}
}