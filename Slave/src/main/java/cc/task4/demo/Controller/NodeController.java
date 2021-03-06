package cc.task4.demo.Controller;

import cc.task4.demo.Exceptions.BucketDirectoryNotFound;
import cc.task4.demo.Exceptions.DataDirectoryNotFound;
import cc.task4.demo.Exceptions.GenericException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NodeController {

    public enum Operation {
        DELETE,
        SEARCH
    }

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
            if ("data".equals(directoryName)) {
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
    }

    private String getCurrentPath(String directoryName) {
        return PATH + "/" + directoryName;
    }


    public ResponseEntity<String> saveFile(Map<String, String> file) {
        String slotName = null;
        String imageInBytes = null;
        String fileName = getFileName(file);
        try {
            imageInBytes = getImageAsString(file);
            Integer hashIndex = getHashIndex(fileName);
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
        return new ResponseEntity<>("Image " + fileName + " successfully added.", HttpStatus.OK);
    }


    public ResponseEntity<String> makeOperation(String imageInBytes, Operation operation) {
        Integer hashIndex = getHashIndex(imageInBytes);
        String bucketFullPath = getCurrentPath(BUCKET_RELATIVE_PATH + hashIndex);
        try {
            checkIfDirectoryExists(bucketFullPath, imageInBytes);
            File folder = new File(bucketFullPath);
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    if (imageInBytes.equals(file.getName())) {
                        if (operation.equals(Operation.DELETE)) {
                            file.delete();
                            log.info("File {} successfully deleted", imageInBytes);
                            return new ResponseEntity<>("File " + imageInBytes +  " successfully deleted", HttpStatus.OK);
                        } else if (operation.equals(Operation.SEARCH)) {
                            log.info("File {} found", imageInBytes);
                            return new ResponseEntity<>(decodeImage(file), HttpStatus.OK);
                        }
                    }
                }
            }
        } catch (GenericException e) {
            log.warn("File {} not found", imageInBytes);
            return new ResponseEntity<>("File " + imageInBytes +  " not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("File " + imageInBytes +  " not found", HttpStatus.NOT_FOUND);
    }

    private String decodeImage(File file) {
        String result = null;
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            result = new String(fileContent, "ISO_8859_1");
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ResponseEntity<List<String>> searchInRange() {
        List<String> result = new ArrayList<>();
        try {
            String currentPath = getCurrentPath(DATA_RELATIVE_PATH);
            checkIfDirectoryExists(getCurrentPath(DATA_RELATIVE_PATH), DATA_RELATIVE_PATH);
            File folder = new File(currentPath);
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {
                if (file.getName().contains("bucket")) {
                    String bucketPath = currentPath + "/" + file.getName();
                    File bucketFile = new File(bucketPath);
                    File[] listOfBuckets = bucketFile.listFiles();
                    for (File eachImage: listOfBuckets) {
                        result.add(eachImage.getName());
                    }
                }
            }

            } catch (GenericException e) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    private void saveToSlot(String imageInBytes, String path, String imageName) {
        String fullPath = path + "/" + imageName;
        File file = new File(fullPath);

        try{
            byte[] newStr = imageInBytes.getBytes("ISO_8859_1");
            ByteArrayInputStream bis = new ByteArrayInputStream(newStr);
            BufferedImage bImage2 = ImageIO.read(bis);
            ImageIO.write(bImage2, "jpg", file );
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private Integer getHashIndex(String imageInBytes) {
        return imageInBytes.length() % BUCKET_SIZE;
    }


}
