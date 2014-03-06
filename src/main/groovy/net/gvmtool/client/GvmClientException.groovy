package net.gvmtool.client

class GvmClientException extends Throwable {
    GvmClientException(String message){
        super(message)
    }

    GvmClientException(Throwable e){
        super(e)
    }

    GvmClientException(String message, Throwable e) {
        super(message, e)
    }
}
