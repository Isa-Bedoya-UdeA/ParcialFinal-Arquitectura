# Evidencia de Pruebas de Integración — API ParcialFinal

## Infraestructura

- **Backend**: Spring Boot 3.5.14 corriendo en Docker (`parcialfinal-app`, `--network host`, puerto 8080)
- **Base de datos**: PostgreSQL 18.4, base de datos `colegioudea`
- **Seed**: 8 estudiantes, 6 materias, 16 notas (periodo `2026-1`)
- **Orquestador**: `tests-sh/run-all.sh` — construye la imagen, levanta el contenedor, espera health check, re-seedea la BD, ejecuta los tests y limpia el contenedor.

## Resultado Global

| Total asserts | Pasadas | Fallidas |
|---------------|---------|----------|
| 72            | 72      | 0        |

---

## 1. Health Check — `test-health.sh`

**Endpoint:** `GET /api/v1/health`

No requiere body.

### Casos probados (8 asserts)

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Health check devuelve 200 | ✅ |
| 2 | Campo `servicio` = `parcialFinal` | ✅ |
| 3 | Campo `estado` = `UP` | ✅ |
| 4 | Campo `version` = `v1` | ✅ |
| 5 | Campo `timestamp` presente | ✅ |
| 6 | `baseDeDatos.estado` = `UP` | ✅ |
| 7 | Campo `baseDeDatos.url` presente | ✅ |
| 8 | Campo `baseDeDatos.driver` presente | ✅ |

### Respuesta esperada

```json
{
  "servicio": "parcialFinal",
  "estado": "UP",
  "version": "v1",
  "timestamp": "2026-06-09T22:54:12.254392851",
  "baseDeDatos": {
    "estado": "UP",
    "url": "jdbc:postgresql://localhost:5432/colegioudea",
    "driver": "PostgreSQL JDBC Driver"
  }
}
```

---

## 2. Consultar Notas de un Estudiante — `test-estudiantes-notas.sh`

**Endpoint:** `GET /api/v1/estudiantes/{cedula}/notas`

No requiere body.

### Casos probados (18 asserts)

| # | Descripción | Cédula | Status | Resultado |
|---|-------------|--------|--------|-----------|
| 1 | Estudiante existente devuelve 200 | `1001234567` | 200 | ✅ |
| 2 | Cédula correcta en response | `1001234567` | 200 | ✅ |
| 3 | `nombreCompleto` = "María González" | `1001234567` | 200 | ✅ |
| 4 | `programa` = "Ingeniería de Sistemas" | `1001234567` | 200 | ✅ |
| 5 | María tiene ≥ 3 notas | `1001234567` | 200 | ✅ |
| 6 | Notas incluyen objeto `materia` con `codigo` | `1001234567` | 200 | ✅ |
| 7 | Estudiante con 1 nota devuelve 200 | `1007890123` | 200 | ✅ |
| 8 | Cédula Camila correcta | `1007890123` | 200 | ✅ |
| 9 | `nombreCompleto` = "Camila Torres" | `1007890123` | 200 | ✅ |
| 10 | Camila tiene exactamente 1 nota | `1007890123` | 200 | ✅ |
| 11 | Cédula inexistente devuelve 404 | `9999999999` | 404 | ✅ |
| 12 | Mensaje contiene "No existe un estudiante" | `9999999999` | 404 | ✅ |
| 13 | Error `.status` = 404 | `9999999999` | 404 | ✅ |
| 14 | Error `.error` = "Not Found" | `9999999999` | 404 | ✅ |
| 15 | Error `.path` correcto | `9999999999` | 404 | ✅ |
| 16 | Error 404 incluye `timestamp` | `9999999999` | 404 | ✅ |
| 17 | Cédula "0" devuelve 404 | `0` | 404 | ✅ |
| 18 | Notas tienen campos: id, valor, periodo, fechaRegistro, materia | `1001234567` | 200 | ✅ |

### Respuesta esperada — Estudiante con notas

`GET /api/v1/estudiantes/1001234567/notas`

```json
{
  "cedula": "1001234567",
  "nombreCompleto": "María González",
  "programa": "Ingeniería de Sistemas",
  "notas": [
    {
      "id": 1,
      "valor": 4.6,
      "periodo": "2026-1",
      "fechaRegistro": "2026-02-15",
      "observaciones": "Excelente desempeño",
      "materia": {
        "codigo": "ING303",
        "nombre": "Arquitectura de Software",
        "creditos": 4
      }
    },
    {
      "id": 2,
      "valor": 4.25,
      "periodo": "2026-1",
      "fechaRegistro": "2026-02-16",
      "observaciones": null,
      "materia": {
        "codigo": "ING201",
        "nombre": "Bases de Datos",
        "creditos": 3
      }
    },
    {
      "id": 3,
      "valor": 3.9,
      "periodo": "2026-1",
      "fechaRegistro": "2026-02-17",
      "observaciones": null,
      "materia": {
        "codigo": "ING305",
        "nombre": "Ingeniería de Requisitos",
        "creditos": 3
      }
    }
  ]
}
```

### Respuesta esperada — Estudiante inexistente

`GET /api/v1/estudiantes/9999999999/notas`

```json
{
  "timestamp": "2026-06-09T22:55:00.000000000",
  "status": 404,
  "error": "Not Found",
  "mensaje": "No existe un estudiante con cédula 9999999999",
  "path": "/api/v1/estudiantes/9999999999/notas"
}
```

---

## 3. Registrar Nota — Casos de Negocio — `test-notas.sh`

**Endpoint:** `POST /api/v1/notas`

### 3.1 Crear nota válida → 201

**URL:** `POST /api/v1/notas`

```json
{
  "cedulaEstudiante": "1001234567",
  "codigoMateria": "ING401",
  "valor": 4.2,
  "periodo": "2025-2",
  "observaciones": "Nota de prueba auto"
}
```

**Respuesta (201):**

```json
{
  "id": 17,
  "valor": 4.2,
  "periodo": "2025-2",
  "fechaRegistro": "2026-06-09",
  "observaciones": "Nota de prueba auto",
  "materia": {
    "codigo": "ING401",
    "nombre": "Inteligencia Artificial",
    "creditos": 4
  }
}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 201 | ✅ |
| 2 | `.valor` = 4.2 | ✅ |
| 3 | `.periodo` = "2025-2" | ✅ |
| 4 | `.materia.codigo` = "ING401" | ✅ |
| 5 | `.observaciones` = "Nota de prueba auto" | ✅ |
| 6 | Campos `id` y `fechaRegistro` presentes | ✅ |

### 3.2 Nota duplicada → 400

**URL:** `POST /api/v1/notas`

```json
{
  "cedulaEstudiante": "1001234567",
  "codigoMateria": "ING401",
  "valor": 3.5,
  "periodo": "2025-2",
  "observaciones": "Duplicada"
}
```

**Respuesta (400):**

```json
{
  "timestamp": "2026-06-09T22:56:00.000000000",
  "status": 400,
  "error": "Bad Request",
  "mensaje": "Ya existe una nota para el estudiante 1001234567 en la materia ING401 para el periodo 2025-2",
  "path": "/api/v1/notas"
}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 400 | ✅ |
| 2 | Mensaje contiene "Ya existe una nota" | ✅ |
| 3 | `.status` = 400 | ✅ |
| 4 | `.error` = "Bad Request" | ✅ |
| 5 | `.path` = "/api/v1/notas" | ✅ |

### 3.3 Estudiante inexistente → 404

**URL:** `POST /api/v1/notas`

```json
{
  "cedulaEstudiante": "0000000000",
  "codigoMateria": "ING303",
  "valor": 4.0,
  "periodo": "2026-2"
}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 404 | ✅ |
| 2 | Mensaje contiene "No existe un estudiante" | ✅ |
| 3 | `.status` = 404 | ✅ |

### 3.4 Materia inexistente → 404

**URL:** `POST /api/v1/notas`

```json
{
  "cedulaEstudiante": "1001234567",
  "codigoMateria": "FAKE999",
  "valor": 4.0,
  "periodo": "2026-2"
}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 404 | ✅ |
| 2 | Mensaje contiene "No existe una materia" | ✅ |

### 3.5 Nota con observaciones null → 201

**URL:** `POST /api/v1/notas`

```json
{
  "cedulaEstudiante": "1002345678",
  "codigoMateria": "ING201",
  "valor": 3.8,
  "periodo": "2025-2",
  "observaciones": null
}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 201 | ✅ |
| 2 | `.observaciones` es null | ✅ |

### 3.6 Nota con valor 0.0 (límite inferior) → 201

**URL:** `POST /api/v1/notas`

```json
{
  "cedulaEstudiante": "1002345678",
  "codigoMateria": "ING305",
  "valor": 0.0,
  "periodo": "2025-2"
}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 201 | ✅ |
| 2 | `.valor` = 0.0 | ✅ |

### 3.7 Nota con valor 5.0 (límite superior) → 201

**URL:** `POST /api/v1/notas`

```json
{
  "cedulaEstudiante": "1003456789",
  "codigoMateria": "ING302",
  "valor": 5.0,
  "periodo": "2025-2"
}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 201 | ✅ |
| 2 | `.valor` = 5.0 | ✅ |

**Subtotal Negocio: 22 pasadas, 0 fallidas**

---

## 4. Validaciones @Valid — `test-notas.sh`

**Endpoint:** `POST /api/v1/notas`

Todas las solicitudes siguientes retornan status **400** con cuerpo de error con estructura:

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "mensaje": "Error de validación",
  "path": "/api/v1/notas",
  "detalles": [
    { "campo": "...", "mensaje": "..." }
  ]
}
```

### V1. Cédula vacía → 400

**URL:** `POST /api/v1/notas`

```json
{
  "cedulaEstudiante": "",
  "codigoMateria": "ING303",
  "valor": 4.0,
  "periodo": "2026-1"
}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 400 | ✅ |
| 2 | Mensaje contiene "validación" | ✅ |
| 3 | `detalles[0].campo` = "cedulaEstudiante" | ✅ |
| 4 | `.status` = 400 | ✅ |

### V2. Código materia vacío → 400

**URL:** `POST /api/v1/notas`

```json
{
  "cedulaEstudiante": "1001234567",
  "codigoMateria": "",
  "valor": 4.0,
  "periodo": "2026-1"
}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 400 | ✅ |
| 2 | Mensaje contiene "validación" | ✅ |
| 3 | `detalles[0].campo` = "codigoMateria" | ✅ |

### V3. Valor > 5.0 → 400

**URL:** `POST /api/v1/notas`

```json
{
  "cedulaEstudiante": "1001234567",
  "codigoMateria": "ING303",
  "valor": 6.0,
  "periodo": "2026-1"
}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 400 | ✅ |
| 2 | Mensaje contiene "nota máxima" | ✅ |
| 3 | `detalles[0].campo` = "valor" | ✅ |

### V4. Valor < 0.0 → 400

**URL:** `POST /api/v1/notas`

```json
{
  "cedulaEstudiante": "1001234567",
  "codigoMateria": "ING303",
  "valor": -1.0,
  "periodo": "2026-1"
}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 400 | ✅ |
| 2 | Mensaje contiene "nota mínima" | ✅ |
| 3 | `detalles[0].campo` = "valor" | ✅ |

### V5. Valor null (campo omitido) → 400

**URL:** `POST /api/v1/notas`

```json
{
  "cedulaEstudiante": "1001234567",
  "codigoMateria": "ING303",
  "periodo": "2026-1"
}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 400 | ✅ |
| 2 | Mensaje contiene "obligatorio" | ✅ |
| 3 | `detalles[0].campo` = "valor" | ✅ |

### V6. Periodo vacío → 400

**URL:** `POST /api/v1/notas`

```json
{
  "cedulaEstudiante": "1001234567",
  "codigoMateria": "ING303",
  "valor": 4.0,
  "periodo": ""
}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 400 | ✅ |
| 2 | Mensaje contiene "obligatorio" | ✅ |
| 3 | `detalles[0].campo` = "periodo" | ✅ |

### V7. Body vacío → 400 (múltiples errores)

**URL:** `POST /api/v1/notas`

```json
{}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 400 | ✅ |
| 2 | Reporta ≥ 3 errores de validación (4 encontrados) | ✅ |

### V8. Múltiples errores simultáneos → 400

**URL:** `POST /api/v1/notas`

```json
{
  "cedulaEstudiante": "",
  "codigoMateria": "",
  "valor": -5.0,
  "periodo": ""
}
```

| # | Assert | Resultado |
|---|--------|-----------|
| 1 | Status 400 | ✅ |
| 2 | Mensaje contiene "validación" | ✅ |
| 3 | Reporta ≥ 4 campos con error (4 encontrados) | ✅ |

**Subtotal Validaciones: 46 pasadas, 0 fallidas**

---

## Resumen por Suite

| Suite | Pasadas | Fallidas |
|-------|---------|----------|
| Health | 8 | 0 |
| Estudiantes/Notas | 18 | 0 |
| Notas — Negocio | 22 | 0 |
| Notas — Validaciones | 46 | 0 |
| **Total** | **72** | **0** |