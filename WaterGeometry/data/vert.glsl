
#define M_PI 3.1415926535897932384626433832795

uniform mat4 transform;
uniform mat4 modelview;
uniform mat4 worldMat;


uniform float _WobbleX;
uniform float _WobbleZ;
uniform float _FillAmount;
uniform vec3 viewDirProc;

attribute vec4 position;
attribute vec4 color;



varying vec3 viewDir;
varying vec3 normal;
varying float fillEdge;

vec4 RotateAroundYInDegrees(vec4 vertex, float degrees){
   float alpha = degrees * M_PI /180;
   float sina = sin(alpha);
   float cosa = cos(alpha);

   mat2 m = mat2(cosa,sina,-sina,cosa);
   return vec4(vertex.yz, m * vertex.xz).xzyw;
}

void main() {
   //POSICION DEL VERTICE EN EL MUNDO
   vec4 posAux = worldMat*position;
   vec3 worldPos = posAux.xyz; //REVISAR
   //ROTAR ALREDEDOR DE XY
   vec3 worldPosX = RotateAroundYInDegrees(vec4(worldPos,0),360).xyz;
   //ROTAR ALREDEDOR DE XZ
   vec3 worldPosZ = vec3(worldPosX.y,worldPosX.z,worldPosX.x);
   //COMBINAR ROTACIONES CON WORLDPOS, BASADO EN ONDA SINUSOIDAL DEL SCRIPT
   vec3 worldPosAdjusted = worldPos + (worldPosX * _WobbleX) + (worldPosZ * _WobbleZ);
   //ALTURA LIQUIDO
   fillEdge = worldPosAdjusted.y + _FillAmount;

   viewDir = viewDirProc; //REVISAR ----------------------- DIRECCION DE VISTA DE LA CAMARA/OBJETO
   
   //normal = normal;

   gl_Position = transform * position; //bien
}