#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform vec2 InSize;

uniform float EdgeThreshold;
uniform float EdgeBoost;

uniform float Aspect;
uniform float TanHalfFov;
uniform float Near;
uniform float Far;

uniform float SoundCount;
uniform float SoundLifeSeconds;

uniform float WaveSpeed;       // blocks/sec
uniform float WaveThickness;   // thickness of bright rim (blocks)
uniform float WaveFeather;     // softness (blocks)
uniform float WaveMaxRadius;   // clamp radius (blocks)
uniform float SkyDepthEps;     // raw depth cutoff for sky exclusion

uniform float SoundPosX0; uniform float SoundPosY0; uniform float SoundPosZ0; uniform float SoundAge0;
uniform float SoundPosX1; uniform float SoundPosY1; uniform float SoundPosZ1; uniform float SoundAge1;
uniform float SoundPosX2; uniform float SoundPosY2; uniform float SoundPosZ2; uniform float SoundAge2;
uniform float SoundPosX3; uniform float SoundPosY3; uniform float SoundPosZ3; uniform float SoundAge3;
uniform float SoundPosX4; uniform float SoundPosY4; uniform float SoundPosZ4; uniform float SoundAge4;
uniform float SoundPosX5; uniform float SoundPosY5; uniform float SoundPosZ5; uniform float SoundAge5;
uniform float SoundPosX6; uniform float SoundPosY6; uniform float SoundPosZ6; uniform float SoundAge6;
uniform float SoundPosX7; uniform float SoundPosY7; uniform float SoundPosZ7; uniform float SoundAge7;
uniform float SoundPosX8; uniform float SoundPosY8; uniform float SoundPosZ8; uniform float SoundAge8;
uniform float SoundPosX9; uniform float SoundPosY9; uniform float SoundPosZ9; uniform float SoundAge9;
uniform float SoundPosX10; uniform float SoundPosY10; uniform float SoundPosZ10; uniform float SoundAge10;
uniform float SoundPosX11; uniform float SoundPosY11; uniform float SoundPosZ11; uniform float SoundAge11;
uniform float SoundPosX12; uniform float SoundPosY12; uniform float SoundPosZ12; uniform float SoundAge12;
uniform float SoundPosX13; uniform float SoundPosY13; uniform float SoundPosZ13; uniform float SoundAge13;
uniform float SoundPosX14; uniform float SoundPosY14; uniform float SoundPosZ14; uniform float SoundAge14;
uniform float SoundPosX15; uniform float SoundPosY15; uniform float SoundPosZ15; uniform float SoundAge15;

in vec2 texCoord;
out vec4 fragColor;

vec3 sonarColor(float t) {
    vec3 deepBlue = vec3(0.05, 0.20, 0.90);
    vec3 cyan     = vec3(0.10, 0.95, 0.95);
    return mix(deepBlue, cyan, clamp(t, 0.0, 1.0));
}

float linearizeDepth(float depth01) {
    float z = depth01 * 2.0 - 1.0;
    float denom = (Far + Near) - z * (Far - Near);
    return (2.0 * Far * Near) / max(denom, 1e-6);
}

// KEEP YOUR ORIGINAL VIEWPOS (this is the one that actually works in your setup)
vec3 reconstructViewPos(vec2 uv, float depth01) {
    float vz = linearizeDepth(depth01);
    vec2 ndc = uv * 2.0 - 1.0;

    float x = ndc.x * vz * TanHalfFov * Aspect;
    float y = ndc.y * vz * TanHalfFov;
    float z = -vz;

    return vec3(x, y, z);
}

/*
  FIXED WAVE:
  - Rim is a thin shell at radius r
  - Tail is ALSO a band near the rim, not the entire interior
  - This guarantees you get an expanding sphere, not "everything on"
*/
float waveMask(vec3 viewPos, vec3 sPos, float ageSec) {
    if (ageSec < 0.0) return 0.0;

    float life = clamp(1.0 - (ageSec / max(SoundLifeSeconds, 1e-3)), 0.0, 1.0);
    if (life <= 0.0) return 0.0;

    // slow fade (your preference)
    life = pow(life, 0.1);

    float r = min(WaveSpeed * ageSec, WaveMaxRadius);
    float d = length(viewPos - sPos);

    // We work in "distance-to-front" space so the wave is always a shell.
    float distToFront = abs(d - r);

    // Rim thickness + feather
    float halfT = 0.5 * max(WaveThickness, 1e-3);
    float rim = 1.0 - smoothstep(halfT, halfT + WaveFeather, distToFront);

    // Tail: only BEHIND the front, within TailLen, but still banded (not interior fill)
    const float TAIL_SECONDS = 1.25;
    float tailLen = max(WaveSpeed * TAIL_SECONDS, 1e-3);

    // behind amount = r - d (positive behind the front)
    float behind = r - d;
    float tailBand = 0.0;
    if (behind >= 0.0 && behind <= tailLen) {
        // fade from 1 at the front to 0 at the end of the tail
        float f = 1.0 - (behind / tailLen);
        // broaden tail slightly but KEEP it near the front by also requiring small distToFront
        float nearFront = 1.0 - smoothstep(halfT * 2.0, halfT * 2.0 + WaveFeather * 2.0, distToFront);
        tailBand = smoothstep(0.0, 1.0, f) * 0.65 * nearFront;
    }

    return clamp((max(rim, tailBand)) * life, 0.0, 1.0);
}

float wavesMask(vec3 viewPos) {
    int n = int(SoundCount + 0.5);
    float m = 0.0;

    if (n > 0)  m = max(m, waveMask(viewPos, vec3(SoundPosX0,  SoundPosY0,  SoundPosZ0),  SoundAge0));
    if (n > 1)  m = max(m, waveMask(viewPos, vec3(SoundPosX1,  SoundPosY1,  SoundPosZ1),  SoundAge1));
    if (n > 2)  m = max(m, waveMask(viewPos, vec3(SoundPosX2,  SoundPosY2,  SoundPosZ2),  SoundAge2));
    if (n > 3)  m = max(m, waveMask(viewPos, vec3(SoundPosX3,  SoundPosY3,  SoundPosZ3),  SoundAge3));
    if (n > 4)  m = max(m, waveMask(viewPos, vec3(SoundPosX4,  SoundPosY4,  SoundPosZ4),  SoundAge4));
    if (n > 5)  m = max(m, waveMask(viewPos, vec3(SoundPosX5,  SoundPosY5,  SoundPosZ5),  SoundAge5));
    if (n > 6)  m = max(m, waveMask(viewPos, vec3(SoundPosX6,  SoundPosY6,  SoundPosZ6),  SoundAge6));
    if (n > 7)  m = max(m, waveMask(viewPos, vec3(SoundPosX7,  SoundPosY7,  SoundPosZ7),  SoundAge7));
    if (n > 8)  m = max(m, waveMask(viewPos, vec3(SoundPosX8,  SoundPosY8,  SoundPosZ8),  SoundAge8));
    if (n > 9)  m = max(m, waveMask(viewPos, vec3(SoundPosX9,  SoundPosY9,  SoundPosZ9),  SoundAge9));
    if (n > 10) m = max(m, waveMask(viewPos, vec3(SoundPosX10, SoundPosY10, SoundPosZ10), SoundAge10));
    if (n > 11) m = max(m, waveMask(viewPos, vec3(SoundPosX11, SoundPosY11, SoundPosZ11), SoundAge11));
    if (n > 12) m = max(m, waveMask(viewPos, vec3(SoundPosX12, SoundPosY12, SoundPosZ12), SoundAge12));
    if (n > 13) m = max(m, waveMask(viewPos, vec3(SoundPosX13, SoundPosY13, SoundPosZ13), SoundAge13));
    if (n > 14) m = max(m, waveMask(viewPos, vec3(SoundPosX14, SoundPosY14, SoundPosZ14), SoundAge14));
    if (n > 15) m = max(m, waveMask(viewPos, vec3(SoundPosX15, SoundPosY15, SoundPosZ15), SoundAge15));

    return clamp(m, 0.0, 1.0);
}

void main() {
    vec2 uv = texCoord;

    vec3 scene = texture(DiffuseSampler, uv).rgb;

    // KEEP YOUR ORIGINAL SKY CHECK (this is what your setup needs)
    float rawDepth = texture(DepthSampler, uv).r;
    if (rawDepth <= SkyDepthEps) {
        fragColor = vec4(scene, 1.0);
        return;
    }

    // Edge detect (White Sharp-ish)
    vec2 one = 1.0 / max(InSize, vec2(1.0, 1.0));
    vec3 rightPixel  = texture(DiffuseSampler, uv + vec2(one.x, 0.0)).rgb;
    vec3 bottomPixel = texture(DiffuseSampler, uv + vec2(0.0, one.y)).rgb;

    vec3 diff = abs(scene - rightPixel) + abs(scene - bottomPixel);
    float diffLen = length(diff);
    float edge = smoothstep(EdgeThreshold, EdgeThreshold * 1.75, diffLen);

    // reversed depth fix (KEEP THIS)
    float depth01 = 1.0 - rawDepth;
    vec3 viewPos = reconstructViewPos(uv, depth01);

    float mask = wavesMask(viewPos);

    // Persistent black underlay while effect is enabled
    vec3 base = vec3(0.0);

    if (mask <= 0.001) {
        fragColor = vec4(base, 1.0);
        return;
    }

    // Edges only over black
    float t = clamp((diffLen - EdgeThreshold) * 6.0, 0.0, 1.0);
    vec3 lineCol = sonarColor(t) * EdgeBoost;

    fragColor = vec4(lineCol * (edge * mask), 1.0);
}