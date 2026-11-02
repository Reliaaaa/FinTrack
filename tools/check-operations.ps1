param(
    [Parameter(Mandatory=$true)][string]$GradleLib,
    [string]$Java = 'java'
)
$ErrorActionPreference = 'Stop'
$taskRoot = Split-Path $PSScriptRoot -Parent
$taskOutput = Join-Path $taskRoot 'build/operation-check'
New-Item -ItemType Directory -Force $taskOutput | Out-Null
$taskStdlib = Join-Path $GradleLib 'kotlin-stdlib-2.0.20.jar'
$taskCoroutines = Join-Path $GradleLib 'kotlinx-coroutines-core-jvm-1.6.4.jar'
$taskClasspath = ((Get-ChildItem -LiteralPath $GradleLib -Filter '*.jar').FullName -join [IO.Path]::PathSeparator)
$taskRuntime = "$taskStdlib$([IO.Path]::PathSeparator)$taskCoroutines"
& $Java -cp $taskClasspath org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -no-stdlib -no-reflect -jvm-target 17 -cp $taskRuntime -d $taskOutput (Join-Path $taskRoot 'app/src/main/java/id/fintrack/app/vm/OperationStore.kt') (Join-Path $PSScriptRoot 'OperationCheck.kt')
if ($LASTEXITCODE -ne 0) { throw "Operation compilation failed ($LASTEXITCODE)." }
& $Java -cp "$taskOutput$([IO.Path]::PathSeparator)$taskRuntime" OperationCheckKt
if ($LASTEXITCODE -ne 0) { throw "Operation checks failed ($LASTEXITCODE)." }
