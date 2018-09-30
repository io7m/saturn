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

package com.io7m.saturn.container.launcher.felix;

import com.io7m.saturn.container.api.SaturnContainerDescription;
import com.io7m.saturn.container.api.SaturnContainerDescriptions;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Properties;

/**
 * Command-line launcher.
 */

public final class SaturnContainerLauncherFelixMain
{
  private static final Logger LOG =
    LoggerFactory.getLogger(SaturnContainerLauncherFelixMain.class);

  private SaturnContainerLauncherFelixMain()
  {

  }

  /**
   * Command-line entry point.
   *
   * @param args The arguments
   *
   * @throws Exception On errors
   */

  public static void main(
    final String[] args)
    throws Exception
  {
    if (args.length != 1) {
      throw new IllegalArgumentException("Usage: container.conf");
    }

    LOG.info("booting");

    try {
      final FileSystem filesystem = FileSystems.getDefault();
      final Properties props;
      try (InputStream stream = Files.newInputStream(filesystem.getPath(args[0]).toAbsolutePath())) {
        props = new Properties();
        props.load(stream);
      }

      final SaturnContainerDescription description =
        SaturnContainerDescriptions.parse(filesystem, props);

      final Framework framework =
        SaturnContainerLauncherFelix.createLauncher()
          .launch(description);

      final FrameworkEvent result = framework.waitForStop(0L);
      final Throwable failure = result.getThrowable();
      if (failure != null) {
        throw new IllegalStateException(failure);
      }
    } finally {
      LOG.info("exiting");
    }
  }
}
