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

package com.io7m.saturn.container.logservice;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogLevel;
import org.osgi.service.log.LogListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An SLF4J-based log reader.
 */

public final class SaturnSLF4JLogReader implements LogListener
{
  private static final Logger LOG = LoggerFactory.getLogger(SaturnSLF4JLogReader.class);

  SaturnSLF4JLogReader()
  {

  }

  @Override
  public void logged(final LogEntry entry)
  {
    final LogLevel level = entry.getLogLevel();
    switch (level) {
      case AUDIT:
        audit(entry);
        break;
      case ERROR:
        error(entry);
        break;
      case WARN:
        warn(entry);
        break;
      case INFO:
        info(entry);
        break;
      case DEBUG:
        debug(entry);
        break;
      case TRACE:
        trace(entry);
        break;
    }
  }

  private static void audit(final LogEntry entry)
  {
    final String name = entry.getBundle().getSymbolicName();
    final String message = entry.getMessage();
    final Throwable ex = entry.getException();
    if (ex != null) {
      LOG.info("[{}]: {}: ", name, message, ex);
    } else {
      LOG.info("[{}]: {}", name, message);
    }
  }

  private static void trace(final LogEntry entry)
  {
    final String name = entry.getBundle().getSymbolicName();
    final String message = entry.getMessage();
    final Throwable ex = entry.getException();
    if (ex != null) {
      LOG.trace("[{}]: {}: ", name, message, ex);
    } else {
      LOG.trace("[{}]: {}", name, message);
    }
  }

  private static void warn(final LogEntry entry)
  {
    final String name = entry.getBundle().getSymbolicName();
    final String message = entry.getMessage();
    final Throwable ex = entry.getException();
    if (ex != null) {
      LOG.warn("[{}]: {}: ", name, message, ex);
    } else {
      LOG.warn("[{}]: {}", name, message);
    }
  }

  private static void info(final LogEntry entry)
  {
    final String name = entry.getBundle().getSymbolicName();
    final String message = entry.getMessage();
    final Throwable ex = entry.getException();
    if (ex != null) {
      LOG.info("[{}]: {}: ", name, message, ex);
    } else {
      LOG.info("[{}]: {}", name, message);
    }
  }

  private static void error(final LogEntry entry)
  {
    final String name = entry.getBundle().getSymbolicName();
    final String message = entry.getMessage();
    final Throwable ex = entry.getException();
    if (ex != null) {
      LOG.error("[{}]: {}: ", name, message, ex);
    } else {
      LOG.error("[{}]: {}", name, message);
    }
  }

  private static void debug(final LogEntry entry)
  {
    final String name = entry.getBundle().getSymbolicName();
    final String message = entry.getMessage();
    final Throwable ex = entry.getException();
    if (ex != null) {
      LOG.debug("[{}]: {}: ", name, message, ex);
    } else {
      LOG.debug("[{}]: {}", name, message);
    }
  }
}
