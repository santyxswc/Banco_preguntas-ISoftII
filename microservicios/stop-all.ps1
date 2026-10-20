# Script PowerShell para detener los microservicios del Taller 09

Write-Host "==========================================================" -ForegroundColor Yellow
Write-Host " Deteniendo Microservicios del Taller 09..." -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Yellow

$ports = @(8081, 8082, 8083, 8084, 8085, 8086)

foreach ($port in $ports) {
    $conns = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($conns) {
        foreach ($conn in $conns) {
            $processId = $conn.OwningProcess
            if ($processId -gt 0) {
                Write-Host "Deteniendo proceso $processId en puerto $port..." -ForegroundColor Red
                Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
            }
        }
    } else {
        Write-Host "Puerto $port ya se encuentra libre." -ForegroundColor Gray
    }
}

Write-Host "`nMicroservicios detenidos exitosamente." -ForegroundColor Green
