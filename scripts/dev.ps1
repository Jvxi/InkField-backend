$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
. (Join-Path $scriptDir "resolve-jdk21.ps1")

Push-Location (Join-Path $scriptDir "..")
try {
  mvn -q clean package -DskipTests
  if ($LASTEXITCODE -ne 0) {
    throw "Backend build failed."
  }
  java -jar target\backend-0.1.0.jar
} finally {
  Pop-Location
}
