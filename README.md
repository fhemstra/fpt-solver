# Using the software
A ready-to-use `.jar` file can be found in the root directory of this repository.
To use it, enter the following into your CLI:
```
java -jar ba-solver-1.0.jar
```
From there on, the help dialog of the tool will list all flags and optional values which can be set.

## Common use-cases
Testing a set of formulas on their internal logical structures using the naive searchtree approach:
```
java -jar ba-solver-1.0.jar -f path/to/formula -int -st
```
Testing a set of fourmulas on graphs you want to supply externally using the naive searchtree approach:
```
java -jar ba-solver-1.0.jar -f path/to/formula -g path/to/graphs -st
```
Either of the above can be decorated with multiple flags. To use the supposed-to-be optimal setup with a timeout of 5 seconds, use:
```
java -jar ba-solver-1.0.jar -f path/to/formula -g path/to/graphs -gu -ke -be -bo -heu -t 5
```
## Given instances
Multiple sets of graphs can be found in `workspace_BA/input_graphs`.

Some formulas are located in `workspace_BA/instances`.


# Input formats
## Formula files
Formula files can be used to pass formulas to the software and optionally a logical structure. To pass a set of formulas, use the `-f` flag. To also use internal logical structures, set the `-int` flag. The fileformat of formulas is `.form`.

```
<name of the instance>
<relation 1 identifier><arity 1> = {(<element>|<element>|...),...}
<relation 2 identifier><arity 2> = {(<element>|<element>|...),...};
<Guard-Relation>
<bound variable 1>,<bound variable 2>,...
<clause 1>
<clause 2>
...
```
Clauses can be specified using the previously defined relations as well as the negation operator:

`~E(x,y)`

and the equality

`=(x,y)`.

This is how one would specify a small vertex-cover instance:
```
vertex-cover
E2 = {(1|2),(2|1),(2|3),(3|2)};
E
x,y
~E(x,y) S(x) S(y)
```

## Graph files
Graph files specify undirected graphs and use the format `.gr`. They can be passed to the software using the `-g` option.

```
p td <# of nodes> <# of edges>
<edge 1 node 1> <edge 1 node 2>
<edge 2 node 1> <edge 2 node 2>
...
```
The same instance from above can be specified like this:
```
p td 3 2
1 2
2 3
```