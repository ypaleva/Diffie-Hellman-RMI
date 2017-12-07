import java.math.BigInteger;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RemoteImpl extends UnicastRemoteObject implements RemoteInterface {

    private final Integer primeNum = 191;
    private final Integer primitiveRoot = 131;
    private Map<String, CipherKey> keys = Collections.synchronizedMap(
            new HashMap<String, CipherKey>());

    public RemoteImpl() throws RemoteException {
        super();
        this.keys = new HashMap<>();
    }

    @Override
    public synchronized String requestCipherFromServer(String username, String ID) throws RemoteException {
        Registry ctreg = LocateRegistry.getRegistry("svm-tjn1f15-comp2207.ecs.soton.ac.uk", 12345);
        CiphertextInterface ctstub = null;
        try {
            System.out.println("[SERVER] Connecting to remote server...");
            ctstub = (CiphertextInterface) ctreg.lookup("CiphertextProvider");
            System.out.println("[SERVER] Connected to remote server! Fetching encrypted message...");
            if (getKeys().get(ID) != null) {
                System.out.println("[SERVER] Returning encrypted message to client...");
                return ctstub.get(username, this.getKeyByID(ID).getKey());
            }
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CipherKey getKeyByID(String ID) {
        return this.getKeys().get(ID);
    }

    public Integer generateX(String ID) {
        System.out.println("[SERVER] Generating large random number for client...");
        Integer aValue = (ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE));
        keys.put(ID, new CipherKey(aValue, null));

        BigInteger a = new BigInteger(String.valueOf(aValue)),
                p = new BigInteger(String.valueOf(this.getPrimeNum())),
                g = new BigInteger(String.valueOf(this.getPrimitiveRoot())),
                x = g.modPow(a, p);
        return x.intValue();
    }

    @Override
    public synchronized void sendYValueToServer(String ID, Integer y) throws RemoteException {
        System.out.println("[SERVER] Storing client's large random number...");
        BigInteger kValueServer = BigInteger.valueOf(y)
                .modPow(BigInteger.valueOf(keys.get(ID).getaValue()), BigInteger.valueOf(this.getPrimeNum()));
        this.getKeys().get(ID).setKey(kValueServer.intValue());
    }

    @Override
    public synchronized Integer requestPrimeNumFromServer() throws RemoteException {
        return this.getPrimeNum();
    }

    @Override
    public synchronized Integer requestPrimRootFromServer() throws RemoteException {
        return this.getPrimitiveRoot();
    }

    @Override
    public synchronized Integer requestXValueFromServer(String ID) throws RemoteException {
        return generateX(ID);
    }

    @Override
    public synchronized boolean register(String ID) throws RemoteException {
        if (getKeys().containsKey(ID)) {
            throw new AlreadyRegisteredException("[SERVER] Client already exists!");
        }
        getKeys().put(ID, null);
        System.out.printf("[SERVER] Client connected %s\n", ID);
        return true;
    }

    @Override
    public synchronized boolean unregister(String ID) throws ClientNotFoundException {
        if (!getKeys().containsKey(ID)) {
            throw new ClientNotFoundException("[SERVER] Client does not exist!");
        }
        keys.remove(ID);
        return true;
    }

    public Integer getPrimeNum() {
        return primeNum;
    }

    public Integer getPrimitiveRoot() {
        return primitiveRoot;
    }

    public Map<String, CipherKey> getKeys() {
        return keys;
    }
}