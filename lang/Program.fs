// Learn more about F# at http://fsharp.org

open System
open ProjectParser
open ProjectInterpreter

[<EntryPoint>]
let main argv =
    //If there are going to be newline characters in the input, it cannot be run via dotnet run, because they are not read in properly
    //Rather, the program has to be generated using the interface
    try
       let input = formatInputString argv.[0]
       match parse input with
       |Some r -> eval r |> eprettyprint |> printfn "%s"
       |None -> failwith "Invalid Expression"
    with
    |_ -> printf "Usage: dotnet run <program>\n<program> is a string representation of the program to be run\n"
    0 // return an integer exit code
