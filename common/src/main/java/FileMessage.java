import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage {

    private String fileName;
    private byte[] data;

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    public FileMessage(Path path) throws IOException {
        this.fileName = path.getFileName().toString();
        this.data = Files.readAllBytes(path);
    }
}
