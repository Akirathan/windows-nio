package org.akirathan.windows.nio;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.containsString;


public class WindowsFetchFromJarTest {
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();
  private Path zipFilePath;

  @Before
  public void setup() throws IOException {
    var tmpDir = tmpFolder.newFolder();
    var subdir = tmpDir.toPath().resolve("subdir");
    subdir.toFile().mkdirs();
    var fileInSubdir = subdir.resolve("file.txt");
    Files.writeString(fileInSubdir, "This is a test file.", StandardOpenOption.CREATE_NEW);
    zipFilePath = tmpDir.toPath().resolve("test.zip");
    zipDirectory(tmpDir.toPath(), zipFilePath);
    assert zipFilePath.toFile().exists();
  }

  @Test
  public void testFetchFileFromZip_ViaURLConnection() throws Exception {
    var zipFileURI = zipFilePath.toUri();
    URI fileInJarURI;
    if (isOnWindows()) {
      fileInJarURI = URI.create("jar:" + zipFileURI + "!/subdir/file.txt");
    } else {
      fileInJarURI = URI.create("jar:" + zipFileURI + "!/subdir/file.txt");
    }
    var conn = fileInJarURI.toURL().openConnection();
    try (var is = conn.getInputStream()) {
      var content = new String(is.readAllBytes());
      assertThat(content, is("This is a test file."));
    } catch (IOException e) {
      System.err.println(
          "fileInJarURI: " + fileInJarURI
      );
      throw new AssertionError(e);
    }
  }

  private static boolean isOnWindows() {
    String osName = System.getProperty("os.name").toLowerCase();
    return osName.contains("win");
  }

  private static void zipDirectory(Path dirToZip, Path zipOut) throws IOException {
    try (var outputStream =
             Files.newOutputStream(
                 zipOut, StandardOpenOption.CREATE_NEW)) {
      try (var zipOutStream = new ZipOutputStream(outputStream)) {
        try (var inputDirStream = Files.walk(dirToZip)) {
          inputDirStream
              .filter(path -> !Files.isDirectory(path))
              .forEach(
                  path -> {
                    var zipEntryName = dirToZip.relativize(path).toString();
                    try {
                      zipOutStream.putNextEntry(new ZipEntry(zipEntryName));
                      Files.copy(path, zipOutStream);
                      zipOutStream.closeEntry();
                    } catch (IOException e) {
                      throw new AssertionError(e);
                    }
                  });
        }
      }
    }
  }
}
