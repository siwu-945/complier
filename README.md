# CS441 Milestone 1

## Usage

- You can run main.java, make sure you are running from the root directory of the project. The default soucre code is
  called "example1.txt"
- You can also run the following command(when you have the main.class file):
    - java -cp out/production/milestone1 main <sourcecode.txt>

## Peephole Optimization Choice

- I choose to remove any tag checks on accesses to "%this." No need to tag checks when accessing fields or methods of
  %this in the IR
- you can find the optimization in TransformIR.java, within the method:
    - public void tagCheck(BasicBlock currentBlock, String className)
    - I checked if the access is to %this, and if it is, I just return without doing anything.

## What I still need to do

- pointer arithmetic?
- generate basic block for classes
    - I do have basic block for classes in a HashMap, with its name being the key, but it has incomplete IRStatements
    - I need to figure a way to check if I need to do tag check within my class' basic block.
    - I also need to add a way to generate new basic block(methods, branch...) in the class' basic block
- proper tag check
- generate basic blocks for methods within classes
- ...

