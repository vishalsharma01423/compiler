%{
#include "parser.tab.h"
#include <stdio.h>

int line_no = 1;
%}

%%

"if"        { return IF; }
"else"      { return ELSE; }
"for"       { return FOR; }
"while"     { return WHILE; }
"return"    { return RETURN; }
"int"|"float"|"char"|"void" { return TYPE; }

[0-9]+                 { return NUMBER; }
[a-zA-Z_][a-zA-Z0-9_]* { return IDENTIFIER; }
\"(\\.|[^"])*\"        { return STRING; }

"=="        { return EQ; }
"!="        { return NEQ; }
"<="        { return LE; }
">="        { return GE; }
"<"         { return LT; }
">"         { return GT; }
"="         { return ASSIGN; }
"+"|"-"|"*"|"/" { return OP; }

";"         { return SEMICOLON; }
","         { return COMMA; }
"("         { return LPAREN; }
")"         { return RPAREN; }
"{"         { return LBRACE; }
"}"         { return RBRACE; }

"//".*                        { /* single-line comment */ }
"/*"([^*]|\*+[^*/])*\*+"/"    { /* multi-line comment */ }
"#".*                         { /* preprocessor */ }

[ \t\r]+                     { /* skip whitespace */ }
\n                           { line_no++; }
.                            { return yytext[0]; }

%%

int yywrap() { return 1; }
