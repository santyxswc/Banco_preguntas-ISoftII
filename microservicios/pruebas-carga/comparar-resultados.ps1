$aqui = Split-Path -Parent $MyInvocation.MyCommand.Path
$uno = Get-Content (Join-Path $aqui "resultados/resumen-1-instancia.json") -Raw | ConvertFrom-Json
$dos = Get-Content (Join-Path $aqui "resultados/resumen-2-instancias.json") -Raw | ConvertFrom-Json

function f($n) { if ($null -eq $n) { "n/d" } else { "{0:N1}" -f $n } }

$mejora = if ($uno.latenciaMs.p95 -gt 0) { (1 - $dos.latenciaMs.p95 / $uno.latenciaMs.p95) * 100 } else { $null }

$tabla = @"
# Resultados del escenario de escalabilidad

| Métrica | 1 instancia | 2 instancias |
|---|---|---|
| Usuarios concurrentes | $($uno.usuariosConcurrentes) | $($dos.usuariosConcurrentes) |
| Peticiones totales | $($uno.peticiones) | $($dos.peticiones) |
| Peticiones por segundo | $(f $uno.peticionesPorSegundo) | $(f $dos.peticionesPorSegundo) |
| Tasa de errores (%) | $(f ($uno.tasaErrores * 100)) | $(f ($dos.tasaErrores * 100)) |
| Latencia p95 (ms) | $(f $uno.latenciaMs.p95) | $(f $dos.latenciaMs.p95) |
| Latencia p99 (ms) | $(f $uno.latenciaMs.p99) | $(f $dos.latenciaMs.p99) |
| p95 crear pregunta (ms) | $(f $uno.p95CrearPreguntaMs) | $(f $dos.p95CrearPreguntaMs) |
| Umbrales cumplidos | $($uno.umbralesCumplidos) | $($dos.umbralesCumplidos) |

Variación de la p95 al pasar a 2 instancias: $(f $mejora) % (positivo = mejora).

Ejecutado: $($uno.fecha) y $($dos.fecha).
"@

$salida = Join-Path $aqui "resultados/comparacion.md"
Set-Content -Path $salida -Value $tabla -Encoding UTF8
Write-Host $tabla
Write-Host "Tabla guardada en $salida" -ForegroundColor Green
