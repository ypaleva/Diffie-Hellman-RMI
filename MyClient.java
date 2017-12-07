import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MyClient extends UnicastRemoteObject {

    private String ID;
    private RemoteInterface stub;
    private Integer b;
    private Integer secretKey = 0;

    public MyClient(String ID) throws RemoteException {
        this.ID = ID;
    }

    public static void main(String[] args) throws RemoteException {
        MyClient client = new MyClient(UUID.randomUUID().toString());

        if(args.length >= 2) {
            client.initializeClient(args[0], args[1]);
        } else {
            client.initializeClient("localhost", "yp1g16");
        }
        client.disconnect();
    }

    public void initializeClient(String host, String username) {
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(host,2605);
            stub = (RemoteInterface) registry.lookup("CipherServer");
            stub.register(this.getID());
            //System.out.printf("[CLIENT: %s] Registered to server - %s\n", this.getID(), "CipherServer");
            System.out.printf("[CLIENT: %s] {", this.getID());
            int x = stub.requestXValueFromServer(this.getID());
            generateY(stub);
            generateSecretKey(x);
            String cipher = stub.requestCipherFromServer(username, this.getID());
            if (cipher != null) {
                System.out.printf(", Secret: %d, Message: %s}\n", getSecretKey(), decrypt(cipher).substring(0, 50));
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void disconnect() throws RemoteException {
        getStub().unregister(this.getID());
    }


    public void generateY(RemoteInterface remoteInterface) throws RemoteException {
        BigInteger p = new BigInteger(String.valueOf(remoteInterface.requestPrimeNumFromServer()));
        BigInteger g = new BigInteger(String.valueOf(remoteInterface.requestPrimRootFromServer()));
        setB(ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE));
        BigInteger b = new BigInteger(String.valueOf(getB()));
        BigInteger y = g.modPow(b, p);
        System.out.printf("Y Value: %d", y);
        remoteInterface.sendYValueToServer(getID(), y.intValue());
    }

    public Integer generateSecretKey(Integer x) throws RemoteException {
        BigInteger kValueClient = BigInteger.valueOf(x).modPow(BigInteger.valueOf(getB()),
                BigInteger.valueOf(getStub().requestPrimeNumFromServer()));
        setSecretKey(kValueClient.intValue());
        return getSecretKey();
    }

    private String encrypt(String message) {
        StringBuilder encryptedMessage = new StringBuilder();
        if (!(message.length() % 8 == 0)) {
            throw new IllegalArgumentException("The string is not divisible by 8 and cannot be encrypted!");
        } else {
            int count = 0;
            for (int i = 0; i < (message.length() / 8); i++) {
                String eight = message.substring(count, count + 8);

                String firstTranspose = transposeRight(eight, (getSecretKey() % 8));
                String firstEncode = caesarEncode(firstTranspose, (getSecretKey() % 26));
                String secondTranspose = transposeRight(firstEncode, (getSecretKey() % 8));
                String secondEncode = caesarEncode(secondTranspose, (getSecretKey() % 26));

                encryptedMessage.append(secondEncode);
                count += 8;
            }
        }
        return encryptedMessage.toString();
    }

    private String decrypt(String message) {
        StringBuilder decryptedMessage = new StringBuilder();
        if (!(message.length() % 8 == 0)) {
            throw new IllegalArgumentException("The string is not divisible by 8 and cannot be decrypted!");
        } else {
            int count = 0;
            for (int i = 0; i < (message.length() / 8); i++) {
                String eight = message.substring(count, count + 8);

                String firstTranspose = transposeLeft(eight, (getSecretKey() % 8));
                String firstEncode = caesarDecode(firstTranspose, (getSecretKey() % 26));
                String secondTranspose = transposeLeft(firstEncode, (getSecretKey() % 8));
                String secondEncode = caesarDecode(secondTranspose, (getSecretKey() % 26));

                decryptedMessage.append(secondEncode);
                count += 8;
            }
        }
        return decryptedMessage.toString();
    }


    private String caesarDecode(String message, int shiftWith) {
        return caesarEncode(message, 26 - shiftWith);
    }

    private String caesarEncode(String message, int shiftWith) {
        char[] charMessage = message.toCharArray();
        shiftWith = shiftWith % 26 + 26;
        StringBuilder caesarCipher = new StringBuilder();
        for (char c : charMessage) {
            if (Character.isLetter(c)) {
                caesarCipher.append((char) ('A' + (c - 'A' + shiftWith) % 26));
            } else {
                System.err.println("String contains characters other than letters!");
                caesarCipher.append(c);
            }
        }
        return caesarCipher.toString();
    }

    private String transposeRight(String sentence, int transposeWith) {
        //checking for invalid parameters
        if (sentence == null || transposeWith < 0) {
            throw new IllegalArgumentException("The string is empty or the int parameter is negative!");
        }
        //breaking the string up to a char array
        char[] charArr = sentence.toCharArray();
        String transposed = null;
        //calculating the difference between the length of the array and the
        //number to rotate with in order to avoid pointless transpositions
        int difference = charArr.length - transposeWith % charArr.length;
        if (difference > 0) {
            //creating a temporary array, same as original
            char[] temp = charArr.clone();
            //from left to right
            for (int i = 0; i < charArr.length; i++) {
                //place every element on its calculated new position
                int j = (i + difference) % charArr.length;
                charArr[i] = temp[j];
            }
        }
        transposed = new String(charArr);
        return transposed;
    }

    private static String transposeLeft(String sentence, int transposeWith) {
        //checking for invalid parameters
        if (sentence == null || transposeWith < 0) {
            throw new IllegalArgumentException("The string is empty or the int parameter is negative!");
        }
        //breaking the string up to a char array
        char[] charArr = sentence.toCharArray();
        String transposed = null;
        //calculating the difference between the length of the array and the
        //number to rotate with in order to avoid pointless transpositions
        int difference = charArr.length - transposeWith % charArr.length;
        if (difference > 0) {
            //creating a temporary array, same as original
            char[] temp = charArr.clone();
            //from left to right
            for (int i = 0; i < charArr.length; i++) {
                //place every element on its calculated new position
                int j = ((i + (charArr.length - difference)) % charArr.length);
                charArr[i] = temp[j];
            }
        }
        transposed = new String(charArr);
        return transposed;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public RemoteInterface getStub() {
        return stub;
    }

    public void setStub(RemoteInterface stub) {
        this.stub = stub;
    }

    public Integer getB() {
        return b;
    }

    public void setB(Integer b) {
        this.b = b;
    }

    public Integer getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(Integer secretKey) {
        this.secretKey = secretKey;
    }
}