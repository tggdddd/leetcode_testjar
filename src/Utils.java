import Generator.Generator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
        Object doClass = null;
        // 使用Solution类
        if (clasz.getSimpleName().equals("Solution")) {
            methods = Arrays.stream(methods).filter(method -> method.getModifiers() == Modifier.PUBLIC).toArray(Method[]::new);
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
            doClass = clasz.newInstance();
            Runtime r = Runtime.getRuntime();
            r.gc();// 计算内存前先垃圾回收一次
            long start = System.currentTimeMillis();// 开始Time
            long startMem = r.freeMemory(); // 开始Memory
            result = method.invoke(doClass, params.toArray());
            long endMem = r.freeMemory(); // 末尾Memory
            long end = System.currentTimeMillis();// 末尾Time
            // 输出
            System.out.println("用时消耗: " + (end - start) + "ms");
            System.out.println("内存消耗: " + (startMem - endMem) / 1024 + "KB");
            resultType = method.getReturnType();
        }
        // 使用其他的类
        else {
            System.out.printf("%-25s结果\n", "方法");
            System.out.println("----------------------------------------");
            // 获得方法调用次数
            int disposeLength = ((List) list.get(0)).size();
            // 调用方法
            for (int i = 0; i < disposeLength; i++) {
                // 获得方法名
                Class parameterType = String.class;
                Object methodName = getParam(parameterType, ((List) list.get(0)).get(i));
                Method method = null;
                for (Method method1 : methods) {
                    if (method1.getName().equals(methodName)) {
                        method = method1;
                        break;
                    }
                }
                // 构造函数
                if (method == null) {
                    if (((List) list.get(1)).get(i) == null) {
                        doClass = clasz.newInstance();
                    } else {
                        // 获得构造参数
                        Constructor constructor = clasz.getDeclaredConstructors()[0];
                        Class<?>[] parameterTypes = constructor.getParameterTypes();
                        LinkedList params = new LinkedList();
                        for (int j = 0; j < parameterTypes.length; j++) {
                            parameterType = parameterTypes[j];
                            Object param = getParam(parameterType, ((List) ((List) list.get(1)).get(i)).get(j));
                            params.add(param);
                        }
                        doClass = constructor.newInstance(params.toArray());
                    }
                } else {
                    // 获得方法参数
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    LinkedList params = new LinkedList();
                    for (int j = 0; j < parameterTypes.length; j++) {
                        parameterType = parameterTypes[j];
                        Object param = getParam(parameterType, ((List) ((List) list.get(1)).get(i)).get(j));
                        params.add(param);
                    }
                    if (method.getParameterCount() != params.size()) {
                        throw new Exception("方法参数的数量与文件的参数数量不符");
                    }
                    result = method.invoke(doClass, params.toArray());
                    resultType = method.getReturnType();
                    System.out.printf("%-25s  ", methodName + ":");
                    print();
                    System.out.println();
                }
            }
        }
    }

    public void print() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        print(resultType,result);
    }

    public void print(Class type,Object object) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        if (type.isArray()) {
            for (int j = 0; j < Array.getLength(object); j++) {
                print(type.getComponentType(), Array.get(object,j));
            }
            System.out.println();
        } else {
            Generator generator = (Generator) Class.forName("Generator._"+type.getSimpleName()).newInstance();
            generator.print(object);
        }
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
