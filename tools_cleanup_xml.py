import os, sys

ROOT = "."
changed_any = False
fixed = []

for dirpath, _, filenames in os.walk(ROOT):
    for fn in filenames:
        if not fn.lower().endswith(".xml"):
            continue
        path = os.path.join(dirpath, fn)

        # Evita limpiar dependencias o build outputs
        if any(skip in path for skip in ("/.git/", "/build/", "/.gradle/")):
            continue

        with open(path, "rb") as f:
            data = f.read()

        orig = data

        # 1) Quita BOM UTF-8 si existe
        if data.startswith(b"\xef\xbb\xbf"):
            data = data[3:]

        # 2) Quita espacios/saltos invisibles antes del primer caracter no-blanco
        #    (evita el famoso "Content is not allowed in prolog")
        i = 0
        while i < len(data) and data[i:i+1] in b" \t\r\n":
            i += 1
        if i:
            data = data[i:]

        # 3) Garantiza salto de línea al final del archivo
        if not data.endswith(b"\n"):
            data += b"\n"

        if data != orig:
            with open(path, "wb") as f:
                f.write(data)
            changed_any = True
            fixed.append(path)

# Reporte
if fixed:
    print("Limpiados {} archivo(s):".format(len(fixed)))
    for p in fixed:
        print(" -", p)
else:
    print("No hacía falta limpiar nada.")

sys.exit(0)
