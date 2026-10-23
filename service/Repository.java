package service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import model.annotation.Id;

/**
 * Practical 13: Generic Repository<T> implementing the data-access layer pattern,
 * dynamic @Id reflection extraction, bounded generic utility, and type-safe varargs.
 */
public class Repository<T> {
    private final Map<String, T> storage = new HashMap<>();

    /**
     * Persists an entity by discovering its @Id field via reflection across the class hierarchy.
     */
    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        String id = extractId(entity);
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalStateException("Entity of type " + entity.getClass().getSimpleName() + " lacks a non-empty @Id value");
        }

        storage.put(id, entity);
        return entity;
    }

    public T findById(String id) {
        if (id == null) {
            return null;
        }
        return storage.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public boolean delete(String id) {
        if (id == null) {
            return false;
        }
        return storage.remove(id) != null;
    }

    public int count() {
        return storage.size();
    }

    /**
     * Saves multiple entities at once using type-safe varargs.
     */
    @SafeVarargs
    public final void saveAll(T... items) {
        if (items != null) {
            for (T item : items) {
                if (item != null) {
                    save(item);
                }
            }
        }
    }

    /**
     * Bounded generic method to compute the mathematical average of an array of numbers.
     */
    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        int count = 0;
        for (N val : values) {
            if (val != null) {
                sum += val.doubleValue();
                count++;
            }
        }
        return count == 0 ? 0.0 : (sum / count);
    }

    private String extractId(T entity) {
        Class<?> clazz = entity.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(entity);
                        return value != null ? value.toString() : null;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Cannot access @Id field: " + field.getName(), e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        throw new IllegalStateException("No @Id field found on class hierarchy of: " + entity.getClass().getName());
    }
}
