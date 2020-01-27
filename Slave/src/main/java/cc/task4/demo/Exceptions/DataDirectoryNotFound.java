package cc.task4.demo.Exceptions;

public class DataDirectoryNotFound extends GenericException {


    public DataDirectoryNotFound(String errorName, String errorMessage) {
        super(errorName, errorMessage);
    }
}
