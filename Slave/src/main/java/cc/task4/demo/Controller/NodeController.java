package cc.task4.demo.Controller;

import cc.task4.demo.Exceptions.BucketDirectoryNotFound;
import cc.task4.demo.Exceptions.DataDirectoryNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class NodeController {

    private Logger log;
    private static final int BUCKET_SIZE = 6;
    private static final String BUCKET_RELATIVE_PATH = "data/bucket";
    private static final String DATA_RELATIVE_PATH = "data";
    private static final String PATH = System.getProperty("user.dir");


    public NodeController() {
        this.log = LoggerFactory.getLogger(NodeController.class);
    }


    public String getFileName(Map<String, String> file) {
        Map.Entry<String,String> entry = file.entrySet().iterator().next();
        return entry.getKey();
    }

    public String getImageAsString(Map<String, String> file) {
        Map.Entry<String,String> entry = file.entrySet().iterator().next();
        return entry.getValue();
    }

    private void checkIfDirectoryExists(String fullPath, String directoryName) {
        File directory = new File(fullPath);
        if (! directory.exists()) {
            if ("images".equals(directoryName)) {
                log.warn("Images file not found");
                throw new DataDirectoryNotFound("ImagesDirectoryNotFound", "Images file not found");
            } else {
                log.warn("{} file not found", directoryName);
                throw new BucketDirectoryNotFound("BucketDirectoryNotFound", "Bucket file not found");
            }
        }
    }

    private void createDirectory (String fullPath) {
        File directory = new File(fullPath);
        directory.mkdir();
        log.info("{} directory was created.", fullPath);
    }

    private String getCurrentPath(String directoryName) {
        return PATH + "/" + directoryName;
    }


    private void saveFile(Map<String, String> file) {
        String slotName = null;
        String imageInBytes = null;
        try {
            imageInBytes = getImageAsString(file);
            Integer hashIndex = getHashIndex(imageInBytes);
            slotName = BUCKET_RELATIVE_PATH + hashIndex;
            checkIfDirectoryExists(getCurrentPath(DATA_RELATIVE_PATH), DATA_RELATIVE_PATH);
            checkIfDirectoryExists(getCurrentPath(slotName), slotName);
        } catch (DataDirectoryNotFound e) {
            createDirectory(getCurrentPath(DATA_RELATIVE_PATH));
            createDirectory(getCurrentPath(slotName));
        } catch (BucketDirectoryNotFound e) {
            createDirectory(getCurrentPath(slotName));
        } finally {
            saveToSlot(imageInBytes, getCurrentPath(slotName), getFileName(file));
        }
    }

    private void saveToSlot(String imageInBytes, String path, String imageName) {
        String fullPath = path + "/" + imageName + ".txt";
        File file = new File(fullPath);
        try{
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(imageInBytes);
            bw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private Integer getHashIndex(String imageInBytes) {
        return imageInBytes.length() % BUCKET_SIZE;
    }


}
