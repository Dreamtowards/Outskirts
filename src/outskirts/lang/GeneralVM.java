package outskirts.lang;

import org.lwjgl.Sys;
import outskirts.lang.interpreter.RuntimeEnvironment;
import outskirts.lang.lexer.Lexer;
import outskirts.lang.lexer.Token;
import outskirts.lang.lexer.parser.RuleLs;
import outskirts.lang.lexer.syntax.*;
import outskirts.util.IOUtils;
import outskirts.util.logging.Log;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collections;

import static outskirts.lang.lexer.parser.RuleLs.*;

public class GeneralVM {



    public static void main(String[] args) throws Throwable {

        RuleLs expr = pass();

        RuleLs funccall = struc(SyntaxMethodCall::new);


        RuleLs primary = pass().or(
                funccall,
                pass().number(SyntaxConstantNumber::new),
                pass().name(SyntaxVariableReference::new),
                pass().string(SyntaxConstantString::new)
        );
        RuleLs factor = pass().or(
                primary,
                pass().id("(").and(expr).id(")"),
                struc(SyntaxNegativeExpression::new).id("-").and(primary)
        );

        // expr setup.
        expr.expr(factor);

        RuleLs block = struc(SyntaxBlock::new);

        RuleLs ifstatement = struc(SyntaxStatementIf::new);

        RuleLs funcdef = struc(SyntaxMethodDeclarate::new).id("def").name()
                .id("(").and(struc(Syntax::new).op(pass().name()).repeat(pass().id(",").name())).id(")").and(block);

        // funccall setup.
        funccall.name().id("(").and(struc(Syntax::new).op(expr).repeat(pass().id(",").and(expr))).id(")");

        RuleLs statement = pass().or(
                ifstatement,
                struc(SyntaxStatementWhile::new).id("while").id("(").and(expr).id(")").and(block),
                struc(SyntaxMethodReturn::new).id("return").op(expr).id(";"),
                struc(SyntaxVariableDeclarate::new).id("var").name().op(pass().id("=").and(expr)).id(";"),
                pass().and(expr).id(";"),
                funcdef,
                pass().and(funccall).id(";"),
                block
        );

        // ifstatement setup.
        ifstatement.id("if").id("(").and(expr).id(")").or(block, statement)
                .op(pass().id("else").or(block, statement));

        // block setup.
        block.id("{").repeat(
                statement
        ).id("}");


        String code = IOUtils.toString(new FileInputStream("/Users/dreamtowards/Projects/Outskirts/src/outskirts/lang/vm/rt/main.g"));
        Lexer lexer = new Lexer(code);


        RuntimeEnvironment env = new RuntimeEnvironment();

        env.declare("print", SyntaxMethodDeclarate.ofDirect("print", farg -> {
            System.out.println(farg[0]);
            return null;
        }, "str"));

        Syntax s = block.read(lexer);
        Log.LOGGER.info(s);
        Log.LOGGER.info(s.eval(env));

    }

}
