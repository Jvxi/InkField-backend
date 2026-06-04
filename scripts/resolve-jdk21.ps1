$jdkHome = ""

try {
  $latest = Get-ChildItem "HKLM:\SOFTWARE\JavaSoft\JDK" |
    Sort-Object PSChildName -Descending |
    Select-Object -First 1

  if ($latest) {
    $jdkHome = (Get-ItemProperty $latest.PSPath).JavaHome
  }
} catch {
  $jdkHome = ""
}

if (-not $jdkHome) {
  throw "No JDK found in HKLM:\SOFTWARE\JavaSoft\JDK"
}

if (-not (Test-Path (Join-Path $jdkHome "bin\java.exe"))) {
  throw "Invalid Java home: $jdkHome"
}

$env:JAVA_HOME = $jdkHome
$env:Path = "$($env:JAVA_HOME)\bin;$env:Path"
