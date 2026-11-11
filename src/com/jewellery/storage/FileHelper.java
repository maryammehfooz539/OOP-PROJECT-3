package com.jewellery.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

// FileHelper class: small helper methods to read and write text files.
// All data of the project (items, customers, sales, rates, uploaded pictures) lives in ONE data folder.
// By default that folder is "data" inside the folder where the program was started.
// It can be changed with the Java option -Djewellery.data=C:/some/folder or the variable JEWELLERY_DATA (useful when deploying).
public class FileHelper {

    // Name of the default data folder
    private static final String DEFAULT_FOLDER = "data";

    // Returns the path of the data folder
    public static String getDataFolder() {
        String folder = System.getProperty("jewellery.data");
        if (folder == null || folder.trim().isEmpty()) {
            folder = System.getenv("JEWELLERY_DATA");   // a hosting service can set this variable
        }
        if (folder == null || folder.trim().isEmpty()) {
            folder = DEFAULT_FOLDER;
        }
        return folder;
    }

    // Returns the full path of one file inside the data folder, e.g. dataFile("items.txt")
    public static String dataFile(String fileName) {
        return getDataFolder() + File.separator + fileName;
    }

    // Returns the folder where pictures uploaded by the admin are saved
    public static String getUploadFolder() {
        return dataFile("uploads");
    }

    // Checks if a file exists
    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    // Reads a text file (UTF-8) and returns all non-empty lines.
    // try-with-resources closes the file automatically, even when an error happens.
    // Throws DataFileException if the file cannot be read.
    public static ArrayList<String> readLines(String path) {
        ArrayList<String> lines = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while (line != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new DataFileException("Could not read the data file: " + path, e);
        }
        return lines;
    }

    // Writes all lines to a text file (UTF-8) and creates the data folder if needed.
    // Throws DataFileException if the file cannot be written.
    public static void writeLines(String path, ArrayList<String> lines) {
        File file = new File(path);
        File folder = file.getParentFile();
        if (folder != null && !folder.exists() && !folder.mkdirs()) {
            throw new DataFileException("Could not create the data folder: " + folder.getPath(), null);
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (int i = 0; i < lines.size(); i++) {
                writer.write(lines.get(i));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new DataFileException("Could not save the data file: " + path, e);
        }
    }
}
