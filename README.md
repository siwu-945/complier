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

## Incomplete?

- phi functions
    - I really tried to implement phi functions, but I couldn't get it to work. I tried to implement it in the
      TransformIR.java file, within the method:
        - public void transformPhiFunctions()
- Sorry if the code is really messy, I only gained the understanding of what I am doing when I wrote a lot, and didn't
  plan a good code readability from the start.

