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

import com.io7m.saturn.container.api.SaturnContainerDescription;
import com.io7m.saturn.container.api.SaturnContainerDescriptions;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.Properties;

public final class SaturnContainerDescriptionTest
{
  @Test
  public void testEquals()
  {
    EqualsVerifier.forClass(SaturnContainerDescription.class)
      .withNonnullFields("bundles", "path", "remoteShellAddress")
      .verify();
  }

  @Test
  public void testProperties()
    throws IOException
  {
    final SaturnContainerDescription description_input =
      SaturnContainerDescription.builder()
        .addBundles(Paths.get("/a/b/c"))
        .addBundles(Paths.get("/a/b/d"))
        .addBundles(Paths.get("/a/b/e"))
        .setPath(Paths.get("/x/y/z"))
        .setRemoteShellAddress(InetSocketAddress.createUnresolved("127.0.0.1", 6000))
        .build();

    final Properties properties =
      SaturnContainerDescriptions.serialize(description_input);

    final SaturnContainerDescription description_output =
      SaturnContainerDescriptions.parse(FileSystems.getDefault(), properties);

    Assertions.assertEquals(description_input, description_output);
  }

  @Test
  public void testPropertiesMissingPath()
  {
    final SaturnContainerDescription description_input =
      SaturnContainerDescription.builder()
        .addBundles(Paths.get("/a/b/c"))
        .addBundles(Paths.get("/a/b/d"))
        .addBundles(Paths.get("/a/b/e"))
        .setPath(Paths.get("/x/y/z"))
        .setRemoteShellAddress(InetSocketAddress.createUnresolved("127.0.0.1", 6000))
        .build();

    final Properties properties =
      SaturnContainerDescriptions.serialize(description_input);

    properties.remove("saturn.path");

    Assertions.assertThrows(IOException.class, () -> {
      SaturnContainerDescriptions.parse(FileSystems.getDefault(), properties);
    });
  }

  @Test
  public void testPropertiesMissingRemoteShellAddress()
  {
    final SaturnContainerDescription description_input =
      SaturnContainerDescription.builder()
        .addBundles(Paths.get("/a/b/c"))
        .addBundles(Paths.get("/a/b/d"))
        .addBundles(Paths.get("/a/b/e"))
        .setPath(Paths.get("/x/y/z"))
        .setRemoteShellAddress(InetSocketAddress.createUnresolved("127.0.0.1", 6000))
        .build();

    final Properties properties =
      SaturnContainerDescriptions.serialize(description_input);

    properties.remove("saturn.remote_shell_address");

    Assertions.assertThrows(IOException.class, () -> {
      SaturnContainerDescriptions.parse(FileSystems.getDefault(), properties);
    });
  }

  @Test
  public void testPropertiesMissingRemoteShellPort()
  {
    final SaturnContainerDescription description_input =
      SaturnContainerDescription.builder()
        .addBundles(Paths.get("/a/b/c"))
        .addBundles(Paths.get("/a/b/d"))
        .addBundles(Paths.get("/a/b/e"))
        .setPath(Paths.get("/x/y/z"))
        .setRemoteShellAddress(InetSocketAddress.createUnresolved("127.0.0.1", 6000))
        .build();

    final Properties properties =
      SaturnContainerDescriptions.serialize(description_input);

    properties.remove("saturn.remote_shell_port");

    Assertions.assertThrows(IOException.class, () -> {
      SaturnContainerDescriptions.parse(FileSystems.getDefault(), properties);
    });
  }
}
