package com.example.OrderManagementSystem.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InFileRepository<T> implements CrudRepository<T, Long> {
    private final String filePath;
    private final Class<T> type;
    private final ObjectMapper objectMapper;

    public InFileRepository(String filePath, Class<T> type) {
        this.filePath = filePath;
        this.type = type;
        this.objectMapper = new ObjectMapper();
    }

    private File getFile() {
        // For writing, we need a writable location
        // Try to use src/main/resources in development, otherwise use working directory
        Path path = Paths.get("src/main/resources", filePath);
        if (!Files.exists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                // Fallback to working directory
                path = Paths.get(filePath);
                try {
                    if (path.getParent() != null) {
                        Files.createDirectories(path.getParent());
                    }
                } catch (IOException ex) {
                    // Last resort: use temp directory
                    path = Paths.get(System.getProperty("java.io.tmpdir"), filePath);
                }
            }
        }
        return path.toFile();
    }

    @Override
    public List<T> findAll() {
        try {
            File file = getFile();

            // If file doesn't exist, try to copy from resources
            if (!file.exists()) {
                // Try to read from resources first
                InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(filePath);
                if (resourceStream != null) {
                    // Copy from resources to file system
                    TypeFactory typeFactory = objectMapper.getTypeFactory();
                    List<T> data = objectMapper.readValue(resourceStream, typeFactory.constructCollectionType(List.class, type));
                    resourceStream.close();
                    // Write to file system for future writes
                    writeToFile(data);
                    return data;
                } else {
                    // Create empty file
                    writeToFile(new ArrayList<>());
                    return new ArrayList<>();
                }
            }

            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(file, typeFactory.constructCollectionType(List.class, type));
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file: " + filePath, e);
        }
    }

    @Override
    public Optional<T> findById(Long id) {
        List<T> items = findAll();
        return items.stream()
                .filter(item -> {
                    try {
                        // Use reflection to get the id field
                        java.lang.reflect.Method getIdMethod = item.getClass().getMethod("getId");
                        Long itemId = (Long) getIdMethod.invoke(item);
                        return itemId != null && itemId.equals(id);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .findFirst();
    }

    @Override
    public T save(T entity) {
        List<T> items = findAll();

        try {
            // Get the id of the entity
            java.lang.reflect.Method getIdMethod = entity.getClass().getMethod("getId");
            Long entityId = (Long) getIdMethod.invoke(entity);

            if (entityId == null) {
                // New entity - generate ID
                Long maxId = items.stream()
                        .mapToLong(item -> {
                            try {
                                java.lang.reflect.Method getId = item.getClass().getMethod("getId");
                                Long id = (Long) getId.invoke(item);
                                return id != null ? id : 0L;
                            } catch (Exception e) {
                                return 0L;
                            }
                        })
                        .max()
                        .orElse(0L);

                // Set the new ID
                java.lang.reflect.Method setIdMethod = entity.getClass().getMethod("setId", Long.class);
                setIdMethod.invoke(entity, maxId + 1);
            } else {
                // Update existing entity - remove old one
                items.removeIf(item -> {
                    try {
                        java.lang.reflect.Method getId = item.getClass().getMethod("getId");
                        Long itemId = (Long) getId.invoke(item);
                        return itemId != null && itemId.equals(entityId);
                    } catch (Exception e) {
                        return false;
                    }
                });
            }

            // Add the entity (new or updated)
            items.add(entity);

            // Write back to file
            writeToFile(items);

            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving entity to file: " + filePath, e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        List<T> items = findAll();
        boolean removed = items.removeIf(item -> {
            try {
                java.lang.reflect.Method getIdMethod = item.getClass().getMethod("getId");
                Long itemId = (Long) getIdMethod.invoke(item);
                return itemId != null && itemId.equals(id);
            } catch (Exception e) {
                return false;
            }
        });

        if (removed) {
            writeToFile(items);
        }

        return removed;
    }

    private void writeToFile(List<T> items) {
        try {
            File file = getFile();
            // Create parent directories if they don't exist
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, items);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file: " + filePath, e);
        }
    }
}

