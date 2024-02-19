# CS441 Milestone 1 (Unfinished)

## Usage

- I didn't make it to be able to run with a command line argument, I just putted everything in the main.java. The
  example program is in a String called "wholeSource". With 2 classes, and some arithmetic operations in main. But main
  doesn't do anything with the classes because I haven't got to there yet.
- Run main.java

## What I have done so far

- Parsing source code into an AST
    - ASTExpression
    - ASTStatement
- Transforming ASTStatement into IR
- Adding IR to different basic blocks
- I have generated vtble and fields
    - global ID for vtble and fields
    - vtble array for each class
    - fields array for each class
- My program can do basic arithmetic translation as shown when you run main.java
    - a basic block called "main", transforming the arithmetic operations into SSA form

## What I still need to do

- pointer arithmetic?
- generate basic block for classes
    - I do have basic block for classes in a HashMap, with its name being the key, but it has incomplete IRStatements
    - I need to figure a way to check if I need to do tag check within my class' basic block.
    - I also need to add a way to generate new basic block(methods, branch...) in the class' basic block
- proper tag check
- generate basic blocks for methods within classes
- ...

