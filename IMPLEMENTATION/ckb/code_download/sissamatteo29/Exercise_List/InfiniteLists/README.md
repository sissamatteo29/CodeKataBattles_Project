Consider an infinite list of binary trees, where: 
1. all the leaves contain a specific value x, 
2. each tree is complete, 
3. the first tree is a single leaf, and each tree has one level more than its previous one in the list. 

Then, consider a second infinite list of binary trees, which is like the previous one, but the first leaf contains the integer 1, and each subsequent tree contains leaves that have the value of the previous one incremented by one. 
E.g. [Leaf 1,  (Branch (Leaf 2)(Leaf 2), ...]

We want to create Erlang processes which return the current element of these "virtual infinite list" with the message next, and terminate with the message stop. 
1. Define a function btrees to create a process corresponding to the first infinite list. 
2. Define a function incbtrees to create a process corresponding to the second infinite list. 
Notes: for security reasons, processes must only answer to their creating process; 
to define trees, you can use suitable tuples with atoms as customary in Erlang (e.g. {branch, {leaf, 1}, {leaf, 1}}).