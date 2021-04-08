package cn.sliew.milky.resource.source;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface Resource extends Source {

    InputStream getInputStream() throws IOException;

    boolean exists();

    URL getURL() throws IOException;

    Resource createRelative(String relativePath) throws IOException;

    String getDescription();
}
