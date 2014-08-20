package net.gvmtool.client;

/**
 * @author Noam Y. Tenne
 */
public class GvmClientException extends Throwable {

    GvmClientException(String message){
        super(message);
    }

    GvmClientException(Throwable e){
        super(e);
    }

    GvmClientException(String message, Throwable e) {
        super(message, e);
    }
}
