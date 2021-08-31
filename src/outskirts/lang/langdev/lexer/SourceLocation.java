package outskirts.lang.langdev.lexer;

import outskirts.util.Intptr;
import outskirts.util.StringUtils;

public class SourceLocation {

    private String sourceLocation;
    private String source;

    private int beginIndex;
    private int endIndex;

    private int _lineNumber;
    private int _charNumber;

    public SourceLocation(String sourceLocation, String source, int beginIndex, int endIndex) {
        this.sourceLocation = sourceLocation;
        this.source = source;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;

        // setup linenumber cache.
        Intptr ln = Intptr.zero(), cn = Intptr.zero();
        StringUtils.locate(source, beginIndex, ln, cn);
        _lineNumber = ln.i;
        _charNumber = cn.i;
    }

    public int getLineNumber() {
        return _lineNumber;
    }
    public int getCharNumber() {
        return _charNumber;
    }
}
