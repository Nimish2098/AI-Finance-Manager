Write-Host "Starting Local Development Environment..." -ForegroundColor Green

# 1. Start Database Container
Write-Host "Starting Postgres Database..." -ForegroundColor Cyan
docker-compose up -d postgres

# 2. Check if DB start was successful
if ($LASTEXITCODE -ne 0) {
    Write-Error "Failed to start database. Make sure Docker is running."
    exit 1
}

Write-Host "Waiting for Database to become ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# 3. Run Application with 'local' profile
Write-Host "Starting Spring Boot Application (Profile: local)..." -ForegroundColor Cyan
Write-Host "Press Ctrl+C to stop the application." -ForegroundColor Gray
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local" "-Dspring-boot.run.jvmArguments=-Duser.timezone=UTC"
