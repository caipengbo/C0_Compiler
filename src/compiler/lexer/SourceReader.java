package compiler.lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Title：读入器
 * Description： 读取每一行C0源程序
 * Created by Myth on 4/13/2017.
 */
public class SourceReader {
    /**
     * 每一行C0源代码
     */
    private List<String> sourceCodeLineList;

    /**
     * 源代码最大行数
     */
    private static final int MAX_SIZE = 100;

    public SourceReader() {
        this.sourceCodeLineList = new ArrayList<>();
    }

    public List<String> getSourceCodeLineList() {
        return sourceCodeLineList;
    }

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
        while((line = bufferedReader.readLine())!=null) {
            sourceCodeLineList.add(line);
        }
        if (sourceCodeLineList.size() > MAX_SIZE) {
            throw new Exception("源程序过长！");
        }
    }


}
