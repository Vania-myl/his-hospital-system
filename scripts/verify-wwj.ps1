$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location (Join-Path $projectRoot "backend")
try {
    & mvn test
    if ($LASTEXITCODE -ne 0) { throw "Backend tests failed" }
} finally { Pop-Location }
Push-Location (Join-Path $projectRoot "frontend")
try {
    & npm ci --no-audit --no-fund
    if ($LASTEXITCODE -ne 0) { throw "Frontend install failed" }
    & npm run build
    if ($LASTEXITCODE -ne 0) { throw "Frontend build failed" }
} finally { Pop-Location }
Write-Host "Backend tests and frontend build passed. Complete the local MySQL walkthrough next."
