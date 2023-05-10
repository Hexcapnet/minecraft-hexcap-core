package net.hexcap.minecraft.core.config.file;

public enum FileType {
    CONFIG("config.yml"),
    MESSAGES("messages.yml"),
    PLAYERS("players.yml");

    private final String fileName;

    FileType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
