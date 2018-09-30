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
import com.io7m.saturn.container.api.SaturnContainerLauncherType;
import com.io7m.saturn.container.builder.felix.SaturnContainerBuilderFelix;
import com.io7m.saturn.container.launcher.felix.SaturnContainerLauncherFelix;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.launch.Framework;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public final class SaturnContainerLauncherFelixTest
{
  @Test
  public void testCreateEmpty()
  {
    Assertions.assertTimeout(Duration.ofSeconds(15L), () -> {
      final SaturnContainerBuilderType builder = SaturnContainerBuilderFelix.createBuilder();
      final SaturnContainerLauncherType launcher = SaturnContainerLauncherFelix.createLauncher();

      final Path path =
        Files.createTempDirectory("saturn-container-felix-");

      final SaturnContainerDescription description =
        SaturnContainerDescription.builder()
          .setPath(path)
          .build();

      builder.createContainer(description);

      final Framework framework = launcher.launch(description);
      Thread.sleep(2_000L);

      final Bundle[] bundles = framework.getBundleContext().getBundles();
      Assertions.assertTrue(bundles.length >= 2, "Bundles are present");

      for (final Bundle bundle : bundles) {
        Assertions.assertEquals(Bundle.ACTIVE, bundle.getState());
      }

      framework.stop();
      framework.waitForStop(1_000L);
    });
  }
}
