package com.jewellery.servlet;

import com.jewellery.storage.FileHelper;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// UploadServlet: shows the pictures that the admin uploaded from his computer.
// Uploaded pictures are saved in the data folder (data/uploads), which is outside the web pages folder,
// so the browser asks this servlet for them using the address /uploads/<file name>.
public class UploadServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getPathInfo();   // for /uploads/item-12.png this is "/item-12.png"

        // safety check: only a plain file name is allowed (no folders, no "..")
        if (name == null || name.length() < 2) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        name = name.substring(1);
        if (name.contains("/") || name.contains("\\") || name.contains("..")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File file = new File(FileHelper.getUploadFolder(), name);
        if (!file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(findType(name));
        response.setContentLengthLong(file.length());
        try (OutputStream out = response.getOutputStream()) {
            Files.copy(file.toPath(), out);   // sends the picture bytes to the browser
        }
    }

    // Chooses the content type from the file ending
    private String findType(String name) {
        String lower = name.toLowerCase();
        if (lower.endsWith(".png")) {
            return "image/png";
        } else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lower.endsWith(".gif")) {
            return "image/gif";
        } else if (lower.endsWith(".webp")) {
            return "image/webp";
        } else if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "application/octet-stream";
    }
}
