package compiler.parser;

/**
 * Title：变量
 * Description：
 * Created by Myth on 5/4/2017.
 */
public class Variable {
    private String name;
    private String scope; //作用域 全局(global)  函数(函数名)
    private int address = -1; //相对位置
    public Variable(String name, String scope, int address) {
        this.name = name;
        this.scope = scope;
        this.address = address;
    }

    public String getScope() {
        return scope;
    }

    public int getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", scope='" + scope + '\'' +
                ", address=" + address +
                '}';
    }
}
