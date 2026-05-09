$ErrorActionPreference = "Stop"

$RepoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$BackendRoot = Join-Path $RepoRoot "backend"
$WebRoot = Join-Path $RepoRoot "apps\web"
$ToolsRoot = Join-Path $RepoRoot "tools"
$MavenCmd = Join-Path $ToolsRoot "apache-maven-3.9.12\bin\mvn.cmd"
$MavenRepo = Join-Path $ToolsRoot "m2"
$HealthUrl = "http://localhost:8080/actuator/health"
$SwaggerUrl = "http://localhost:8080/swagger-ui/index.html"
$WebUrl = "http://localhost:5173"
$ContestUrl = "$WebUrl/"
$ClipsUrl = "$WebUrl/clips"
$LeaderboardUrl = "$WebUrl/leaderboard"
$AdminContestUrl = "$WebUrl/admin/contests"
$ModerationUrl = "$WebUrl/admin/moderation"

function Sync-SocialAuthEnvironment {
  if (-not $env:VITE_GOOGLE_CLIENT_ID -and $env:GOOGLE_OAUTH_CLIENT_ID) {
    $env:VITE_GOOGLE_CLIENT_ID = $env:GOOGLE_OAUTH_CLIENT_ID
  }
  if (-not $env:GOOGLE_OAUTH_CLIENT_ID -and $env:VITE_GOOGLE_CLIENT_ID) {
    $env:GOOGLE_OAUTH_CLIENT_ID = $env:VITE_GOOGLE_CLIENT_ID
  }

  if (-not $env:VITE_MICROSOFT_CLIENT_ID -and $env:MICROSOFT_OAUTH_CLIENT_ID) {
    $env:VITE_MICROSOFT_CLIENT_ID = $env:MICROSOFT_OAUTH_CLIENT_ID
  }
  if (-not $env:MICROSOFT_OAUTH_CLIENT_ID -and $env:VITE_MICROSOFT_CLIENT_ID) {
    $env:MICROSOFT_OAUTH_CLIENT_ID = $env:VITE_MICROSOFT_CLIENT_ID
  }

  if (-not $env:VITE_MICROSOFT_TENANT_ID -and $env:MICROSOFT_OAUTH_TENANT_ID) {
    $env:VITE_MICROSOFT_TENANT_ID = $env:MICROSOFT_OAUTH_TENANT_ID
  }
}

function Write-Step($Message) {
  Write-Host ""
  Write-Host "==> $Message" -ForegroundColor Cyan
}

function Test-HttpOk($Url) {
  try {
    $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 3
    return $response.StatusCode -ge 200 -and $response.StatusCode -lt 500
  } catch {
    return $false
  }
}

function Wait-ForHttp($Url, $Label, $Seconds) {
  $deadline = (Get-Date).AddSeconds($Seconds)
  while ((Get-Date) -lt $deadline) {
    if (Test-HttpOk $Url) {
      Write-Host "$Label is ready: $Url" -ForegroundColor Green
      return $true
    }
    Start-Sleep -Seconds 2
  }
  return $false
}

function Test-DockerReady {
  try {
    docker version | Out-Null
    return $true
  } catch {
    return $false
  }
}

function Start-DockerDesktopIfNeeded {
  if (Test-DockerReady) {
    return
  }

  $dockerDesktop = "C:\Program Files\Docker\Docker\Docker Desktop.exe"
  if (Test-Path $dockerDesktop) {
    Write-Step "Starting Docker Desktop"
    Start-Process -FilePath $dockerDesktop -WindowStyle Hidden | Out-Null
    for ($i = 0; $i -lt 60; $i++) {
      Start-Sleep -Seconds 2
      if (Test-DockerReady) {
        return
      }
    }
  }

  throw "Docker is not running. Start Docker Desktop and run this file again."
}

function Find-Maven {
  if (Test-Path $MavenCmd) {
    return $MavenCmd
  }

  $globalMaven = Get-Command "mvn.cmd" -ErrorAction SilentlyContinue
  if ($globalMaven) {
    return $globalMaven.Source
  }

  throw "Maven was not found. Expected local Maven at $MavenCmd."
}

function Start-Backend {
  if (Test-HttpOk $HealthUrl) {
    Write-Host "Backend is already running." -ForegroundColor Green
    return
  }

  $mvn = Find-Maven
  $repoArg = "-Dmaven.repo.local=$MavenRepo"
  $command = "& '$mvn' '$repoArg' spring-boot:run"

  Write-Step "Starting backend"
  Start-Process -FilePath "powershell.exe" `
    -ArgumentList @("-NoExit", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", $command) `
    -WorkingDirectory $BackendRoot `
    -WindowStyle Normal | Out-Null

  if (-not (Wait-ForHttp $HealthUrl "Backend" 90)) {
    throw "Backend did not become healthy within 90 seconds. Check the backend window."
  }
}

function Start-Frontend {
  if (Test-HttpOk $WebUrl) {
    Write-Host "Frontend is already running." -ForegroundColor Green
    return
  }

  if (-not (Test-Path (Join-Path $WebRoot "node_modules"))) {
    Write-Step "Installing frontend dependencies"
    Push-Location $WebRoot
    try {
      npm.cmd install
    } finally {
      Pop-Location
    }
  }

  Write-Step "Starting frontend"
  Start-Process -FilePath "cmd.exe" `
    -ArgumentList @("/k", "npm.cmd run dev -- --host 127.0.0.1 --port 5173") `
    -WorkingDirectory $WebRoot `
    -WindowStyle Normal | Out-Null

  if (-not (Wait-ForHttp $WebUrl "Frontend" 60)) {
    throw "Frontend did not become reachable within 60 seconds. Check the frontend window."
  }
}

if (-not (Test-Path $BackendRoot)) {
  throw "Backend folder not found: $BackendRoot"
}

if (-not (Test-Path $WebRoot)) {
  throw "Web app folder not found: $WebRoot"
}

Write-Host "Starting ProPokerTV from $RepoRoot" -ForegroundColor White

Sync-SocialAuthEnvironment

Start-DockerDesktopIfNeeded

Write-Step "Starting PostgreSQL"
Push-Location $BackendRoot
try {
  docker compose up -d
} finally {
  Pop-Location
}

Start-Backend
Start-Frontend

Write-Step "Opening weekly contest"
Start-Process $ContestUrl | Out-Null

Write-Host ""
Write-Host "ProPokerTV is running." -ForegroundColor Green
Write-Host "Weekly Contest:  $ContestUrl"
Write-Host "Clips:           $ClipsUrl"
Write-Host "Leaderboard:     $LeaderboardUrl"
Write-Host "Admin Contests:  $AdminContestUrl"
Write-Host "Moderation:      $ModerationUrl"
Write-Host "API:             $HealthUrl"
Write-Host "Swagger:         $SwaggerUrl"
Write-Host "Google login:    $(if ($env:GOOGLE_OAUTH_CLIENT_ID -and $env:VITE_GOOGLE_CLIENT_ID) { 'configured' } else { 'not configured' })"
Write-Host "Microsoft login: $(if ($env:MICROSOFT_OAUTH_CLIENT_ID -and $env:VITE_MICROSOFT_CLIENT_ID) { 'configured' } else { 'not configured' })"
Write-Host ""
Write-Host "Set GOOGLE_OAUTH_CLIENT_ID or VITE_GOOGLE_CLIENT_ID before starting to enable Google login."
Write-Host "Set MICROSOFT_OAUTH_CLIENT_ID or VITE_MICROSOFT_CLIENT_ID before starting to enable Microsoft login."
Write-Host "Close the backend/frontend terminal windows to stop the app processes."
