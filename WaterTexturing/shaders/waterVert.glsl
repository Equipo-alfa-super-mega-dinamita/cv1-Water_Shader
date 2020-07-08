
uniform mat4 transform;
uniform mat4 modelview;
uniform mat4 model;
uniform mat4 texMatrix;

attribute vec4 position;
attribute vec4 color;
attribute vec2 texCoord;

uniform vec4 plane;
varying vec4 vertColor;
varying vec4 vertTexCoord;

out vec4 clipSpace;
out vec2 dudvTexCoords;
out vec3 orientation;

uniform vec3 eyePosition;

const float tiling = 6.0;

void main(void) {

	vec4 worldPosition = position * model;
	clipSpace = transform * position;
	gl_Position = clipSpace;
	//dudvTexCoords = vec2(position.x/2.0 + 0.5, position.y/2.0 + 0.5) * tiling; //Thin Matrix
	//dudvTexCoords = (texMatrix * vec4(texCoord, 1.0, 1.0)).st; //Processing shaders
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);

	orientation = eyePosition - worldPosition.xyz;

}