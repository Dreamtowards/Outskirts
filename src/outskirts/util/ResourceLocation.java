package outskirts.util;

import java.io.InputStream;

public final class ResourceLocation {

    private String resourceDomain;
    private String resourcePath;

    public ResourceLocation(String resource) {
        String[] bound = resolve(resource);
        this.resourceDomain = bound[0];
        this.resourcePath = bound[1];
    }

    public ResourceLocation(String resourceDomain, String resourcePath) {
        this.resourceDomain = resourceDomain;
        this.resourcePath = resourcePath;
    }

    private static String[] resolve(String resource) {
        if (resource.contains(":")) {
            return resource.split(":");
        } else {
            return new String[] {"outskirts", resource};
        }
    }

    public final InputStream getInputStream() {
        return ResourceManager.getInputStream(resourceDomain, resourcePath);
    }

    public String getResourceDomain() {
        return resourceDomain;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    @Override
    public String toString() {
        return resourceDomain + ":" + resourcePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResourceLocation) {
            return resourceDomain.equals(((ResourceLocation) obj).resourceDomain) && resourcePath.equals(((ResourceLocation) obj).resourcePath);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
