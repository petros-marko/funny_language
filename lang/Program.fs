// Learn more about F# at http://fsharp.org

open System
open ProjectParser
open ProjectInterpreter

[<EntryPoint>]
let main argv =
    try
       let input = formatInputString argv.[0]
       match parse input with
       |Some r -> eval r |> eprettyprint |> printfn "%s"
       |None -> failwith "Invalid Expression"
    with
       | :? IndexOutOfRangeException -> printf "Usage: dotnet run <program>\n<program> is a string representation of the program to be run\n"
       | e -> printfn "%s" e.Message
    0 // return an integer exit code
