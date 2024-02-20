package example.micronaut;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@MappedEntity("message")
public record Message(
    @Id @GeneratedValue @Nullable Long id,
    @DateCreated @Nullable LocalDate dateCreated,
    @NonNull @NotNull String message) {
}
