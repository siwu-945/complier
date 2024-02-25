# CS441 Milestone 3

## Usage

- You can run main.java, make sure you are running from the root directory of the project. The default soucre code is
  called "example1.txt." You can also try example2.txt
- You can also run the following command(when you have the main.class file):
    - java -cp out/production/milestone1 main <sourcecode.txt>
- Since with incomplete phi functions and while, the program may not work as expected with some source code.

## Peephole Optimization Choice

- I choose to remove any tag checks on accesses to "%this." No need to tag checks when accessing fields or methods of
  %this in the IR
- you can find the optimization in TransformIR.java, within the method:
    - public void tagCheck(BasicBlock currentBlock, String className)
    - I checked if the access is to %this, and if it is, I just return without doing anything.

## Incomplete?

- phi functions
    - I really tried to implement phi functions, but I couldn't get it to work. I tried to implement when the statement
      is a "while" statement, but I couldn't figure a way to use phi function with it.
- Sorry if the code is really messy, I only gained the understanding of what I am doing when I wrote a lot, and didn't
  plan a good code readability from the start.

