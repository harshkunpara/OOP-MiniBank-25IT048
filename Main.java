public class Main{

    public static void main(String[] args) {

        Customer c1 = new Customer("Riya", "riya@gmail.com", "9876543210");
        Customer c2 = new Customer("Harsh", "harsh@gmail.com", "9876500000");
        Customer c3 = new Customer("Amit", "amit@gmail.com", "9999999999");
          //Practical 3
        c1.setAddress(new Customer.Address("Rajkot", "Gujarat"));
        c2.setAddress(new Customer.Address("Surendranagar", "Gujarat"));
        c3.setAddress(new Customer.Address("Surat", "Gujarat"));

        Account[] accounts = new Account[3];

        accounts[0] = new Account(c1.getname(), 5000);
        accounts[1] = new Account(c2.getname(), 10000);
        accounts[2] = new Account(c3.getname());

        accounts[0].DEPOSIT(2000);
        System.out.println("Withdraw 3000 : " + accounts[0].WITHDRAW(3000));
        System.out.println("Withdraw 10000 : " + accounts[0].WITHDRAW(10000));

        accounts[1].DEPOSIT(1000);
        accounts[1].WITHDRAW(500);

        accounts[2].DEPOSIT(7000);
        accounts[2].WITHDRAW(2500);

        System.out.println("\n===== CUSTOMER DETAILS =====");
        System.out.println(c1);
        System.out.println(c2);
        System.out.println(c3);

 
        System.out.println("\n===== ACCOUNT DETAILS =====");
        for (Account account : accounts) {
            System.out.println(account);
        }

 
        System.out.println("\n===== EQUALS =====");
        System.out.println(accounts[0].equals(accounts[1]));

   
        System.out.println("\n===== INSTANCEOF =====");
        if (accounts[0] instanceof Account) {
            System.out.println("accounts[0] is an Account object");
        }

   
        System.out.println("\n===== CLONE =====");
        Customer copy = c1.clone();
        System.out.println(copy);
    }
}