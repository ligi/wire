package com.squareup.wire;

import com.squareup.javapoet.JavaFile;
import java.nio.file.Path;

final class StringWireLogger implements WireLogger {
  private final boolean isQuiet;
  private StringBuilder buffer = new StringBuilder();

  public StringWireLogger(boolean quiet) {
    this.isQuiet = quiet;
  }

  @Override public void artifact(Path outputPath, JavaFile javaFile) {
    buffer.append(outputPath);
    buffer.append(" ");
    buffer.append(javaFile.packageName);
    buffer.append(".");
    buffer.append(javaFile.typeSpec.name);
    buffer.append('\n');
  }

  @Override public void info(String message) {
    if (!isQuiet) {
      buffer.append(message);
      buffer.append('\n');
    }
  }

  public String getLog() {
    return buffer.toString();
  }
}
