package com.randomidentity.tools;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SkinPoolGenerator {

    private static final int TARGET_SKINS = 400;

    private static final HttpClient HTTP =
            HttpClient.newBuilder()
                    .followRedirects(
                            HttpClient.Redirect.NORMAL
                    )
                    .build();

    /*
     * NameMC's random skin page.
     */
    private static final String NAMEMC_URL =
            "https://namemc.com/minecraft-skins/random";

    /*
     * Minecraft texture IDs are 64 hexadecimal
     * characters.
     */
    private static final Pattern TEXTURE_ID =
            Pattern.compile(
                    "textures\\.minecraft\\.net/texture/"
                            + "([a-fA-F0-9]{64})"
            );

    public static void main(String[] args)
            throws Exception {

        System.out.println(
                "RandomIdentity Skin Pool Generator"
        );

        System.out.println(
                "Target skins: "
                        + TARGET_SKINS
        );

        Set<String> textures =
                new LinkedHashSet<>();

        /*
         * Fetch several random pages.
         *
         * Each page contains a collection of
         * random Minecraft skins.
         */
        for (int page = 0;
                page < 25 &&
                textures.size() < TARGET_SKINS;
                page++) {

            String url;

            if (page == 0) {

                url = NAMEMC_URL;

            } else {

                url =
                        NAMEMC_URL
                                + "/"
                                + page;
            }

            System.out.println(
                    "Scanning: "
                            + url
            );

            String html =
                    download(url);

            extractTextures(
                    html,
                    textures
            );

            System.out.println(
                    "Found "
                            + textures.size()
                            + " unique textures."
            );
        }

        /*
         * If the source provided fewer than 400,
         * tell the user rather than inventing IDs.
         */
        if (textures.size() < TARGET_SKINS) {

            System.out.println();
            System.out.println(
                    "WARNING:"
            );

            System.out.println(
                    "Only found "
                            + textures.size()
                            + " valid texture IDs."
            );

            System.out.println(
                    "No fake IDs will be added."
            );
        }

        writeSkinsFile(
                textures
        );

        System.out.println();
        System.out.println(
                "Done!"
        );

        System.out.println(
                "Wrote "
                        + textures.size()
                        + " skins to skins.yml"
        );
    }

    private static String download(
            String url
    ) throws IOException,
            InterruptedException {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(url)
                        )
                        .header(
                                "User-Agent",
                                "RandomIdentity/1.0"
                        )
                        .GET()
                        .build();

        HttpResponse<String> response =
                HTTP.send(
                        request,
                        HttpResponse.BodyHandlers.ofString(
                                StandardCharsets.UTF_8
                        )
                );

        if (response.statusCode() != 200) {

            throw new IOException(
                    "HTTP "
                            + response.statusCode()
                            + " while downloading "
                            + url
            );
        }

        return response.body();
    }

    private static void extractTextures(
            String html,
            Set<String> textures
    ) {

        Matcher matcher =
                TEXTURE_ID.matcher(
                        html
                );

        while (matcher.find()) {

            String texture =
                    matcher.group(1)
                            .toLowerCase();

            textures.add(
                    texture
            );

            if (textures.size()
                    >= TARGET_SKINS) {

                break;
            }
        }
    }

    private static void writeSkinsFile(
            Set<String> textures
    ) throws IOException {

        StringBuilder yaml =
                new StringBuilder();

        yaml.append(
                "# RandomIdentity skin pool\n"
        );

        yaml.append(
                "# Generated automatically.\n"
        );

        yaml.append(
                "# Texture IDs are hosted by Mojang.\n\n"
        );

        yaml.append(
                "skins:\n"
        );

        for (String texture :
                textures) {

            yaml.append(
                    "  - \""
            );

            yaml.append(
                    texture
            );

            yaml.append(
                    "\"\n"
            );
        }

        yaml.append(
                "\nsettings:\n"
        );

        yaml.append(
                "  avoid-duplicates: true\n"
        );

        yaml.append(
                "  cache-skins: true\n"
        );

        Path output =
                Path.of(
                        "src",
                        "main",
                        "resources",
                        "skins.yml"
                );

        Files.createDirectories(
                output.getParent()
        );

        Files.writeString(
                output,
                yaml.toString(),
                StandardCharsets.UTF_8
        );
    }
}