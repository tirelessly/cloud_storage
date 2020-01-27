package cc.task4.demo.Exceptions;

public class ImageNotFound extends GenericException {


    public ImageNotFound(String errorName, String errorMessage) {
        super(errorName, errorMessage);
    }
}
