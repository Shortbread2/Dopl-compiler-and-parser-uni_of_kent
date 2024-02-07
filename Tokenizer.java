import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Tokenizer, aka Scanner, aka Lexical analyser
 * for DOPL files.
 */
public class Tokenizer {
    /**
     * @param filename The file to be analysed.
     * @throws IOException on any file issue
     */
    private ArrayList<String> output;

    public Tokenizer(String filename)
            throws IOException {
        output = new ArrayList<>();
        HashMap<String, String> lexemeMap = new HashMap<>();
        lexemeMap.put("character", "KEYWORDS");
        lexemeMap.put("do", "KEYWORDS");
        lexemeMap.put("else", "KEYWORDS");
        lexemeMap.put("endif", "KEYWORDS");
        lexemeMap.put("endloop", "KEYWORDS");
        lexemeMap.put("finish", "KEYWORDS");
        lexemeMap.put("if", "KEYWORDS");
        lexemeMap.put("integer", "KEYWORDS");
        lexemeMap.put("logical", "KEYWORDS");
        lexemeMap.put("loopif", "KEYWORDS");
        lexemeMap.put("print", "KEYWORDS");
        lexemeMap.put("start", "KEYWORDS");
        lexemeMap.put("then", "KEYWORDS");

        // "./dopl-samples/mytest.dopl"
        // "C:/Users/epicc/Desktop/uni work/year2/computer systems/assignment 4/dopl-samples/dopl-samples/loop2.dopl"
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        ArrayList<String> Lines = new ArrayList<>();

        String symbol = "";
        int lineNum = 1;
        String theLine;
        while ((theLine = reader.readLine()) != null) {
            theLine = theLine.trim();

            for (String lexeme : theLine.split(" ")) {
                for (int i = 0; i < lexeme.length(); i++) {
                    if (lexeme.substring(i, i + 1).equals(")") || lexeme.substring(i, i + 1).equals(";")
                            || lexeme.substring(i, i + 1).equals("(") || lexeme.substring(i, i + 1).equals(",")) {
                        if (lexeme.length() > 1) {
                            if (i != 0)
                                if (lexeme.substring(i - 1, i).matches("^[\\S+]$")) {
                                    lexeme = lexeme.substring(0, i) + " " + lexeme.substring(i, lexeme.length());
                                    i++;
                                }
                            if (i != lexeme.length() - 1)
                                if (lexeme.substring(i + 1, i + 2).matches("^[\\S+]$")) {
                                    lexeme = lexeme.substring(0, i + 1) + " "
                                            + lexeme.substring(i + 1, lexeme.length());
                                }
                        }
                    }
                    if (lexeme.substring(i, i + 1).equals(".")) {
                        if (lexeme.length() > 1) {
                            if (i != 0)
                                if (lexeme.substring(i - 1, i).matches("^[.]|[0-9]$")) {
                                    lexeme = lexeme.substring(0, i) + " " + lexeme.substring(i, lexeme.length());
                                    i++;
                                }
                            if (i != lexeme.length() - 1)
                                if (lexeme.substring(i + 1, i + 2).matches("^[.]|[0-9]$")) {
                                    lexeme = lexeme.substring(0, i + 1) + " "
                                            + lexeme.substring(i + 1, lexeme.length());
                                }
                        }
                    }
                }
                for (int i = 0; i < lexeme.length() - 1; i++) {
                    if (lexeme.substring(i, i + 2).equals("<-")) {
                        if (lexeme.length() > 2) {
                            if (i != 0)
                                if (lexeme.substring(i - 1, i).matches("^[\\S+]$")) {
                                    lexeme = lexeme.substring(0, i) + " " + lexeme.substring(i, lexeme.length());
                                    i++;
                                }
                            if (i != lexeme.length() - 1)
                                if (lexeme.substring(i + 2, i + 3).matches("^[\\S+]$")) {
                                    lexeme = lexeme.substring(0, i + 2) + " "
                                            + lexeme.substring(i + 2, lexeme.length());
                                }
                        }
                    }
                }

                for (String lexeme2 : lexeme.split(" ")) {
                    if (!lexeme2.equals("")) {
                        symbol = "";

                        if (lexemeMap.containsKey(lexeme2)) {
                            symbol = lexemeMap.get(lexeme2);
                        } else if (lexeme2.matches("^([a-zA-Z][0-9a-zA-Z_]*)$")) {
                            symbol = "IDENTIFIER";
                        } else if (lexeme2.matches("^([0-9]+)$")) {
                            symbol = "INTEGER_CONSTANT";
                        } else if (lexeme2.substring(0, 1).equals("\"")) {
                            if (lexeme2.substring(2, 3).equals("\"")) {
                                symbol = "CHARACTER_CONSTANT";
                            }
                        } else if (lexeme2.substring(0, 1).equals(".")) {
                            if (lexeme2.substring(lexeme2.length() - 1, lexeme2.length()).equals(".")) {
                                symbol = "OPERATORS";
                            }
                        } else if (lexeme2.contains(";")) {
                            symbol = "SEMICOLON";
                        } else if (lexeme2.contains("(")) {
                            symbol = "OPENING_BRACKET";
                        } else if (lexeme2.contains(")")) {
                            symbol = "CLOSING_BRACKET";
                        } else if (lexeme2.contains("<-")) {
                            symbol = "ASSIGN";
                        } else if (lexeme2.contains(",")) {
                            symbol = "COMMA";
                        } else {
                            System.err.println("error");
                            System.exit(0);
                        }
                        output.add(symbol + " " + lineNum + " " + lexeme2);
                    }
                }
            }

            Lines.add(theLine);
            lineNum += 1;
        }
        reader.close();
        // System.out.println(output);
        // System.out.println(Lines);
    }

    public ArrayList<String> getParserInput() {
        return output;
    }

}