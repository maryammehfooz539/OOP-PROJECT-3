package com.jewellery.storage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Files;

// ImageDownloader class: when the admin pastes an online picture link (for example from Pinterest) in the item
// form, this class downloads that picture and saves it inside the project (data/uploads), so the picture
// stays after the program is closed, shows without internet and goes with the project folder to other computers.
// The code that downloads (HttpURLConnection) was written for this project.
public class ImageDownloader {

    // A picture may be at most 3 MB (same limit as the upload button)
    private static final int MAX_BYTES = 3 * 1024 * 1024;

    // Some websites refuse programs, so we say we are a normal browser
    private static final String BROWSER = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/120.0 Safari/537.36";

    // Downloads the picture of a link and saves it in the upload folder.
    //  - Returns the saved address (like "uploads/item-1700000000000.jpg") when it worked.
    //  - Returns null when the picture could not be reached (no internet, website refused): the caller keeps the link.
    //  - Throws IllegalArgumentException (with a clear message) when the link is not a usable picture.
    public static String download(String link) {
        String current = link;
        try {
            for (int hop = 0; hop < 5; hop++) {   // we follow at most 5 redirects
                URL url = new URL(current);
                if (!isAllowedAddress(url)) {
                    throw new IllegalArgumentException("This address cannot be used for a picture link.");
                }
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty("User-Agent", BROWSER);
                connection.setRequestProperty("Accept", "image/webp,image/*,*/*;q=0.8");

                int code = connection.getResponseCode();
                if (code == 301 || code == 302 || code == 303 || code == 307 || code == 308) {
                    String next = connection.getHeaderField("Location");
                    connection.disconnect();
                    if (next == null) {
                        return null;
                    }
                    current = new URL(url, next).toString();   // the new address
                    continue;
                }
                if (code != 200) {
                    connection.disconnect();
                    return null;   // refused or not found: keep the link
                }

                String extension = findExtension(connection.getContentType(), current);
                byte[] data = readLimited(connection.getInputStream());
                connection.disconnect();
                return save(data, extension);
            }
        } catch (IOException e) {
            return null;   // no internet, timeout ...
        }
        return null;
    }

    // Decides the file ending from the content type of the answer (or from the link)
    private static String findExtension(String contentType, String link) {
        String type = contentType == null ? "" : contentType.toLowerCase().split(";")[0].trim();
        if (type.equals("image/jpeg") || type.equals("image/jpg")) {
            return "jpg";
        } else if (type.equals("image/png")) {
            return "png";
        } else if (type.equals("image/gif")) {
            return "gif";
        } else if (type.equals("image/webp")) {
            return "webp";
        } else if (type.equals("image/svg+xml")) {
            return "svg";
        } else if (type.startsWith("image/")) {
            throw new IllegalArgumentException("This picture format is not supported. Please use JPG, PNG, GIF, WEBP or SVG.");
        } else if (type.isEmpty() || type.equals("application/octet-stream") || type.equals("binary/octet-stream")) {
            // the website did not say what it is: trust the ending of the link
            String lower = link.toLowerCase().split("\\?")[0];
            String[] endings = {"jpg", "jpeg", "png", "gif", "webp", "svg"};
            for (int i = 0; i < endings.length; i++) {
                if (lower.endsWith("." + endings[i])) {
                    return endings[i].equals("jpeg") ? "jpg" : endings[i];
                }
            }
        }
        throw new IllegalArgumentException("This link is a web page, not a picture. Right click the picture and choose "
                + "\"Copy image address\", then paste that link.");
    }

    // Reads the picture bytes, but stops with an error if the picture is bigger than 3 MB
    private static byte[] readLimited(InputStream in) throws IOException {
        try (InputStream input = in) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read = input.read(buffer);
            while (read != -1) {
                out.write(buffer, 0, read);
                if (out.size() > MAX_BYTES) {
                    throw new IllegalArgumentException("The picture is too big (more than 3 MB). Please choose a smaller one.");
                }
                read = input.read(buffer);
            }
            return out.toByteArray();
        }
    }

    // Writes the bytes to data/uploads under a new name and returns the address like "uploads/item-123.jpg"
    private static String save(byte[] data, String extension) throws IOException {
        File folder = new File(FileHelper.getUploadFolder());
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Could not create the upload folder");
        }
        long stamp = System.currentTimeMillis();
        File file = new File(folder, "item-" + stamp + "." + extension);
        while (file.exists()) {   // a very fast second picture: make the name different
            stamp++;
            file = new File(folder, "item-" + stamp + "." + extension);
        }
        Files.write(file.toPath(), data);
        return "uploads/" + file.getName();
    }

    // Only normal internet addresses: a picture link must not point inside our own computer or network
    // (this keeps a public website safe). For tests this can be switched off with -Djewellery.allowPrivateDownloads=true
    private static boolean isAllowedAddress(URL url) throws IOException {
        String protocol = url.getProtocol();
        if (!protocol.equals("http") && !protocol.equals("https")) {
            return false;
        }
        if ("true".equals(System.getProperty("jewellery.allowPrivateDownloads"))) {
            return true;
        }
        InetAddress address = InetAddress.getByName(url.getHost());   // an unknown host throws IOException
        return !(address.isLoopbackAddress() || address.isSiteLocalAddress()
                || address.isLinkLocalAddress() || address.isAnyLocalAddress());
    }
}
