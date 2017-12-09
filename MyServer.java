import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MyServer extends UnicastRemoteObject {

    public MyServer() throws RemoteException {

    }

    public static void main(String[] args) throws RemoteException {
        System.setProperty( "java.security.policy", "policy" );
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        MyServer server = new MyServer();
        server.initializeServer();
    }

    public void initializeServer() {
        Registry registry = null;
        RemoteImpl stub = null;
        CiphertextInterface ct_stub = null;
        try {
            registry = LocateRegistry.createRegistry(2605);
            stub = new RemoteImpl();
            registry.rebind("CipherServer", stub);
            System.out.println("[SERVER] Ready!");

        } catch (RemoteException e) {
            System.err.println("[SERVER] Exception: " + e.toString());
            //e.printStackTrace();
            System.exit(-1);
        }
    }

}