package outskirts.util;

import java.io.InputStream;

public final class Identifier {

    private String domain;
    private String path;

    public Identifier(String id) {
        String[] bound = resolve(id);
        this.domain = bound[0];
        this.path = bound[1];
    }

    public Identifier(String domain, String path) {
        this.domain = domain;
        this.path = path;
    }

    private static String[] resolve(String id) {
        return id.contains(":") ? StringUtils.explode(id, ":") : new String[] {"outskirts", id};
    }

    public final InputStream getInputStream() {
        return ResourceManager.getInputStream(domain, path);
    }

    public String getDomain() {
        return domain;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return domain + ":" + path;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Identifier) && ((Identifier)obj).domain.equals(this.domain) && ((Identifier)obj).path.equals(this.path);
    }

    @Override
    public int hashCode() {
        return domain.hashCode() ^ path.hashCode();
    }
}
