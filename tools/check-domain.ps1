param(
    [Parameter(Mandatory=$true)][string]$GradleLib,
    [string]$Java = 'java'
)
$ErrorActionPreference = 'Stop'
$taskRoot = Split-Path $PSScriptRoot -Parent
$taskOutput = Join-Path $taskRoot 'build/domain-check'
$taskStdlib = Join-Path $GradleLib 'kotlin-stdlib-2.0.20.jar'
if (-not (Test-Path -LiteralPath $taskStdlib)) {
    throw 'Supply the lib directory of Gradle 8.11.1 (Kotlin 2.0.20).'
}
New-Item -ItemType Directory -Force $taskOutput | Out-Null
$taskClasspath = ((Get-ChildItem -LiteralPath $GradleLib -Filter '*.jar').FullName -join [IO.Path]::PathSeparator)
$taskSources = (Get-ChildItem -LiteralPath (Join-Path $taskRoot 'app/src/main/java/id/fintrack/app/domain') -Filter '*.kt').FullName
& $Java -cp $taskClasspath org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -no-stdlib -no-reflect -jvm-target 17 -nowarn -cp $taskStdlib -d $taskOutput @taskSources (Join-Path $PSScriptRoot 'DomainCheck.kt')
if ($LASTEXITCODE -ne 0) { throw "Domain compilation failed ($LASTEXITCODE)." }
& $Java -cp "$taskOutput$([IO.Path]::PathSeparator)$taskStdlib" DomainCheckKt
if ($LASTEXITCODE -ne 0) { throw "Domain checks failed ($LASTEXITCODE)." }
