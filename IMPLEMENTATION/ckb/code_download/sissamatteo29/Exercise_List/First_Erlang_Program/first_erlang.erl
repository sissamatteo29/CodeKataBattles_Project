-module(first_erlang).
-export([double/1, double2/1]).

double(X) when not (X > 0); X rem 2 =:= 0 ->
    2 * X.

double2(X) ->
    2 * X.
