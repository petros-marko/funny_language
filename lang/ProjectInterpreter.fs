module ProjectInterpreter

open ProjectParser

let evalHelperfundef (FunctionDefinition (name, argNames, e)) = (name, (argNames, e))

let rec lookUp k m =
                match m with
                | [] -> None
                | p::ps -> if fst p = k then Some (snd p) else lookUp k ps

let rec sub e dict =
    match e with
    | Sequence (e1, e2) -> Sequence (sub e1 dict, sub e2 dict)
    | Value p ->
                match p with
                | Arg s -> match lookUp s dict with
                           | None -> failwith "no value to sub"
                           | Some v -> v
                | _ -> Value p
    | Application (n, exps) -> Application (n, List.map (fun x -> sub x dict) exps)
    | FunctionDefinition _ -> failwith "this situation should not occur in a well formed program"

let rec evalHelper e cs =
    match e with
    | Sequence (e1, e2) -> evalHelper e2 ((evalHelperfundef e1)::cs)
    | Value p -> Value p
    | FunctionDefinition _ -> failwith "A well formatted program should not encounter this situation"
    | Application (fname, args) ->
                                 match fname with
                                 |"id" -> evalHelper args.[0] cs
                                 |"plus" -> match (evalHelper args.[0] cs, evalHelper args.[1] cs) with
                                            |(Value (Integer n), Value (Integer m)) -> Value (Integer (n + m))
                                            |(Value (Integer n), Value (Real x)) -> Value (Real ((float n) + x))
                                            |(Value (Real x), Value (Integer n)) -> Value (Real ((float n) + x))
                                            |(Value (Real x), Value (Real y)) -> Value (Real (x + y))
                                            | _ -> failwith "incorrect arguments passed to function plus"
                                 |"minus" -> match (evalHelper args.[0] cs, evalHelper args.[1] cs) with
                                             |(Value (Integer n), Value (Integer m)) -> Value (Integer (n - m))
                                             |(Value (Integer n), Value (Real x)) -> Value (Real ((float n) - x))
                                             |(Value (Real x), Value (Integer n)) -> Value (Real ((float n) - x))
                                             |(Value (Real x), Value (Real y)) -> Value (Real (x - y))
                                             | _ -> failwith "incorrect arguments passed to function minus"
                                 |"times" -> match (evalHelper args.[0] cs, evalHelper args.[1] cs) with
                                             |(Value (Integer n), Value (Integer m)) -> Value (Integer (n * m))
                                             |(Value (Integer n), Value (Real x)) -> Value (Real ((float n) * x))
                                             |(Value (Real x), Value (Integer n)) -> Value (Real ((float n) * x))
                                             |(Value (Real x), Value (Real y)) -> Value (Real (x * y))
                                             | _ -> failwith "incorrect arguments passed to function times"
                                 |"div" -> match (evalHelper args.[0] cs, evalHelper args.[1] cs) with
                                             |(Value (Integer n), Value (Integer m)) -> Value (Integer (n / m))
                                             |(Value (Integer n), Value (Real x)) -> Value (Real ((float n) / x))
                                             |(Value (Real x), Value (Integer n)) -> Value (Real ((float n) / x))
                                             |(Value (Real x), Value (Real y)) -> Value (Real (x / y))
                                             | _ -> failwith "incorrect arguments passed to function div"
                                 |"mod" -> match (evalHelper args.[0] cs, evalHelper args.[1] cs) with
                                             |(Value (Integer n), Value (Integer m)) -> Value (Integer (n % m))
                                             | _ -> failwith "incorrect arguments passed to function mod"
                                 |"pair" -> match (evalHelper args.[0] cs, evalHelper args.[1] cs) with
                                             |(Value v1, Value v2) -> Value (Pair (v1, v2))
                                             | _ -> failwith "incorrect arguments passed to function pair"
                                 |"fst" -> match evalHelper args.[0] cs with
                                             |Value (Pair (p1, p2)) -> Value p1
                                             |_ -> failwith "incorrect argument passed to function fst"
                                 |"snd" -> match evalHelper args.[0] cs with
                                             |Value (Pair (p1, p2)) -> Value p2
                                             |_ -> failwith "incorrect argument passed to function fst"
                                 |"gr" -> match (evalHelper args.[0] cs, evalHelper args.[1] cs) with
                                             |(Value (Integer n), Value (Integer m)) -> Value (Bool (n > m))
                                             |(Value (Integer n), Value (Real x)) -> Value (Bool ((float n) > x))
                                             |(Value (Real x), Value (Integer n)) -> Value (Bool ((float n) > x))
                                             |(Value (Real x), Value (Real y)) -> Value (Bool (x > y))
                                             | _ -> failwith "incorrect arguments passed to function gr"
                                 |"le" -> match (evalHelper args.[0] cs, evalHelper args.[1] cs) with
                                             |(Value (Integer n), Value (Integer m)) -> Value (Bool (n < m))
                                             |(Value (Integer n), Value (Real x)) -> Value (Bool ((float n) < x))
                                             |(Value (Real x), Value (Integer n)) -> Value (Bool ((float n) < x))
                                             |(Value (Real x), Value (Real y)) -> Value (Bool (x < y))
                                             | _ -> failwith "incorrect arguments passed to function le"
                                 |"eq" -> match (evalHelper args.[0] cs, evalHelper args.[1] cs) with
                                          |(Value (Integer n), Value (Integer m)) -> Value (Bool (n = m))
                                          |(Value (Integer n), Value (Real x)) -> Value (Bool ((float n) = x))
                                          |(Value (Real x), Value (Integer n)) -> Value (Bool ((float n) = x))
                                          |(Value (Real x), Value (Real y)) -> Value (Bool (x = y))
                                          |(Value (Bool n), Value (Bool m)) -> Value (Bool (n = m))
                                          |(Value Nil, Value Nil) -> Value (Bool true)
                                          |(Value _, Value Nil) -> Value (Bool false)
                                          |(Value Nil, Value _ ) -> Value (Bool false)
                                          |_  -> failwith "incorrect arguments passed to function eq"
                                 |"toInteger" -> match evalHelper args.[0] cs with
                                                 |Value (Integer n) -> Value (Integer n)
                                                 |Value (Real x) -> Value (Integer (int x))
                                                 |Value (Bool b) -> Value (Integer (if b then 1 else 0))
                                                 |_ -> failwith "incorrect argument passed to function toInteger"
                                 |"toReal" -> match evalHelper args.[0] cs with
                                                 |Value (Integer n) -> Value (Real (float n))
                                                 |Value (Real x) -> Value (Real x)
                                                 |Value (Bool b) -> Value (Real (if b then 1.0 else 0.0))
                                                 |_ -> failwith "incorrect argument passed to function toInteger"
                                 |"if" -> match evalHelper args.[0] cs with
                                          | Value (Bool c) -> if c then evalHelper args.[1] cs else evalHelper args.[2] cs
                                          | _ -> failwith "incorrect arguments passed to function if"
                                 | _ -> match lookUp fname cs with
                                        | None -> failwith ("function " + fname + " has not been defined")
                                        | Some (argNames, fex) -> evalHelper (sub fex (List.zip argNames (List.map (fun a -> evalHelper a cs) args))) cs

let eval e = evalHelper e []
