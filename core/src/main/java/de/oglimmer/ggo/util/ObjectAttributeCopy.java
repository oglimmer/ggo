package de.oglimmer.ggo.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectAttributeCopy {

	@SuppressWarnings("unchecked")
	public static <T> void copyAllFields(T to, T from, Class<?> filter) {
		Class<T> clazz = (Class<T>) from.getClass();
		// OR:
		// Class<T> clazz = (Class<T>) to.getClass();
		List<Field> fields = getAllModelFields(clazz, filter);

		for (Field field : fields) {
			try {
				field.setAccessible(true);
				field.set(to, field.get(from));
			} catch (IllegalAccessException e) {
				log.error("Failed to copy field {}", field.getName());
			}
		}
	}

	private static List<Field> getAllModelFields(Class<?> clazz, Class<?> filter) {
		List<Field> allCollectedFields = new ArrayList<>();
		do {
			if (filter == null || clazz.equals(filter)) {
				Collections.addAll(allCollectedFields, clazz.getDeclaredFields());
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return allCollectedFields;
	}

}
