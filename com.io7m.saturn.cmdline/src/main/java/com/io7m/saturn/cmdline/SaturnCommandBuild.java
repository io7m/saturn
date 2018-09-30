/*
 * Copyright © 2018 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.saturn.cmdline;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.io7m.saturn.container.api.SaturnContainerBuilderType;
import com.io7m.saturn.container.api.SaturnContainerDescription;
import com.io7m.saturn.container.builder.felix.SaturnContainerBuilderFelix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.List;

/**
 * A command for building containers.
 */

@Parameters(
  commandNames = "build",
  commandDescription = "Build a container")
public final class SaturnCommandBuild extends SaturnCommandRoot
{
  private static final Logger LOG = LoggerFactory.getLogger(SaturnCommandBuild.class);

  @Parameter(
    names = "--output",
    required = true,
    description = "The output directory")
  private Path path_output;

  @Parameter(
    names = "--remote-shell-address",
    description = "The remote shell address")
  private String remote_shell_address = "127.0.0.1";

  @Parameter(
    names = "--remote-shell-port",
    description = "The remote shell port")
  private int remote_shell_port = 6666;

  @Parameter(
    names = "--add-bundle",
    description = "Bundles to be installed")
  private List<Path> bundles = List.of();

  /**
   * Construct a command.
   */

  public SaturnCommandBuild()
  {

  }

  @Override
  public Status execute()
    throws Exception
  {
    super.execute();

    final SaturnContainerBuilderType builder =
      SaturnContainerBuilderFelix.createBuilder();

    final SaturnContainerDescription description =
      SaturnContainerDescription.builder()
        .setPath(this.path_output.toAbsolutePath())
        .addAllBundles(this.bundles)
        .setRemoteShellAddress(
          InetSocketAddress.createUnresolved(
            this.remote_shell_address,
            this.remote_shell_port))
        .build();

    builder.createContainer(description);
    return Status.SUCCESS;
  }
}
