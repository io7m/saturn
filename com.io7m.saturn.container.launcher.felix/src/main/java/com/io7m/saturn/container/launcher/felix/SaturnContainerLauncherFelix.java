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
import com.io7m.saturn.container.api.SaturnContainerLauncherType;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.felix.framework.util.FelixConstants.LOG_LEVEL_PROP;
import static org.apache.felix.framework.util.FelixConstants.LOG_LOGGER_PROP;
import static org.osgi.framework.Constants.FRAMEWORK_STORAGE;
import static org.osgi.framework.Constants.FRAMEWORK_STORAGE_CLEAN;
import static org.osgi.framework.Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA;

/**
 * A launcher of Felix containers.
 */

public final class SaturnContainerLauncherFelix implements SaturnContainerLauncherType
{
  private static final Logger LOG = LoggerFactory.getLogger(SaturnContainerLauncherFelix.class);

  private static final Pattern VERSION_PATTERN =
    Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)(.*)");

  private SaturnContainerLauncherFelix()
  {

  }

  /**
   * @return A new launcher
   */

  public static SaturnContainerLauncherType createLauncher()
  {
    return new SaturnContainerLauncherFelix();
  }

  @Override
  public Framework launch(
    final SaturnContainerDescription description)
    throws Exception
  {
    Objects.requireNonNull(description, "description");

    final Path root = Files.createDirectories(description.path());
    final Path root_system = Files.createDirectories(root.resolve("system"));
    final Path root_cache = Files.createDirectories(root.resolve("cache"));

    /*
     * Get access to an OSGi framework factory and configure the required
     * properties to enable logging. Note the use of an Apache Felix specific
     * configuration value that passes in a Logger implementation.
     */

    final FrameworkFactory frameworks =
      ServiceLoader.load(FrameworkFactory.class).iterator().next();

    final Map<String, Object> config = new HashMap<>();
    config.put(FRAMEWORK_STORAGE, root_cache.toString());
    config.put(FRAMEWORK_STORAGE_CLEAN, "onFirstInit");
    config.put(LOG_LEVEL_PROP, "999");
    config.put(LOG_LOGGER_PROP, new SaturnContainerFelixLogger());
    exportHostPackages(config);

    final Object cast = config;
    @SuppressWarnings("unchecked") final Map<String, String> config_strings = (Map<String, String>) cast;

    /*
     * Start the framework.
     */

    LOG.debug("starting framework");

    final Framework framework = frameworks.newFramework(config_strings);
    framework.start();

    final BundleContext c = framework.getBundleContext();

    /*
     * Install all of the bundles.
     */

    final List<Bundle> bundles = new LinkedList<>();
    installSystemBundles(root_system, c, bundles);
    installApplicationBundles(description, c, bundles);
    startAllBundles(bundles);
    return framework;
  }

  private static void startAllBundles(final List<Bundle> bundles)
    throws BundleException
  {
    LOG.debug("starting {} bundles", Integer.valueOf(bundles.size()));

    for (final Bundle bundle : bundles) {
      LOG.debug("starting: {}", bundle);
      bundle.start();
    }

    for (final Bundle bundle : bundles) {
      final int state = bundle.getState();
      switch (state) {
        case Bundle.UNINSTALLED: {
          LOG.debug("bundle {} is UNINSTALLED", bundle);
          break;
        }
        case Bundle.INSTALLED: {
          LOG.debug("bundle {} is INSTALLED", bundle);
          break;
        }
        case Bundle.RESOLVED: {
          LOG.debug("bundle {} is RESOLVED", bundle);
          break;
        }
        case Bundle.STARTING: {
          LOG.debug("bundle {} is STARTING", bundle);
          break;
        }
        case Bundle.STOPPING: {
          LOG.debug("bundle {} is STOPPING", bundle);
          break;
        }
        case Bundle.ACTIVE: {
          LOG.debug("bundle {} is ACTIVE", bundle);
          break;
        }
      }
    }
  }

  private static void installApplicationBundles(
    final SaturnContainerDescription description,
    final BundleContext c,
    final List<Bundle> bundles)
    throws BundleException
  {
    LOG.debug("installing application bundles");

    if (description.bundles().isEmpty()) {
      LOG.debug("no application bundles to install");
      return;
    }

    for (final Path path : description.bundles()) {
      final String location =
        new StringBuilder(64)
          .append("reference:file:")
          .append(path.toAbsolutePath())
          .toString();

      LOG.debug("installing {}", location);
      final Bundle bundle = c.installBundle(location);
      bundles.add(bundle);
      bundle.adapt(BundleStartLevel.class).setStartLevel(5);
    }
  }

  private static void installSystemBundles(
    final Path root_system,
    final BundleContext c,
    final List<Bundle> bundles)
    throws BundleException, IOException
  {
    LOG.debug("installing system bundles");

    final List<Path> bundle_paths;
    try (Stream<Path> path_stream = Files.list(root_system)) {
      bundle_paths = path_stream.collect(Collectors.toList());
    }

    if (bundle_paths.isEmpty()) {
      LOG.debug("no system bundles to install");
      return;
    }

    for (final Path path : bundle_paths) {
      final String pack = path.getFileName().toString();
      final String file =
        new StringBuilder(128)
          .append("reference:file:")
          .append(root_system)
          .append("/")
          .append(pack)
          .toString();

      LOG.debug("installing {} ({})", pack, file);
      final Bundle bundle = c.installBundle(file);
      bundles.add(bundle);
      bundle.adapt(BundleStartLevel.class).setStartLevel(1);
    }
  }

  /**
   * Expose the host's SLF4J API to the container. This ensures that any time a package requires the
   * SLF4J, the actual implementation will be resolved to the one on the host. Additionally,
   * export sun.misc in order to allow access to sun.misc.Unsafe.
   */

  private static void exportHostPackages(
    final Map<String, Object> config)
  {
    final Package package_ = Logger.class.getPackage();
    final String raw_version = package_.getImplementationVersion();
    LOG.debug("SLF4J raw version: {}", raw_version);

    final Matcher matcher = VERSION_PATTERN.matcher(raw_version);
    if (!matcher.matches()) {
      throw new IllegalStateException("Unparseable package version: " + raw_version);
    }

    final String clean_version =
      new StringBuilder(64)
        .append(matcher.group(1))
        .append('.')
        .append(matcher.group(2))
        .toString();

    final Collection<String> exports = new ArrayList<>(8);
    exports.add(export("org.slf4j", clean_version));
    exports.add(export("org.slf4j.*", clean_version));
    exports.add(export("sun.misc", "0.0.0"));
    exports.add(export("sun.misc.resources", "0.0.0"));

    exports.forEach(e -> LOG.debug("export: {}", e));
    final String joined = String.join(",", exports);
    LOG.debug("exports joined: {}", joined);
    config.put(FRAMEWORK_SYSTEMPACKAGES_EXTRA, joined);
  }

  private static String export(
    final String package_name,
    final String version)
  {
    return String.format("%s;version=\"%s\"", package_name, version);
  }
}
