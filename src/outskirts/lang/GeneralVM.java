package outskirts.lang;

import outskirts.lang.interpreter.RuntimeEnvironment;
import outskirts.lang.lexer.Lexer;
import outskirts.lang.lexer.parser.RuleLs;
import outskirts.lang.syntax.*;
import outskirts.util.IOUtils;
import outskirts.util.logging.Log;

import java.io.FileInputStream;

import static outskirts.lang.lexer.parser.RuleLs.*;

public class GeneralVM {



    public static void main(String[] args) throws Throwable {

        RuleLs expr = pass();

        RuleLs funccall = struc(SyntaxMethodCall::new);

        RuleLs varref = pass().name(SyntaxVariableReference::new);

        RuleLs primary = pass().or(
                funccall,
                pass().number(SyntaxConstantNumber::new),
                varref,
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

        RuleLs vardef = struc(SyntaxVariableDeclarate::new).id("var").name().ornull(pass().id("=").and(expr)).id(";");



        RuleLs clazz = struc(SyntaxClassDeclarate::new).id("class").name().and(block);

        RuleLs statement = pass().or(
                ifstatement,
                struc(SyntaxStatementWhile::new).id("while").id("(").and(expr).id(")").and(block),
                struc(SyntaxMethodReturn::new).id("return").ornull(expr).id(";"),
                clazz,
                vardef,
                funcdef,
                pass().and(expr).id(";"),
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

//        Log.LOGGER.info(expr.read(new Lexer("abc . abc . abc")));
        Syntax s = block.read(lexer);
//        Log.LOGGER.info(s);
        s.eval(env);

    }

}
