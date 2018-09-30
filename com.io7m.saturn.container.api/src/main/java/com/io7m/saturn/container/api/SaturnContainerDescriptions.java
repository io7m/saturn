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

package com.io7m.saturn.container.api;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

/**
 * Functions to serialize and parse container descriptions.
 */

public final class SaturnContainerDescriptions
{
  private SaturnContainerDescriptions()
  {

  }

  /**
   * Parse a description from the given properties.
   *
   * @param filesystem The filesystem used to construct paths
   * @param properties The properties
   *
   * @return A description
   *
   * @throws IOException On I/O and parse errors
   */

  public static SaturnContainerDescription parse(
    final FileSystem filesystem,
    final Properties properties)
    throws IOException
  {
    Objects.requireNonNull(filesystem, "filesystem");
    Objects.requireNonNull(properties, "properties");

    final SaturnContainerDescription.Builder builder = SaturnContainerDescription.builder();

    IOException exception = null;
    exception = parsePath(filesystem, properties, builder, exception);
    exception = parseBundles(filesystem, properties, builder, exception);

    if (exception != null) {
      throw exception;
    }

    return builder.build();
  }

  private static IOException parseBundles(
    final FileSystem filesystem,
    final Properties properties,
    final SaturnContainerDescription.Builder builder,
    final IOException exception)
  {
    int index = 0;
    while (true) {
      final String bundle_path = properties.getProperty("saturn.bundle." + Integer.toUnsignedString(
        index));
      if (bundle_path == null) {
        break;
      }

      builder.addBundles(filesystem.getPath(bundle_path).toAbsolutePath());
      ++index;
    }
    return exception;
  }

  private static IOException parsePath(
    final FileSystem filesystem,
    final Properties properties,
    final SaturnContainerDescription.Builder builder,
    final IOException exception)
  {
    final String path = properties.getProperty("saturn.path");
    if (path == null) {
      return addException(exception, "Missing key: saturn.path");
    }

    builder.setPath(filesystem.getPath(path).toAbsolutePath());
    return exception;
  }

  private static IOException addException(
    final IOException exception,
    final String message)
  {
    if (exception == null) {
      return new IOException(message);
    }
    exception.addSuppressed(exception);
    return exception;
  }

  /**
   * Serialize a description.
   *
   * @param description The description
   *
   * @return Serialized properties
   */

  public static Properties serialize(
    final SaturnContainerDescription description)
  {
    Objects.requireNonNull(description, "description");

    final Properties props = new Properties();
    props.setProperty("saturn.path", description.path().toAbsolutePath().toString());

    int index = 0;
    for (final Path bundle : description.bundles()) {
      props.setProperty(
        "saturn.bundle." + Integer.toUnsignedString(index),
        bundle.toAbsolutePath().toString());
      ++index;
    }

    return props;
  }

}
