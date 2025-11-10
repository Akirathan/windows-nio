package org.akirathan.windows.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.containsString;


public class WindowsNIOTest {
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Test
  public void foo() throws IOException {
    var tmpDir = tmpFolder.newFolder();
    createDirs(tmpDir.toPath());
    SimpleEnsoFile.delete(tmpDir.toPath(), true);
    assertThat(tmpDir.exists(), is(false));
    SimpleEnsoFile.createDirectories(tmpDir.toPath());
    assertThat(tmpDir.exists(), is(true));
  }

  @Test
  public void createAndDeleteDirInLoop() throws IOException {
    var tmpDir = tmpFolder.newFolder();
    var dir = tmpDir.toPath().resolve("dir");
    var file = dir.resolve("file.txt");
    for (int i = 0; i < 1_00; i++) {
      Files.createDirectories(dir);
      try (var writer = Files.newBufferedWriter(file, StandardOpenOption.CREATE_NEW)) {
        writer.write("Iteration " + i);
      }
      Files.delete(file);
      Files.delete(dir);
    }
  }

  private static void createDirs(Path rootDir) throws IOException {
    var dirA = rootDir.resolve("A");
    var dirB = dirA.resolve("B");
    var dirC = dirB.resolve("C");
    Files.createDirectories(dirC);
    var fileB = dirA.resolve("fileB.txt");
    Files.writeString(fileB, "This is file B.", StandardOpenOption.CREATE_NEW);
    assertThat(dirC.toFile().exists(), is(true));
    assertThat(dirA.toFile().exists(), is(true));
  }
}
