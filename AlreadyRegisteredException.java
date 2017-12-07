import java.rmi.RemoteException;

public class AlreadyRegisteredException extends RemoteException {

    public AlreadyRegisteredException(String msg) {
        super(msg);
    }
}