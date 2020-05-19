namespace funny_language_tests

open System
open Microsoft.VisualStudio.TestTools.UnitTesting
open ProjectParser
open ProjectInterpreter

[<TestClass>]
type TestClass () =

    [<TestMethod>]
    member this.parsesInteger () =
        let input = "-3"
        let expected = Value (Integer -3)
        match parse input with
        |Some r -> Assert.AreEqual(expected, r)
        |None -> Assert.IsTrue false

    [<TestMethod>]
    member this.parsesReal () =
        let input = "3.14159"
        let expected = Value (Real 3.14159)
        match parse input with
        |Some r -> Assert.AreEqual(expected, r)
        |None -> Assert.IsTrue false

    [<TestMethod>]
    member this.parsesBoolean () =
        let input = "True"
        let expected = Value (Bool true)
        match parse input with
        |Some r -> Assert.AreEqual(expected, r)
        |None -> Assert.IsTrue false
    
    [<TestMethod>]
    member this.parsesNil () =
        let input = "Nil"
        let expected = Value Nil
        match parse input with
        |Some r -> Assert.AreEqual(expected, r)
        |None -> Assert.IsTrue false

    [<TestMethod>]
    member this.parsesApplication () =
        let input = "( plus 10 10 )"
        let expected = Application ("plus", [Value (Integer 10); Value (Integer 10)])
        match parse input with
        |Some r -> Assert.AreEqual(expected, r)
        |None -> Assert.IsTrue false

    [<TestMethod>]
    member this.parsesFunctionDefinition () =
        let input = "(fun plusOne n ( plus 1 n ))"
        let expected = FunctionDefinition ("plusOne", ["n"], Application ("plus", [Value (Integer 1); Value (Arg "n")]))
        match parse input with
        |Some r -> Assert.AreEqual(expected, r)
        |None -> Assert.IsTrue false

    [<TestMethod>]
    member this.parsesSequence () =
        let input = "\n(fun plusOne n ( plus 1 n ))\n( plusOne 0 )"
        let expected = Sequence ((FunctionDefinition ("plusOne", ["n"], Application ("plus", [Value (Integer 1); Value (Arg "n")]))), (Application("plusOne", [Value (Integer 0)])))
        match parse input with
        |Some r -> Assert.AreEqual(expected, r)
        |None -> Assert.IsTrue false

    [<TestMethod>]
    member this.evalsValue () =
        let input = "Nil"
        let expected = Value Nil
        match parse input with
        |Some r -> Assert.AreEqual(expected, eval r)
        |None -> Assert.IsTrue false

    [<TestMethod>]
    member this.evalsApplication () =
        let input = "( plus 3 5 )"
        let expected = Value (Integer 8)
        match parse input with
        |Some r -> Assert.AreEqual(expected, eval r)
        |None -> Assert.IsTrue false

    [<TestMethod>]
    member this.evalsSequence () =
        let input = "\n(fun max m n ( if ( gr m n ) m n ))\n( max 3 5 )"
        let expected = Value (Integer 5)
        match parse input with
        |Some r -> Assert.AreEqual(expected, eval r)
        |None -> Assert.IsTrue false

    [<TestMethod>]
    member this.evalsRecursive () =
        let input = "\n(fun or p q ( if p True q ))\n(fun fib n ( if ( or ( eq n 0 ) ( eq n 1 ) ) 1 ( plus ( fib ( minus n 1 ) ) ( fib ( minus n 2 ) ) ) ))\n( fib 5 )"
        let expected = Value (Integer 8)
        match parse input with
        |Some r -> Assert.AreEqual(expected, eval r)
        |None -> Assert.IsTrue false

