# Scheme Language Interpreter

A comprehensive implementation of a Scheme language interpreter, following the R5RS standard, built in Java.

## Project Overview

This project demonstrates a complete implementation of an interpreter for the Scheme programming language. Scheme is a minimalist dialect of the Lisp family of programming languages, known for its elegance and simplicity in design.

### Key Features

- Complete lexical and syntax analysis
- Support for core Scheme data types and operations
- Implementation of lexical scoping with proper closures
- Support for special forms (`define`, `lambda`, `if`, `cond`, etc.)
- Object-oriented design for AST representation
- Multi-threaded test framework

## Technical Implementation

The interpreter follows the classic compiler/interpreter design pattern with these key components:

1. **Lexer** - Transforms source code into tokens
2. **Parser** - Builds abstract syntax tree (AST) from tokens
3. **AST** - Represents program structure with polymorphic node classes
4. **Interpreter** - Evaluates AST nodes in appropriate environments
5. **Value System** - Type representation and operations for Scheme values

### Project Structure

```
src/com/company/
├── Interpreter.java     - Main interpreter implementation
├── Tester.java          - Multi-threaded test framework
├── ast/                 - Abstract Syntax Tree nodes
├── lexer/               - Lexical analysis components
├── parser/              - Syntax analysis components
├── value/               - Scheme value representation
├── interpret/           - Runtime environment and evaluation
├── util/                - Utility functions
└── pre/                 - Preprocessing components
```

## Supported Scheme Features

- Primitive data types (numbers, strings, booleans, symbols)
- Compound data structures (lists, vectors, pairs)
- Lexical variable scoping (`let`, `let*`, `letrec`)
- First-class functions with proper closures
- Special forms (`if`, `cond`, `case`, `begin`, `quote`, etc.)
- Tail-call optimization
- Quasiquotation and macros

## Code Highlights

- Object-oriented representation of AST nodes
- Environment-based lexical scoping implementation
- Proper tail-call handling for recursion
- Comprehensive test suite with multi-threaded execution


## Example Programs

The project includes several example Scheme programs that demonstrate the interpreter's capabilities, including:

- Mathematical operations and recursive functions
- List manipulation and higher-order functions
- Sorting algorithms (bubble sort, selection sort)
- Macro definition and expansion

Here are some basic examples of Scheme code that can be run with this interpreter:

```scheme
; Simple arithmetic
(+ 1 2 3 4 5)  ; => 15

; Function definition and recursion
(define factorial
  (lambda (n)
    (if (eq? n 1)
        1
        (* n (factorial (- n 1))))))
(factorial 5)  ; => 120

; List manipulation
(cons 5 (cons 5 3))  ; => (5 5 3)
(list (list 1 (list 2) 3) (list 4 5 6))  ; => ((1 (2) 3) (4 5 6))

; Macros
(define-syntax test (syntax-rules () ((test a b) (+ a b))))
(test 7 8)  ; => 15
```

