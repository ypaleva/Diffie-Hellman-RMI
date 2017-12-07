import java.rmi.RemoteException;

public class UnsecureConnectionException extends RemoteException {

    public UnsecureConnectionException(String msg) {
        super(msg);
    }
}
