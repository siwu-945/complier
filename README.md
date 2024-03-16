# CS441 Milestone 2

## Usage

- You can run main.java, make sure you are running from the root directory of the project. The default soucre code is
  called "example1.txt." You can also try example2.txt
- You can also run the following command(when you have the main.class file):
    - java -cp out/production/milestone1 main <sourcecode.txt>

## Optimized SSA Transformation

- Whenever I detect a variable assignment, I add the variable assigned to a HashMap in that specific basic blocks
    - In my basic block, a HashMap has the variable (from the source code) stored as key, and the tmp variables are
      stored in a list (in the value of that hashmap)
        - So I can know how many times the variable has been used
    - I also have a hashmap that keep tracks which tmp variable has been used in which block
        - So in my phi function, I can know which tmp variable is used in which block
- I also keep track of the predecessor blocks of the current block

- In example1.txt
    - In class A, it has a method that has 2 variables.
        - In the if and else block, only 'x' has been given values in different predecessor blocks.
        - Thus, it has inserted one phi insertion, keeping track which block it has been used
        - Ignoring other variable, 'y', which has been assigned only in one block.
    - In class B, it has a method that has 2 variables. Each variable has been assigned in different blocks.
        - Thus, it has inserted two phi insertion, keeping track which block it has been used
  

