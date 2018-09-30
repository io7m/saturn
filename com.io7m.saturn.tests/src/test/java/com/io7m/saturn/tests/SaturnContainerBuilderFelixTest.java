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

package com.io7m.saturn.tests;

import com.io7m.saturn.container.api.SaturnContainerBuilderType;
import com.io7m.saturn.container.api.SaturnContainerDescription;
import com.io7m.saturn.container.builder.felix.SaturnContainerBuilderFelix;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SaturnContainerBuilderFelixTest
{
  @Test
  public void testCreateEmpty()
    throws IOException
  {
    final SaturnContainerBuilderType builder =
      SaturnContainerBuilderFelix.createBuilder();

    final Path path =
      Files.createTempDirectory("saturn-container-felix-");

    builder.createContainer(
      SaturnContainerDescription.builder()
        .setPath(path)
        .setRemoteShellAddress(InetSocketAddress.createUnresolved("127.0.0.1", 6000))
        .build());

    Assertions.assertAll(
      () -> Assertions.assertTrue(Files.isDirectory(path.resolve("system"))),
      () -> Assertions.assertTrue(Files.isDirectory(path.resolve("host"))),
      () -> Assertions.assertTrue(Files.isDirectory(path.resolve("log"))),
      () -> Assertions.assertTrue(Files.isDirectory(path.resolve("lib"))),
      () -> Assertions.assertTrue(Files.isDirectory(path.resolve("cache"))),
      () -> Assertions.assertTrue(Files.isRegularFile(path.resolve("logback.xml")))
    );
  }
}
