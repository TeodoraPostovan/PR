package Application;

public class Account {

    private int cardNumber;
    private int pin;
    private int cardBalance;

    public Account(int cardNumber, int pin, int cardBalance) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.cardBalance = cardBalance;
    }

    public int getCardNumber() { return cardNumber; }
    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getPin() { return pin; }
    public void setPin(int pin) {
        this.pin  = pin;
    }

    public int getCardBalance() { return cardBalance; }
    public void setCardBalance(int cardBalance) {
        this.cardBalance = cardBalance;
    }

}
