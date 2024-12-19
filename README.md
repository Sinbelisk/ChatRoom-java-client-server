# Sala de Chat UDP en Java

Este proyecto implementa una sala de chat cliente-servidor utilizando sockets UDP en Java. La aplicación permite a múltiples clientes comunicarse entre sí a través de un servidor central que gestiona los mensajes y los comandos del chat.

---

## 📋 Requisitos

- **Java Development Kit (JDK)** 17+
- Un entorno de desarrollo como **IntelliJ IDEA** o **Eclipse**
- Conexión a una red para la comunicación cliente-servidor

---

## 📂 Estructura del Proyecto

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

## 🚀 Uso

### **1. Compilar y Ejecutar el Proyecto**

#### **Desde un IDE (IntelliJ IDEA o Eclipse)**
El repositorio se puede clonar desde el repositorio:
```bash
   git clone https://github.com/Sinbelisk/ChatRoom-java-client-server.git 
```
1. **Importar el Proyecto**:
    - Importa el proyecto en tu IDE (como **IntelliJ IDEA** o **Eclipse**).
    - Asegúrate de que el proyecto esté configurado correctamente para compilar todas las clases. La mayoría de los IDEs lo hacen automáticamente cuando el proyecto se importa.

2. **Ejecución del Proyecto**:
    - **Cliente y Servidor desde el IDE**: Puedes ejecutar tanto el servidor como el cliente directamente desde el IDE.
    - Para ejecutar el **servidor**:
        - Abre la clase `ServerMain` y ejecútala, especificando el puerto como argumento en la configuración de ejecución.
    - Para ejecutar el **cliente**:
        - Abre la clase `ClientMain` y ejecútala, asignando los parámetros del servidor (IP y puerto).

3. **Ejecutar múltiples instancias**:
    - Algunos IDEs como **IntelliJ IDEA** permiten ejecutar múltiples instancias de la aplicación. Esto es útil para probar el servidor y varios clientes simultáneamente. Para ello, puedes configurar cada clase (`ServerMain` y `ClientMain`) y permitir la ejecución de múltiples instancias desde **Edit Configurations** > **Modify Options** > **Allow Multiple Instances**.

#### **Desde la Terminal**

1. **Compilación (Sistemas basados en Unix)**:
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

---

### **3. Configuración del Cliente y Servidor en IntelliJ IDEA**

Si prefieres ejecutar el proyecto desde **IntelliJ IDEA** en lugar de la terminal, sigue estos pasos para configurar correctamente las clases y ejecutar el servidor y el cliente:

#### **Configurar el Cliente en IntelliJ IDEA**
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

Esta estructura proporciona una explicación clara de cómo compilar, ejecutar y solucionar problemas, con énfasis en el uso tanto desde un IDE como desde la terminal. También incluye una sección con notas adicionales para enriquecer la comprensión del proyecto y sus características técnicas.


## 💬 Comandos Disponibles

Dentro del chat, los usuarios pueden usar los siguientes comandos:

| Comando                        |  Descripción                                     |
|--------------------------------|--------------------------------------------------|
| `/help`                        | Muestra la lista de comandos disponibles         |
| `/list`                        | Lista los usuarios conectados al servidor        |
| `/private [usuario] [mensaje]` | Envía un mensaje privado a un usuario específico |
| `/exit`                        | Desconecta al cliente del servidor               |