package eu.simplejson;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class JsonHelper {

    public static final int MIN_BUFFER_SIZE = 10;
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final Pattern NEED_ESCAPE_NAME = Pattern.compile("[,\\{\\[\\}\\]\\s:#\"']|//|/\\*");

    /**
     * Creates an Object from scratch
     *
     * @param tClass the object class
     */
    public static <T> T createEmptyObject(Class<T> tClass) {
        if (tClass.isEnum()) {
            return tClass.getEnumConstants()[0];
        }
        try {
            return tClass.newInstance();
        } catch (Exception ignored) {

            try {
                Constructor<?> constructor;

                try {
                    List<Constructor<?>> constructors = Arrays.asList(tClass.getDeclaredConstructors());

                    constructors.sort(Comparator.comparingInt(Constructor::getParameterCount));

                    constructor = constructors.get(constructors.size() - 1);
                } catch (Exception e) {
                    constructor = null;
                }

                //Iterates through all Constructors to create a new Instance of the Object
                //And to set all values to null, -1 or false
                T object = null;
                if (constructor != null) {
                    Object[] args = new Object[constructor.getParameters().length];
                    for (int i = 0; i < constructor.getParameterTypes().length; i++) {
                        final Class<?> parameterType = constructor.getParameterTypes()[i];
                        if (Number.class.isAssignableFrom(parameterType)) {
                            args[i] = -1;
                        } else if (parameterType.equals(boolean.class) || parameterType.equals(Boolean.class)) {
                            args[i] = false;
                        } else if (parameterType.equals(int.class) || parameterType.equals(double.class) || parameterType.equals(short.class) || parameterType.equals(long.class) || parameterType.equals(float.class) || parameterType.equals(byte.class)) {
                            args[i] = -1;
                        } else if (parameterType.equals(Integer.class) || parameterType.equals(Double.class) || parameterType.equals(Short.class) || parameterType.equals(Long.class) || parameterType.equals(Float.class) || parameterType.equals(Byte.class)) {
                            args[i] = -1;
                        } else {
                            args[i] = null;
                        }
                    }
                    object = (T) constructor.newInstance(args);
                }

                if (object == null) {
                    if (!tClass.isInterface()) {
                        object = tClass.newInstance();
                    }
                }

                return object;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static boolean isPunctuatedChar(int c) {
        return c == '{' || c == '}' || c == '[' || c == ']' || c == ',' || c == ':';
    }

    /**
     * Loads all subclasses (extended classes)
     * of another class
     *
     * @param clazz the start-class
     * @return set of classes
     */
    public static List<Class<?>> loadAllSubClasses(Class<?> clazz) {
        List<Class<?>> res = new ArrayList<>();


        do {
            res.add(clazz);

            // First, add all the interfaces implemented by this class
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length > 0) {
                res.addAll(Arrays.asList(interfaces));

                for (Class<?> interfaze : interfaces) {
                    res.addAll(loadAllSubClasses(interfaze));
                }
            }

            // Add the super class
            Class<?> superClass = clazz.getSuperclass();

            // Interfaces does not have java,lang.Object as superclass, they have null, so break the cycle and return
            if (superClass == null) {
                break;
            }

            // Now inspect the superclass
            clazz = superClass;
        } while (!"java.lang.Object".equals(clazz.getCanonicalName()));

        return res;
    }

    public static boolean isWhiteSpace(int ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }

    /**
     * Checks if the provided text starts with something
     *
     * @param text the text
     * @return boolean value (yes/no)
     */
    public static boolean startsWith(String text) {
        int p;
        if (text.startsWith("true") || text.startsWith("null")) {
            p = 4;
        } else if (text.startsWith("false")) {
            p = 5;
        } else {
            return false;
        }
        while (p < text.length() && isWhiteSpace(text.charAt(p))) {
            p++;
        }
        if (p == text.length()) {
            return true;
        }
        char ch = text.charAt(p);
        return ch == ',' || ch == '}' || ch == ']' || ch == '#' || ch == '/' && (text.length() > p + 1 && (text.charAt(p + 1) == '/' || text.charAt(p + 1) == '*'));
    }
    /**
     * Gets the Wrapper-class of a primitive class
     *
     * @param primitiveClass the primitive class
     * @return the class
     */
    public static Class<?> getWrapperClassForPrimitive(Class<?> primitiveClass) {
        if (primitiveClass == boolean.class) {
            return Boolean.class;
        } else if (primitiveClass == int.class) {
            return Integer.class;
        } else if (primitiveClass == double.class) {
            return Double.class;
        } else if (primitiveClass == byte.class) {
            return Byte.class;
        } else if (primitiveClass == short.class) {
            return Short.class;
        } else if (primitiveClass == float.class) {
            return Float.class;
        } else if (primitiveClass == long.class) {
            return Long.class;
        } else if (primitiveClass == char.class) {
            return Character.class;
        } else {
            return primitiveClass;
        }
    }


}
