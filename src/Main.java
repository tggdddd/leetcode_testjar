import Generator.Example;

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
        //处理list数据并执行Solution
        utils.dispose(Solution.class,list);
        //对Solution执行结果进行输出  utils.result
        System.out.println("运行结果如下：");
        utils.print();


    }
}

class Solution {
    public int countNicePairs(Example nums) {
        return 1;
    }
}