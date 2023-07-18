package br.com.edward.dinosaur.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectUtil {

    public static void writeObjectToFile(final Object obj) {
        final var fileName = obj.getClass().getSimpleName() + ".ser";
        final var path = Paths.get(fileName);
        try (final var oos = new ObjectOutputStream(Files.newOutputStream(path))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static <T> Optional<T> readObjectFromFile(final Class<?> clazz) {
        final var fileName = clazz.getSimpleName() + ".ser";
        final var path = Paths.get(fileName);
        try (final var ois = new ObjectInputStream(Files.newInputStream(path))) {
            return Optional.of((T) ois.readObject());
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            log.warn("Failed to read object from file {}: {}", fileName, e.getMessage());
            return Optional.empty();
        }
    }
}
