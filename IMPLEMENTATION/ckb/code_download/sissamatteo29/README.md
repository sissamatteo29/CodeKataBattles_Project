Erlang is a concurrent, functional programming language designed for building scalable and fault-tolerant systems. 

Developed by Ericsson in the late 1980s, it has gained popularity in telecommunications, distributed systems, and real-time applications. Erlang's key strengths lie in its lightweight processes, message-passing concurrency model, and pattern-matching syntax. The language excels at handling concurrent tasks, making it well-suited for developing highly responsive and fault-tolerant applications. With features like hot code swapping and distribution support, Erlang empowers developers to create robust and maintainable systems, particularly in industries where reliability and uptime are critical. 

Whether used for building telecommunications infrastructure or distributed, fault-tolerant servers, Erlang's design philosophy prioritizes reliability, scalability, and ease of concurrent programming.

**INSTALLING THE ERLANG SYSTEM AND RUNNING ERLANG CODE**

An Erlang installation can be found on the official web site https://www.erlang.org/downloads. 
Once the installation is complete, it is necessary to add the bin folder of the installed environment on the PATH variable in the local machine, 
in order to be able to start the Erlang interactive shell from the terminal. If installing the Erlang packages on Windows, the default path for the bin directory is C:\Program Files\Erlang OTP\bin.

Once this is done, open a terminal window and run the command erl. If everything has gone the right way, the interactive Erlang shell should start.
From this shell, Erlang source code files can be compiled and their functions called interactively to verify their behavior. 
In order to compile and run Erlang code, there are some things to keep in mind:
- Erlang source code has to be written in files with .erl extension.
- Each .erl file contains an Erlang module, whose name is specified at the beginning of the file with the notation -module("module_name").
- It is important to give names to Erlang modules that start with a lowercase letter, since capital letters names are interpreted by Erlang as variables.
- It is at the same time essential to name the file containing the module in the same way as the module (plus the .erl extention).
- After the module declaration, it is fundamental to define which functions are exported, so the functions that can be called from outside the module. 
  The syntax to do that is 

  -export(["function"/"arity", "function"/"arity", ...])

- After this "heading" of the document, the Erlang module can be designed and written down. Once the module is complete, it is possible to compile the module from the terminal using the erl interactive environment:
	- Open the terminal and navigate to the directory where the Erlang source code is located.
	- Run erl to start the interactive Erlang environment.
	- Compile the source code with the command c("file_name").
	- Now the code is compiled and it is possible to call the functions that are defined and exported in the Erlang module from the terminal with the command "module_name":"function"("params").

- In case modifications are applied on the source code file, it is necessary to recompile the file every time before being able to see the modifications inside the Erlang interactive environment.