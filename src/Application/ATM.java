package Application;

import java.io.Serializable;
import java.util.ArrayList;

public class ATM implements Serializable {


    public static int cardNum; // 8
    public static int pin; // 4
    public static int amount;
    public int balance;
    public static int demand;

    static Account consumer1 = new Account (01234567, 0011,0);
    static Account consumer2 = new Account (76543210, 1254,13467);
    static Account consumer3 = new Account (98993471, 9999,2300);
    static Account consumer4 = new Account (76779908, 2008,1010);

    public static ArrayList<Account> consumers = new ArrayList<>();
    static {
        consumers.add(consumer1);
        consumers.add(consumer2);
        consumers.add(consumer3);
        consumers.add(consumer4);
    }

    public ATM() {}

    public ATM(int cardNum, int pin) {
        this(1, cardNum, pin, -1);
    }
    public ATM(int demand, int cardNum, int pin, int amount) {
        this.cardNum = cardNum;
        this.pin = pin;
        this.demand = demand;
        this.amount = amount;
    }


    public int getCardNum() { return cardNum; }
    public void setCardNum(int cardNum){this.cardNum = cardNum;}

    public int getPin() { return pin; }
    public void setPin(int pin){this.pin = pin;}

    public int getAmount() { return amount; }
    public void setAmount(int amount){this.amount = amount;}

    public int getBalance() { return balance; }
    public void setBalance(int balance){this.balance = balance;}

    public int getDemand() { return demand; }
    public void setDemand(int demand){this.demand = demand;}
}
