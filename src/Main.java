import java.util.LinkedList;

/**
 * @ClassName t
 * @Description
 * @Author 15014
 * @Time 2023/1/12 13:54
 * @Version 1.0
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Utils utils = new Utils();
        //读取input文件
        // utils.readFile("input.txt");
        LinkedList list = utils.readFile();
        // 处理list数据并执行Solution
        // utils.dispose(MKAverage.class,list);
        utils.dispose(Solution.class, list);
        // 对Solution执行结果进行输出  utils.result
        System.out.println("最终运行结果为：");
        utils.print();
    }
}

class Solution {
    public int get() {
        return 1;
    }
}

class MKAverage {
    public MKAverage(int m, int k) {
    }

    public void addElement(int num) {

    }

    public int calculateMKAverage() {
        return 1;
    }
}