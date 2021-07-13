# HyperATLMC - HyperATL* Model Checking

HyperATLMC is a model checker for a fragment of HyperATL*.
This repository is the implementation of the CONCUR 2021 paper "A temporal logic for Strategic Hyperproperties" [1].

This tools takes a HyperATL* formula and a program and computes a combined parity game.
The game can then be solved with existing parity game solvers such as [pgsolver](https://github.com/tcsprojects/pgsolver/).
The tool relies on an external tool to convert an LTL formula to a deterministic parity automaton. 
We use [rabinizer 4](https://www7.in.tum.de/~kretinsk/rabinizer4.html).

##Dependencies

To run this tool you require the following:

- Java JDK (tested with openjdk 16.0.1 2021-04-20)
- ANT (tested with version 1.10.10)
- [rabinizer 4](https://www7.in.tum.de/~kretinsk/rabinizer4.html) 
- [pgsolver](https://github.com/tcsprojects/pgsolver/)


## How to Build

To build simply run ``ant`` in the main directory. Afterwards run ``ant build-jar``. This will create a new directory ``/jar`` containing the file ``hyperatlmc.jar``. 
You can run this file via ``java -jar hyperatlmc.jar``.

To run the tool you need rabinzer 4. Download the prebuild binaries [here](https://www7.in.tum.de/~kretinsk/rabinizer4.html). 
The tool ``ltl2dpa`` is located in the ``bin`` directory of the download archive. On unix Linux and macOS you can run the executable ``ltl2dpa`` directly. 
On Windows you can use the startup script ``ltl2pda.bat``. 
You need to pass the path to the tool (e.g. ``~/rabinizer/bin/ltl2dpa`` on linux or ``C:\rabinzer\bin\ltl2dpa.bat`` on Windows) to hyperatlmc.
See command line arguments (given below) for details.

## Command Line Arguments

hyperatlmc supports the following command line arguments:

- ``-f`` specifies the file containing the formula
- ``-p`` specifies the file containing the program
- ``-rab`` specifies the path to the rabinizer tool
- ``-out`` specifies the output file

Options ``-f`` and ``-p`` are mandatory. If ``-rab`` is not used, the program expects `ltl2dpa` in the current directory (on Windows you still need to use `-rab ltl2dpa.bat` to run the startup script).
If `-out` is not set, the output is, by default, written to `out.txt` in the current directory.

### Example

Suppose the formula to be checked is contained in ``formula.txt``, the program in `p.txt` and `ltl2dpa` is located at `~/rabinizer/bin/ltl2dpa`.
Then we can run 
```
java -jar hyperatlmc -f formula.txt -p p.txt -rab "~/rabinizer/bin/ltl2dpa"
```


## Formula syntax

hyperatlmc supports HyperATL* formulas in the self-composed fragment. See [1] for details.
Every formula has thus the form `[<<A1>> p1. .... <<An>> pn.] /phi` where `/phi` is an LTL formula (referring to the propositions on each path variable) and `A1,..., An` are sets of agents. 
The three agents are the nondeterminism player, the high-security player and the low-security player. See [1] for details.
We assign ID `0` to the high-player, `1` to the low-player and `2` to the nondeterminism. 
In case the CGS is stuttered, we assign `3` to the scheduling agent.

The given CGS can be modified at each quantifier by either shifting the game or stuttering the game.
The overall formula has the form (given for the case of two quantifiers).
```
[<<A1>> p1 S1. <<A2>> p2. S2 ] "/phi"
```
Here `A1, A2` are set of agents, i.e., subsets of `{0, 1, 2, 3}` (3 is only possible if the system is stuttered) `p1, p2` are path names. `S1, S2` specify additional transformations, i.e., shifting or stuttering.
Each `Si` can be either `(SHIFT)`, `(STUTTER)` or empty, corresponding to each of the possible transformations.
To refer to atomic propositions ``a`` we use `a_p` in the LTL formula where `p` is the name of the path defined in the quantifier prefix.
The LTL formula follows the syntax supported by rabinzer. 

An example formula is the following (specifying simulation security):
```
[<<{}>>p. <<{2}>>q (SHIFT).] "(G(l0_p <->  X l0_q)) -> G(o0_p <-> X o0_q)"
```
Here we use the empty agent set for path `p` and the set `{2}` for path `q`. The second path is resolved on the shifted copy.

## Program Syntax

The tool supports a simple language supporting conditionals, non-deterministic branching, loops. See [1] for details on the program and supported expressions.
At the beginning of the program the bitwidth of each variable must be specified.
As an example, consider the following program which fixes the bitwidth of `l, o, h` to 3. 

```
==DOMAIN==
h 3
o 3
l 3
==BEGIN==
o := TRUE;
WHILE(TRUE) {
h <- READH(0);
IF(h[0]) THEN {
o := ! o
} ELSE {
o := (!o) & (h || !h)
}
}
==END==
```

The following constructs are supported:

- Assignments `x := e` where `e` is an expressions
- Inputs: `x <- READH(0)` or `x <- READL(0)`
- Conditionals: `IF(e) THEN { P1} ELSE {P2}` where `P1, P2` are programs
- Composition: `P1; P2`
- Loops: `WHILE(e) {P}`
- Nondeterministic branching: `IF* THEN {P1} ELSE {P2}`

AN expression can be one of the following:
- A variable
- `TRUE` or `FALSE`
- (Pointwise) Conjunction or disjunction of two expressions with the same width `e && e`, `e || e`.
- (Pointwise) negation `! e`
- Concatenation: `e @ e`
- Projection: `e[n]` where `n` is a natrual number.

The atomic proposition at each step are the content of the variables. If `x` is a variable in the program of bitwidth `n` then `xi` for `0 <= i < n` are atomic propositions. 
`xi` holds iff the `i`th bit of `x` is set to true.




