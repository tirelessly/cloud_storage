package cc.cloud_storage.demo.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
            log.info("Received response for insert request from node: {}", bucketUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseFromNode;
    }

    public String sendDeleteRequestToNode(String key){
        String nodeUrl = getBucketURLByHashIndex(new String(key.getBytes()));
        log.info("About to delete a value for key: {}", key);
        new RestTemplate().getForObject(nodeUrl + "api/v1/delete/" + key, String.class);
        log.info("Received response for delete request from node {}", nodeUrl);

        return nodeUrl;
    }

    public Map<String, byte[]> sendSearchRequestToNode(String key) throws Exception {
        String nodeUrl = getBucketURLByHashIndex(new String(key.getBytes()));
        try {
            log.info("About to search for a value for key: {}", key);
            String responseFromNode = new RestTemplate().getForObject(nodeUrl + "api/v1/search/" + key, String.class);

            Map<String, byte[]> response = new HashMap<>();

            response.put(nodeUrl, responseFromNode.getBytes(ENCODING));
            log.info("Received response for search for key {} from node {}", key, nodeUrl);


            return response;
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    public List<String> sendRangeRequestToNode(String key1, String key2) {
        Iterator<Map.Entry<Integer, String>> itr = endpoints.entrySet().iterator();
        List<String> response = new LinkedList<>();
        while(itr.hasNext()) {
            Map.Entry<Integer, String> entry = itr.next();
            String nodeUrl = entry.getValue();
            List<String> responseFromNode = new RestTemplate().getForObject(nodeUrl + "/api/v1/range", List.class);
            if(responseFromNode != null) {
                response.addAll(responseFromNode);
            }
        }
        List<String> sortedList = response.stream()
            .filter(a -> a.compareTo(key1) >= 0)
            .filter(a -> a.compareTo(key2) <= 0)
            .collect(Collectors.toList());

        return sortedList;
    }

    private String getBucketURLByHashIndex(String fileName) {
        Integer hashIndex = fileName.length() % serviceNumber;
        log.info("Hash index is {}", hashIndex);
        return this.endpoints.get(hashIndex);
    }

    private void addEndpoints() {
        this.endpoints.put(0, "http://" + System.getenv("NODE1") + ":8080/");
        this.endpoints.put(1, "http://" + System.getenv("NODE2") + ":8080/");
        this.endpoints.put(2, "http://" + System.getenv("NODE3") + ":8080/");
        this.endpoints.put(3, "http://" + System.getenv("NODE4") + ":8080/");

    }

}
