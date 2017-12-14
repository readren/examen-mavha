Test Técnico exigido por MAvha
 
Problema
Implementar una solución en REST en JAX-WS que permita guardar una Entidad Persona con las siguientes características:
PERSONA
DNI (ID)
Nombre
Apellido
Edad

Implementar:
Listado de personas: GET localhost:port/personas que retorna un json con todas las personas
Alta de persona: POST localhost:port/personas/{ID_persona} que crea una persona nueva


Consideraciones:
Armar un proyecto con maven 
Utilizar JAX-RS 2.0 (No hace falta implementar ningún front)
Utilizar JPA
Utilizar la DB en memoria hsqldb
Deploy en JBOSS o Wildfly o GlassFish
Armar los Test para el alta y listado con JUNIT
publicar el proyecto en github o bitbucket y enviar el link por correo a martin.marlatto@mavha.com y maximiliano.mattig@mavha.com
