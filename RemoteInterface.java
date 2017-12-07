import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote {

    String requestCipherFromServer() throws RemoteException, NotBoundException;

    void sendYValueToServer(String ID, Integer y) throws RemoteException;

    Integer requestPrimeNumFromServer() throws RemoteException;

    Integer requestPrimRootFromServer() throws RemoteException;

    Integer requestXValueFromServer() throws RemoteException;

    /**
     * @param ID     - The ID of the new client we want to register.
     * @return - Returns true if client is registered successfully, false otherwise.
     */
    boolean register(String ID) throws RemoteException;

    /**
     * @param ID - The ID of the client we want to unregister.
     * @return - Returns true if client is unregistered successfully, false otherwise.
     * @throws RemoteException
     */
    boolean unregister(String ID) throws RemoteException;

}