#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D MaskSampler;
uniform vec2 OutSize;
uniform float Time;

in vec2 texCoord;
out vec4 fragColor;

float hash(float value) {
    return fract(sin(value * 91.731 + 47.193) * 43719.371);
}

float hash(vec2 value) {
    return hash(dot(value, vec2(97.13, 419.71)));
}

void main() {
    vec4 source = texture(DiffuseSampler, texCoord);
    float mask = texture(MaskSampler, texCoord).a;

    if (mask <= 0.01) {
        fragColor = vec4(source.rgb, 1.0);
        return;
    }

    float intensity = clamp(mask, 0.0, 1.0);
    float pixelLine = floor(texCoord.y * OutSize.y);
    float narrowBand = floor(texCoord.y * 76.0);
    float mediumBand = floor(texCoord.y * 42.0);
    float broadBand = floor(texCoord.y * 23.0);

    float longPhase = floor(Time / mix(24.0, 8.0, intensity));
    float mediumPhase = floor(Time / mix(12.0, 4.0, intensity));
    float fastPhase = floor(Time * mix(0.45, 1.25, intensity));

    float longNoise = hash(vec2(longPhase, 17.0));
    float mediumNoise = hash(vec2(mediumPhase, 51.0));
    float narrowNoise = hash(vec2(narrowBand, fastPhase));
    float mediumNoiseBand = hash(vec2(mediumBand, mediumPhase));
    float broadNoise = hash(vec2(broadBand, longPhase));

    float sustainedGlitch = step(0.72 - intensity * 0.20, longNoise);
    float mediumGlitch = step(0.78 - intensity * 0.18, mediumNoise);
    float narrowTearActive = step(0.84 - intensity * 0.15, narrowNoise);
    float mediumTearActive = step(0.88 - intensity * 0.13, mediumNoiseBand);
    float broadTearActive = step(0.92 - intensity * 0.10, broadNoise);

    float wave = sin(texCoord.y * 106.0 + Time * 1.72) * 0.00188 * intensity;
    wave += sin(texCoord.y * 53.0 - Time * 0.81) * 0.00120 * intensity;
    wave *= 1.0 + sustainedGlitch * 1.20 + mediumGlitch * 0.65;

    float narrowTear = (hash(vec2(narrowBand + 13.0, fastPhase)) - 0.5) * 0.024 * narrowTearActive * intensity;
    float mediumTear = (hash(vec2(mediumBand + 47.0, mediumPhase)) - 0.5) * 0.043 * mediumTearActive * intensity * max(mediumGlitch, sustainedGlitch);
    float broadTear = (hash(vec2(broadBand + 89.0, longPhase)) - 0.5) * 0.064 * broadTearActive * intensity * sustainedGlitch;

    float lineNoise = hash(vec2(pixelLine, floor(Time * 6.2)));
    float lineJitter = (lineNoise - 0.5) * 0.0047 * step(0.94 - intensity * 0.03, lineNoise) * intensity;

    float displacement = wave + narrowTear + mediumTear + broadTear + lineJitter;
    vec2 shiftedUv = clamp(texCoord + vec2(displacement, 0.0), vec2(0.001), vec2(0.999));

    vec2 screenCenter = vec2(0.5);
    vec2 centerDirection = texCoord - screenCenter;
    float centerDistance = length(centerDirection);
    vec2 radialDirection = centerDistance > 0.0001 ? centerDirection / centerDistance : vec2(0.0);

    float baseAberration = 0.0025 * intensity;
    float sustainedAberration = sustainedGlitch * 0.0045 * intensity;
    float tearAberration = abs(displacement) * 0.48;
    float eventAberration = narrowTearActive * 0.0020 + mediumTearActive * 0.0040 + broadTearActive * 0.0075;

    float aberrationStrength = (baseAberration + sustainedAberration + tearAberration + eventAberration * intensity) * mask;

    vec2 radialOffset = radialDirection * aberrationStrength;
    vec2 horizontalOffset = vec2(aberrationStrength * 1.05, 0.0);

    vec2 redUv = clamp(shiftedUv + radialOffset + horizontalOffset, vec2(0.001), vec2(0.999));
    vec2 greenUv = shiftedUv;
    vec2 blueUv = clamp(shiftedUv - radialOffset - horizontalOffset * 1.35, vec2(0.001), vec2(0.999));

    vec3 aberrated;
    aberrated.r = texture(DiffuseSampler, redUv).r;
    aberrated.g = texture(DiffuseSampler, greenUv).g;
    aberrated.b = texture(DiffuseSampler, blueUv).b;

    vec3 shiftedSource = texture(DiffuseSampler, shiftedUv).rgb;
    vec3 fringe = aberrated - shiftedSource;

    float fringeBoost = 1.90 + intensity * 1.70 + sustainedGlitch * 1.25;
    vec3 distorted = shiftedSource + fringe * fringeBoost;

    float coarseStatic = hash(floor(texCoord * OutSize * vec2(0.32, 0.16)) + floor(Time * 7.0));
    float fineStatic = hash(floor(texCoord * OutSize * vec2(0.64, 0.30)) + floor(Time * 13.0));

    float staticStrength = (0.017 + sustainedGlitch * 0.027 + mediumGlitch * 0.017) * intensity;

    distorted += (coarseStatic - 0.5) * staticStrength;
    distorted += (fineStatic - 0.5) * staticStrength * 0.45;

    float scanline = step(0.5, fract((pixelLine + Time * 1.15) * 0.5));
    distorted *= mix(0.960, 1.040, scanline * intensity);

    float corruption = max(mediumTearActive * mediumGlitch, broadTearActive * sustainedGlitch);
    float luminance = dot(distorted, vec3(0.299, 0.587, 0.114));
    vec3 corrupted = vec3(luminance * 0.63, luminance * 0.96, luminance * 1.08);

    distorted = mix(distorted, corrupted, corruption * intensity * 0.25);

    float finalBlend = clamp(mask * (0.78 + intensity * 0.42), 0.0, 1.0);

    fragColor = vec4(mix(source.rgb, vec3(1.0, 0.0, 1.0), mask), 1.0);
}