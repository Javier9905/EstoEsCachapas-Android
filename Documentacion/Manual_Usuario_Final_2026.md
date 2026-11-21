# Manual de usuario — Esto Es Cachapas y Algo Más

**Asignatura:** Desarrollo de Aplicaciones Móviles Nativas  
**Autor:** Javier Andrés Barrera Cárdenas  
**Versión documentada:** prototipo Android local (noviembre de 2026)

## Antes de comenzar

En este manual muestro el recorrido por las tres cuentas del aplicativo, porque cada rol encuentra diferentes opciones. Los pasos describen lo que se observa en el código; todavía hacen falta **capturas originales de un teléfono físico** para completar la versión final del PDF.

## 1. Instalación y acceso

Descarga el repositorio, abre su carpeta raíz en Android Studio y espera a que finalice **Gradle Sync**. Conecta un teléfono Android compatible y ejecuta la aplicación. El archivo `APK/Cachapas.apk` corresponde a una versión previa y no prueba que las integraciones de esta actividad estén terminadas.

| Rol | Cuenta de demostración |
|---|---|
| Comprador | `comprador@cachapas.demo` |
| Vendedor | `vendedor@cachapas.demo` |
| Administrador | `admin@cachapas.demo` |

**Contraseña compartida para las cuentas de prueba:** `Cachapas2026!`  
Estas credenciales son solo para pruebas locales; no deben reutilizarse en producción.

## 2. Comprador

1. Abre la app y entra con la cuenta de comprador.
2. Consulta el Inicio y selecciona Catálogo.
3. Busca una cachapa por su nombre o categoría y abre `Ver producto`.
4. Revisa precio y disponibilidad, indica la cantidad y usa `Agregar al carrito`.
5. Entra en Carrito, modifica cantidades si hace falta y revisa el total.
6. Selecciona `Confirmar pedido`; completa nombre y teléfono, marca la recogida local y acepta las condiciones.
7. Elige un **resultado de pago simulado**; no se cobra dinero.
8. Abre Historial/Pedidos y consulta el detalle.
9. Desde Perfil edita los datos o activa la biometría si el dispositivo es compatible.

**Capturas por añadir:** acceso, inicio, catálogo, detalle, carrito, confirmación de pedido, historial y perfil.

## 3. Vendedor

1. Entra con `vendedor@cachapas.demo`.
2. Abre el Panel para consultar productos y pedidos.
3. En Productos pulsa `Crear producto` y completa nombre, categoría, precio, inventario y descripción.
4. Opcionalmente elige una foto de galería y guarda. La foto queda en la instalación local, no en Firebase Storage.
5. Usa `Editar producto` para corregir valores y `Eliminar del menú` para darlo de baja lógicamente.
6. Abre Pedidos y, si hay un pedido con pago simulado aprobado, cambia su estado por las opciones permitidas.

**Capturas por añadir:** panel, productos, formulario y seguimiento del pedido.

## 4. Administrador

1. Entra con `admin@cachapas.demo`.
2. Abre Usuarios y consulta las cuentas disponibles.
3. Crea o edita una cuenta y asigna su rol.
4. Si corresponde, desactiva una cuenta desde su gestión.
5. Revisa Productos, Pedidos y Reporte de ventas para consultar los registros de prueba.

**Capturas por añadir:** usuarios, edición de rol, productos y reporte.

## 5. Advertencias

- Los datos se almacenan localmente en el teléfono: no se sincronizan con Firebase.
- Los pagos son simulados: no existe confirmación bancaria.
- La recuperación de contraseña muestra un código local, no envía correo.
- Puedes solicitar coordenadas desde la pantalla de pedido aceptando el permiso de ubicación. La tarifa y cobertura de domicilios aún no están implementadas, por eso la compra con domicilio sigue deshabilitada.
- Si la huella no está disponible, entra por correo y contraseña.

## 6. Lista para completar el PDF del manual

- [ ] Captura 1: acceso
- [ ] Captura 2: catálogo
- [ ] Captura 3: detalle y carrito
- [ ] Captura 4: formulario de pedido
- [ ] Captura 5: historial
- [ ] Captura 6: panel vendedor
- [ ] Captura 7: edición de producto
- [ ] Captura 8: panel administrador
- [ ] Captura 9: gestión de usuarios
- [ ] Captura 10: reporte de ventas

**Repositorio:** https://github.com/Javier9905/EstoEsCachapas-Android
