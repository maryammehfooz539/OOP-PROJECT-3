package com.jewellery.storage;

// DataFileException class: our own exception, thrown when a data file (items.txt, sales.txt ...)
// cannot be read or written. It is an unchecked exception (RuntimeException), so the manager
// methods stay simple. LoginServlet catches it at start-up, and web.xml sends any other
// DataFileException to the friendly error.jsp page instead of showing a raw Java error.
public class DataFileException extends RuntimeException {

    // message = what went wrong (shown to the user), cause = the original IOException
    public DataFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
