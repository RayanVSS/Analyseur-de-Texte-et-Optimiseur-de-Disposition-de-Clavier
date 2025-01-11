package services;

import utils.FileCounter;

import java.util.List;

public class TextFileDataSource implements DataSource {

    private FileCounter fileCounter;

    public TextFileDataSource() {
        this.fileCounter = new FileCounter();
    }

    @Override
    public List<String> getFilePaths(String directoryPath) {
        return fileCounter.selectFiles(directoryPath);
    }
}
