package cc.task4.demo.Controller;

import cc.task4.demo.Exceptions.BucketDirectoryNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NodeController {

    private Logger log;
    private static final int BUCKET_SIZE = 6;


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



    private void saveFile(Map<String, String> file) {

    }

    private Integer getHashIndex(String imageInBytes) {
        return imageInBytes.length() % BUCKET_SIZE;
    }


}
