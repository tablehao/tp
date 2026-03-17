package linuxlingo.shell;

import java.util.List;

/**
 * Transforms a raw input string into a structured execution plan.
 *
 * <p><b>Owner: A</b></p>
 *
 * <p>Parsing pipeline:</p>
 * <pre>
 *   Raw Input String
 *       │
 *       ▼
 *   Tokenizer        — split by whitespace, respecting quotes ("..." and '...')
 *       │
 *       ▼
 *   Operator Splitter — split on operators: |  &gt;  &gt;&gt;  &amp;&amp;  ;
 *       │
 *       ▼
 *   Command Segment List — each segment = command name + arguments
 *       │
 *       ▼
 *   Execution Engine  — execute segments, connecting via pipes/redirects
 * </pre>
 */
public class ShellParser {

    public enum TokenType {
        WORD, PIPE, REDIRECT, APPEND, AND, SEMICOLON
    }

    public static class Token {
        public final String value;
        public final TokenType type;

        public Token(String value, TokenType type) {
            this.value = value;
            this.type = type;
        }

        @Override
        public String toString() {

            return type + ":" + value;
        }
    }

    public static class RedirectInfo {
        public final String operator; // ">" or ">>"
        public final String target;   // file path

        public RedirectInfo(String operator, String target) {
            this.operator = operator;
            this.target = target;
        }

        public boolean isAppend() {
            return ">>".equals(operator);
        }
    }

    public static class Segment {
        public final String commandName;
        public final String[] args;
        public final RedirectInfo redirect;

        public Segment(String commandName, String[] args, RedirectInfo redirect) {
            this.commandName = commandName;
            this.args = args;
            this.redirect = redirect;
        }
    }

    public static class ParsedPlan {
        public final List<Segment> segments;
        public final List<TokenType> operators;

        public ParsedPlan(List<Segment> segments, List<TokenType> operators) {
            this.segments = segments;
            this.operators = operators;
        }
    }

    /**
     * Parse a raw input string into a {@link ParsedPlan}.
     *
     * <p>Steps:</p>
     * <ol>
     *   <li>Tokenize input — split by whitespace, respecting single/double quotes.
     *       Recognize operators: {@code |}, {@code >}, {@code >>}, {@code &&}, {@code ;}.</li>
     *   <li>Split token list by inter-segment operators (PIPE, AND, SEMICOLON).</li>
     *   <li>Within each segment, extract command name, args, and optional redirect info.</li>
     * </ol>
     */
    public ParsedPlan parse(String input) {
        List<Segment> segments = new java.util.ArrayList<>();
        List<TokenType> operators = new java.util.ArrayList<>();

        // Edge case
        if (input == null || input.trim().isEmpty()) {
            return new ParsedPlan(segments, operators);
        }

        // Tokenizer (char-by-char state machine)
        List<Token> tokens = new java.util.ArrayList<>();

        enum State { NORMAL, IN_SINGLE_QUOTE, IN_DOUBLE_QUOTE}
        State state = State.NORMAL;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            switch(state) {
            case NORMAL:
                switch(c) {
                case ' ', '\t':
                    if (!current.isEmpty()) {
                        tokens.add(new Token(current.toString(), TokenType.WORD));
                        current.setLength(0);
                    }
                    break;
                case '|':
                    if (!current.isEmpty()) {
                        tokens.add(new Token(current.toString(), TokenType.WORD));
                        current.setLength(0);
                    }
                    tokens.add(new Token("|", TokenType.PIPE));
                    break;
                case ';':
                    if (!current.isEmpty()) {
                        tokens.add(new Token(current.toString(), TokenType.WORD));
                        current.setLength(0);
                    }
                    tokens.add(new Token(";", TokenType.SEMICOLON));
                    break;
                case '&':
                    if (i + 1 < input.length() && input.charAt(i + 1) == '&') {
                        if (!current.isEmpty()) {
                            tokens.add(new Token(current.toString(), TokenType.WORD));
                            current.setLength(0);
                        }
                        tokens.add(new Token("&&", TokenType.AND));
                        i++; // to skip the second '&'
                    } else {
                        current.append(c); // treat lone '&' symbol as an ordinary char
                    }
                    break;
                case '>':
                    if (!current.isEmpty()) {
                        tokens.add(new Token(current.toString(), TokenType.WORD));
                        current.setLength(0);
                    }
                    if (i + 1 < input.length() && input.charAt(i + 1) == '>') {
                        tokens.add(new Token(">>", TokenType.APPEND));
                        i++; // to skip the second '>'
                    } else {
                        tokens.add(new Token(">", TokenType.REDIRECT));
                    }
                    break;
                case '\'':
                    state = State.IN_SINGLE_QUOTE;
                    break;// do not add quote to current
                case '"':
                    state = State.IN_DOUBLE_QUOTE;
                    break;// do not add quote to current
                default:
                    current.append(c);
                }
            case IN_SINGLE_QUOTE:
                if (c == '\'') {
                    state = State.NORMAL; // closing quote, not to be added to current
                } else {
                    current.append(c); // everything inside single quotes is literal
                }
                break;
            case IN_DOUBLE_QUOTE:
                if (c == '"') {
                    state = State.NORMAL; // closing quote, not to be added to current
                } else {
                    current.append(c); // everything inside double quotes is literal
                }
                break;
            }
        }

        // Emitting any remaining token after end of input
        if (!current.isEmpty()) {
            tokens.add(new Token(current.toString(), TokenType.WORD));
        }

        // Splitting tokens into Segment objects
        // Traverse the token list accumulating WORDs into the current segment
        // On REDIRECT/APPEND: consume the next WORD as the redirect target
        // ON PIPE/AND/SEMICOLON: finalize the current segment, record operator.
        List<String> currentWords = new java.util.ArrayList<>();
        RedirectInfo currentRedirect = null;
        boolean expectRedirectTarget = false;
        String pendingRedirectOp = null;

        for (Token tok : tokens) {
            if (expectRedirectTarget) {
                // The token immediately after > or >> is considered the target file path
                if (tok.type == TokenType.WORD) {
                    currentRedirect = new RedirectInfo(pendingRedirectOp, tok.value);
                    expectRedirectTarget = false;
                    pendingRedirectOp = null;
                    continue;
                }
                // For improper input i.e. no target provided, reset

                expectRedirectTarget = false;
                pendingRedirectOp = null;
            }

            switch (tok.type) {
            case WORD:
                currentWords.add(tok.value);
                break;
            case REDIRECT:
                expectRedirectTarget = true;
                pendingRedirectOp = ">";
                break;
            case APPEND:
                expectRedirectTarget = true;
                pendingRedirectOp = ">>";
            case PIPE, AND, SEMICOLON:
                // Finalizing the current segment before recording the operator
                if (!currentWords.isEmpty()) {
                    segments.add(buildSegment(currentWords, currentRedirect));
                    currentWords = new java.util.ArrayList<>();
                    currentRedirect = null;
                }
                operators.add(tok.type);
            }
        }

        // Finalizing the last segment (no trailing operator)
        if (!currentWords.isEmpty()) {
            segments.add(buildSegment(currentWords, currentRedirect));
        }

        return new ParsedPlan(segments, operators);
    }

    /**
     * build a segment from an accumulated word list.
     * @param words the accumulated word list
     * @param redirect optional redirect
     * @return the segment
     */
    private Segment buildSegment(List<String> words, RedirectInfo redirect) {
        String commandName = words.get(0);
        String[] args = new String[words.size() - 1];
        for(int i = 1; i < words.size(); i++) {
            args[i - 1] = words.get(i);
        }
        return new Segment(commandName, args, redirect);
    }
}
