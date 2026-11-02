param(
    [string]$Gateway = "http://localhost:8080",
    [string]$RabbitContainer = "rabbitmq-taller09",
    [long]$AutorId = 2,
    [long]$AdminId = 1,
    [long]$RevisorId = 4
)

$ErrorActionPreference = "Stop"

function Get-QueueDepth([string]$queue) {
    $line = docker exec $RabbitContainer rabbitmqctl -q list_queues name messages | Where-Object { $_ -match "^$([regex]::Escape($queue))\s" }
    if (-not $line) { return 0 }
    return [int](($line -split "\s+")[1])
}

Write-Host "1. Confirme que notification-service esta DETENIDO antes de continuar (puerto 8086 libre)." -ForegroundColor Yellow
if (Get-NetTCPConnection -LocalPort 8086 -State Listen -ErrorAction SilentlyContinue) {
    throw "notification-service sigue escuchando en 8086. Detengalo y vuelva a ejecutar el script."
}

$antes = Get-QueueDepth "reviewer.assigned.queue"
Write-Host "   Mensajes en reviewer.assigned.queue antes: $antes"

Write-Host "2. Creando pregunta y enviandola a revision por el gateway..." -ForegroundColor Cyan
$pregunta = @{
    text = "Cual es la capital administrativa del departamento del Cauca?"
    options = @(
        @{ id = "A"; text = "Popayan" },
        @{ id = "B"; text = "Cali" },
        @{ id = "C"; text = "Pasto" },
        @{ id = "D"; text = "Neiva" }
    )
    correctOptionId = "A"
    authorId = $AutorId
} | ConvertTo-Json -Depth 4
$creada = Invoke-RestMethod -Method Post -Uri "$Gateway/api/v1/preguntas" -ContentType "application/json; charset=utf-8" -Body ([System.Text.Encoding]::UTF8.GetBytes($pregunta))
$id = $creada.id
Write-Host "   Pregunta creada con id $id"
Invoke-RestMethod -Method Post -Uri "$Gateway/api/v1/preguntas/$id/submit-review?requestedBy=$AutorId" | Out-Null
Start-Sleep -Seconds 2

Write-Host "3. Asignando el revisor $RevisorId con notification-service caido..." -ForegroundColor Cyan
$asignacion = @{ questionId = $id; reviewerIds = @($RevisorId); assignedBy = $AdminId } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri "$Gateway/api/v1/asignaciones" -ContentType "application/json" -Body $asignacion | Out-Null
Start-Sleep -Seconds 1

$durante = Get-QueueDepth "reviewer.assigned.queue"
Write-Host "   Mensajes en reviewer.assigned.queue con el consumidor caido: $durante"
if ($durante -le $antes) { throw "El evento RevisorAsignado no quedo en la cola." }

Write-Host "4. Inicie ahora notification-service (..\..\mvnw.cmd spring-boot:run en notification-service) y presione Enter cuando haya arrancado." -ForegroundColor Yellow
Read-Host | Out-Null
Start-Sleep -Seconds 3

$despues = Get-QueueDepth "reviewer.assigned.queue"
$notificaciones = Invoke-RestMethod -Uri "$Gateway/api/v1/notificaciones/recipient/$RevisorId"
$deEstaPregunta = @($notificaciones | Where-Object { $_.subject -match "#$id\)" })
Write-Host "   Mensajes en la cola despues del reinicio: $despues"
Write-Host "   Notificaciones del revisor $RevisorId para la pregunta ${id}: $($deEstaPregunta.Count)"

if ($despues -eq 0 -and $deEstaPregunta.Count -eq 1) {
    Write-Host "RESULTADO: OK. El evento se conservo en RabbitMQ y se proceso una sola vez al reiniciar el consumidor." -ForegroundColor Green
} else {
    Write-Host "RESULTADO: FALLO. Revise los logs de notification-service y la consola de RabbitMQ (http://localhost:15672)." -ForegroundColor Red
    exit 1
}
