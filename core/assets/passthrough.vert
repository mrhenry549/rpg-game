//incoming Position attribute from our SpriteBatch
attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
			
varying vec4 vColor;
varying vec2 vTexCoord;

//the transformation matrix of our SpriteBatch
uniform mat4 u_projTrans;

void main() {
	vColor = a_color;
	vTexCoord = a_texCoord0;
    //transform our 2D screen space position into 3D world space
    gl_Position = u_projTrans * a_position;
}