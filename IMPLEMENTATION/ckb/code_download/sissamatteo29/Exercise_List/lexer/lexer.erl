% Define a parallel lexer, which takes as input a string x and a chunk size n, and translates all the words in the strings to atoms, 
% sending to each worker a chunk of x of size n (the last chunk could be shorter than n). 
% You can assume that the words in the string are separated only by space characters (they can be more than one - the ASCII code for ' ' is 32); 
% it is ok also to split words, if they overlap on different chunks. 
% 
% E.g.    plex("this is a nice  test", 6)  returns  [[this,i],[s,a,ni],[ce,te],[st]] 
% 
% For you convenience, you can use the library functions: 
% • lists:sublist(List, Position, Size) which returns the sublist of List of size Size from position Position (starting at 1); 
% • list_to_atom(Word) which translates the string Word into an atom.

-module(lexer).
-export([lexer/2,
        process_chunk/2,
        get_word/2,
        process/1]).


list_map([], _) ->
    [];
list_map([E|T], Fun) ->
    [Fun(E) | list_map(T, Fun)].

get_word([], Res) ->
    {[], Res};
get_word([E|T], Res) ->
    if
        E =:= 32 -> {T, Res};
        true -> get_word(T, lists:append(Res, [E]))
    end.

process_chunk ([], Res) ->
    list_map(Res, fun list_to_atom/1);
process_chunk ([E|T], Res) ->
    if 
        E =:= 32 -> process_chunk (T, Res);
        true -> {NewList, Word} = get_word([E|T], []),
                NewRes = lists:append(Res, [Word]),
                process_chunk(NewList, NewRes)
    end.

process (L) ->
    receive
        PidParent -> Res = process_chunk(L, [])
    end,
    PidParent ! {self(), Res}.


spawn_children ([], _, Children) ->
    Children;
spawn_children ([E|T], N, Children) ->
    ExtractN = lists:sublist([E|T], 1, N),
    PidC = spawn(?MODULE, process, [ExtractN]),
    PidC ! self(),
    spawn_children(lists:sublist([E|T], N + 1, length([E|T])), N, lists:append(Children, [PidC])).

collect_results([], Res) ->
    Res;
collect_results([E|T], Res) ->
    receive
        {E, PL} -> Res1 = Res ++ [PL],
                    collect_results(T, Res1)
    end.

lexer (S, N) when N > 0, is_list(S) ->
    Children = spawn_children(S, N, []),
    Res = collect_results(Children, []),
    Res.    
