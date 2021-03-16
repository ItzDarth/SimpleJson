
package io.vson.elements.object;

import io.vson.VsonValue;
import io.vson.elements.VsonArray;
import io.vson.elements.VsonLiteral;
import io.vson.elements.other.Pair;
import io.vson.enums.FileFormat;
import io.vson.enums.VsonComment;
import io.vson.enums.VsonSettings;
import io.vson.enums.VsonType;
import io.vson.manage.json.JsonParser;
import io.vson.manage.vson.VsonParser;
import io.vson.other.TempVsonOptions;
import io.vson.tree.VsonTree;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VsonObject extends VsonValue implements Iterable<VsonMember> {

    private List<String> names;
    private List<VsonValue> values;

    private File file;
    private HashIndexTable table;
    private final Map<Integer, Pair<String[], VsonComment>> comments = new HashMap<>();
    private List<VsonSettings> vsonSettings = new LinkedList<>();

    public VsonObject() {
        this.names = new LinkedList<>();
        this.values = new LinkedList<>();
        this.table = new HashIndexTable();
    }

    public VsonObject(VsonSettings... vsonSettings) {
        this();
        this.vsonSettings.addAll(Arrays.asList(vsonSettings));
        this.clearCheck();
    }

    public VsonObject(VsonObject object) {
        this(object, false);
        this.clearCheck();
    }

    public VsonObject(VsonObject object, VsonSettings... vsonSettings) {
        this(object, false);
        this.vsonSettings.addAll(Arrays.asList(vsonSettings));
        this.clearCheck();
    }

    public VsonObject(String input) throws IOException {
        this(new JsonParser(input).parse().asVsonObject());
        this.clearCheck();
    }

    public VsonObject(String input, VsonSettings... vsonSettings) throws IOException {
        this(input);
        this.vsonSettings.addAll(Arrays.asList(vsonSettings));
        this.clearCheck();
    }

    public VsonObject(File file) throws IOException {
        this(file.exists() ? new VsonParser(new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)), new TempVsonOptions()).parse().asVsonObject() : new VsonObject());
        this.file = file;
        this.clearCheck();
    }

    public VsonObject(File file, VsonSettings... vsonSettings) throws IOException {
        this(file);
        this.vsonSettings.addAll(Arrays.asList(vsonSettings));
        this.clearCheck();
    }

    private VsonObject(VsonObject object, boolean unmodifiable) {
        this.table = new HashIndexTable();
        this.names = unmodifiable ? Collections.unmodifiableList(object.getNames()) : new LinkedList<>(object.getNames());
        this.values = unmodifiable ? Collections.unmodifiableList(object.getValues()) : new LinkedList<>(object.getValues());
        this.updateHashIndex();

    }

    private void clearCheck() {
        if (this.vsonSettings.contains(VsonSettings.CLEAR_ON_LOAD)) {
            this.clear();
        }
    }

    public VsonObject clone(VsonObject vsonObject) {
        this.vsonSettings = vsonObject.getVsonSettings();
        this.names = vsonObject.getNames();
        this.values = vsonObject.getValues();
        this.table = vsonObject.getTable();
        this.updateHashIndex();
        return this;
    }

    public VsonObject putAll(Object value) {
        VsonObject tree = (VsonObject) new VsonTree(value).tree();
        return this.clone(tree);
    }

    public VsonObject append(String name, int value) {
        submit(name, valueOf(value));
        return this;
    }

    public VsonObject append(String name, long value) {
        submit(name, valueOf(value));
        return this;
    }

    public VsonObject append(String name, float value) {
        submit(name, valueOf(value));
        return this;
    }

    public VsonObject append(String name, double value) {
        submit(name, valueOf(value));
        return this;
    }

    public VsonObject append(String name, boolean value) {
        this.submit(name, valueOf(value));
        return this;
    }

    public VsonObject append(String name, String value) {
        this.submit(name, valueOf(value));
        return this;
    }

    public VsonObject append(String key, VsonObject value) {
        this.submit(key, value);
        return this;
    }

    public VsonObject comment(String key,  VsonComment vsonComment, String... comment) {
        if (this.get(key) instanceof VsonObject || this.get(key) instanceof VsonArray) {
            throw new UnsupportedOperationException("Comments are not available for VsonObjects and VsonArrys at the moment");
        }
        this.comments.put(this.indexOf(key), new Pair<>(comment, vsonComment));
        return this;
    }

    public void submit(String name, VsonValue value) {
        if (value == null) {
            this.append(name, VsonLiteral.NULL);
            return;
        }
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        if (this.has(name)) {
            if (this.vsonSettings == null) {
                throw new NullPointerException("vsonSettings is null");
            }
            if (this.vsonSettings.contains(VsonSettings.OVERRITE_VALUES)) {
                this.set(name, value);
                return;
            }
        }
        table.add(name, names.size());
        names.add(name);
        values.add(value);
    }


    public VsonObject append(String name, Object value) {
        VsonTree vsonTree = new VsonTree(value);
        if (this.vsonSettings.contains(VsonSettings.SAFE_TREE_OBJECTS)) {
            vsonTree.setSafe(true);
        }
        submit(name, vsonTree.tree());
        return this;
    }

    public VsonObject append(String name, List<?> value) {
        VsonArray vsonArray = new VsonArray();
        for (Object o : value) {
            vsonArray.submit(o);
        }
        this.append(name, vsonArray);
        return this;
    }


    public VsonObject set(String name, int value) {
        set(name, valueOf(value));
        return this;
    }

    public VsonObject set(String name, long value) {
        set(name, valueOf(value));
        return this;
    }

    public VsonObject set(String name, float value) {
        set(name, valueOf(value));
        return this;
    }

    public VsonObject set(String name, short value) {
        set(name, valueOf(value));
        return this;
    }

    public VsonObject set(String name, byte value) {
        set(name, valueOf(value));
        return this;
    }

    public VsonObject set(String name, double value) {
        set(name, valueOf(value));
        return this;
    }

    public VsonObject set(String name, boolean value) {
        set(name, valueOf(value));
        return this;
    }

    public VsonObject set(String name, String value) {
        set(name, valueOf(value));
        return this;
    }

    public VsonObject set(String name, VsonValue value) {
        int index = indexOf(name);
        if (index !=- 1) {
            values.set(index, value);
        } else {
            table.add(name, names.size());
            names.add(name);
            values.add(value);
        }
        return this;
    }

    public VsonObject remove(String name) {
        int index = indexOf(name);
        if (index != -1) {
            table.remove(index);
            names.remove(index);
            values.remove(index);
        }
        return this;
    }

    public Object getObject(String key) {
        VsonValue value = this.get(key);
        if (value.isString()) {
            return value.asString();
        } else if (value.isInt()) {
            return value.asInt();
        } else if (value.isDouble()) {
            return value.asDouble();
        } else if (value.isShort()) {
            return value.asShort();
        } else if (value.isByte()) {
            return value.asByte();
        } else if (value.isFloat()) {
            return value.asFloat();
        } else if (value.isBoolean()) {
            return value.asBoolean();
        } else if (value.isArray()) {
            return value.asArray().asList();
        } else if (value.isObject()) {
            return value.asVsonObject();
        } else if (value.isNull()) {
            return VsonLiteral.NULL;
        } else {
            return value.asObject();
        }
    }

    public VsonValue get(String name) {
        int index = indexOf(name);
        return index !=-1 ? values.get(index) : null;
    }

    public List<String> keys() {
        List<String> keys = new LinkedList<>();
        for (int i = 0; i < this.size(); i++) {
            keys.add(this.getNames().get(i));
        }
        return keys;
    }

    public VsonArray getArray(String key) {
        return !this.has(key) ? new VsonArray() : this.get(key).asArray();
    }

    public <T> T getObject(String key, Class<T> tClass) {
        return new VsonTree().unTree(this.get(key), tClass);
    }

    public <T> T getObject(String key, Type type) {
        return new VsonTree().unTree(this.get(key), type);
    }

    public <T> T getAs(Class<T> tClass) {
        return new VsonTree().unTree(this, tClass);
    }

    public boolean has(String key) {
        return this.get(key) != null;
    }

    public void clear() {
        this.keys().forEach(this::remove);
    }

    public int getInteger(String name, int defaultValue) {
        VsonValue value = this.get(name);
        return value!=null ? value.asInt() : defaultValue;
    }

    public int getInteger(String name) {
        return this.get(name).asInt();
    }


    public long getLong(String name, long defaultValue) {
        if (!this.has(name)) {
            this.append(name, defaultValue);
            return defaultValue;
        }
        return this.get(name).asLong();
    }

    public long getLong(String name) {
        return this.get(name).asLong();
    }


    public float getFloat(String name, float defaultValue) {
        if (!this.has(name)) {
            this.append(name, defaultValue);
            return defaultValue;
        }
        return this.get(name).asFloat();
    }

    public float getFloat(String name) {
        return this.get(name).asFloat();
    }

    public double getDouble(String name, double defaultValue) {
        if (!this.has(name)) {
            this.append(name, defaultValue);
            return defaultValue;
        }
        return this.get(name).asDouble();
    }

    public double getDouble(String name) {
        return this.get(name).asDouble();
    }

    public short getShort(String name, short defaultValue) {
        if (!this.has(name)) {
            this.append(name, defaultValue);
            return defaultValue;
        }
        return this.get(name).asShort();
    }

    public short getShort(String name) {
        return this.get(name).asShort();
    }

    public byte getByte(String name, byte defaultValue) {
        if (!this.has(name)) {
            this.append(name, defaultValue);
            return defaultValue;
        }
        return this.get(name).asByte();
    }

    public byte getByte(String name) {
        return this.get(name).asByte();
    }


    public boolean getBoolean(String name, boolean defaultValue) {
        if (!this.has(name)) {
            this.append(name, defaultValue);
            return defaultValue;
        }
        return this.get(name).asBoolean();
    }

    public boolean getBoolean(String name) {
        return this.get(name).asBoolean();
    }

    public String getString(String name, String defaultValue) {
        if (!this.has(name)) {
            this.append(name, defaultValue);
            return defaultValue;
        }
        return this.get(name).asString();
    }

    public String getString(String key) {
        return this.get(key).asString();
    }

    public VsonObject getVson(String key) {
        return this.get(key).asVsonObject();
    }

    public VsonObject getVson(String key, VsonSettings... vsonSettings) {
        VsonObject vsonObject = this.get(key).asVsonObject();
        vsonObject.getVsonSettings().addAll(Arrays.asList(vsonSettings));
        return vsonObject;
    }

    public VsonObject getVson(String key, VsonObject defaultValue) {
        if (!this.has(key)) {
            this.append(key, defaultValue);
            return defaultValue;
        }
        return this.get(key).asVsonObject();
    }


    public List<String> getList(String key) {
        return new LinkedList<>(Arrays.asList(this.getString(key).split("\n")));
    }

    public List<String> getList(String key, List<String> defaultValue) {
        return this.has(key) ? new LinkedList<>(Arrays.asList(this.getString(key).split("\n"))) : defaultValue;
    }
    public <T> List<T> getList(String key, Class<T> tClass) {
        List<T> result = new LinkedList<>();
        for (VsonValue vsonValue : this.getArray(key)) {
            result.add(new VsonTree().unTree(vsonValue, tClass));
        }
        return result;
    }

    public <V> Map<String, V> getMap(String key,Class<V> vClass) {
        Map<String, V> map = new LinkedHashMap<>();
        VsonObject keys = this.get(key).asVsonObject();
        for (String s : keys.keys()) {
            map.put(s, keys.get(s).asVsonObject().getAs(vClass));
        }
        return map;
    }

    public int size() {
        return names.size();
    }

    public boolean isEmpty() {
        return names.isEmpty() && values.isEmpty();
    }

    public Iterator<VsonMember> iterator() {
        final Iterator<String> namesIterator = names.iterator();
        final Iterator<VsonValue> valuesIterator = values.iterator();
        return new Iterator<VsonMember>() {

            public boolean hasNext() {
                return namesIterator.hasNext();
            }

            public VsonMember next() {
                String name = namesIterator.next();
                VsonValue value = valuesIterator.next();
                return new VsonMember(name, value);
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

    @Override
    public VsonType getType() {
        return VsonType.OBJECT;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public VsonObject asVsonObject() {
        return this;
    }

    public VsonValue ofIndex(int index) {
        for (VsonMember vsonMember : this) {
            if (this.indexOf(vsonMember.getName()) == index) {
                return vsonMember.getValue();
            }
        }
        return null;
    }

    public int indexOf(String name) {
        int index = table.get(name);
        if (index != -1 && name.equals(names.get(index))) {
            return index;
        }
        return names.lastIndexOf(name);
    }

    private synchronized void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        this.table = new HashIndexTable();
        this.updateHashIndex();
    }

    private void updateHashIndex() {
        for (int i = 0; i < names.size(); i++) {
            table.add(names.get(i), i);
        }
    }

    public void save() {
        this.save(this.file, FileFormat.VSON);
    }
    public void save(FileFormat format) {
        if (this.file != null) {
            this.save(this.file, format);
        } else {
            throw new NullPointerException("File not found");
        }
    }

    public void save(File file) {
        this.file = file;
        this.save();
    }

    public void save(File file, FileFormat format) {
        try {
            if (!this.file.exists()) {
                if (this.vsonSettings.contains(VsonSettings.CREATE_FILE_IF_NOT_EXIST)) {
                    if (this.file.createNewFile()) {
                        this.save(file, format);
                    }
                } else {
                    throw new UnsupportedOperationException("Can not save File " + file.getName() + " because File does not exist and VsonSettings do not contain " + VsonSettings.CREATE_FILE_IF_NOT_EXIST.name() + "!");
                }
            }
            if (format.equals(FileFormat.PROPERTIES)) {
                Properties properties = new Properties();
                properties.load(new FileInputStream(file));
                for (String key : this.keys()) {
                    properties.put(key, this.getObject(key).toString());
                }
                properties.save(new FileOutputStream(file), "Edit by VsonObject");
            } else {
                PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true);
                w.print(this.toString(format));
                w.flush();
                w.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return this.toString(FileFormat.VSON);
    }


    public List<String> getNames() {
        return names;
    }

    public List<VsonValue> getValues() {
        return values;
    }

    public Map<Integer, Pair<String[], VsonComment>> getComments() {
        return comments;
    }

    public File getFile() {
        return file;
    }

    public HashIndexTable getTable() {
        return table;
    }

    public List<VsonSettings> getVsonSettings() {
        return vsonSettings;
    }


    public static Map<String, Object> encode(VsonObject vsonObject) {
        Map<String, Object> map = new HashMap<>();
        vsonObject.forEach(member -> map.put(member.getName(), vsonObject.getObject(member.getName())));
        return map;
    }
    public static VsonObject encode(Map<String, Object> map) {
        VsonObject vsonObject = new VsonObject();
        map.forEach(vsonObject::append);
        return vsonObject;
    }
}
