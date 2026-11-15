"""Exporta DOCUMENTO_ARQUITECTURA_CORTE3.md a PDF.

Uso (desde la raíz del repositorio; requiere Python 3 con el paquete `markdown` y Chromium o Chrome):

    python3 arquitectura/exportar-pdf.py DOCUMENTO_ARQUITECTURA_CORTE3.md documento.html
    chromium --headless --no-pdf-header-footer --print-to-pdf=DOCUMENTO_ARQUITECTURA_CORTE3.pdf documento.html
    rm documento.html

El HTML debe quedar en la raíz para que las rutas de las imágenes (arquitectura/diagramas/...) funcionen.
"""
import sys, re, pathlib, markdown
src = pathlib.Path(sys.argv[1]); out = pathlib.Path(sys.argv[2])
text = src.read_text(encoding='utf-8')
# La portada está en un <div align="center"> con Markdown dentro
text = text.replace('<div align="center">', '<div class="portada" markdown="1">', 1)
html = markdown.markdown(text, extensions=['tables', 'fenced_code', 'md_in_html', 'toc', 'sane_lists'],
                         extension_configs={'toc': {'slugify': lambda v, s: re.sub(r'[^\w\- ]', '', v.lower(), flags=re.U).strip().replace(' ', '-')}})
css = '''
@page { size: Letter; margin: 2.2cm 2.2cm 2.4cm 2.2cm; }
body { font-family: "Liberation Serif", "Times New Roman", serif; font-size: 11.5pt; line-height: 1.45; color: #111; }
h1, h2, h3 { font-family: "Liberation Sans", Arial, sans-serif; color: #0b2e59; page-break-after: avoid; }
h1 { font-size: 20pt; } h2 { font-size: 15pt; margin-top: 1.4em; border-bottom: 1px solid #c7d2e0; padding-bottom: 3px; }
h3 { font-size: 12.5pt; margin-top: 1.1em; }
.portada { text-align: center; page-break-after: always; padding-top: 3.2cm; font-size: 13pt; }
.portada h1 { font-size: 24pt; border: none; margin-bottom: 1.2cm; }
.portada p { margin: 1cm 0; }
table { border-collapse: collapse; width: 100%; margin: .6em 0 1em; font-size: 9.2pt; page-break-inside: auto; font-family: "Liberation Sans", Arial, sans-serif; }
tr { page-break-inside: avoid; }
th { background: #0b2e59; color: #fff; text-align: left; }
th, td { border: 1px solid #9aa7b8; padding: 4px 6px; vertical-align: top; word-break: break-word; }
tr:nth-child(even) td { background: #f3f6fa; }
code { font-family: "Liberation Mono", monospace; font-size: 8.8pt; background: #eef1f5; padding: 0 2px; border-radius: 2px; word-break: break-all; }
pre { background: #f4f6f8; border: 1px solid #d5dbe3; padding: 8px; font-size: 7.4pt; line-height: 1.3; white-space: pre-wrap; word-break: break-all; page-break-inside: avoid; }
pre code { background: none; font-size: inherit; padding: 0; }
img { max-width: 100%; max-height: 21cm; display: block; margin: .8em auto .2em; }
p > em:only-child { display: block; text-align: center; font-size: 9.5pt; color: #333; }
blockquote { border-left: 3px solid #0b2e59; margin: .8em 0; padding: .2em .9em; background: #f3f6fa; font-size: 10.5pt; }
a { color: #0b4884; text-decoration: none; }
hr { border: none; border-top: 1px solid #c7d2e0; margin: 1.2em 0; }
'''
out.write_text(f'<!doctype html><html lang="es"><head><meta charset="utf-8"><title>Documento de Arquitectura — Banco de Preguntas Saber Pro (Corte 3)</title><style>{css}</style></head><body>{html}</body></html>', encoding='utf-8')
