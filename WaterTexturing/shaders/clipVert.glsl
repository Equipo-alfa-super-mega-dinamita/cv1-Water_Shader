uniform mat4 transform;
uniform mat4 model;
uniform mat4 texMatrix;
uniform mat4  worldMatrix;

attribute vec4 position;
attribute vec4 color;
attribute vec2 texCoord;

uniform vec4 plane;

varying vec4 vertColor;
varying vec4 vertTexCoord;




void main(void) {


	vec4 worldPosition =  model * position;
	gl_ClipDistance[0] = dot(worldPosition, plane);


	gl_Position = transform * position;
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
	vertColor = color;

}