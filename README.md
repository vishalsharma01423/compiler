# Compiler Project

This project is a simple compiler built using *Flex (Lex)* and *Bison (Yacc)*. It performs lexical and syntax analysis on input source code and demonstrates the basic functionality of a compiler front-end.

## Project Structure

- lexer.l – Lex specification file for lexical analysis.
- parser.y – Bison specification file for syntax parsing.
- parser.tab.c / parser.tab.h – Generated files by Bison.
- lex.yy.c – Generated file by Flex.
- test.c – Sample C file used for testing parsing and token generation.

## How to Build

bash
bison -d parser.y         # Generates parser.tab.c and parser.tab.h
flex lexer.l              # Generates lex.yy.c
gcc lex.yy.c parser.tab.c -o parser -lfl  # Compile everything

How to Run

./parser < test.c

Requirements

GCC compiler

Flex

Bison


Author

Vishal Sharma
