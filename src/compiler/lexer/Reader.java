package compiler.lexer;

import java.util.LinkedList;
import java.util.List;

/**
 * Title：读入器
 * Description： 读取 C0源程序各个单词 转化为 符号symbol
 * Created by Myth on 4/13/2017.
 */
public class Reader {
    /**
     * 每一行C0源代码
     */
    private List<String> sourceCodeLineList;

    public Reader() {
        this.sourceCodeLineList = new LinkedList<>();
    }

    //TODO:初步想法：读取所有源程序，将各个单词转化为Symbol

}
