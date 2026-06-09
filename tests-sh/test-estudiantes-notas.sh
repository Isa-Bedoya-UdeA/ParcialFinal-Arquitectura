#!/usr/bin/env bash
# Tests para GET /api/v1/estudiantes/{cedula}/notas
source "$(dirname "$0")/common.sh"

echo "━━━ GET /api/v1/estudiantes/{cedula}/notas ━━━"

# 1. Estudiante existente con varias notas (María González, cédula 1001234567)
RESP=$(curl -s -w "\n%{http_code}" "$BASE/api/v1/estudiantes/1001234567/notas")
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Estudiante existente devuelve 200" "200" "$CODE"
assert_status_and_field "Cédula correcta" "200" "$CODE" ".cedula" "1001234567" "$BODY"
assert_status_and_field "nombreCompleto correcto" "200" "$CODE" ".nombreCompleto" "María González" "$BODY"
assert_status_and_field "programa correcto" "200" "$CODE" ".programa" "Ingeniería de Sistemas" "$BODY"

NOTAS_COUNT=$(echo "$BODY" | jq '.notas | length' 2>/dev/null)
if [ "$NOTAS_COUNT" -ge "3" ]; then
    echo "  ✅ María tiene $NOTAS_COUNT notas (esperado ≥3)"
    ((PASS++))
else
    echo "  ❌ María tiene $NOTAS_COUNT notas (esperado ≥3)"
    ((FAIL++))
fi

FIRST_NOTA_MATERIA=$(echo "$BODY" | jq -r '.notas[0].materia.codigo' 2>/dev/null)
if [ -n "$FIRST_NOTA_MATERIA" ]; then
    echo "  ✅ Notas incluyen objeto materia (codigo=$FIRST_NOTA_MATERIA)"
    ((PASS++))
else
    echo "  ❌ Notas no incluyen objeto materia"
    ((FAIL++))
fi

# 2. Estudiante con pocas notas (Camila Torres, cédula 1007890123 → 1 nota)
RESP=$(curl -s -w "\n%{http_code}" "$BASE/api/v1/estudiantes/1007890123/notas")
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Estudiante con 1 nota devuelve 200" "200" "$CODE"
assert_status_and_field "Cédula Camila" "200" "$CODE" ".cedula" "1007890123" "$BODY"
assert_status_and_field "nombreCompleto Camila" "200" "$CODE" ".nombreCompleto" "Camila Torres" "$BODY"

NOTAS_COUNT=$(echo "$BODY" | jq '.notas | length' 2>/dev/null)
if [ "$NOTAS_COUNT" = "1" ]; then
    echo "  ✅ Camila tiene exactamente 1 nota"
    ((PASS++))
else
    echo "  ❌ Camila tiene $NOTAS_COUNT notas (esperado 1)"
    ((FAIL++))
fi

# 3. Cédula que no existe → 404 RecursoNoEncontradoException
RESP=$(curl -s -w "\n%{http_code}" "$BASE/api/v1/estudiantes/9999999999/notas")
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Cédula inexistente devuelve 404" "404" "$CODE"
assert_status_and_contains "Mensaje: No existe un estudiante" "404" "$CODE" "No existe un estudiante" "$BODY"
assert_status_and_field "Error status 404" "404" "$CODE" ".status" "404" "$BODY"
assert_status_and_field "Error type Not Found" "404" "$CODE" ".error" "Not Found" "$BODY"
assert_status_and_field "Error path correcto" "404" "$CODE" ".path" "/api/v1/estudiantes/9999999999/notas" "$BODY"

HAS_TIMESTAMP=$(echo "$BODY" | jq 'has("timestamp")' 2>/dev/null)
if [ "$HAS_TIMESTAMP" = "true" ]; then
    echo "  ✅ Error 404 incluye timestamp"
    ((PASS++))
else
    echo "  ❌ Error 404 no incluye timestamp"
    ((FAIL++))
fi

# 4. Otra cédula inexistente (formate distinto)
RESP=$(curl -s -w "\n%{http_code}" "$BASE/api/v1/estudiantes/0/notas")
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Cédula '0' devuelve 404" "404" "$CODE"

# 5. Verificar estructura completa de notas (valores y campos)
RESP=$(curl -s -w "\n%{http_code}" "$BASE/api/v1/estudiantes/1001234567/notas")
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)

HAS_ID=$(echo "$BODY" | jq '.notas[0] | has("id")' 2>/dev/null)
HAS_VALOR=$(echo "$BODY" | jq '.notas[0] | has("valor")' 2>/dev/null)
HAS_PERIODO=$(echo "$BODY" | jq '.notas[0] | has("periodo")' 2>/dev/null)
HAS_FECHA=$(echo "$BODY" | jq '.notas[0] | has("fechaRegistro")' 2>/dev/null)
HAS_MATERIA=$(echo "$BODY" | jq '.notas[0] | has("materia")' 2>/dev/null)

if [ "$HAS_ID" = "true" ] && [ "$HAS_VALOR" = "true" ] && [ "$HAS_PERIODO" = "true" ] && [ "$HAS_FECHA" = "true" ] && [ "$HAS_MATERIA" = "true" ]; then
    echo "  ✅ Notas tienen todos los campos (id, valor, periodo, fechaRegistro, materia)"
    ((PASS++))
else
    echo "  ❌ Notas no tienen todos los campos"
    ((FAIL++))
fi

print_summary "Estudiantes/Notas"