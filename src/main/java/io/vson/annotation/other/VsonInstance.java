package io.vson.annotation.other;


import io.vson.elements.object.VsonObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class VsonInstance {

	private static final VsonInstance instance = new VsonInstance();

	public static VsonInstance getInstance() {
		return instance;
	}

	public void registerClass(Object classToLoad) {
		Class<?> clazz = classToLoad.getClass();
		try {
			for (Field field : clazz.getDeclaredFields()) {
				try {
					VsonConfigValue annotation = field.getAnnotation(VsonConfigValue.class);
					if (annotation != null) {
						if (new File(annotation.file()).exists() && !new VsonObject(new File(annotation.file())).isEmpty()) {
							field.setAccessible(true);

							String[] sub = annotation.subVsons();
							VsonObject vsonObject = new VsonObject(new File(annotation.file()));
							if (annotation.key().equals("no_key")) {
								field.set(classToLoad, vsonObject.getAs(annotation.treeClass()));
							} else {
								if (sub.length != 0) {
									for (String s : sub) {
										vsonObject = vsonObject.getVson(s);
									}
								}
								if (annotation.treeClass() == VsonConfigValue.class) {
									field.set(classToLoad, vsonObject.getObject(annotation.key()));
								} else {
									field.set(classToLoad, vsonObject.getObject(annotation.key(), annotation.treeClass()));
								}
							}
						} else {
							System.out.println("[VSON] Couldn't load Field " + field + " because file wasn't found or VsonObject was empty!");
						}
					} else {
						VsonConfiguration configuration = field.getAnnotation(VsonConfiguration.class);
						if (configuration != null) {
							field.setAccessible(true);
							field.set(classToLoad, new VsonObject(new File(configuration.file()), configuration.vsonSettings()));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

