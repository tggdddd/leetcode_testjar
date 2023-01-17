package Generator;

/**
 * @ClassName _int
 * @Description
 * @Author 15014
 * @Time 2023/1/17 17:29
 * @Version 1.0
 */
public class _int extends Generator {
    @Override
    public void set(Object value) {
        this.value = Integer.valueOf((String) value);
    }
}
