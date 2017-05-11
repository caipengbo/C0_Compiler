package compiler.parser;

/**
 * Title：函数类
 * Description： 描述函数定义
 * Created by Myth on 5/11/2017.
 */
public class Function {
    private String name;
    private boolean hasReturn; //是否有返回值
    /*
    生成解释代码时，说明需要的栈空间大小 开辟空间
    ， 初始为3，最终大小 = 变量定义个数 + 初始 + 返回值(如果有)
     */
    private int size = 3;

    public Function(String name, boolean hasReturn) {
        this.name = name;
        this.hasReturn = hasReturn;
    }

    @Override
    public String toString() {
        return "name:" + name + " hasReturn:" + hasReturn + " size:" + size;
    }
}
