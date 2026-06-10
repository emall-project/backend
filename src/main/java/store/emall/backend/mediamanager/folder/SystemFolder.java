package store.emall.backend.mediamanager.folder;

public enum SystemFolder {
    TEMP_FOLDER("TEMP_FOLDER");
    private final String folderName;

    SystemFolder(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}