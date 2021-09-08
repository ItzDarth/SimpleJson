package eu.simplejson.helper;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonString;
import eu.simplejson.helper.adapter.JsonSerializer;
import eu.simplejson.helper.other.JsonProvider;

import java.lang.reflect.Constructor;
import java.util.*;

public class JsonUtils {

    /**
     * Creates an Object from scratch
     *
     * @param tClass the object class
     */
    public static <T> T createEmptyObject(Class<T> tClass) {
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
                    object = tClass.newInstance();
                }

                return object;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
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

    /**
     * Searches for all {@link JsonSerializer}s that match a given {@link Class}
     * If checkSerializersForSubClasses is enabled it will scan for all sub-classes
     * if the main-class does not have any serializer registered
     *
     * @param typeClass the type class
     * @param <T> the generic
     * @return serializer or null
     */
    public static <T> JsonSerializer<T> getSerializerOrNull(Json json, Class<T> typeClass) {
        JsonSerializer<T> jsonSerializer = null;
        List<Class<?>> subClasses = JsonUtils.loadAllSubClasses(typeClass);
        if (json.getRegisteredSerializers().containsKey(typeClass)) {
            jsonSerializer = (JsonSerializer<T>) json.getRegisteredSerializers().get(typeClass);
            if (jsonSerializer == null) {
                if (json.isCheckSerializersForSubClasses()) {
                    for (Class<?> aClass : JsonUtils.loadAllSubClasses(typeClass)) {
                        jsonSerializer = (JsonSerializer<T>) getSerializerOrNull(json, aClass);
                        break;
                    }
                }
            } else {
                return jsonSerializer;
            }
        } else {
            for (Class<?> subClass : subClasses) {
                if (json.getRegisteredSerializers().containsKey(subClass)) {
                    jsonSerializer = (JsonSerializer<T>) getSerializerOrNull(json, subClass);
                }
            }
        }
        return jsonSerializer;
    }

    /**
     * Gets an object from a given {@link JsonEntity} if its adapter
     * exists, otherwise it will just return null
     *
     * @param json the entity
     * @param typeClass the type class
     * @param <T> the generic
     * @return object or null
     */
    public static  <T> T getSerializedOrNull(Json instance, JsonEntity json, Class<T> typeClass) {
        JsonSerializer<T> adapterOrNull = getSerializerOrNull(instance, typeClass);
        return adapterOrNull == null ? null : adapterOrNull.deserialize(json);
    }

    public static boolean isInvalidDsfChar(char c) {
        return c == '{' || c == '}' || c == '[' || c == ']' || c == ',';
    }


    public static JsonEntity parse(JsonProvider[] dsfProviders, String value) {
        for (JsonProvider dsf : dsfProviders) {
            try {
                JsonEntity res = dsf.parse(value);
                if (res != null) return res;
            } catch (Exception exception) {
                throw new RuntimeException("DSF-" + dsf.getName() + " failed; " + exception.getMessage());
            }
        }
        return new JsonString(value);
    }
    public static String stringify(JsonProvider[] dsfProviders, JsonEntity value) {
        for (JsonProvider dsf : dsfProviders) {
            try {
                String text = dsf.toString(value);
                if (text != null) {
                    boolean isInvalid = false;
                    char[] textc = text.toCharArray();
                    for (char ch : textc) {
                        if (isInvalidDsfChar(ch)) {
                            isInvalid = true;
                            break;
                        }
                    }
                    if (isInvalid || text.length() == 0 || textc[0] == '"')
                        throw new Exception("value may not be empty, start with a quote or contain a punctuator character except colon: " + text);
                    return text;
                }
            } catch (Exception exception) {
                throw new RuntimeException("DSF-" + dsf.getName() + " failed; " + exception.getMessage());
            }
        }
        return null;
    }

}
