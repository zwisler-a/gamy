in vec4 in_Position;
in vec2 in_texCoord;

uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

varying out vec2 pass_texCoord;

void main(void) {
  	pass_texCoord = in_texCoord ;
  	gl_Position = transformationMatrix * in_Position;
}