#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif


uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudv;

in vec4 clipSpace;
in vec3 orientation;

const float waveStrength = 0.008;

uniform float offset;
uniform sampler2D texture;

varying vec4 vertTexCoord;

in vec2 dudvTexCoords;


void main(void) {

    vec2 ndc = (clipSpace.xy / clipSpace.w)/2.0 + 0.5;

    vec2 refractTexCoords = vec2(ndc.x, 1.0 - ndc.y);
    vec2 reflectTexCoords = vec2(ndc.x, 1.0 - ndc.y);

    vec2 dudvCoords = vertTexCoord.st;


    vec2 distortion1 = (texture2D(texture, vec2(dudvCoords.x - offset, dudvCoords.y)).rg * 2.0 - 1.0) * waveStrength;
    vec2 distortion2 = (texture2D(texture, vec2(offset - dudvCoords.x , dudvCoords.y + offset)).rg * 2.0 - 1.0) * waveStrength;

    vec2 distortion = distortion1 + distortion2;


    refractTexCoords += distortion;
    refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);

    reflectTexCoords += distortion;
    reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
    reflectTexCoords.y = clamp(reflectTexCoords.y, 0.001, 0.999);



    vec4 reflectColour = texture(reflectionTexture, reflectTexCoords);
    vec4 refractColour = texture(refractionTexture, refractTexCoords);

    vec4 dudvColour = texture2D(texture, vertTexCoord.st);
    vec3 viewVector = normalize(orientation);

    float refractiveFactor = dot(viewVector, vec3(0.0, 1.0, 0.0));

    gl_FragColor = mix(mix(reflectColour, refractColour, refractiveFactor * 0.8), vec4(0.09 ,0.4902 ,0.651 ,1.0), 0.3); //vec4(0.1647 ,0.7255 ,0.851,  1.0), 0.35);
    //gl_FragColor = texture2D(texture, vertTexCoord.st);
}