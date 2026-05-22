package ps.emall.mediamanager.file;

import java.util.Set;

public final class FileTypeWhitelist {

    private FileTypeWhitelist() {}

    public static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg","jpeg","png","webp","gif",
            "mp4","WebM",
            "pdf",
            "doc","docx",
            "xls","xlsx",
            "ppt","pptx",
            "txt"
    );
}