package compiler.lexer;

/**
 * Title：字符位置
 * Description：用于描述正在分析的源码位置，行号，行中位置
 * Created by Myth on 4/27/2017.
 */
public class CharPosition {
    private int lineNumber; //哪一行
    private int position; //行内位置

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
}