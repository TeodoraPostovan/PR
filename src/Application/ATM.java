package Application;

import java.io.Serializable;

public class ATM implements Serializable {

    public static int cardNum; // 8
    public static int pin; // 4
    public static int amount;
    public int balance;
    public static int demand;

    public ATM() {}


    public ATM(int demand, int cardNum, int pin, int amount) {
        this.cardNum = cardNum;
        this.pin = pin;
        this.demand = demand;
        this.amount = amount;
    }

    public ATM(int cardNum, int pin) {
        this(0, cardNum, pin, -1);
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
