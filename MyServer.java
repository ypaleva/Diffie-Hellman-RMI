import pkg.CiphertextInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MyServer extends UnicastRemoteObject {

    private final String remoteServer = "svm-tjn1f15-comp2207.ecs.soton.ac.uk";
    private String ID = "CipherServer";
    private static final Integer primeNum = 191;
    private static final Integer primitiveRoot = 131;

    public MyServer() throws RemoteException {

    }

    public static void main(String[] args) throws RemoteException {
        MyServer server = new MyServer();
        server.initializeServer();
    }

    public void initializeServer() {
//      System.setProperty( "java.security.policy", "policy" );
//      if (System.getSecurityManager() == null) {
//          System.setSecurityManager(new SecurityManager());
//      }
        Registry registry = null;
        RemoteImpl stub = null;
        CiphertextInterface ct_stub = null;
        try {
            registry = LocateRegistry.createRegistry(2605);
            stub = new RemoteImpl();
            registry.rebind("CipherServer", stub);
            System.out.println("Server ready...");

        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

}