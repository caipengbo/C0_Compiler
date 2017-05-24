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
    private int size;
    /**
     * 函数（的入口地址在解释程序中的下标）
     */
    private int entryAddress = 0;

    public Function(String name, boolean hasReturn) {
        this.name = name;
        this.hasReturn = hasReturn;
        if (hasReturn == true) {
            this.size = 4;
        } else {
            this.size = 3;
        }
    }

    /**
     * 在原来的size的基础上加上addSize
     * @param addSize
     */
    public void updateSize(int addSize) {
        this.size += addSize;
    }

    public int getSize() {
        return size;
    }

    public void setEntryAddress(int entryAddress) {
        this.entryAddress = entryAddress;
    }

    public int getEntryAddress() {
        return entryAddress;
    }

    @Override
    public String toString() {
        return "Function{" +
                "name='" + name + '\'' +
                ", hasReturn=" + hasReturn +
                ", size=" + size +
                ", entryAddress=" + entryAddress +
                '}';
    }
}
