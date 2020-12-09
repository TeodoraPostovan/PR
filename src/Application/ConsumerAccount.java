package Application;

import java.util.ArrayList;

public class ConsumerAccount {

    static Account consumer1 = new Account (01234567, 0011,0);
    static Account consumer2 = new Account (76543210, 1254,13467);
    static Account consumer3 = new Account (98993471, 9999,2300);
    static Account consumer4 = new Account (76779908, 2008,1010);

    static ArrayList<Account> consumers = new ArrayList<>();
    static {
        consumers.add(consumer1);
        consumers.add(consumer2);
        consumers.add(consumer3);
        consumers.add(consumer4);
    }
}
