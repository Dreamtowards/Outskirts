package outskirts.lang;

import outskirts.lang.lexer.Lexer;
import outskirts.lang.lexer.parser.Rule;
import outskirts.lang.lexer.syntax.*;
import outskirts.util.IOUtils;
import outskirts.util.StringUtils;
import outskirts.util.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static outskirts.lang.lexer.parser.Rule.rulels;

public class GeneralVM {



    public static void main(String[] args) throws Throwable {

        Rule rMethodCall = rulels(SyntaxMethodCall::new).name().id("(").id(")");

        Rule statement = new Rule.RuleOr(
                rMethodCall
        );
        Rule rBlock = rulels(SyntaxBlock::new).id("{").repeat(statement).id("}");

        Rule rField  = rulels(SyntaxField::new).name().name().id(";");
        Rule rMethod = rulels(SyntaxMethod::new).name().name().id("(").id(")").and(rBlock);


        Rule rClass = rulels(SyntaxClass::new).id("class").name().id("{").repeat(new Rule.RuleOr(rField, rMethod)).id("}");

        String code = IOUtils.toString(new FileInputStream("/Users/dreamtowards/Projects/Outskirts/src/outskirts/lang/vm/rt/main.g"));
        Lexer lexer = new Lexer(code);

        Log.LOGGER.info(rClass.read(lexer));
//        Log.LOGGER.info(rMethod.read(new Lexer("void main() { func() }")));

    }

}
