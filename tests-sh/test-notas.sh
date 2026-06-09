#!/usr/bin/env bash
# Tests para POST /api/v1/notas (casos de negocio + validaciones @Valid)
source "$(dirname "$0")/common.sh"

echo "━━━ POST /api/v1/notas — Casos de negocio ━━━"

# 1. Crear nota exitosa
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaEstudiante": "1001234567",
    "codigoMateria": "ING401",
    "valor": 4.2,
    "periodo": "2025-2",
    "observaciones": "Nota de prueba auto"
  }')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Crear nota válida devuelve 201" "201" "$CODE"
assert_status_and_field "Nota creada: valor" "201" "$CODE" ".valor" "4.2" "$BODY"
assert_status_and_field "Nota creada: periodo" "201" "$CODE" ".periodo" "2025-2" "$BODY"
assert_status_and_field "Nota creada: materia.codigo" "201" "$CODE" ".materia.codigo" "ING401" "$BODY"
assert_status_and_field "Nota creada: observaciones" "201" "$CODE" ".observaciones" "Nota de prueba auto" "$BODY"

HAS_ID=$(echo "$BODY" | jq 'has("id")' 2>/dev/null)
HAS_FECHA=$(echo "$BODY" | jq 'has("fechaRegistro")' 2>/dev/null)
if [ "$HAS_ID" = "true" ] && [ "$HAS_FECHA" = "true" ]; then
    echo "  ✅ Nota creada incluye id y fechaRegistro"
    ((PASS++))
else
    echo "  ❌ Nota creada no incluye id o fechaRegistro"
    ((FAIL++))
fi

# 2. Crear nota duplicada → 400 ReglaNegocioException
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaEstudiante": "1001234567",
    "codigoMateria": "ING401",
    "valor": 3.5,
    "periodo": "2025-2",
    "observaciones": "Duplicada"
  }')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Nota duplicada devuelve 400" "400" "$CODE"
assert_status_and_contains "Mensaje: Ya existe una nota" "400" "$CODE" "Ya existe una nota" "$BODY"
assert_status_and_field "Error status 400" "400" "$CODE" ".status" "400" "$BODY"
assert_status_and_field "Error type Bad Request" "400" "$CODE" ".error" "Bad Request" "$BODY"
assert_status_and_field "Error path /api/v1/notas" "400" "$CODE" ".path" "/api/v1/notas" "$BODY"

# 3. Estudiante que no existe → 404
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaEstudiante": "0000000000",
    "codigoMateria": "ING303",
    "valor": 4.0,
    "periodo": "2026-2"
  }')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Estudiante inexistente devuelve 404" "404" "$CODE"
assert_status_and_contains "Mensaje: No existe un estudiante" "404" "$CODE" "No existe un estudiante" "$BODY"
assert_status_and_field "Error status 404" "404" "$CODE" ".status" "404" "$BODY"

# 4. Materia que no existe → 404
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaEstudiante": "1001234567",
    "codigoMateria": "FAKE999",
    "valor": 4.0,
    "periodo": "2026-2"
  }')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Materia inexistente devuelve 404" "404" "$CODE"
assert_status_and_contains "Mensaje: No existe una materia" "404" "$CODE" "No existe una materia" "$BODY"

# 5. Crear nota con observaciones null → 201 (campo opcional)
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaEstudiante": "1002345678",
    "codigoMateria": "ING201",
    "valor": 3.8,
    "periodo": "2025-2",
    "observaciones": null
  }')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Crear nota con observaciones null → 201" "201" "$CODE"
OBS=$(echo "$BODY" | jq -r '.observaciones' 2>/dev/null)
if [ "$OBS" = "null" ]; then
    echo "  ✅ Observaciones es null como se esperaba"
    ((PASS++))
else
    echo "  ❌ Observaciones no es null: $OBS"
    ((FAIL++))
fi

# 6. Crear nota en el límite inferior (0.0)
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaEstudiante": "1002345678",
    "codigoMateria": "ING305",
    "valor": 0.0,
    "periodo": "2025-2"
  }')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Nota con valor 0.0 → 201" "201" "$CODE"
assert_status_and_field "Valor guardado 0.0" "201" "$CODE" ".valor" "0.0" "$BODY"

# 7. Crear nota en el límite superior (5.0)
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaEstudiante": "1003456789",
    "codigoMateria": "ING302",
    "valor": 5.0,
    "periodo": "2025-2"
  }')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Nota con valor 5.0 → 201" "201" "$CODE"
assert_status_and_field "Valor guardado 5.0" "201" "$CODE" ".valor" "5.0" "$BODY"

print_summary "Notas — Negocio"

# =============================================
# Validaciones @Valid
# =============================================
echo "━━━ POST /api/v1/notas — Validaciones @Valid ━━━"

# V1. Cédula vacía → 400
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaEstudiante": "",
    "codigoMateria": "ING303",
    "valor": 4.0,
    "periodo": "2026-1"
  }')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Cédula vacía → 400" "400" "$CODE"
assert_status_and_contains "Mensaje contiene validación" "400" "$CODE" "validación" "$BODY"
assert_status_and_field "Campo cedulaEstudiante en detalles" "400" "$CODE" ".detalles[0].campo" "cedulaEstudiante" "$BODY"
assert_status_and_field "Error status 400" "400" "$CODE" ".status" "400" "$BODY"

# V2. Código materia vacío → 400
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaEstudiante": "1001234567",
    "codigoMateria": "",
    "valor": 4.0,
    "periodo": "2026-1"
  }')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Código materia vacío → 400" "400" "$CODE"
assert_status_and_contains "Mensaje contiene validación" "400" "$CODE" "validación" "$BODY"
assert_status_and_field "Campo codigoMateria en detalles" "400" "$CODE" ".detalles[0].campo" "codigoMateria" "$BODY"

# V3. Nota > 5.0 → 400
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaEstudiante": "1001234567",
    "codigoMateria": "ING303",
    "valor": 6.0,
    "periodo": "2026-1"
  }')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Nota > 5.0 → 400" "400" "$CODE"
assert_status_and_contains "Mensaje contiene nota máxima" "400" "$CODE" "nota máxima" "$BODY"
assert_status_and_field "Campo valor en detalles" "400" "$CODE" ".detalles[0].campo" "valor" "$BODY"

# V4. Nota < 0.0 → 400
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaEstudiante": "1001234567",
    "codigoMateria": "ING303",
    "valor": -1.0,
    "periodo": "2026-1"
  }')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Nota < 0.0 → 400" "400" "$CODE"
assert_status_and_contains "Mensaje contiene nota mínima" "400" "$CODE" "nota mínima" "$BODY"
assert_status_and_field "Campo valor en detalles" "400" "$CODE" ".detalles[0].campo" "valor" "$BODY"

# V5. Nota null (campo omitido) → 400
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaEstudiante": "1001234567",
    "codigoMateria": "ING303",
    "periodo": "2026-1"
  }')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Nota null → 400" "400" "$CODE"
assert_status_and_contains "Mensaje contiene obligatorio" "400" "$CODE" "obligatorio" "$BODY"
assert_status_and_field "Campo valor en detalles" "400" "$CODE" ".detalles[0].campo" "valor" "$BODY"

# V6. Periodo vacío → 400
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaEstudiante": "1001234567",
    "codigoMateria": "ING303",
    "valor": 4.0,
    "periodo": ""
  }')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Periodo vacío → 400" "400" "$CODE"
assert_status_and_contains "Mensaje contiene obligatorio" "400" "$CODE" "obligatorio" "$BODY"
assert_status_and_field "Campo periodo en detalles" "400" "$CODE" ".detalles[0].campo" "periodo" "$BODY"

# V7. Body vacío (todos los requeridos faltan) → 400 con múltiples errores
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{}')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Body vacío → 400" "400" "$CODE"
ERROR_COUNT=$(echo "$BODY" | jq '.detalles | length' 2>/dev/null)
if [ "$ERROR_COUNT" -ge "3" ]; then
    echo "  ✅ Body vacío reporta ≥3 errores de validación ($ERROR_COUNT)"
    ((PASS++))
else
    echo "  ❌ Body vacío reporta $ERROR_COUNT errores (esperado ≥3)"
    ((FAIL++))
fi

# V8. Múltiples errores a la vez → 400 con varios detalles
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/v1/notas" \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaEstudiante": "",
    "codigoMateria": "",
    "valor": -5.0,
    "periodo": ""
  }')
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Múltiples errores → 400" "400" "$CODE"
assert_status_and_contains "Mensaje indica múltiples errores" "400" "$CODE" "validación" "$BODY"
ERROR_COUNT=$(echo "$BODY" | jq '.detalles | length' 2>/dev/null)
if [ "$ERROR_COUNT" -ge "4" ]; then
    echo "  ✅ Reporta ≥4 campos con error ($ERROR_COUNT)"
    ((PASS++))
else
    echo "  ❌ Reporta $ERROR_COUNT errores (esperado ≥4)"
    ((FAIL++))
fi

print_summary "Notas — Validaciones"