package br.com.edward.dinosaur.helper;

import br.com.edward.dinosaur.ai.NeuralNetwork;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectUtilTests {

    private String originalUserHome;

    @BeforeEach
    void redirectUserHome(@TempDir final Path tempHome) {
        this.originalUserHome = System.getProperty("user.home");
        System.setProperty("user.home", tempHome.toString());
    }

    @AfterEach
    void restoreUserHome() {
        System.setProperty("user.home", this.originalUserHome);
    }

    @Test
    @DisplayName("Writes and reads back the same object preserving its state")
    void testRoundTrip() {
        final var original = new NeuralNetwork();
        ObjectUtil.writeObjectToFile(original);

        final var loaded = ObjectUtil.<NeuralNetwork>readObjectFromFile(NeuralNetwork.class);

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getGeneration()).isEqualTo(original.getGeneration());
        assertThat(loaded.get().getOutputLayer().getNeurons())
                .hasSameSizeAs(original.getOutputLayer().getNeurons());
    }

    @Test
    @DisplayName("Reading a missing file returns an empty Optional")
    void testMissingFile() {
        assertThat(ObjectUtil.readObjectFromFile(NeuralNetwork.class)).isEmpty();
    }

    @Test
    @DisplayName("Reading a corrupted file returns an empty Optional without propagating the error")
    void testCorruptedFile() throws IOException {
        final var file = Path.of(System.getProperty("user.home"), "dinosaur",
                NeuralNetwork.class.getSimpleName() + ".ser");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "this is not a serialized object");

        assertThat(ObjectUtil.readObjectFromFile(NeuralNetwork.class)).isEmpty();
    }
}
