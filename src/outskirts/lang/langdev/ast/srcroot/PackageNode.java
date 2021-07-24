package outskirts.lang.langdev.ast.srcroot;

import java.util.List;

public class PackageNode {

    public String pkgNodeName;

    public List<PackageNode> childNodes;

    public int type;  // package, class

}
