# CS441 Milestone 3

## Usage

- You can run main.java, make sure you are running from the root directory of the project. The default soucre code is
  called "example1.txt." You can also try example2.txt
- You can also run the following command(when you have the main.class file):
    - java -cp out/production/milestone1 main <sourcecode.txt>
- Since with incomplete phi functions and while, the program may not work as expected with some source code.

## Type-based optimization:

- Field Access:
    - Removed the field map
        - -Correspondingly, I removed this IR so I don't have to jump to 2nd slot for field map: %1 = %x0 + 8
        - I can directly load the object and access the field
    - No longer need to go to field address and store @fieldsMap
        - removed the IR:
            - %1 = %x0 + 8
            - store(%1, @fieldsA)

- Method calls
    - No longer need to check if the method lookup succeeds


