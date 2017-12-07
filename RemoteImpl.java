import java.math.BigInteger;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RemoteImpl extends UnicastRemoteObject implements RemoteInterface {

    private volatile Integer secretKey;
    private final Integer primeNum = 191;
    private final Integer primitiveRoot = 131;
    private volatile Integer a = 0;
    private volatile Integer numClients = 0;
    private volatile ArrayList<String> clients;
    private Map<String, Integer> keys = Collections.synchronizedMap(
            new HashMap<String, Integer>());

    public RemoteImpl() throws RemoteException {
        super();
        this.clients = new ArrayList<>();
        this.keys = new HashMap<>();
    }

    @Override
    public synchronized String requestCipherFromServer() throws RemoteException, NotBoundException {
        Registry ctreg = LocateRegistry.getRegistry("svm-tjn1f15-comp2207.ecs.soton.ac.uk", 12345);
        CiphertextInterface ctstub = (CiphertextInterface) ctreg.lookup("CiphertextProvider");
        return ctstub.get("yp1g16", this.getSecretKey());
    }

    public Integer generateX() {
        BigInteger p = new BigInteger(String.valueOf(this.getPrimeNum()));
        BigInteger g = new BigInteger(String.valueOf(this.getPrimitiveRoot()));
        setA(ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE));
        BigInteger a = new BigInteger(String.valueOf(getA()));
        BigInteger x = g.modPow(a, p);
        System.out.println("X value for server: " + x);
        return x.intValue();
    }

    @Override
    public synchronized void sendYValueToServer(String ID, Integer y) throws RemoteException {
        BigInteger kValueServer = BigInteger.valueOf(y).modPow(BigInteger.valueOf(this.getA()), BigInteger.valueOf(this.getPrimeNum()));
        this.setSecretKey(kValueServer.intValue());
        System.out.println("Secret key for server is: " + this.getSecretKey());
        keys.put(ID, this.getSecretKey());
        System.out.println("IDs and Keys: " + this.keys.toString());
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
    public synchronized Integer requestXValueFromServer() throws RemoteException {
        return generateX();
    }

    @Override
    public synchronized boolean register(String ID) throws RemoteException {
        if (clients.contains(ID)) {
            throw new AlreadyRegisteredException("Client already exists!");
        }
        clients.add(ID);
        System.out.printf("[SERVER] Client connected %s\n", ID);
        numClients++;
        return true;
    }

    @Override
    public synchronized boolean unregister(String ID) throws ClientNotFoundException {
        if (!clients.contains(ID)) {
            throw new ClientNotFoundException("Client does not exist!");
        }
        clients.removeIf(s -> s.equals(ID));
        numClients--;
        return true;
    }

    public Integer getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(Integer secretKey) {
        this.secretKey = secretKey;
    }

    public Integer getPrimeNum() {
        return primeNum;
    }

    public Integer getPrimitiveRoot() {
        return primitiveRoot;
    }

    public Integer getA() {
        return a;
    }

    public void setA(Integer a) {
        this.a = a;
    }

    public Integer getNumClients() {
        return numClients;
    }

    public void setNumClients(Integer numClients) {
        this.numClients = numClients;
    }

    public ArrayList<String> getClients() {
        return clients;
    }

    public void setClients(ArrayList<String> clients) {
        this.clients = clients;
    }

    public Map<String, Integer> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, Integer> keys) {
        this.keys = keys;
    }
}