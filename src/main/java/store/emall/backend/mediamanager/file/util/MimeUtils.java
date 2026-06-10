
package store.emall.backend.mediamanager.file.util;

public class MimeUtils {
    public static String getExtension(String mimeType) {
        // Images
        if (mimeType.equals("image/jpeg")) return "jpg";
        if (mimeType.equals("image/png")) return "png";
        if (mimeType.equals("image/webp")) return "webp";
        if (mimeType.equals("image/gif")) return "gif";

        // Video
        if (mimeType.equals("video/mp4")) return "mp4";
        if (mimeType.equals("video/webm")) return "webm";

        // Documents
        if (mimeType.equals("application/pdf")) return "pdf";
        if (mimeType.equals("application/msword")) return "doc";
        if (mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) return "docx";

        // Spreadsheets
        if (mimeType.equals("application/vnd.ms-excel")) return "xls";
        if (mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) return "xlsx";

        // Presentations
        if (mimeType.equals("application/vnd.ms-powerpoint")) return "ppt";
        if (mimeType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) return "pptx";

        // Text
        if (mimeType.equals("text/plain")) return "txt";

        return "bin";
    }
}