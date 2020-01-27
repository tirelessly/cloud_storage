package cc.task4.demo.Exceptions;

public class BucketDirectoryNotFound extends GenericException {


    public BucketDirectoryNotFound(String errorName, String errorMessage) {
        super(errorName, errorMessage);
    }
}
