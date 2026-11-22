# Aplicación de Gestión y Análisis de Rendimiento Deportivo (2.º DAM)

Aplicación de escritorio en **Java 25+ / Maven** desarrollada con **Java Swing, FlatLaf, Apache POI, Apache PDFBox y JFreeChart**.

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
