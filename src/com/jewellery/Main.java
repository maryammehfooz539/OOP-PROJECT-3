package com.jewellery;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

// Main class: this is the class you RUN in IntelliJ IDEA (right-click -> Run 'Main').
// It starts a small web server (Apache Tomcat, embedded - its jar files are in the "lib" folder),
// loads our website from the "webapp" folder and opens http://localhost:8080/ in the browser.
// No Maven or separate server installation is needed.
//
// REFERENCE: starting Tomcat from a main() method follows the "Embedded Tomcat" way described in the
// official Apache Tomcat documentation (https://tomcat.apache.org/tomcat-9.0-doc/). The code below was
// written for this project.
public class Main {

    // The port number of the website: http://localhost:8080/
    // A hosting service (Render, Railway ...) tells us its port in the PORT variable, so we read it first.
    private static final int PORT = findPort();

    // Reads the PORT environment variable (if the host gave one), otherwise uses 8080
    private static int findPort() {
        String fromHost = System.getenv("PORT");
        if (fromHost != null) {
            try {
                return Integer.parseInt(fromHost.trim());
            } catch (NumberFormatException e) {
                System.out.println("PORT variable is not a number, using 8080.");
            }
        }
        return 8080;
    }

    public static void main(String[] args) {
        // The folder of the program: the project folder (when started from IntelliJ) or the folder where
        // gemora.jar / Gemora.exe is (when started by double-click). It holds webapp, lib and data.
        File appFolder = findAppFolder();

        // The folder with the JSP pages, CSS, images and WEB-INF/web.xml
        File webFolder = new File(appFolder, "webapp");
        if (!webFolder.exists()) {
            System.out.println("ERROR: the 'webapp' folder was not found in: " + appFolder.getAbsolutePath());
            System.out.println("In IntelliJ open Run -> Edit Configurations and set 'Working directory' to the project folder.");
            System.out.println("When using Gemora.exe or gemora.jar, keep the 'webapp', 'lib' and 'data' folders next to it.");
            return;
        }
        // The data files (items, customers ...) are kept in the "data" folder next to webapp, unless the
        // owner chose another folder (-Djewellery.data or the JEWELLERY_DATA variable).
        if (System.getProperty("jewellery.data") == null && System.getenv("JEWELLERY_DATA") == null) {
            System.setProperty("jewellery.data", new File(appFolder, "data").getAbsolutePath());
        }

        try {
            Tomcat server = new Tomcat();
            // Tomcat keeps its temporary work files (compiled JSP pages) in the computer's temp folder.
            // We delete the old ones at every start, so an old copy of a page can never be shown
            // after the project was updated.
            File work = new File(System.getProperty("java.io.tmpdir"), "jewellery-tomcat");
            deleteFolder(work);
            server.setBaseDir(work.getAbsolutePath());
            server.setPort(PORT);
            server.getConnector();   // creates the HTTP connector for the port

            // "" means the website opens at the main address (http://localhost:8080/)
            Context website = server.addWebapp("", webFolder.getAbsolutePath());
            website.setParentClassLoader(Main.class.getClassLoader());   // lets the website find our servlet classes

            server.start();
            System.out.println("==============================================");
            System.out.println(" Gemora Jewellery Management System is running");
            System.out.println(" Open:  http://localhost:" + PORT + "/");
            System.out.println(" Password: the admin password (default: admin123)");
            System.out.println(" Stop the server with the red Stop button (or close this window).");
            System.out.println("==============================================");

            openBrowser("http://localhost:" + PORT + "/");
            server.getServer().await();   // keeps the program running until it is stopped
        } catch (LifecycleException e) {
            // most common reason: the port is already used by another program
            System.out.println("Could not start the server: " + e.getMessage());
            System.out.println("Is another copy of this program (or another program) already using port " + PORT + "?");
        }
    }

    // Finds the folder of the program. First choice: the folder where the program was started (the project
    // folder in IntelliJ). If it has no "webapp" folder, we use the folder where gemora.jar is.
    private static File findAppFolder() {
        File here = new File("").getAbsoluteFile();
        if (new File(here, "webapp").exists()) {
            return here;
        }
        try {
            File location = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File beside = location.getParentFile();   // the folder that contains gemora.jar
            if (beside != null && new File(beside, "webapp").exists()) {
                return beside;
            }
        } catch (Exception e) {
            // we simply keep the started-in folder
        }
        return here;
    }

    // Deletes a folder with everything inside it (used to clean Tomcat's temporary work folder)
    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteFolder(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        folder.delete();
    }

    // Tries to open the website in the default browser. If this is not possible, we only print the address.
    private static void openBrowser(String address) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(address));
            }
        } catch (Exception e) {
            System.out.println("Please open " + address + " in your browser.");
        }
    }
}
