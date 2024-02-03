% Consider an infinite list of binary trees, where: 
% 1. all the leaves contain a specific value x, 
% 2. each tree is complete, 
% 3. the first tree is a single leaf, and each tree has one level more than its previous one in the list. 

% Then, consider a second infinite list of binary trees, which is like the previous one, but the first leaf contains the integer 1, and each subsequent tree contains leaves that have the value of the previous one incremented by one. 
% E.g. [Leaf 1,  (Branch (Leaf 2)(Leaf 2), ...]

% We want to create Erlang processes which return the current element of these "virtual infinite list" with the message next, and terminate with the message stop. 
% 1. Define a function btrees to create a process corresponding to the first infinite list. 
% 2. Define a function incbtrees to create a process corresponding to the second infinite list. 
% Notes: for security reasons, processes must only answer to their creating process; 
% to define trees, you can use suitable tuples with atoms as customary in Erlang (e.g. {branch, {leaf, 1}, {leaf, 1}}).

-module(infinite_lists).
-export([
        start_btrees/2,
        start_inc_trees/1,
        inc_trees/1
        ]).

% Helper function to add one level to the Tree parameter.
add_level(Tree) -> 
    {branch, Tree, Tree}.

%try_add_level() -> io:format("~p", [add_level({leaf, 1})]).

% A simple fold function that operates on trees.
fold_tree(Fun, {leaf, X}) -> 
    {leaf, Fun(X)};
fold_tree(Fun, {branch, X, Y}) ->
    Acc1 = fold_tree(Fun, X),
    Acc2 = fold_tree(Fun, Y),
    {branch, Acc1, Acc2}.

% Helper function to add one to all the leaf nodes of a tree.
add_one_trees(Tree) ->
    fold_tree(fun (X) -> X + 1 end, Tree).

%try_fold_tree(Fun, Tree) ->
%    Tree2 = fold_tree(Fun, Tree),
%    io:format("~p~n",[Tree2]).


% First infinite list:
% N is the number of trees from the list that the user wants to print.
% Init is the value to be replicated in all the lists.
start_btrees(N, Init) -> 
    PidSon = spawn(?MODULE, btree, [self(), Init]),
    print_btrees(PidSon, N).

print_btrees(PidSon, N) ->
    case N > 0 of
        true -> 
            PidSon ! {self(), next},
            receive
                Tree -> io:format("~p~n", [Tree])
            end,
            print_btrees(PidSon, N - 1);
        false -> PidSon ! {self(), stop}, true
    end.

btree(PidParent, Init) -> 
    btree_helper(PidParent, {leaf, Init}).

btree_helper(PidParent, Tree) ->
    receive
        {PidParent, next} -> 
            Tree2 = add_level(Tree),
            PidParent ! Tree2,
            btree_helper(PidParent, Tree2);
        {PidParent, stop} ->
            true,
            io:format("Process with Pid ~w dying ~n", [self()])
    end.


% Second infinite list:
% N is the number of trees the user wants to print out.
start_inc_trees(N) ->
    PidSon = spawn(?MODULE, inc_trees, [self()]),
    print_trees(PidSon, N).

print_trees(PidSon, N) ->
    case N > 0 of 
        true -> 
            PidSon ! {self(), next},
            receive
                Tree -> io:format("~p~n", [Tree])
            end,
            print_trees(PidSon, N-1);
        false ->
            PidSon ! {self(), stop}
    end.

inc_trees(PidParent) ->
    io:format("Son initiated~n", []),
    receive
        {PidParent, next} -> 
            PidParent ! {leaf, 0},
            inc_trees_helper(PidParent, {leaf, 0});
        {PidParent, stop} ->
            io:format("Son dying~n", [])
    end.

inc_trees_helper(PidParent, Tree) ->
    receive
        {PidParent, next} -> 
            AddTree = add_one_trees(Tree),
            AddTreeLevel = add_level(AddTree),
            PidParent ! AddTreeLevel,
            inc_trees_helper(PidParent, AddTreeLevel);
        {PidParent, stop} ->
            io:format("Son dying~n", [])
    end.



