import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import Generator.*;
/**
 * @ClassName Utils
 * @Description
 * @Author 15014
 * @Time 2023/1/17 11:47
 * @Version 1.0
 */
public class Utils {
    public static final char PRE_SQUARE_BRACKET = '[';
    public static final char SUF_SQUARE_BRACKET = ']';
    public static final char COMMA = ',';

    /* 方法调用后的返回值 */
    public Object result;
    public Class resultType;
    /**
     * @param clasz Solution类的位置
     * @return 返回一个包含输入参数的列表
     * @Description 读取文件输入参数
     */
    void dispose(Class clasz, LinkedList list) throws Exception {
        Method[] methods = clasz.getDeclaredMethods();
        if (methods.length != 1) {
            throw new Exception("无法识别到调用的方法，请不要将Solution内的其他方法的访问范围设定为public");
        }
        Method method = methods[0];
        if (method.getParameterCount() != list.size()) {
            throw new Exception("方法参数的数量与文件的行数不符");
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        LinkedList params = new LinkedList();
        for (int i = 0; i < list.size(); i++) {
            Class parameterType = parameterTypes[i];
            Object param = getParam(parameterType, list.get(i));
            params.add(param);
        }
        Runtime r = Runtime. getRuntime();
        r.gc();//计算内存前先垃圾回收一次
        long start = System.currentTimeMillis();//开始Time
        long startMem = r.freeMemory(); // 开始Memory
        result = method.invoke(clasz.newInstance(), params.toArray());
        long endMem =r.freeMemory(); // 末尾Memory
        long end = System.currentTimeMillis();//末尾Time
        //输出
        System.out.println("用时消耗: "+ (end - start) +"ms");
        System.out.println("内存消耗: "+ (startMem - endMem) / 1024 +"KB");
        resultType = method.getReturnType();
    }
    public void print() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Generator generator = (Generator) Class.forName("Generator._"+resultType.getSimpleName()).newInstance();
        generator.print(result);
    }
    public Object getParam(Class parameterType, Object list) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        if (parameterType.isArray()) {
            LinkedList p = (LinkedList) list;
            Object res = Array.newInstance(parameterType.getComponentType(), p.size());
            for (int j = 0; j < p.size(); j++) {
                Object o = getParam(parameterType.getComponentType(), p.get(j));
                Array.set(res, j, o);
            }
            return res;
        } else {
            Method set;
            Generator generator = (Generator) Class.forName("Generator._"+parameterType.getSimpleName()).newInstance();
            set = generator.getClass().getMethod("set", Object.class);
            Method get = generator.getClass().getMethod("get");
            set.invoke(generator, list);
            return get.invoke(generator);
        }
    }

    /**
     * @param file 读取文件的位置
     * @return 返回一个包含输入参数的列表
     * @Description 读取文件输入参数
     */
    public LinkedList readFile(String file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        LinkedList res = new LinkedList();
        while ((line = bufferedReader.readLine()) != null) {
            if (line.charAt(0) == PRE_SQUARE_BRACKET) {
                createList(res, line, 0);
            } else {
                res.add(line);
            }
        }
        return res;
    }

    /**
     * @return 返回一个包含输入参数的列表
     * @Description 读取文件输入参数
     */
    public LinkedList readFile() throws IOException {
        return readFile("input.txt");
    }

    /**
     * @param res  列表
     * @param line String行
     * @param pos  String行的读取坐标
     * @return String行的读取坐标
     * @Description 生成元素列表
     */
    private int createList(LinkedList res, String line, int pos) {
        while (pos < line.length()) {
            switch (line.charAt(pos)) {
                //    前方括号 新增列表
                case PRE_SQUARE_BRACKET:
                    LinkedList sub = new LinkedList();
                    res.add(sub);
                    pos = createList(sub, line, pos + 1);
                    break;
                //    后方括号，表明列表已经结束
                case SUF_SQUARE_BRACKET:
                    return pos + 1;
                //   分隔符，表明下一个列表
                case COMMA:
                    pos++;
                    break;
                //    遇到元素开始获取 pos指到分隔符
                default:
                    int suffix = line.substring(pos).indexOf(SUF_SQUARE_BRACKET);
                    String[] elems = line.substring(pos, pos + suffix).split(String.valueOf(COMMA));
                    for (String elem : elems) {
                        res.add(elem);
                    }
                    pos = pos + suffix + 1;
                    return pos;
            }
        }
        return pos;
    }
}
