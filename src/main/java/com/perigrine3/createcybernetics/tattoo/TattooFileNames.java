package com.perigrine3.createcybernetics.tattoo;

import java.nio.file.Path;
import java.util.Locale;

public final class TattooFileNames {
    private TattooFileNames() {
    }

    public static String sanitizePngFileName(String originalName) {
        String name = originalName == null ? "tattoo.png" : originalName.trim();

        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }

        if (name.toLowerCase(Locale.ROOT).endsWith(".png")) {
            name = name.substring(0, name.length() - 4);
        }

        name = name.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_\\-.]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_+", "")
                .replaceAll("_+$", "");

        if (name.isEmpty()) {
            name = "tattoo";
        }

        return name + ".png";
    }

    public static String idPathFromFileName(String fileName) {
        String name = fileName;

        if (name.toLowerCase(Locale.ROOT).endsWith(".png")) {
            name = name.substring(0, name.length() - 4);
        }

        name = name.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_\\-.]", "_")
                .replace('.', '_')
                .replace('-', '_')
                .replaceAll("_+", "_")
                .replaceAll("^_+", "")
                .replaceAll("_+$", "");

        if (name.isEmpty()) {
            name = "tattoo";
        }

        return "tattoos/" + name;
    }

    public static String displayNameFromFileName(String fileName) {
        String name = fileName;

        if (name.toLowerCase(Locale.ROOT).endsWith(".png")) {
            name = name.substring(0, name.length() - 4);
        }

        name = name.replace('_', ' ').replace('-', ' ').trim();

        if (name.isEmpty()) {
            return "Tattoo";
        }

        StringBuilder builder = new StringBuilder();
        boolean capitalizeNext = true;

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            if (Character.isWhitespace(c)) {
                builder.append(c);
                capitalizeNext = true;
                continue;
            }

            builder.append(capitalizeNext ? Character.toUpperCase(c) : c);
            capitalizeNext = false;
        }

        return builder.toString();
    }

    public static Path uniquePath(Path folder, String sanitizedFileName) {
        Path target = folder.resolve(sanitizedFileName);
        if (!target.toFile().exists()) {
            return target;
        }

        String base = sanitizedFileName;
        if (base.toLowerCase(Locale.ROOT).endsWith(".png")) {
            base = base.substring(0, base.length() - 4);
        }

        int index = 2;
        while (true) {
            Path candidate = folder.resolve(base + "_" + index + ".png");
            if (!candidate.toFile().exists()) {
                return candidate;
            }
            index++;
        }
    }
}