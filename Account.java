import java.util.Objects;

public abstract class Account{
    private final String accountNumber;
    private String ownerName;
    private long balance;
    private boolean active; 
    private static int accountcounter=1;
    private static String genrateaccountnumber(){
        return String.format("AC%04d",accountcounter++);
    }
   public Account(String ownerName,long balance){
      this.accountNumber=genrateaccountnumber();
    this.ownerName=ownerName;
   this.balance=balance;
   this.active=true;
   }
   public Account(String ownerName){
    this(ownerName,0);
   }
   public void DEPOSIT(long amount){
    if(amount<0){
  return;
    }
    balance=balance+amount;
   }
   public boolean WITHDRAW(long amount){
      if(amount<0){
       return false;
      }
      if(balance>=amount){
        balance=balance-amount;
      return true;  
    }
      return false;
   }
   public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }
    //Practical 3
       @Override
    public String toString() {
        return accountNumber+" | "+ownerName +" | "+ balance;
    }

    @Override
    public boolean equals(Object obj) {

        if (this==obj)
            return true;

        if (obj==null||getClass()!=obj.getClass())
            return false;

        Account a=(Account)obj;

        return accountNumber.equals(a.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
    public abstract double interestRate();

public abstract boolean canWithdraw(long amount);
}
