package compiler.common;

/**
 * Title：错误提示类
 * Description：为错误处理机制所设置的类
 * Created by Myth on 4/15/2017.
 */
public class Wrong {
    private int no;
    private String info;
    private String position;
    private int sourceLine = -1;
    public Wrong(String position, int no, String info) {
        this.no = no;
        this.info = info;
        this.position = position;
        //System.out.println(this.toString());
    }
    //调试专用，为了知道是Java代码中哪里产生的错误
    public Wrong(String position, int no, String info, int sourceLine) {
        this.no = no;
        this.info = info;
        this.position = position;
        this.sourceLine = sourceLine;
        //System.out.println(this.toString());
    }
    @Override
    public String toString() {
        //return "错误 " + no + ":" + info + " 位置：" + position ;
        return "错误 " + no + ":" + info + " 位置：" + position + "源码行：" + sourceLine;
    }
}
