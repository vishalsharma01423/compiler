%{
#include <stdio.h>

extern int yylex();
extern int line_no;

void yyerror(const char *s);
%}

%token IF ELSE FOR WHILE RETURN TYPE
%token IDENTIFIER NUMBER STRING
%token LPAREN RPAREN LBRACE RBRACE SEMICOLON COMMA ASSIGN
%token EQ NEQ LE GE LT GT OP

%%

program:
    program statement
    | statement
    ;

statement:
      declaration SEMICOLON
    | assignment SEMICOLON
    | return_stmt SEMICOLON
    | if_stmt
    | loop_stmt
    | block
    | error SEMICOLON { printf("Line %d: Invalid or incomplete statement\n", line_no); yyerrok; }
    ;

block:
    LBRACE program RBRACE
    ;

declaration:
      TYPE IDENTIFIER
    | TYPE IDENTIFIER ASSIGN expression
    ;

assignment:
    IDENTIFIER ASSIGN expression
    ;

return_stmt:
    RETURN expression
    ;

expression:
      NUMBER
    | IDENTIFIER
    | STRING
    | expression OP expression
    | LPAREN expression RPAREN
    ;

if_stmt:
    IF LPAREN expression RPAREN statement
    | IF LPAREN expression RPAREN statement ELSE statement
    ;

loop_stmt:
    WHILE LPAREN expression RPAREN statement
    | FOR LPAREN expression SEMICOLON expression SEMICOLON expression RPAREN statement
    ;

%%

void yyerror(const char *s) {
    printf("Line %d: Syntax error: %s\n", line_no, s);
}

int main() {
    yyparse();
    return 0;
}
