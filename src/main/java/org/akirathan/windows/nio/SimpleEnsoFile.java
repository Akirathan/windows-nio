package org.akirathan.windows.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

/**
 * Simplified {@code EnsoFile.java}.
 */
public class SimpleEnsoFile {
  public static void createDirectories(Path path) throws IOException {
    Files.createDirectories(path);
  }

  public static void delete(Path path, boolean recursive) throws IOException {
    if (recursive && Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
      deleteRecursively(path);
    } else {
      Files.delete(path);
    }
  }

  private static void deleteRecursively(Path file) throws IOException {
    if (Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS)) {
      try (var entries = Files.newDirectoryStream(file)) {
        for (var entry : entries) {
          deleteRecursively(entry);
        }
      }
    }
    Files.delete(file);
  }
}
