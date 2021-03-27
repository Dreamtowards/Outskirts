package outskirts.lang;

import outskirts.lang.lexer.Lexer;
import outskirts.lang.lexer.parser.rule.Rule;
import outskirts.lang.lexer.parser.rule.RuleExpr;
import outskirts.lang.lexer.parser.rule.RuleLs;
import outskirts.lang.syntax.*;
import outskirts.util.IOUtils;
import outskirts.util.logging.Log;

import java.io.FileInputStream;

import static outskirts.lang.lexer.parser.rule.RuleLs.pass;
import static outskirts.lang.lexer.parser.rule.RuleLs.struct;

public class SyntaxConstructor {

    private static final RuleLs block = struct(SyntaxBlock::new);

    private static final RuleLs expr = pass();

    private static final RuleLs statementif = struct(SyntaxStatementIf::new);
    private static final RuleLs statementwhile = struct(SyntaxStatementWhile::new);
    private static final RuleLs statement = pass();

    private static final RuleLs variable_declaration = struct(SyntaxVariableDeclarate::new);
    private static final RuleLs variable_reference = pass();

    private static final RuleLs constant_number = pass();
    private static final RuleLs constant_string = pass();

    private static final RuleLs function_call = struct(SyntaxMethodCall::new).lookahead(2);

    public static void main(String[] args) throws Exception {

        variable_reference.name(SyntaxVariableReference::new);
        constant_number.number(SyntaxConstantNumber::new);
        constant_string.string(SyntaxConstantString::new);
        {
            RuleLs primary = pass().or(
                    function_call,
                    variable_reference,
                    constant_number,
                    constant_string
            );
            RuleLs factor = pass().or(
                    primary,
                    pass().id("(").and(expr).id(")"),
                    struct(SyntaxNegativeExpression::new).id("-").and(expr)
            );

            expr.and(new RuleExpr(SyntaxBiExpression::new, factor));
        }



        statement.or(
                block,  // statement could contains blocks.
                statementif,
                statementwhile,
                variable_declaration
        );

        statementif.id("if").id("(").and(expr).id(")").and(statement).op(pass().id("else").and(statement));
        statementwhile.id("while").id("(").and(expr).id(")").and(statement);

        variable_declaration.name().name().op(pass().id("=").and(expr)).id(";");

        function_call.name().id("(")
                .op(pass().and(expr).repeat(pass().id(",").and(expr)))
                .id(")");

        block.id("{").repeat(statement).id("}");


        Lexer lexer = new Lexer(IOUtils.toString(new FileInputStream("/Users/dreamtowards/Projects/Outskirts/src/outskirts/lang/vm/rt/main.g")));

        Syntax s = function_call.readone(lexer);

        Log.LOGGER.info(s);
    }

}
