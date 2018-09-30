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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class SaturnFelixSystemBundles
{
  private static final Logger LOG =
    LoggerFactory.getLogger(SaturnFelixSystemBundles.class);

  private SaturnFelixSystemBundles()
  {

  }

  public static List<SaturnFelixSystemBundle> hostBundles()
    throws IOException
  {
    try (final BufferedReader reader = openHostBundleList()) {
      return loadList(reader, "host");
    }
  }

  public static List<SaturnFelixSystemBundle> systemBundles()
    throws IOException
  {
    try (final BufferedReader reader = openSystemBundleList()) {
      return loadList(reader, "system");
    }
  }

  private static BufferedReader openSystemBundleList()
  {
    return new BufferedReader(
      new InputStreamReader(
        SaturnContainerBuilderFelix.class.getResourceAsStream(
          "/com/io7m/saturn/container/builder/felix/artifacts_system.txt"), UTF_8));
  }

  private static BufferedReader openHostBundleList()
  {
    return new BufferedReader(
      new InputStreamReader(
        SaturnContainerBuilderFelix.class.getResourceAsStream(
          "/com/io7m/saturn/container/builder/felix/artifacts_host.txt"), UTF_8));
  }

  private static List<SaturnFelixSystemBundle> loadList(
    final BufferedReader reader,
    final String type)
  {
    final List<SaturnFelixSystemBundle> bundles = new ArrayList<>(16);
    for (final String raw_line : reader.lines().collect(Collectors.toList())) {
      final String name = filterLine(raw_line);
      final String jar_name = name + ".jar";
      final String path =
        new StringBuilder(64)
          .append("/com/io7m/saturn/container/builder/felix/")
          .append(type)
          .append("/")
          .append(jar_name)
          .toString();

      LOG.trace("jar: {}", path);
      bundles.add(
        SaturnFelixSystemBundle.builder()
          .setName(name)
          .setResourcePath(path)
          .build());
    }
    return bundles;
  }

  private static String filterLine(
    final String raw_line)
  {
    return raw_line.replace(',', ' ').trim();
  }
}
