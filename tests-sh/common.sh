#!/usr/bin/env bash
# Funciones compartidas para los tests de API
# Se importa con: source tests-sh/common.sh

BASE="${BASE:-http://localhost:8080}"
PASS=0
FAIL=0

reset_counts() {
    PASS=0
    FAIL=0
}

assert_status() {
    local test_name="$1" expected="$2" actual="$3"
    if [ "$actual" -eq "$expected" ]; then
        echo "  ✅ $test_name (HTTP $actual)"
        ((PASS++))
    else
        echo "  ❌ $test_name — esperado $expected, recibido $actual"
        ((FAIL++))
    fi
}

assert_status_and_field() {
    local test_name="$1" expected_status="$2" actual_status="$3" field="$4" expected_val="$5" body="$6"
    local actual_val
    actual_val=$(echo "$body" | jq -r "$field" 2>/dev/null)
    if [ "$actual_status" -eq "$expected_status" ] && [ "$actual_val" = "$expected_val" ]; then
        echo "  ✅ $test_name (HTTP $actual_status, $field=$actual_val)"
        ((PASS++))
    else
        echo "  ❌ $test_name — esperado HTTP $expected_status con $field=$expected_val, recibido HTTP $actual_status con $field=$actual_val"
        ((FAIL++))
    fi
}

assert_status_and_contains() {
    local test_name="$1" expected_status="$2" actual_status="$3" substring="$4" body="$5"
    local found=false
    if echo "$body" | jq -r '.mensaje' 2>/dev/null | grep -qi "$substring"; then
        found=true
    fi
    if echo "$body" | jq -r '.detalles[]?.mensaje' 2>/dev/null | grep -qi "$substring"; then
        found=true
    fi
    if [ "$actual_status" -eq "$expected_status" ] && [ "$found" = true ]; then
        echo "  ✅ $test_name (HTTP $actual_status, contiene '$substring')"
        ((PASS++))
    else
        echo "  ❌ $test_name — esperado HTTP $expected_status con texto que contenga '$substring'"
        echo "     Recibido HTTP $actual_status"
        echo "$body" | jq . 2>/dev/null || echo "$body"
        ((FAIL++))
    fi
}

print_summary() {
    local name="$1"
    echo ""
    echo "  ── $name: $PASS pasadas, $FAIL fallidas ──"
    echo ""
}

total_summary() {
    echo "========================================="
    TOTAL=$((PASS + FAIL))
    echo "  Total: $TOTAL  |  ✅ Pasadas: $PASS  |  ❌ Fallidas: $FAIL"
    echo "========================================="
    if [ "$FAIL" -eq 0 ]; then
        echo "  🎉 ¡Todas las pruebas pasaron!"
        exit 0
    else
        echo "  ⚠️  Hubo $FAIL prueba(s) fallida(s)"
        exit 1
    fi
}