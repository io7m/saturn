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

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

/**
 * A system bundle.
 */

@Value.Immutable
@ImmutablesStyleType
public interface SaturnFelixSystemBundleType
{
  /**
   * @return The path to the bundle inside a hosting jar file
   */

  @Value.Parameter
  String resourcePath();

  /**
   * @return The name of the bundle (minus the .jar suffix)
   */

  @Value.Parameter
  String name();

  /**
   * @return The name of the jar
   */

  @Value.Derived
  default String jarName()
  {
    return this.name() + ".jar";
  }
}
