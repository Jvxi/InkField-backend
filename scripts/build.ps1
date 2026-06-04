$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
. (Join-Path $scriptDir "resolve-jdk21.ps1")

Push-Location (Join-Path $scriptDir "..")
try {
  mvn clean package
} finally {
  Pop-Location
}
