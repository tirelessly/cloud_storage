package cc.cloud_storage.demo.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import sun.jvm.hotspot.runtime.Bytes;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MasterNode {

    private Map<Integer, String> endpoints = new HashMap<>();
    private static final String ENCODING = "ISO_8859_1";
    private static final String IP = "78.104.29.118";

    private Integer serviceNumber;
    private Logger log;

    public MasterNode() {
        this.log = LoggerFactory.getLogger(MasterNode.class);
        addEndpoints();
        this.serviceNumber = this.endpoints.size();
    }

    public String sendInsertRequestToNode(MultipartFile file) {
        String responseFromNode = null;
        try {
            String bytesArr = new String(file.getResource().getFilename().getBytes(), ENCODING);
            String bucketUrl = getBucketURLByHashIndex(bytesArr);

            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put(file.getResource().getFilename(),  new String(file.getBytes(), ENCODING));

            log.info("About to send image to node: {}", bucketUrl);
            responseFromNode = new RestTemplate().postForObject(bucketUrl + "api/v1/insert", dataToSend, String.class);
            log.info("Received response for insert request {} from node: {}", responseFromNode, bucketUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseFromNode;
    }

    public String sendDeleteRequestToNode(String key){
        String nodeUrl = getBucketURLByHashIndex(new String(key.getBytes()));
        log.info("About to delete a value for key: {}", key);
        String responseFromNode = new RestTemplate().getForObject(nodeUrl + "api/v1/delete/" + key, String.class);
        log.info("Received response for delete request {} from node {}", responseFromNode, nodeUrl);

        return responseFromNode;
    }

    private String getBucketURLByHashIndex(String fileName) {
        Integer hashIndex = fileName.length() % serviceNumber;
        log.info("Hash index is {}", hashIndex);
        return this.endpoints.get(hashIndex);
    }

    private void addEndpoints() {
        this.endpoints.put(0, "http://" + IP + ":8080/");
        this.endpoints.put(1, "http://" + IP + ":8080/");
        this.endpoints.put(2, "http://" + IP + ":8080/");
        this.endpoints.put(3, "http://" + IP + ":8080/");

    }

}
