/*
 * Copyright © 2018 Mark Raynsford <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.saturn.container.builder.felix;

import com.io7m.saturn.container.api.SaturnContainerBuilderType;
import com.io7m.saturn.container.api.SaturnContainerDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class SaturnContainerBuilderFelix implements SaturnContainerBuilderType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(SaturnContainerBuilderFelix.class);

  private SaturnContainerBuilderFelix()
  {

  }

  public static SaturnContainerBuilderType createBuilder()
  {
    return new SaturnContainerBuilderFelix();
  }

  private static void copyBundles(
    final List<SaturnFelixSystemBundle> jars,
    final String type,
    final Path system_dir)
    throws IOException
  {
    for (final SaturnFelixSystemBundle jar : jars) {
      final Path output_path = system_dir.resolve(jar.jarName());
      try (OutputStream output = Files.newOutputStream(output_path)) {
        try (InputStream input = SaturnContainerBuilderFelix.class.getResourceAsStream(jar.resourcePath())) {
          LOG.trace("copy {} {} {}", type, jar.resourcePath(), output_path);
          input.transferTo(output);
          output.flush();
        }
      }
    }
  }

  @Override
  public void createContainer(
    final SaturnContainerDescription description)
    throws IOException
  {
    Objects.requireNonNull(description, "description");

    final Path root = description.path();
    LOG.debug("creating container {}", root);

    final Path host_dir = root.resolve("host");
    Files.createDirectories(host_dir);
    final Path system_dir = root.resolve("system");
    Files.createDirectories(system_dir);
    final Path lib_dir = root.resolve("lib");
    Files.createDirectories(lib_dir);
    final Path cache_dir = root.resolve("cache");
    Files.createDirectories(cache_dir);

    copyBundles(SaturnFelixSystemBundles.hostBundles(), "host", host_dir);
    copyBundles(SaturnFelixSystemBundles.systemBundles(), "system", system_dir);
  }
}

