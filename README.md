# Informe Water shader
|       Integrante      |                 github nick                   |
|-----------------------|-----------------------------------------------|
| Nicolai Romero         | [anromerom](https://github.com/anromerom) |
| Julian Rodriguez      | [jdrodriguezrui](https://github.com/jdrodriguezrui)       |
| Edder Hernández      | [Heldeg](https://github.com/Heldeg)       |
## Introducción
Este proyecto está dividido en dos partes. Por un lado, buscamos hacer la representación del movimiento de un fluido en un objeto, es decir, ondulaciones. Y por el otro lado, mostrar ciertas propiedades de los líquidos como la refracción y la reflexión.

El trabajo fue inspirado por una propuesta de efecto rápido para videojuegos de la mano del creador de contenido [*Minions Art*](https://www.patreon.com/posts/18245226), en el cual explica cómo en Unity crea el efecto de fluido en botella.

![Error en imagen](./resources/0.gif)

Centrados en el tema, se quiso realizar un proceso de estudio de varios efectos líquidos, concretamente los mencionados anteriormente; el objetivo era obtener un mayor realismo en cuanto a liquido al aplicar una mayor cantidad de detalles. Para este hecho nos basamos en una serie de videos tutorial del canal ThinMatrix llamados [OpenGL water tutorial](https://www.youtube.com/watch?v=HusvGeEDU_U&t=3s).

Los anteriores recursos pueden tomarse como precedentes a nuestro tema tratado. Aun así, después de una serie de indagaciones respecto a nuestro tema, no fue posible encontrar recurso alguno, semejante, asociado a [processing](https://processing.org/). Es de esta forma que se decidió implementar un ejemplo para este mismo, junto a la ayuda de la librería [nub](https://github.com/VisualComputing/nub#interactivity) 
## Objetivo
Implementar un ejemplo con uso de shaders y la librería nub en processesing que simule varios aspectos del agua.
## Diseño solución
El proyecto fue dividido en dos partes. Por un lado se trabajaría todo lo referente al efecto de liquido en botella, y por el otro se trataría las propiedades de refracción y reflexión para posteriormente combinar estos dos. En ambos casos se empezó estudiando los ejemplos anteriormente mencionados, para posteriormente ser implementados en processing.
### Water texture
### Water geometry
Para falsificar el contenido de líquido, se estableció una altura a partir de la que se dejaría de renderizar color en los fragmentos. Haciendo uso de la variable de openGL gl_FrontFacing, se usaría otro color para simular un volumen cuando la superficie del líquido estuviera orientada hacia el ojo de la escena. Ambos colores se establecen como variables al interior de processing y se pasan como parámetros al shader para su uso.

El efecto de ondulación del líquido al mover o rotar el recipiente se logra haciendo uso de las propiedades de posición y orientación del nodo proveído por la librería para processing Nub. Se lleva a cabo el cálculo de la velocidad lineal y angular, y con ellos se cálcula la cantidad de ondulación en los ejes X y Z del líquido para ser utilizados en una onda sinusoidal que es interpolada a 0 con el tiempo para simular la estabilización del líquido. Es el valor de esta onda sinusoidal el que se pasará al shader como parámetro para la ondulación en X y Z.
## Demo
### Water texture
### Water geometry
#### Unity
![Error en imagen](./resources/3.gif)
#### Processing
![Error en imagen](./resources/4.gif)
## Conclusiones
Se logró una porción del objetivo principal. La aplicación de los efectos de reflexión y refracción fue completada por su lado, mientras que se estableció lo básico del efecto de movimiento de liquido en una botella; quedo pendiente la combinación de ambas partes y el pulido de detalles.
