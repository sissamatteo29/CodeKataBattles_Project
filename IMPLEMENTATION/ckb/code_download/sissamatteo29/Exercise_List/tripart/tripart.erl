% Define a program tripart which takes a list, two values x and y, with x < y, and three functions, taking one argument which must be a list. 

% • tripart first partitions the list in three sublists, one containing values that are less than both x and y, 
% one containing values v such that x ≤ v ≤ y, and one containing values that are greater than both x and y. 
% 
% • Three processes are then spawned in parallel, running the three given functions and passing the three sublists in order 
% (i.e. the first function must work on the first sublist and so on). 
% 
% • Lastly, the program must wait the termination of the three processes in the spawning order, assuming that each one will return the pair {P, V}, where P is its PID and V the resulting value. 
% 
% • tripart must return the three resulting values in a list, with the resulting values in the same order as the corresponding sublists.


-module(tripart).
-export([tripart/6,
        process1/1,
        process2/1,
        process3/1]).

maptuple ({A, B, C}, F) ->
    {F(A), F(B), F(C)}.

partition ([], _, _, List1, List2, List3) ->
    maptuple({List1, List2, List3}, fun lists:reverse/1);
partition ([E|T], X, Y, List1, List2, List3) ->
    if
       E > X, E > Y -> partition(T, X, Y, [E|List1], List2, List3);
       E >= X, E =< Y -> partition(T, X, Y, List1, [E|List2], List3);
       E < X, E < Y -> partition(T, X, Y, List1, List2, [E|List3])
    end.



tripart (L, X, Y, F1, F2, F3) when X < Y, X > 0->
    {A, B, C} = partition(L, X, Y, [], [], []),
    Pid1 = spawn(?MODULE, F1, [A]),
    Pid2 = spawn(?MODULE, F2, [B]),
    Pid3 = spawn(?MODULE, F3, [C]),

    %Child processes don't know the parent's Pid, it is necessary to send it over.
    Pid1 ! self(),
    Pid2 ! self(),
    Pid3 ! self(),

    receive
        {Pid1, Value1} -> 
            receive
                {Pid2, Value2} ->
                    receive 
                        {Pid3, Value3} -> [Value1, Value2, Value3]
                    end
            end

    end.
    


doublelist ([]) ->
    [];
doublelist ([E|T]) ->
    [2*E | doublelist(T)].

% The first function doubles all the elements of a list
process1 ([]) ->
    receive 
        PidParent -> PidParent ! {self(), "Error, empty list"}
    end;
process1 (L) ->
    receive 
        PidParent -> PidParent ! {self(), doublelist(L)}
    end.



oddelements ([]) ->
    [];
oddelements ([E|T]) ->
    case E rem 2 =:= 0 of
        true -> oddelements(T);
        false -> [E | oddelements(T)]
    end.

% This process returns a list filtered by eliminating the even elements
process2 ([]) ->
    receive
        PidParent -> PidParent ! {self(), "Error, empty list"}
    end;
process2 (L) ->
    receive
        PidParent -> PidParent ! {self(), oddelements(L)}
    end.

product ([], Res) ->
    Res;
product ([E|T], [Acc]) ->
    Temp = E * Acc,
    product(T, [Temp]).

% This process returns the product of all the elements of the list wrapped into a list
process3 ([]) ->
    receive
        PidParent -> PidParent ! {self(), "Error, empty list"}
    end;
process3 (L) ->
    receive 
        PidParent -> PidParent ! {self(), product(L, [1])}
    end.
