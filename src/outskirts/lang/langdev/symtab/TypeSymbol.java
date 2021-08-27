package outskirts.lang.langdev.symtab;

public interface TypeSymbol {

    String getQualifiedName();

    default String getSimpleName() {
        String fullname = getQualifiedName();
        int i = fullname.lastIndexOf('.');
        return i==-1 ? fullname : fullname.substring(i+1);
    }
    // int getBaseType();

}
