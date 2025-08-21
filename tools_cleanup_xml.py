import os, sys

ROOT = "."
fixed = []

def should_skip(path: str) -> bool:
    # No tocar git/build/.gradle
    parts = path.replace("\\", "/")
    return ("/.git/" in parts) or ("/build/" in parts) or ("/.gradle/" in parts)

for dirpath, _, filenames in os.walk(ROOT):
    for fn in filenames:
        if not fn.lower().endswith(".xml"):
            continue
        path = os.path.join(dirpath, fn)
        if should_skip(path):
            continue

        with open(path, "rb") as f:
            data = f.read()

        orig = data

        # 1) Quitar BOM UTF-8 si existe
        if data.startswith(b"\xef\xbb\xbf"):
            data = data[3:]

        # 2) Quitar espacios/saltos antes del primer no-blanco
        i = 0
        while i < len(data) and data[i:i+1] in b" \t\r\n":
            i += 1
        if i:
            data = data[i:]

        # 3) Asegurar salto de línea final
        if not data.endswith(b"\n"):
            data += b"\n"

        if data != orig:
            with open(path, "wb") as f:
                f.write(data)
            fixed.append(path)

if fixed:
    print(f"Limpiados {len(fixed)} archivo(s):")
    for p in fixed:
        print(" -", p)
else:
    print("No hacía falta limpiar nada.")
