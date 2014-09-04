#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP 
#endif

varying LOWP vec4 vColor;
varying vec2 vTexCoord;

uniform sampler2D u_texture;
const vec3 W = vec3(0.2125, 0.7154, 0.0721);

vec3 OverlayBlender(vec3 Color, vec3 filter){
    vec3 filter_result;
    float luminance = dot(filter, W);

    if(luminance < 0.5)
        filter_result = 2. * filter * Color;
    else
        filter_result = 1. - (1. - (2. *(filter - 0.5)))*(1. - Color);

    return filter_result;
}

void main() {
	const vec3 W = vec3(0.2125, 0.1754, 0.0721); 
	vec4 texColor = texture2D(u_texture, vTexCoord);
	vec4 blueColor = vec4(0, 0, 0.75, 0.75);
	//texColor.rgb = 1.0 - texColor.rgb;
	//texColor.rgb = texColor.rgb + blueColor.rgb;
	texColor.rgb = OverlayBlender(texColor.rgb, blueColor.rgb);
    //gl_FragColor = texColor * vColor;
	gl_FragColor = texColor;
}