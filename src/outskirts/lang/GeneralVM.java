package outskirts.lang;

import outskirts.lang.lexer.Lexer;
import outskirts.lang.lexer.parser.Parser;
import outskirts.lang.lexer.parser.Rule;
import outskirts.lang.lexer.syntax.Syntax;
import outskirts.util.IOUtils;
import outskirts.util.StringUtils;
import outskirts.util.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static outskirts.lang.lexer.parser.Parser.rule;

public class GeneralVM {



    public static void main(String[] args) throws Throwable {

        Rule rField  = new Rule.RuleList().name().name().id(";");
        Rule rMethod = new Rule.RuleList().name().name().id("(").id(")").id("{").id("}");


        Rule rClass = new Rule.RuleList().id("class").name().id("{").repeat(new Rule.RuleOr(rField, rMethod)).id("}");

        String base = "/Users/dreamtowards/Projects/Outskirts/src/outskirts/lang/vm/rt";
        Lexer lexer = new Lexer(IOUtils.toString(new FileInputStream(new File(base, "main.g"))));

        Log.LOGGER.info(rClass.read(lexer));

    }

}
