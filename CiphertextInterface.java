import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CiphertextInterface extends Remote {

    public String get(String uid, int key) throws RemoteException;

}