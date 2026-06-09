#!/usr/bin/env bash
# Tests para GET /api/v1/health
source "$(dirname "$0")/common.sh"

echo "━━━ GET /api/v1/health ━━━"

# 1. Health check exitoso
RESP=$(curl -s -w "\n%{http_code}" "$BASE/api/v1/health")
BODY=$(echo "$RESP" | head -n -1)
CODE=$(echo "$RESP" | tail -1)
assert_status "Health check devuelve 200" "200" "$CODE"

# 2. Campo servicio = parcialFinal
assert_status_and_field "Campo servicio" "200" "$CODE" ".servicio" "parcialFinal" "$BODY"

# 3. Campo estado = UP
assert_status_and_field "Campo estado" "200" "$CODE" ".estado" "UP" "$BODY"

# 4. Campo version = v1
assert_status_and_field "Campo version" "200" "$CODE" ".version" "v1" "$BODY"

# 5. Campo timestamp existe
HAS_TS=$(echo "$BODY" | jq 'has("timestamp")' 2>/dev/null)
if [ "$HAS_TS" = "true" ]; then
    echo "  ✅ Campo timestamp presente"
    ((PASS++))
else
    echo "  ❌ Campo timestamp ausente"
    ((FAIL++))
fi

# 6. Sección baseDeDatos con estado UP
assert_status_and_field "Base de datos estado UP" "200" "$CODE" ".baseDeDatos.estado" "UP" "$BODY"

# 7. Base de datos tiene url
HAS_URL=$(echo "$BODY" | jq '.baseDeDatos | has("url")' 2>/dev/null)
if [ "$HAS_URL" = "true" ]; then
    echo "  ✅ Campo baseDeDatos.url presente"
    ((PASS++))
else
    echo "  ❌ Campo baseDeDatos.url ausente"
    ((FAIL++))
fi

# 8. Base de datos tiene driver
HAS_DRIVER=$(echo "$BODY" | jq '.baseDeDatos | has("driver")' 2>/dev/null)
if [ "$HAS_DRIVER" = "true" ]; then
    echo "  ✅ Campo baseDeDatos.driver presente"
    ((PASS++))
else
    echo "  ❌ Campo baseDeDatos.driver ausente"
    ((FAIL++))
fi

print_summary "Health"