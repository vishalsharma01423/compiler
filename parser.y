%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int yylex();
void yyerror(const char *msg);

extern int lineno;   // from lexer

int current_function_type = 0; // 1: int, 2: float, 3: string
char *current_function_name = NULL;  // track current function name
%}

%union {
    char* str;
}

%token <str> ID STR_LITERAL
%token INT FLOAT STRING RETURN COMMA SEMICOLON ASSIGN PLUS

%type <str> type expression return_value

%%

program:
    function_list
    ;

function_list:
    function
    | function_list function
    ;

function:
    type ID '(' ')' '{' {
        if (current_function_name) free(current_function_name);
        current_function_name = strdup($2);
        current_function_type = 0; // will be set by 'type' rule below
    }
    declarations statements RETURN return_value SEMICOLON '}' {
        if (current_function_type == 1 && $9[0] == '"') {
            printf("Error at line %d: Function '%s' should not return a string (should return int)\n", lineno, current_function_name);
        }
        free(current_function_name);
        current_function_name = NULL;
        current_function_type = 0;
    }
    ;

type:
    INT     { current_function_type = 1; $$ = "int"; }
    | FLOAT { current_function_type = 2; $$ = "float"; }
    | STRING { current_function_type = 3; $$ = "string"; }
    ;

declarations:
    | declarations declaration
    ;

declaration:
    type id_list SEMICOLON
    ;

id_list:
    ID
    | id_list ID {
        printf("Warning at line %d: Missing comma in variable declaration near '%s'\n", lineno, $2);
    }
    | id_list COMMA ID
    ;

statements:
    | statements statement
    ;

statement:
    ID ASSIGN expression SEMICOLON
    ;

expression:
    ID
    | expression PLUS ID
    ;

return_value:
    ID
    | STR_LITERAL
    ;

%%

void yyerror(const char *msg) {
    fprintf(stderr, "Syntax error at line %d: %s\n", lineno, msg);
}
