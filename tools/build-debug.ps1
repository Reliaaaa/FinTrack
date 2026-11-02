param(
    [Parameter(Mandatory=$true)][string]$JavaHome,
    [Parameter(Mandatory=$true)][string]$AndroidSdk,
    [switch]$Offline
)
$ErrorActionPreference = 'Stop'
$taskRoot = Split-Path $PSScriptRoot -Parent
$taskOutput = Join-Path $taskRoot 'build'
$taskKey = Join-Path $taskOutput 'fintrack-debug.jks'
New-Item -ItemType Directory -Force $taskOutput | Out-Null
if (-not (Test-Path -LiteralPath $taskKey)) {
    & (Join-Path $JavaHome 'bin/keytool.exe') -genkeypair -keystore $taskKey -storepass android -keypass android -alias androiddebugkey -keyalg RSA -keysize 2048 -validity 10000 -dname 'CN=Android Debug,O=Android,C=US'
    if ($LASTEXITCODE -ne 0) { throw 'Could not generate the local debug keystore.' }
}
$taskPreviousJava = $env:JAVA_HOME
$taskPreviousSdk = $env:ANDROID_HOME
Push-Location $taskRoot
try {
    $env:JAVA_HOME = $JavaHome
    $env:ANDROID_HOME = $AndroidSdk
    $taskArgs = @('--no-build-cache', '-Pkotlin.compiler.execution.strategy=in-process',
        "-Pandroid.injected.signing.store.file=$taskKey",
        '-Pandroid.injected.signing.store.password=android',
        '-Pandroid.injected.signing.key.alias=androiddebugkey',
        '-Pandroid.injected.signing.key.password=android',
        ':app:assembleDebug', ':app:lintDebug', ':app:compileDebugAndroidTestKotlin', '--console=plain')
    if ($Offline) { $taskArgs += '--offline' }
    & ./gradlew.bat @taskArgs
    if ($LASTEXITCODE -ne 0) { throw "Debug validation failed ($LASTEXITCODE)." }
} finally {
    Pop-Location
    $env:JAVA_HOME = $taskPreviousJava
    $env:ANDROID_HOME = $taskPreviousSdk
}
