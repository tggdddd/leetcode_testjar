package Generator;

/**
 * @ClassName _boolean
 * @Description
 * @Author 15014
 * @Time 2023/1/19 11:37
 * @Version 1.0
 */
public class _boolean extends Generator {
    public void set(Object value) {
        this.value = Boolean.parseBoolean(String.valueOf(value));
    }
}
