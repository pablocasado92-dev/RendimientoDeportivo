# Aplicación de Gestión y Análisis de Rendimiento Deportivo (2.º DAM)

Aplicación de escritorio en **Java 25+ / Maven** desarrollada con **Java Swing, FlatLaf, Apache POI, Apache PDFBox y JFreeChart**.

> [!NOTE]
> **Nota sobre el repositorio y el historial de commits:**  
> El desarrollo incremental y los commits de este proyecto se realizaron originalmente en otro repositorio. Debido a que surgieron conflictos de sincronización con el historial existente en dicho repositorio, se decidió crear este repositorio limpio e independiente con la versión completa y funcional subida de una sola vez.

---

## 🚀 Características Principales

1. **Gestión Integral de Deportistas**:
   - Altas, bajas, modificaciones y fichas con fotografía/avatar.
   - Registro de entrenamientos (duración, distancia, FC, RPE 1-10) y pruebas de 5 km.
2. **Motor Matemático y de Carga (Foster / Sesión-RPE)**:
   - Carga semanal agrupada en semanas naturales de lunes a domingo.
   - Media móvil de carga (3 semanas).
   - Índices de monotonía y tensión semanal.
   - Estadísticas descriptivas (Media, Mediana, Mín, Máx, Desviación típica, CV%).
   - Regresión lineal de marcas ($y = a + bx$), coeficiente $R^2$ y proyección a la siguiente semana.
   - Correlación de Pearson ($r$).
3. **Exportación a Microsoft Excel (.xlsx) con Apache POI**:
   - 5 hojas obligatorias: `Resumen`, `Deportistas`, `Entrenamientos`, `Análisis` y `Rendimiento`.
   - Paneles inmovilizados, filtros automáticos, formatos numéricos (`0.00`, `0.0%`), fórmulas nativas (`SUM`, `AVERAGE`), formato condicional y gráfico insertado.
4. **Informe Individual PDF (5 Páginas) con Apache PDFBox 3.x**:
   - Pág 1: Portada con escudo vectorial del club, foto/avatar del atleta y periodo evaluado.
   - Pág 2: Resumen global, texto ejecutivo y 4 tarjetas de KPIs.
   - Pág 3: Tabla semanal de Foster y gráfico de barras de carga semanal (JFreeChart).
   - Pág 4: Historial de tests 5k, recta de regresión y gráfico de evolución (JFreeChart).
   - Pág 5: Interpretación automática de tendencias, control de monotonía y descargo de responsabilidad legal.
5. **Estética Profesional**:
   - Interfaz moderna con **FlatLaf** con selector de tema claro / oscuro en caliente.

---

## 🛠️ Ejecución y Pruebas

- **Compilar y ejecutar las pruebas unitarias (14 tests)**:
  ```powershell
  mvn clean test
  ```
- **Lanzar la aplicación**:
  ```powershell
  mvn exec:java
  ```
  *(O abrir la carpeta en VS Code y pulsar **F5**).*

---

## 📋 Guía de Corrección y Estructura del Proyecto

Esta sección detalla la organización de cada módulo, paquete y funcionalidad para facilitar la evaluación y corrección técnica del proyecto.

### ⚡ Flujo Rápido de Corrección (Paso a Paso)

1. **Verificación de Pruebas Automatizadas**:
   - Ejecutar `mvn clean test`. Se ejecutan y validan las **14 pruebas unitarias** que cubren el motor matemático, exportaciones (Excel y PDF), persistencia e interfaz de usuario.
2. **Arranque de la Aplicación**:
   - Ejecutar `mvn exec:java` o presionar `F5` en VS Code. La ventana se abrirá centrada con el tema visual inicializado en el Event Dispatch Thread (EDT).
3. **Carga Inmediata de Datos Demo**:
   - En la barra de herramientas superior se incluye el botón **"Restablecer Datos Demo"**. Al pulsarlo se cargan automáticamente varios atletas con semanas completas de entrenamientos y marcas históricas de 5 km, permitiendo comprobar todas las funciones, análisis y exportaciones de inmediato sin tener que rellenar formularios a mano.
4. **Navegación por Pestañas**:
   - **Deportistas**: Gestión de altas, bajas, modificaciones y avatar/fotografía.
   - **Entrenamientos y Tests**: Formularios de registro con validaciones de límites y fechas.
   - **Análisis de Rendimiento**: Métricas Foster, monotonía, tensión, regresión y gráficos dinámicos JFreeChart integrados en vivo.
   - **Exportación de Informes**: Generación de archivo Excel (.xlsx) de 5 hojas e informe individual PDF de 5 páginas.
5. **Alternancia de Tema Visual**:
   - El botón **"Alternar Modo Oscuro / Claro"** en la barra superior permite comprobar la actualización dinámica del tema FlatLaf en caliente.

---

### 🏛️ Arquitectura y Organización de Paquetes (`src/main/java/informesExcel/`)

El código sigue una arquitectura por capas desacoplada y orientada a buenas prácticas:

```
src/main/java/informesExcel/
├── App.java                               # Punto de entrada (main) con inicialización segura en EDT
├── modelo/                                # Capa de entidades del dominio
│   ├── Deportista.java                    # Entidad atleta (datos biométricos, cálculo de edad, avatar)
│   ├── SesionEntrenamiento.java           # Sesión de entrenamiento (RPE 1-10, duración, FC, cálculo carga)
│   └── TestRendimiento.java               # Registro de test de 5 km (marca, ritmo min/km, FC máx)
├── servicio/                              # Capa lógica de negocio y cálculo
│   ├── CalculadoraRendimiento.java        # Motor matemático: Foster, monotonía, tensión, regresión lineal, Pearson
│   ├── EstadisticaDescriptiva.java        # Record/DTO con media, mediana, mín, máx, desviación y CV%
│   ├── ResultadoRegresion.java            # Record/DTO con pendiente (b), corte (a), R² y proyección
│   ├── ResumenSemanal.java                # Resumen de carga y métricas agrupadas por semana natural (L-D)
│   ├── GestorPersistencia.java            # Serialización y deserialización JSON con Jackson (JavaTimeModule)
│   ├── GeneradorDatosDemo.java            # Atletas y entrenamientos de prueba listos para corrección
│   ├── GeneradorGraficos.java             # Creación de gráficos JFreeChart (barras de carga, evolución 5k)
│   └── RecursosGraficos.java              # Renderizado vectorial del escudo del club y avatares por defecto
├── gui/                                   # Capa de presentación (Java Swing + FlatLaf)
│   ├── VentanaPrincipal.java              # JFrame principal con JTabbedPane y barra de herramientas
│   ├── PanelDeportistas.java              # Gestión y fichas de atletas con subida de imágenes
│   ├── PanelEntrenamientosTests.java      # Formularios de alta de sesiones y marcas con validaciones
│   ├── PanelAnalisis.java                 # Visualización de métricas, estadísticas y gráficos interactivos
│   ├── PanelExportacion.java              # Exportador a Excel (.xlsx) y generación de PDF (5 páginas)
│   ├── TemaFlatLaf.java                   # Gestión y cambio de tema claro/oscuro en caliente
│   └── ValidadorFormularios.java          # Validaciones de entrada (rangos numéricos, fechas, campos requeridos)
└── exportacion/                           # Capa de generación documental e informes
    ├── ExportadorExcel.java               # Generador de libro Excel (.xlsx) con Apache POI (5 hojas requeridas)
    └── GeneradorInformePDF.java           # Generador de informe PDF formal de 5 páginas con Apache PDFBox 3.x
```

---

### 🔍 Explicación de Cada Módulo del Proyecto

#### 1. Capa de Modelo (`informesExcel.modelo`)
- **`Deportista`**: Gestiona los datos del atleta (ID, nombre, apellidos, fecha de nacimiento con cálculo dinámico de edad en años, peso, altura, especialidad y fotografía/avatar). Mantiene las colecciones vinculadas de entrenamientos y marcas de rendimiento.
- **`SesionEntrenamiento`**: Representa una sesión deportiva (fecha, tipo de actividad, duración en minutos, escala RPE 1-10, frecuencia cardíaca y distancia). Implementa el método para calcular la carga individual de la sesión ($RPE \times Duración$).
- **`TestRendimiento`**: Registra la prueba de 5 km (fecha, marca en minutos y segundos, cálculo automático del ritmo promedio por km y FC máxima alcanzada).

#### 2. Capa de Servicios y Motor Matemático (`informesExcel.servicio`)
- **`CalculadoraRendimiento`**: Núcleo de cálculo científico y deportivo:
  - **Agrupación Semanal**: Agrupa entrenamientos en semanas naturales (lunes a domingo) según el estándar ISO (`WeekFields.ISO`).
  - **Carga Foster**: Sumatorio semanal de las cargas individuales ($RPE \times min$).
  - **Media Móvil de Carga**: Ventana deslizante de 3 semanas para suavizar la tendencia de carga.
  - **Monotonía**: Cociente entre la media de carga diaria y su desviación típica ($\frac{\overline{x}}{\sigma}$). Avisa de riesgo de sobrecarga cuando supera $2.0$.
  - **Tensión (*Strain*)**: Producto de la carga semanal por la monotonía ($Carga \times Monotonía$).
  - **Estadísticas Descriptivas**: Media, mediana, valor mínimo, máximo, desviación típica muestral y coeficiente de variación porcentual ($CV\%$).
  - **Regresión Lineal ($y = a + bx$)**: Ajuste por mínimos cuadrados de las marcas de 5 km, coeficiente de determinación $R^2$, correlación de Pearson ($r$) y proyección del tiempo para el próximo test.
- **`GestorPersistencia`**: Guarda y recupera todos los datos en `datos/club_rendimiento.json` con Jackson Databind y el módulo `jsr310` (`JavaTimeModule`) para serialización de fechas sin pérdida de formato.
- **`GeneradorDatosDemo`**: Genera un conjunto completo de atletas con datos realistas para demostración y evaluación rápida.
- **`GeneradorGraficos` & `RecursosGraficos`**: Genera gráficos con JFreeChart listos para embeber en Swing y exportar a PDF (gráfico de barras de carga semanal y gráfico de dispersión con línea de regresión de 5 km), además del escudo vectorial del club.

#### 3. Capa de Interfaz Gráfica (`informesExcel.gui`)
- **`VentanaPrincipal`**: Ventana principal `JFrame` que organiza la aplicación mediante un `JTabbedPane`. Contiene la barra superior para guardar datos, cargar demo y alternar tema visual.
- **`PanelDeportistas`**: Tabla de deportistas con selector, ficha detallada, edición CRUD y botón para seleccionar imagen de perfil desde disco.
- **`PanelEntrenamientosTests`**: Formulario dual para dar de alta sesiones de entrenamiento o pruebas de 5 km asociadas a cualquier deportista.
- **`PanelAnalisis`**: Selección de deportista para visualizar métricas semanales de Foster, tabla estadística de 5 km, resumen descriptivo y gráficos JFreeChart embebidos en el propio panel de Swing.
- **`PanelExportacion`**: Interfaz de exportación documental con selector de deportista, selección de rutas de guardado para Excel (.xlsx) y PDF individual, y opción de apertura automática tras generar.
- **`TemaFlatLaf`**: Configuración y alternancia en tiempo de ejecución entre `FlatLightLaf` y `FlatDarkLaf` con actualización segura de la jerarquía de componentes Swing.
- **`ValidadorFormularios`**: Centralización de validaciones y diálogos (`JOptionPane`) para asegurar que no se introduzcan valores nulos, duraciones negativas o RPE fuera de 1-10.

#### 4. Exportación a Excel con Apache POI (`informesExcel.exportacion.ExportadorExcel`)
Crea un archivo `.xlsx` estructurado cumpliendo las 5 hojas requeridas:
1. **`Resumen`**: Cuadro de mando ejecutivo con tarjetas y métricas globales del club (total de atletas, horas, kilómetros y sesiones).
2. **`Deportistas`**: Directorio de atletas con formato tabular, cabeceras con estilo y formatos numéricos.
3. **`Entrenamientos`**: Registro cronológico de todas las sesiones con la fórmula de carga Foster calculada.
4. **`Análisis`**: Resumen semanal de carga, media móvil, índices de monotonía y tensión. Utiliza **fórmulas nativas de Excel** (`SUM`, `AVERAGE`), **formato condicional** (resaltado en rojo si la monotonía supera 2.0) y formatos de celda (`0.00`, `0.0%`).
5. **`Rendimiento`**: Historial de marcas de 5 km, ritmos medios y **gráfico incrustado** en la propia hoja.
- **Características adicionales**: Paneles inmovilizados (`createFreezePane`) en cabeceras de todas las hojas, filtros automáticos activados (`setAutoFilter`) y ajuste automático del ancho de columnas.

#### 5. Generación de Informe PDF con Apache PDFBox 3.x (`informesExcel.exportacion.GeneradorInformePDF`)
Genera programáticamente un informe formal de **5 páginas exactas** para el deportista seleccionado:
- **Página 1 (Portada)**: Escudo vectorial del club, título institucional, ficha del atleta con su avatar y periodo de fechas analizado.
- **Página 2 (Resumen Global y KPIs)**: Cuadro ejecutivo con 4 tarjetas de KPIs (Carga acumulada, Volumen en km, Frecuencia media y Mejor marca 5k) junto a un texto de diagnóstico.
- **Página 3 (Monitorización de Carga - Foster)**: Tabla desglosada por semanas naturales (Carga, Media móvil 3 sem, Monotonía, Tensión) y gráfico de barras de carga generado con JFreeChart en alta resolución.
- **Página 4 (Evolución de Rendimiento en 5 km)**: Tabla cronológica de marcas, parámetros de la recta de regresión ($y = a + bx$, $R^2$), proyección futura y gráfico de evolución temporal con línea de tendencia.
- **Página 5 (Interpretación, Recomendaciones y Descargo)**: Evaluación automática del estado de fatiga o riesgo de sobreentrenamiento según los índices calculados, pautas de tapering y descargo de responsabilidad legal y médica.

---

### 🧪 Batería de Pruebas Unitarias (`src/test/java`)

El proyecto incluye **14 pruebas unitarias automatizadas** implementadas con **JUnit 5**:

| Clase de Test | Componente Evaluado | Comprobaciones Realizadas |
| :--- | :--- | :--- |
| `CalculadoraRendimientoTest` | Motor matemático | Carga Foster ($RPE \times min$), agrupación ISO, monotonía, tensión, media móvil, regresión lineal ($a, b, R^2$) y correlación de Pearson. |
| `ExportadorExcelTest` | Apache POI | Creación de las 5 hojas requeridas, paneles inmovilizados, fórmulas nativas y formato condicional. |
| `GeneradorInformePDFTest` | Apache PDFBox 3.x | Generación del documento, verificación de que contiene exactamente 5 páginas e integridad del contenido. |
| `ModeloPersistenciaTest` | Modelo y Jackson | Serialización y deserialización JSON sin pérdida de datos, soporte de fechas Java Time y cálculo de edad del atleta. |
| `PanelAnalisisTest` | Swing GUI | Carga y refresco de datos en el panel de análisis con métricas y gráficos integrados. |
| `VentanaPrincipalTest` | Swing GUI | Inicialización en EDT, estructura de pestañas `JTabbedPane` y acciones de la barra de herramientas. |
| `TemaFlatLafTest` | Estilos UI | Cambio en caliente entre tema claro y tema oscuro sin errores ni parpadeos. |
