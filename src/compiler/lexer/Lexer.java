package compiler.lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Title：词法分析器
 * Description：分析源程序的每一个字符，组合成单词，将单词转化成符号
 * Created by Myth on 4/27/2017.
 */
public class Lexer {
    //每一行C0源代码
    private List<String> sourceCodeLineList;

    //当前源代码行数
    private int lineSize = 0;
    //源代码最大行数
    private static final int MAX_LINE_SIZE = 100;

    //终止位置
    private static final CharPosition END_CHAR_POSITION = new CharPosition(-1,-1);

    /**
     * 打开原文件，读取每一行原文件存至
     * @param pathname
     * @throws Exception
     */
    public void openFile(String pathname) throws Exception{
        File file = new File(pathname);
        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        sourceCodeLineList = new ArrayList<>();
        while((line = bufferedReader.readLine())!=null) {
            sourceCodeLineList.add(line);
        }
        lineSize = sourceCodeLineList.size();
        if (lineSize > MAX_LINE_SIZE) {
            lineSize = 0;
            throw new Exception("源程序过长！");
        }
    }

    /*
     * 前移位置
     * @param lineNumber
     * @param position
     * @return
     */
    private CharPosition forthPostion(String currentLine,int lineNumber,int position) {
        if (position >= currentLine.length()-1) {
            lineNumber++;
            position = 0;
        } else {
            position++;
        }
        if (lineNumber >= lineSize) {
            return new CharPosition(-1,-1);
        }
        return new CharPosition(lineNumber,position);
    }
    /**
     * 将源代码转换成符号
     * @param startPostion 开始分析的字符位置
     * @return 分析完一个符号后的位置,作为下一次转换的开始位置
     * @throws Exception
     */
    public CharPosition convertToSymbol(CharPosition startPostion) throws Exception {
        if (startPostion.equals(END_CHAR_POSITION)) return startPostion; //终止位置，终止
        int lineNumber = startPostion.getLineNumber();
        int position = startPostion.getPosition();
        CharPosition returnPostion;
        String currentLine = sourceCodeLineList.get(lineNumber);
        while (currentLine.charAt(position) == ' ') {
            returnPostion = forthPostion(currentLine,lineNumber,position);
        }

    }
}
