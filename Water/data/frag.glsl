#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif


uniform vec3 _Tint;
uniform vec3 _TopColor;
uniform vec3 _FoamColor;
uniform float _Rim;
uniform vec3 _RimColor;
uniform float _RimPower;

attribute vec4 position;


varying vec3 viewDir;
varying vec3 normal;
varying float fillEdge;

void main() {
  vec4 col = vec4(_Tint,1.0);
  //RIM LIGHT (Unused)
  float dotProduct = 1-pow(dot(normal,viewDir),_RimPower); //viewDir, normal -- REVISAR
  vec4 rimResult = vec4(smoothstep(0.5,1.0,dotProduct)) * vec4(_RimColor,1.0);
  //FOAM EDGE
  vec4 foam = vec4( step(fillEdge, 0.5) - step(fillEdge, (0.5 - _Rim)) );
  vec4 foamColored = vec4(foam * (vec4(_FoamColor,1.0) * 0.9));
  //REMAINING LIQUID
  vec4 result = vec4(step(fillEdge,0.5)) - foam;
  vec4 resultColored = result * col;
  
  vec4 finalResult = resultColored + foamColored;
  //finalResult.rgb = finalResult.rgb + rimResult.xyz;

  //Color de la cima / parte trasera
  vec4 topColor = vec4(_TopColor,1.0) * (foam + result);
  //return facing > 0 ? finalResult: topColor; //  -----TO DO------
  if(gl_FrontFacing){
    gl_FragColor = topColor;
  }else{
    gl_FragColor = finalResult;
  }
  //gl_FragColor = finalResult;
}