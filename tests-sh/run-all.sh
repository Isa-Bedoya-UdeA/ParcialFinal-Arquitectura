#!/usr/bin/env bash
# Runner principal — ejecuta todos los tests de API
# Inicia el backend en Docker, re-seedea la BD, corre los tests y limpia al final
# Uso: bash tests-sh/run-all.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
IMAGE_NAME="parcialfinal"
CONTAINER_NAME="parcialfinal-app"
HEALTH_URL="http://localhost:8080/api/v1/health"
MAX_WAIT=30

# ── Limpiar contenedor previo si existe ──
if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "🛑 Deteniendo contenedor anterior..."
    docker rm -f "$CONTAINER_NAME" > /dev/null 2>&1
fi

# ── Construir imagen si no existe ──
if ! docker image inspect "$IMAGE_NAME" > /dev/null 2>&1; then
    echo "🏗️  Construyendo imagen Docker..."
    docker build -t "$IMAGE_NAME" "$PROJECT_DIR"
fi

# ── Iniciar contenedor ──
echo "🚀 Iniciando contenedor $CONTAINER_NAME..."
docker run --network host --name "$CONTAINER_NAME" -d "$IMAGE_NAME" > /dev/null

# ── Esperar a que el backend esté listo ──
echo "⏳ Esperando al backend..."
elapsed=0
until curl -sf "$HEALTH_URL" > /dev/null 2>&1; do
    sleep 1
    elapsed=$((elapsed + 1))
    if [ "$elapsed" -ge "$MAX_WAIT" ]; then
        echo "❌ El backend no respondió en ${MAX_WAIT}s"
        docker logs "$CONTAINER_NAME" --tail 20
        docker rm -f "$CONTAINER_NAME" > /dev/null 2>&1
        exit 1
    fi
done
echo "✅ Backend listo (esperó ${elapsed}s)"

# ── Re-seedear la BD para tests limpios ──
echo "🗄️  Re-seedeando base de datos..."
psql -U postgres -d colegioudea -c "DELETE FROM notas; SELECT setval('notas_id_seq', 1, false);" > /dev/null 2>&1
psql -U postgres -d colegioudea -f "$PROJECT_DIR/seed.sql" > /dev/null 2>&1
echo "✅ BD re-seedeada"

# ── Ejecutar tests ──
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
    docker rm -f "$CONTAINER_NAME" > /dev/null 2>&1
    exit 0
else
    echo "  ⚠️  Hubo $TOTAL_FAIL prueba(s) fallida(s)"
    docker rm -f "$CONTAINER_NAME" > /dev/null 2>&1
    exit 1
fi