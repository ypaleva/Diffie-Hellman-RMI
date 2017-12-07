import pkg.CiphertextInterface;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class Test
{
    public static void main(String args[]) {

        try {
            System.setProperty( "java.security.policy", "mypolicy" );
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            Registry ctreg = LocateRegistry.getRegistry( "svm-tjn1f15-comp2207.ecs.soton.ac.uk", 12345 );
            CiphertextInterface ctstub = (CiphertextInterface) ctreg.lookup( "CiphertextProvider" );
            System.out.println( ctstub.get( "user", 84 ) );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}