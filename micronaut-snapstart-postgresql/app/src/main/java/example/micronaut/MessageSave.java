package example.micronaut;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record MessageSave(String message) {
}
