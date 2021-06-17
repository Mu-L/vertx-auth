/*
 * Copyright (c) 2021 Dmitry Novikov
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package io.vertx.ext.auth.otp;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.ext.auth.otp.impl.OtpKeyGeneratorImpl;

@VertxGen
public interface OtpKeyGenerator {

  OtpKey generate();

  OtpKey generate(int keySize);

  String getAlgorithm();

  static OtpKeyGenerator create() {
    return new OtpKeyGeneratorImpl();
  }

  static OtpKeyGenerator create(String algorithm) {
    return new OtpKeyGeneratorImpl(algorithm);
  }
}
