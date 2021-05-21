package outskirts.lang.g1;

import outskirts.lang.g1.interpreter.ObjectInstance;
import outskirts.lang.g1.interpreter.RuntimeEnvironment;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.g1.parser.expr.RuleExpr;
import outskirts.lang.g1.parser.RuleLs;
import outskirts.lang.g1.syntax.*;
import outskirts.util.IOUtils;
import outskirts.util.logging.Log;

import java.io.FileInputStream;

import static outskirts.lang.g1.parser.RuleLs.pass;
import static outskirts.lang.g1.parser.RuleLs.struct;

public class SyntaxConstructor {

    private static final RuleLs block = struct(SyntaxBlock::new).initname("Block");

    private static final RuleLs expr = pass().initname("BiExpression");

    private static final RuleLs statementif = struct(SyntaxStatementIf::new).initname("IfStatement");
    private static final RuleLs statementwhile = struct(SyntaxStatementWhile::new).initname("WhileStatement");
    private static final RuleLs statement = pass().initname("StatementElect");

    private static final RuleLs variable_declaration = struct(SyntaxVariableDeclarate::new).initname("VariableDeclaration");
    private static final RuleLs variable_reference = pass().initname("VariableReference");

    private static final RuleLs constant_number = pass().initname("ConstantNumber");
    private static final RuleLs constant_string = pass().initname("ConstantString");

    private static final RuleLs function_call = struct(SyntaxMethodCall::new).initname("FunctionCall");
    private static final RuleLs function_declaration = struct(SyntaxMethodDeclarate::new).initname("FunctionDeclaration");

    private static final RuleLs class_declaration = struct(SyntaxClassDeclarate::new).initname("ClassDeclaration");

    public static void main(String[] args) throws Exception {

        function_call.and(variable_reference).id("(").markLookaheadUntil()
                .and(struct(Syntax::new).op(pass().and(expr).repeat(pass().id(",").and(expr))))
                .id(")");
        variable_reference.name(SyntaxVariableReference::new);
        constant_number.number(SyntaxConstantNumber::new);
        constant_string.string(SyntaxConstantString::new);
        {
            RuleLs primary = pass().or(
                    function_call,
                    variable_reference,
                    constant_number,
                    constant_string
            ).initname("PrimaryElect");
            RuleLs factor = pass().or(
                    primary,
                    pass().id("(").and(expr).id(")"),
                    struct(SyntaxNegativeExpression::new).id("-").and(expr)
            ).initname("FactorElect");

            expr.and(new RuleExpr(SyntaxBiExpression::new, factor));
        }



        statement.or(
                block,  // statement could contains blocks.
                statementif,
                statementwhile,
                function_declaration,
                class_declaration,
                variable_declaration,
                pass().and(expr).id(";")
        );

        statementif.id("if").id("(").and(expr).id(")").and(statement).op(pass().id("else").and(statement));
        statementwhile.id("while").id("(").and(expr).id(")").and(statement);

        variable_declaration.name().name().markLookaheadUntil().ornull(pass().id("=").and(expr)).id(";");

        function_declaration.id("def").name().id("(")
                .and(struct(Syntax::new).op(pass().and(variable_reference).repeat(pass().id(",").and(variable_reference))))
                .id(")").and(block);

        block.id("{").repeat(statement).id("}");

        class_declaration.id("class").name().and(block);


//        Lexer lexer = new Lexer(IOUtils.toString(new FileInputStream("/Users/dreamtowards/Projects/Outskirts/src/outskirts/lang/vm/rt/main.g")));
//        lexer = new Lexer("5 + 3");

//        Syntax s = expr.readone(lexer);
//
//        RuntimeEnvironment env = new RuntimeEnvironment();
//        env.declare("print", new ObjectInstance("function", SyntaxMethodDeclarate.ofDirect("print", farg -> {
//            System.out.println(farg[0]);
//            return null;
//        }, "str")));
//
//        Log.LOGGER.info(s);
//        s.eval(env);


//        CodeBuf codebuf = new CodeBuf();
//        s.compile(codebuf);
//
//        Executor exec = new Executor();
//        exec.exec(codebuf.arr());
    }

}
