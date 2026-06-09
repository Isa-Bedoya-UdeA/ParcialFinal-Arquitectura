#!/usr/bin/env bash
# Runner principal — ejecuta todos los tests de API
# Uso: bash tests-sh/run-all.sh

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

TOTAL_PASS=0
TOTAL_FAIL=0

for script in "$SCRIPT_DIR"/test-*.sh; do
    echo ""
    echo "▶ Ejecutando $(basename "$script")..."
    echo ""
    output=$(bash "$script" 2>&1)
    echo "$output"
    p=$(echo "$output" | grep -oP '✅' | wc -l)
    f=$(echo "$output" | grep -oP '❌' | wc -l)
    TOTAL_PASS=$((TOTAL_PASS + p))
    TOTAL_FAIL=$((TOTAL_FAIL + f))
done

echo ""
echo "========================================="
echo "  RESUMEN GLOBAL"
echo "========================================="
TOTAL=$((TOTAL_PASS + TOTAL_FAIL))
echo "  Total asserts: $TOTAL  |  ✅ Pasadas: $TOTAL_PASS  |  ❌ Fallidas: $TOTAL_FAIL"
echo "========================================="
if [ "$TOTAL_FAIL" -eq 0 ]; then
    echo "  🎉 ¡Todas las pruebas pasaron!"
    exit 0
else
    echo "  ⚠️  Hubo $TOTAL_FAIL prueba(s) fallida(s)"
    exit 1
fi