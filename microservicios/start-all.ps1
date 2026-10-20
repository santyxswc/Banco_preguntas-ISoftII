# Script PowerShell para iniciar RabbitMQ y los 6 microservicios del Taller 09

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " Iniciando Arquitectura de Microservicios - Taller 09" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Verificar y levantar RabbitMQ
Write-Host "`n[1/7] Verificando contenedor Docker de RabbitMQ..." -ForegroundColor Yellow
docker compose up -d

# 2. Configurar JAVA_HOME si es necesario
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot"

$services = @(
    @{ Name = "User Service";         Dir = "user-service";         Port = 8083 },
    @{ Name = "Validation Service";   Dir = "validation-service";   Port = 8082 },
    @{ Name = "Lifecycle Service";    Dir = "lifecycle-service";    Port = 8084 },
    @{ Name = "Question Service";     Dir = "question-service";     Port = 8081 },
    @{ Name = "Review Service";       Dir = "review-service";       Port = 8085 },
    @{ Name = "Notification Service"; Dir = "notification-service"; Port = 8086 }
)

$PSScriptRootCurrent = Split-Path -Parent $MyInvocation.MyCommand.Path

foreach ($s in $services) {
    Write-Host "`nIniciando $($s.Name) en puerto $($s.Port)..." -ForegroundColor Green
    $workingDir = Join-Path $PSScriptRootCurrent $s.Dir
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "`$env:JAVA_HOME='$($env:JAVA_HOME)'; cd '$workingDir'; ..\..\mvnw.cmd spring-boot:run" -WindowStyle Normal
    Start-Sleep -Seconds 3
}

Write-Host "`n==========================================================" -ForegroundColor Cyan
Write-Host " Todos los microservicios fueron lanzados." -ForegroundColor Cyan
Write-Host " Puertos activos:" -ForegroundColor White
Write-Host "  - RabbitMQ Management: http://localhost:15672 (guest/guest)" -ForegroundColor Gray
Write-Host "  - Question Service:    http://localhost:8081/swagger-ui.html" -ForegroundColor Gray
Write-Host "  - Validation Service:  http://localhost:8082/swagger-ui.html" -ForegroundColor Gray
Write-Host "  - User Service:        http://localhost:8083/swagger-ui.html" -ForegroundColor Gray
Write-Host "  - Lifecycle Service:   http://localhost:8084/swagger-ui.html" -ForegroundColor Gray
Write-Host "  - Review Service:      http://localhost:8085/swagger-ui.html" -ForegroundColor Gray
Write-Host "  - Notification Service:http://localhost:8086/swagger-ui.html" -ForegroundColor Gray
Write-Host "==========================================================" -ForegroundColor Cyan
