package cc.task4.demo.Controller;

import cc.task4.demo.Exceptions.BucketDirectoryNotFound;
import cc.task4.demo.Exceptions.DataDirectoryNotFound;
import cc.task4.demo.Exceptions.GenericException;
import cc.task4.demo.Exceptions.ImageNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
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
        log.info("About to create directory" + fullPath);
        directory.mkdir();
        log.info("{} directory was created.", fullPath);
    }

    private String getCurrentPath(String directoryName) {
        return PATH + "/" + directoryName;
    }


    public ResponseEntity<String> saveFile(Map<String, String> file) {
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
        return new ResponseEntity<>("Image successfully added.", HttpStatus.OK);
    }


    public ResponseEntity<String> deleteFile(String imageInBytes) {
        Integer hashIndex = getHashIndex(imageInBytes);
        String bucketFullPath = getCurrentPath(BUCKET_RELATIVE_PATH + hashIndex);
        try {
            checkIfDirectoryExists(bucketFullPath, "");

            File folder = new File(bucketFullPath);
            File[] listOfFiles = folder.listFiles();

            for (File file : listOfFiles) {
                if (file.isFile()) {
                    if (imageInBytes.equals(file.getName())) {
                        file.delete();
                        return new ResponseEntity<String>("File " + imageInBytes +  " successfully deleted", HttpStatus.OK);
                    }
                }
                System.out.println(file.getName());
            }
        } catch (GenericException e) {
            log.warn("File not found");
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


    public ResponseEntity<Map<String,String>> searchFile(String imageInBytes) {
        Integer hashIndex = getHashIndex(imageInBytes);
        String bucketFullPath = getCurrentPath(BUCKET_RELATIVE_PATH + hashIndex);
        try {
            checkIfDirectoryExists(bucketFullPath, "");
            File folder = new File(bucketFullPath);
            File[] listOfFiles = folder.listFiles();

            for (File file : listOfFiles) {
                if (file.isFile()) {
                    if (imageInBytes.equals(file.getName())) {
                        Map<String, String> response = new HashMap<>();
                        response.put(file.getName(), decodeImage(file));
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }
            throw new ImageNotFound("ImageNotFoundException", "Image " + imageInBytes + "not found");
        } catch (GenericException e) {
            log.warn("File {} not found", imageInBytes);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
        log.info("image length {}", imageInBytes.length());
        return imageInBytes.length() % BUCKET_SIZE;
    }


}
