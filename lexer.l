%{
#define _POSIX_C_SOURCE 200809L
#include "parser.tab.h"
#include <string.h>
#include <stdlib.h>

int lineno = 1;  // Track current line number
%}

%%

"int"       { return INT; }
"float"     { return FLOAT; }
"string"    { return STRING; }
"return"    { return RETURN; }

\"[^\"]*\"   { yylval.str = strdup(yytext); return STR_LITERAL; }

[a-zA-Z_][a-zA-Z0-9_]*   { yylval.str = strdup(yytext); return ID; }

","         { return COMMA; }
";"         { return SEMICOLON; }
"="         { return ASSIGN; }
"+"         { return PLUS; }

"("         { return '('; }
")"         { return ')'; }
"{"         { return '{'; }
"}"         { return '}'; }

[ \t]+      ;  // ignore spaces and tabs

\n          { lineno++; }  // increment line number on newline

.           { return yytext[0]; }

%%

int yywrap() { return 1; }
