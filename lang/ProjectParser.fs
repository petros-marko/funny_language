module ProjectParser

open System
open Parser

type Primitive = 
     | Bool of bool
     | Nil
     | Integer of int
     | Real of float
     | Pair of Primitive * Primitive
     | Arg of string

let rec pprettyprint p =
    match p with
    |Bool b -> string b
    |Nil -> "Nil"
    |Integer n -> string n
    |Real x -> string x
    |Pair (p1, p2) -> "[" + (pprettyprint p1) + ", " + (pprettyprint p2) + "]"
    |Arg s -> s

type Expression =
     | Sequence of Expression * Expression
     | Value of Primitive
     | Application of string * (Expression list)
     | FunctionDefinition of string * (string list) * Expression

let eprettyprint e =
    match e with
    | Value p -> pprettyprint p
    | o -> string o

let pbool      = (pstr "True") <|> (pstr "False") |>> Convert.ToBoolean |>> Bool <!> "bool"
let pnil       = pfresult (pstr "Nil") Nil <!> "nil"
let pinteger   = ((pseq (pchar '-') (pmany1 pdigit) (fun (a,b) -> a::b)) <|> (pmany1 pdigit)) |>> String.Concat |>> int |>> Integer <!> "number"
let preal      = (pseq (pseq ((pseq (pchar '-') (pmany0 pdigit) (fun (a,b) -> a::b)) <|> (pmany0 pdigit)) (pchar '.') (fun (a, b) -> (stringify a) + (string b))) (pmany1 pdigit) (fun (a,b) -> a + (stringify b))) |>> float |>> Real
let parg       = pmany1 pletter |>> String.Concat |>> Arg <!> "arg"
let pprimitive = pbool <|> pnil <|> preal <|> pinteger <|> parg <!> "primitive"

let rec sequencer s =
        match s with
        |[a] -> a
        |e::es -> Sequence (e, sequencer es)

let expr, exprImp = recparser()
let psequence     = pmany1 (pright (pchar '\n') expr) |>> sequencer<!> "sequence"
let pvalue        = pprimitive |>> Value <!> "value"
let papplication  = pbetween (pchar '(') (pchar ')') ((pseq (pbetween (pchar ' ') (pchar ' ') (pmany1 pletter) |>> String.Concat) (pmany1 (pleft expr (pchar ' '))) Application)) <!> "application"
let pfdefinition = pbetween (pchar '(') (pchar ')') (pseq (pseq (pright (pstr "fun") (pbetween (pchar ' ') (pchar ' ') (pmany1 pletter) |>> String.Concat)) (pmany1 (pleft (pmany1 pletter) (pchar ' ') |>> String.Concat)) id) expr (fun ((a,b),c) -> FunctionDefinition(a,b,c))) <!> "function"
exprImp := psequence <|> pvalue <|> papplication <|> pfdefinition <!> "expression"

let grammar = pleft expr peof <!> "grammar"

let parse inp =
    match grammar (prepare inp) with
    |Success (r, _) -> Some r
    |Failure _ -> None

let formatInputString (s : string) = s.Replace("\\n","\n")
