import java.rmi.RemoteException;

public class ClientNotFoundException extends RemoteException {

    public ClientNotFoundException(String msg) {
        super(msg);
    }
}
