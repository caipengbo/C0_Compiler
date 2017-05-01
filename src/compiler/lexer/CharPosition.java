package compiler.lexer;

/**
 * Title：字符位置
 * Description：用于描述正在分析的源码位置，行号，行中位置
 * Created by Myth on 4/27/2017.
 */
public class CharPosition {
    private int lineNumber; //哪一行，-1终止
    private int position; //行内位置, -1终止

    public CharPosition() {}

    public CharPosition(int lineNumber, int position) {
        this.lineNumber = lineNumber;
        this.position = position;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CharPosition)) {
            return false;
        }
        if (((CharPosition) obj).getLineNumber() == lineNumber && ((CharPosition) obj).getPosition() == position) {
            return true;
        }
        return false;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "{" + "line=" + lineNumber + ", position=" + position + '}';
    }
}
