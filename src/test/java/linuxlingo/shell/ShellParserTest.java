package linuxlingo.shell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ShellParser
 */
class ShellParserTest {

    private ShellParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ShellParser();
    }

    @Test
    public void parse_singleCommand_returnsOneSegment() {
        ShellParser.ParsedPlan plan = parser.parse("echo hello");
        assertEquals(1, plan.segments.size());
        assertEquals("echo", plan.segments.get(0).commandName);
        assertEquals(1, plan.segments.get(0).args.length);
        assertEquals("hello", plan.segments.get(0).args[0]);
    }

    @Test
    public void parse_emptyInput_returnsEmptyPlan() {
        ShellParser.ParsedPlan plan = parser.parse("");
        assertEquals(0, plan.segments.size());
        assertEquals(0, plan.operators.size());
    }

    @Test
    public void parse_nullInput_returnsEmptyPlan() {
        ShellParser.ParsedPlan plan = parser.parse(null);
        assertEquals(0, plan.segments.size());
        assertEquals(0, plan.operators.size());
    }

}