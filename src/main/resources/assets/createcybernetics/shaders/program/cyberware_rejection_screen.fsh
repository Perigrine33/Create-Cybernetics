#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
uniform float Time;
uniform float Intensity;
uniform float Fugue;
uniform float Pulse;
uniform float Burst;

in vec2 texCoord;
out vec4 fragColor;

float hash(float value) {
    return fract(sin(value * 73.156 + 19.731) * 43817.531);
}

float hash(vec2 value) {
    return hash(dot(value, vec2(113.27, 241.93)));
}

float smoothEnvelope(float value) {
    value = clamp(value, 0.0, 1.0);
    return value * value * (3.0 - 2.0 * value);
}

void main() {
    vec2 uv = texCoord;
    vec4 source = texture(DiffuseSampler, uv);

    float sustainedStrength = Burst * (0.70 + Intensity * 0.55);
    float effectiveIntensity = clamp(max(Intensity, Fugue) + sustainedStrength * 0.48 + Pulse * 0.72, 0.0, 1.65);

    if (effectiveIntensity <= 0.001) {
        fragColor = vec4(source.rgb, 1.0);
        return;
    }

    vec2 center = vec2(0.5);
    vec2 centerDirection = uv - center;
    float radialDistance = length(centerDirection);
    vec2 radialDirection = radialDistance > 0.0001 ? centerDirection / radialDistance : vec2(0.0);

    float peripheral = smoothstep(0.08, 0.68, radialDistance);
    float centerCorruption = smoothstep(0.72, 0.08, radialDistance) * (Burst * 0.45 + Fugue * 0.70 + Pulse * 0.90);
    float affectedArea = clamp(0.28 + peripheral * 0.72 + centerCorruption, 0.0, 1.0);

    float pixelLine = floor(uv.y * OutSize.y);
    float narrowBand = floor(uv.y * 82.0);
    float mediumBand = floor(uv.y * 39.0);
    float broadBand = floor(uv.y * 19.0);

    float lineNoise = hash(vec2(pixelLine, floor(Time * 4.5)));
    float narrowNoise = hash(vec2(narrowBand, floor(Time * 0.72)));
    float mediumNoise = hash(vec2(mediumBand, floor(Time * 0.43)));
    float broadNoise = hash(vec2(broadBand, floor(Time * 0.24)));

    float ordinaryTear = step(0.86 - Intensity * 0.16 - Burst * 0.14, narrowNoise);
    float sustainedTear = step(0.78 - Burst * 0.20 - Fugue * 0.16, mediumNoise);
    float severeTear = step(0.88 - Fugue * 0.18 - Pulse * 0.25 - Burst * 0.10, broadNoise);

    float slowWave = sin(uv.y * 48.0 + Time * 0.68) * 0.00215;
    float mediumWave = sin(uv.y * 103.0 - Time * 1.44) * 0.00120;
    float fineWave = sin(uv.y * 181.0 + Time * 2.72) * 0.00060;

    float waveStrength = effectiveIntensity * (0.45 + affectedArea * 0.55);
    waveStrength *= 1.0 + Burst * 0.85 + Fugue * 0.95 + Pulse * 1.30;

    float waveOffset = (slowWave + mediumWave + fineWave) * waveStrength;

    float ordinaryOffset = (hash(vec2(narrowBand, floor(Time * 2.1))) - 0.5) * 0.030 * ordinaryTear;
    float sustainedOffset = (hash(vec2(mediumBand + 31.0, floor(Time * 1.35))) - 0.5) * 0.054 * sustainedTear * max(Burst, Fugue * 0.75);
    float severeOffset = (hash(vec2(broadBand + 97.0, floor(Time * 4.6))) - 0.5) * 0.084 * severeTear * max(max(Fugue, Pulse), Burst * 0.80);

    float fineJitter = (lineNoise - 0.5) * 0.0050 * step(0.95 - effectiveIntensity * 0.025, lineNoise);
    float horizontalOffset = waveOffset + (ordinaryOffset + sustainedOffset + severeOffset + fineJitter) * affectedArea * effectiveIntensity;

    float verticalDrift = sin(Time * 0.19 + broadBand * 0.21) * 0.0040 * sustainedTear * Burst;
    verticalDrift += sin(Time * 0.31) * 0.0094 * severeTear * Fugue;
    verticalDrift += sin(Time * 0.47) * 0.0134 * Pulse;

    vec2 shiftedUv = clamp(uv + vec2(horizontalOffset, verticalDrift), vec2(0.001), vec2(0.999));

    float radialAberration = 0.0015 + radialDistance * radialDistance * 0.010;
    radialAberration *= effectiveIntensity;
    radialAberration *= 0.55 + peripheral * 0.85;

    radialAberration += Burst * (0.0035 + peripheral * 0.0055);
    radialAberration += Fugue * (0.0050 + peripheral * 0.0075);
    radialAberration += Pulse * 0.012;

    float tearAberration = abs(horizontalOffset) * 0.42;
    tearAberration += ordinaryTear * 0.0015 * Intensity;
    tearAberration += sustainedTear * 0.0045 * Burst;
    tearAberration += severeTear * 0.0080 * max(Fugue, Pulse);

    float aberrationStrength = radialAberration + tearAberration;

    vec2 radialOffset = radialDirection * aberrationStrength;
    vec2 horizontalSplit = vec2(aberrationStrength * 0.85, 0.0);

    vec2 redUv = clamp(shiftedUv + radialOffset + horizontalSplit, vec2(0.001), vec2(0.999));
    vec2 greenUv = shiftedUv;
    vec2 blueUv = clamp(shiftedUv - radialOffset - horizontalSplit * 1.30, vec2(0.001), vec2(0.999));

    vec3 aberrated;
    aberrated.r = texture(DiffuseSampler, redUv).r;
    aberrated.g = texture(DiffuseSampler, greenUv).g;
    aberrated.b = texture(DiffuseSampler, blueUv).b;

    vec3 shiftedSource = texture(DiffuseSampler, shiftedUv).rgb;
    vec3 fringe = aberrated - shiftedSource;

    float fringeBoost = 1.65 + Intensity * 1.10 + Burst * 1.25 + Fugue * 1.65 + Pulse * 2.20;
    vec3 distorted = shiftedSource + fringe * fringeBoost;

    float coarseStatic = hash(floor(uv * OutSize * vec2(0.22, 0.11)) + floor(Time * 6.0));
    float fineStatic = hash(floor(uv * OutSize * vec2(0.52, 0.24)) + floor(Time * 11.0));

    float staticStrength = 0.012 * Intensity;
    staticStrength += 0.021 * Burst;
    staticStrength += 0.034 * Fugue;
    staticStrength += 0.057 * Pulse;
    staticStrength *= affectedArea;

    distorted += (coarseStatic - 0.5) * staticStrength;
    distorted += (fineStatic - 0.5) * staticStrength * 0.45;

    float scanline = step(0.5, fract((pixelLine + Time * 1.4) * 0.5));
    float scanlineMultiplier = mix(0.970, 1.030, scanline);
    distorted *= mix(1.0, scanlineMultiplier, clamp(effectiveIntensity * 0.75 + Burst * 0.35, 0.0, 1.0));

    float corruption = max(sustainedTear * Burst, severeTear * max(Fugue, Pulse));
    float luminance = dot(distorted, vec3(0.299, 0.587, 0.114));
    vec3 corruptedColor = vec3(luminance * 0.58, luminance * 0.92, luminance * 1.05);

    distorted = mix(distorted, corruptedColor, corruption * 0.25);

    float colorPulse = sin(Time * 0.28) * 0.5 + 0.5;
    vec3 rejectionTint = mix(vec3(0.94, 1.02, 1.08), vec3(1.07, 0.92, 1.05), colorPulse);
    distorted *= mix(vec3(1.0), rejectionTint, clamp(Intensity * 0.12 + Burst * 0.16 + Fugue * 0.22, 0.0, 0.42));

    float vignette = smoothstep(0.28, 0.82, radialDistance);
    float vignetteStrength = Intensity * 0.12 + Burst * 0.09 + Fugue * 0.16 + Pulse * 0.20;

    distorted *= 1.0 - vignette * vignetteStrength;

    float baselineBlend = 0.30 + Intensity * 0.55;
    float eventBlend = Burst * 0.72 + Fugue * 0.90 + Pulse;
    float blendAmount = clamp(max(baselineBlend * affectedArea, eventBlend * (0.55 + affectedArea * 0.45)), 0.0, 1.0);

    fragColor = vec4(mix(source.rgb, clamp(distorted, 0.0, 1.0), blendAmount), 1.0);
}