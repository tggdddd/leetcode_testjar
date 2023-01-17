package Generator;

import java.util.regex.Pattern;

/**
 * @ClassName _String
 * @Description
 * @Author 15014
 * @Time 2023/1/17 17:34
 * @Version 1.0
 */
public class _String extends Generator{
    @Override
    public void set(Object value) {
        String temp = (String) value;
        if(temp.startsWith("\"")&&temp.endsWith("\"")&&temp.length()>1){
            this.value = temp.substring(1,temp.length()-1);
        }else {
            this.value = value;
        }
    }
}
