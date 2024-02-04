package org.SacconeSissaZappia.GitHubIntegrationMicroservice.UnzipUtil;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/***
 * A very simple utility class that provides basic methods to unzip files with ZIP format.
 * This class leverages the classes of the java.util.zip package.
 * The paths passed to this class are absolute paths, the class is only concerned with the extraction.
 */
public class UnzipUtil {

    /***
     * This class will never be instantiated directly, it simply provides utility methods which are static.
     */
    private UnzipUtil(){
    }

    public static void unzipFileFromTo(Path fromFile, Path toDir) throws IOException {

        /* Generate the zipInputStream to read the content of the input file */
        ZipInputStream readZip = new ZipInputStream(new FileInputStream(fromFile.toFile()));
        byte[] buffer = new byte[4096];     // Byte array for reading the various entries of the zip file

        /* In case the second parameter is null, the extraction happens in the same directory where the file is placed */
        if(toDir == null){
            toDir = Path.of(fromFile.getParent().toString());
        }

        /* Create the directory in which the file result of the extraction has to be placed */
        if (!toDir.toFile().exists()) {
            toDir.toFile().mkdirs();
        }

        /* Looping on the entries of the zipInputStream and perform the extraction */
        ZipEntry entry = readZip.getNextEntry();
        while (entry != null) {
            /* Create absolute path to this new file or directory */
            Path toEntry = toDir.resolve(Path.of(entry.getName()));

            /* Divide the cases of a directory or a file */
            if (entry.isDirectory()) {
                toEntry.toFile().mkdirs();
            } else {
                /* Root directory of zip file not returned by the getNextEntry calls */
                if (!toEntry.getParent().toFile().exists()) {
                    toEntry.getParent().toFile().mkdirs();
                }
                /* Copy the file in new location */
                OutputStream output = new FileOutputStream(toEntry.toFile());
                int read;
                while ((read = readZip.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.close();
            }
            entry = readZip.getNextEntry();
        }
        /* Clean memory */
        readZip.closeEntry();
        readZip.close();
    }

    public static void renameDirectory(Path parentDirectory, String initialPartOfDirectoryName, String newDirectoryName) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(parentDirectory)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry) && entry.getFileName().toString().startsWith(initialPartOfDirectoryName)) {
                    // Construct the new directory name
                    String newDirectoryPath = entry.resolveSibling(newDirectoryName).toString();

                    // Rename the directory
                    Files.move(entry, Paths.get(newDirectoryPath), StandardCopyOption.REPLACE_EXISTING);
                    return; // Assuming there is only one matching directory, exit after renaming.
                }
            }

            System.out.println("No directory matching the specified initial part found in the parent directory.");
        }
    }

    public static void removeFolderRecursively(Path pathToFolder) {
        try {
            Files.walkFileTree(pathToFolder, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    // Handle failure to visit a file (e.g., permission issue)
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            // Handle IOException
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        removeFolderRecursively(Path.of("C:\\Users\\matte\\Desktop\\target"));
    }

}


