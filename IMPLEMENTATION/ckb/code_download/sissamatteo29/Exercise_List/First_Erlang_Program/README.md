This is a first Erlang program that can be used to verify if the installation procedure for the Erlang environment has been correctly carried out.
There are some things to notice:
- Each .erl file contains an Erlang module, whose name is specified at the beginning of the file with the notation -module("module_name").
- It is important to give names to Erlang modules that start with a lowercase letter, since capital letters names are interpreted by Erlang as variables.
- It is at the same time essential to name the file containing the module in the same way as the module (plus the .erl extention).
- After the module declaration, it is fundamental to define which functions are exported, so the functions that can be called from outside the module. 
  The syntax to do that is 

  -export(["function"/"arity", "function"/"arity", ...])

- Then the Erlang module can be designed and written down. Once the module is complete, it is possible to compile the module from the terminal using the erl environment:
	- Open the terminal and navigate to the directory where the Erlang source code is located.
	- Run erl to start the interactive Erlang environment.
	- Compile the source code with the command c("file_name").
	- Now the code is compiled and it is possible to call the functions that are defined and exported in the Erlang module from the terminal with the command "module_name":"function"("params").

- In case modifications are applied on the source code file, it is necessary to recompile the file every time before being able to see the modifications inside the Erlang interactive environment.