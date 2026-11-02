param(
    [ValidateSet(1, 2)]
    [int]$Instancias = 1,
    [int]$Usuarios = 500,
    [string]$Mantener = "2m",
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"
$aqui = Split-Path -Parent $MyInvocation.MyCommand.Path
$etiqueta = "$Instancias-instancia" + $(if ($Instancias -gt 1) { "s" } else { "" })
New-Item -ItemType Directory -Force -Path (Join-Path $aqui "resultados") | Out-Null

Write-Host "Escenario de escalabilidad: $Usuarios usuarios concurrentes, $Instancias instancia(s) de question-service" -ForegroundColor Cyan
if ($Instancias -eq 2) {
    Write-Host "Requisitos: question-service en 8081 y 8091 (QUESTION_SERVICE_PORT=8091) y el gateway con SPRING_PROFILES_ACTIVE=escalado." -ForegroundColor Yellow
    if (-not (Get-NetTCPConnection -LocalPort 8091 -State Listen -ErrorAction SilentlyContinue)) {
        throw "No hay una segunda instancia de question-service escuchando en 8091."
    }
}

Push-Location $aqui
try {
    if (Get-Command k6 -ErrorAction SilentlyContinue) {
        k6 run -e BASE_URL=$BaseUrl -e ETIQUETA=$etiqueta -e USUARIOS=$Usuarios -e MANTENER=$Mantener escenario-escalabilidad.js
    } else {
        $url = $BaseUrl -replace "localhost", "host.docker.internal"
        docker run --rm -v "${aqui}:/scripts" -w /scripts grafana/k6:0.54.0 run -e BASE_URL=$url -e ETIQUETA=$etiqueta -e USUARIOS=$Usuarios -e MANTENER=$Mantener escenario-escalabilidad.js
    }
} finally {
    Pop-Location
}

Write-Host "Resultados guardados en resultados/resumen-$etiqueta.json" -ForegroundColor Green
