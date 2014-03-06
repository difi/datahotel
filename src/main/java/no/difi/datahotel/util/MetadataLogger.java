package no.difi.datahotel.util;

import no.difi.datahotel.model.Metadata;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MetadataLogger {

    private Logger logger = Logger.getAnonymousLogger();
    private Metadata metadata;

    public MetadataLogger(Metadata metadata) {
        this.metadata = metadata;
    }

    public void log(Level level, String message) {
        logger.log(level, message + getExtension());
    }

    public void log(Level level, String message, Throwable throwable) {
        logger.log(level, message + getExtension(), throwable);
    }

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void warning(String message) {
        log(Level.WARNING, message);
    }

    private String getExtension() {
        return " [" + metadata.getLocation() + "]";
    }


}
