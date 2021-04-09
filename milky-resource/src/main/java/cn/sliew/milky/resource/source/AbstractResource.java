//package cn.sliew.milky.resource.source;
//
//import cn.sliew.milky.log.Logger;
//import cn.sliew.milky.log.LoggerFactory;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.net.URL;
//
//public abstract class AbstractResource implements Resource {
//
//    public boolean exists() {
//        Logger logger;
//        if (this.isFile()) {
//            try {
//                return this.getFile().exists();
//            } catch (IOException var4) {
//                logger = LoggerFactory.getLogger(this.getClass());
//                if (logger.isDebugEnabled()) {
//                    logger.debug("Could not retrieve File for existence check of " + this.getDescription(), var4);
//                }
//            }
//        }
//
//        try {
//            this.getInputStream().close();
//            return true;
//        } catch (Throwable var3) {
//            logger = LoggerFactory.getLogger(this.getClass());
//            if (logger.isDebugEnabled()) {
//                logger.debug("Could not retrieve InputStream for existence check of " + this.getDescription(), var3);
//            }
//
//            return false;
//        }
//    }
//
//
//    public boolean isFile() {
//        return false;
//    }
//
//    public URL getURL() throws IOException {
//        throw new FileNotFoundException(this.getDescription() + " cannot be resolved to URL");
//    }
//
//    public Resource createRelative(String relativePath) throws IOException {
//        throw new FileNotFoundException("Cannot create a relative resource for " + this.getDescription());
//    }
//
//    public boolean equals(Object other) {
//        return this == other || other instanceof Resource &&
//                ((Resource) other).getDescription().equals(this.getDescription());
//    }
//
//    public int hashCode() {
//        return this.getDescription().hashCode();
//    }
//
//    public String toString() {
//        return this.getDescription();
//    }
//}
