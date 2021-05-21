

bi-oper: %: REM ? MOD

function: Return-value and Paramaters. the Arbitration

parser:

List<AST> read(Lexer lex);
// simpler, but invisiable for 'inner-layers-parsers'

void read(Lexer lex, List<AST> out);
// visiable for inner-parsers

