package compiler.common;

/**
 * Title：源程序中的每个单词
 * Description：词法分析识别源程序中的每个单词
 * Created by Myth on 2017/4/20.
 */
public class Word {
    private Symbol type; //单词的类别
    private String value; //如果是标识符，存入标识符的名字,如果是数字,存入数字的值（注意转化成int）

    public Symbol getType() {
        return type;
    }

    public void setType(Symbol type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
