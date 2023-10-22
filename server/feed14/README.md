# news feed in C# on ASP.NET

local dev setup

```bash
cd service
dotnet add package MySql.Data --version 8.1.0
dotnet add package NRedisStack
dotnet add package CassandraCSharpDriver --version 3.19.3
cd ../tests
dotnet add package moq
```

deving locally

```bash
cd service
dotnet build
cd ../tests
dotnet test
cd ../service
dotnet run
```
