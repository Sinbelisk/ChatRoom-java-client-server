# Sala de Chat UDP en Java

Este proyecto implementa una **sala de chat cliente-servidor** utilizando **sockets UDP** en Java. La aplicación permite a múltiples clientes comunicarse entre sí a través de un servidor central que gestiona los mensajes y los comandos del chat.


---

## Características

- **Conversación en tiempo real**: Permite a varios usuarios conversar simultáneamente en una sala de chat gestionada por un servidor central.

- **Redirección de mensajes**: El servidor se encarga de procesar los mensajes enviados por los usuarios y redirigirlos a todos los usuarios conectados a la sala, asegurando la comunicación fluida.

- **Gestión de comandos**: El servidor soporta varios comandos predefinidos (como `/help`, `/list`, `/private`, etc.) y tiene una arquitectura extensible gracias al patrón de diseño **Command**, lo que facilita la adición de nuevos comandos sin modificar la lógica base.

- **Detección de inactividad**: El servidor monitorea la actividad de los usuarios. Si un usuario no interactúa durante un período prolongado, el servidor le enviará un **"ping"**. El cliente responde automáticamente al ping, si no responde _(principalmente debido a una salida forzada)_ al tercer ping, el servidor expulsará automáticamente al usuario de la sala para garantizar que los recursos no sean ocupados innecesariamente.

- **Interfaz de usuario intuitiva**: Aunque el chat se basa en consola, el cliente es fácil de usar y los comandos son intuitivos. La aplicación puede servir como base para una futura interfaz gráfica (GUI) si se desea expandir.

- **Registro de mensajes**: Todos los mensajes enviados por los usuarios del chat se guardan en un registro temporal que **almacena hasta 10**. Cada vez que entra un usuario el servidor le envía estos mensajes.

- **Sistema de avisos:** El servidor envía avisos a los usuarios sobre determinados eventos, como comandos inválidos o conexiones y desconexiones de usuarios.


## 📋 Requisitos

- **Java Development Kit (JDK)** 17+
- Un entorno de desarrollo como **IntelliJ IDEA** o **Eclipse**
- Conexión a una red para la comunicación cliente-servidor

## 🚀 Uso

### **1. Compilar y Ejecutar el Proyecto**

#### **Desde un IDE (IntelliJ IDEA o Eclipse)**
1. **Importar el Proyecto**:
    - Importa el proyecto en tu IDE (como **IntelliJ IDEA** o **Eclipse**).
    - El IDE configurará automáticamente las clases a compilar.

2. **Ejecución del Cliente y Servidor**:
    - **Cliente y Servidor desde el IDE**: Puedes ejecutar tanto el servidor como el cliente directamente desde el IDE.
    - **Servidor**: Ejecuta la clase `ServerMain` desde el IDE, especificando el puerto como parámetro.
    - **Cliente**: Abre la clase `ClientMain` y ejecútala, proporcionando la IP y el puerto del servidor como parámetros.

3. **Ejecutar múltiples instancias**:
    - Si deseas probar el servidor con múltiples clientes, IntelliJ IDEA permite ejecutar varias instancias simultáneamente. Para ello, ve a **Edit Configurations > Modify Options > Allow Multiple Instances.**
#### **Desde la Terminal**

1. **Compilación (Sistemas basados en Unix)**:
Es posible compilar de forma sencilla el proyecto desde la terminal:
   - Dirígete al directorio raíz del proyecto.
   - Compila todas las clases en una carpeta de salida (por ejemplo, `out`):
     ```bash
     javac -d out -cp src $(find src -name "*.java")
     ```

2. **Ejecución**:
   - **Iniciar el Servidor**: Ejecuta el servidor desde la carpeta `server`, especificando el `path` de salida (`out`) y el puerto donde escuchará:
     ```bash
     java -cp out server.ServerMain <puerto>
     ```
     Ejemplo:
     ```bash
     java -cp out server.ServerMain 12345
     ```
   - **Iniciar un Cliente**: Ejecuta el cliente desde la carpeta `client`, especificando la IP del servidor y el puerto donde se encuentra:
     ```bash
     java -cp out client.ClientMain <ip_servidor> <puerto>
     ```
     Ejemplo:
     ```bash
     java -cp out client.ClientMain 127.0.0.1 12345
     ```


### **2. Resolución de Problemas Comunes**

Si al intentar conectarte aparece el mensaje **"Unable to connect to server"**, verifica lo siguiente:

- **Servidor Activo**: Asegúrate de que el servidor esté en ejecución y funcionando correctamente.
- **IP y Puerto Correctos**: Confirma que el cliente está configurado con la dirección IP y el puerto correctos del servidor.
- **Puerto Válido**: Verifica que el puerto configurado en el servidor esté accesible y no esté utilizado por otro servicio.
- **Firewall**: Revisa que el Firewall no esté bloqueando el puerto utilizado para el servidor.

---

### **3. Configuración en IntelliJ IDEA**

Para ejecutar correctamente el cliente y servidor en **IntelliJ IDEA** hay que seguir los siguientes pasos:

#### **Configurar el Cliente**
1. Haz clic en las tres líneas horizontales junto al botón de ejecución en la esquina superior derecha (**Run Configurations**) y selecciona **Edit Configurations**.
2. En **Run Configurations**, selecciona la clase `ClientMain`.
3. En **Program Arguments**, asigna los parámetros del cliente: `<ip_servidor> <puerto>`. Ejemplo: `127.0.0.1 12345`.
4. Para ejecutar múltiples instancias, selecciona **Modify Options** > **Allow Multiple Instances**.

#### **Configurar el Servidor en IntelliJ IDEA**
1. Repite el proceso anterior, pero selecciona la clase `ServerMain`.
2. En **Program Arguments**, especifica el puerto del servidor. Ejemplo: `12345`.

#### **Ejemplo Visual**:
![client conf.png](ReadmeImages%2Fclient%20conf.png)

---

## 💬 Comandos Disponibles

Dentro del chat, los usuarios pueden usar los siguientes comandos:

| Comando                        |  Descripción                                     |
|--------------------------------|--------------------------------------------------|
| `/help`                        | Muestra la lista de comandos disponibles         |
| `/list`                        | Lista los usuarios conectados al servidor        |
| `/private [usuario] [mensaje]` | Envía un mensaje privado a un usuario específico |
| `/exit`                        | Desconecta al cliente del servidor               |

---

## 📂 Estructura del Proyecto
El proyecto presenta la siguente estructura:

```plaintext
├───client                           # Paquete del cliente
│   │   Client.java                  # Lógica principal del cliente
│   │   ClientMain.java              # Clase principal para ejecutar el cliente
│   │
│   └───model                        # Modelos específicos del cliente
│       └───message                  # Paquete para manejar mensajes del cliente
│               ClientMessage.java   # Clase para representar mensajes enviados por el cliente
│
├───common                           # Paquete común con utilidades compartidas entre cliente y servidor
│   │   UDPOperation.java            # Interfaz que define operaciones de paquetes UDP (recibir, enviar , procesar)
│   │   UDPSocket.java               # Implementación de la interfaz con métodos predefinidos comunes para enviar y recibir en cliente y servidor
│   │
│   └───util                         # Herramientas comunes
│           MessageUtil.java         # Utilidad para procesar y manejar mensajes
│           SimpleLogger.java        # Clase simple para logging
│
└───server                           # Paquete del servidor
    │   MessageSender.java           # Lógica para enviar mensajes desde el servidor a los clientes
    │   Server.java                  # Lógica principal del servidor (manejo de conexiones y eventos)
    │   ServerMain.java              # Clase principal para ejecutar el servidor
    │
    ├───commands                     # Paquete para manejar comandos del servidor
    │   │   Command.java             # Interfaz para definir comandos
    │   │   CommandHandler.java      # Manejo centralizado de los comandos recibidos
    │   │
    │   └───commands                 # Implementaciones específicas de comandos
    │           ExitCommand.java     # Comando para cerrar el servidor
    │           HelpCommand.java     # Comando para listar los comandos disponibles
    │           ListCommand.java     # Comando para mostrar usuarios conectados
    │           LoginCommand.java    # Comando para autenticar usuarios
    │           PrivateCommand.java  # Comando para enviar mensajes privados
    │
    └───model                        # Modelos específicos del servidor
        │   ChatRoom.java            # Clase para representar una sala de chat
        │   User.java                # Clase para representar un usuario conectado
        │
        └───message                  # Paquete para manejar mensajes en el servidor
                ChatMessage.java     # Clase para representar mensajes de chat
                ServerMessage.java   # Clase para mensajes generados por el servidor
```

---