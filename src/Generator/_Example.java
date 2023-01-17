package Generator;

import java.util.List;

/**
 * @ClassName _Example
 * @Description
 * @Author 15014
 * @Time 2023/1/17 18:37
 * @Version 1.0
 */
public class _Example extends Generator{
    @Override
    public void set(Object value) {
        List<List> list = (List<List>) value;
        for (List s : list) {
            for (Object o : s) {
                System.out.print(o +" ");
            }
            System.out.println();
        }
    }

    @Override
    public void print() {
        super.print();
    }
}
