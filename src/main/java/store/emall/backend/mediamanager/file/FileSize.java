package store.emall.backend.mediamanager.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileSize {
    SMALL("small"),
    MEDIUM("medium"),
    ORIGINAL("original"),
    OPTIMIZED_ORIGINAL("optimized-original");

    private final String size;
}
