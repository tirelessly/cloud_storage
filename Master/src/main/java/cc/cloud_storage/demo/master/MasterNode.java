package cc.cloud_storage.demo.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MasterNode {

    private Map<Integer, String> endpoints = new HashMap<>();
    private static final String ENCODING = "ISO_8859_1";
    private static final String IP = "78.104.29.82";

    private Integer serviceNumber;
    private Logger log;

    public MasterNode() {
        this.log = LoggerFactory.getLogger(MasterNode.class);
        addEndpoints();
        this.serviceNumber = this.endpoints.size();
    }

    public String sendInsertRequestToNode(String key, MultipartFile file) {
        String responseFromBucket = null;
        try {
            String bytesArr = new String(file.getName().getBytes(), ENCODING);
            String bucketUrl = getBucketURLByHashIndex(bytesArr);

            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put(file.getName(),  new String(key.getBytes(), ENCODING));

            log.info("About to send image to node: {}", bucketUrl);
            responseFromBucket = new RestTemplate().postForObject(bucketUrl + "api/v1/insert", dataToSend, String.class);
            log.info("Received response {} from node: {}", responseFromBucket, bucketUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseFromBucket;
    }

    private String getBucketURLByHashIndex(String fileName) {
        Integer hashIndex = fileName.length() % serviceNumber;
        return this.endpoints.get(hashIndex);
    }

    private void addEndpoints() {
        this.endpoints.put(1, "http://" + IP + ":8080/");
        this.endpoints.put(2, "http://" + IP + ":8080/");
        this.endpoints.put(3, "http://" + IP + ":8080/");

    }

}
