import java.util.Scanner;
enum MenuOption{
         OPEN_ACCOUNT,DEPOSIT,WITHDRAW,TRANSFER,EXIT
        }
         record BankInfo(String Name,String Branch){}
public class MiniBank {
     public static void main(String[] args) {
        
        BankInfo bank=new BankInfo(" MiniBank"," Dhrangadhra");
        System.out.println(bank);
        while(true){
        System.out.println("\n===== MINI BANK OF DHRANGADHRA=====");
        System.out.println("1. Open Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer");
        System.out.println("5. Exit");
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter choice:");
        int choice=sc.nextInt();
        switch(choice){
            case 1->
            System.out.println("OpenAccount - To be implemented later");
            case 2->
            System.out.println("Deposit - To be implemented later");
            case 3->
            System.out.println("Withdraw - To be implemented later");
            case 4->
            System.out.println("Transfer - To be implemented later");
            case 5->{
            System.out.println("Thank You for Using Mini Bank.");
                    sc.close();
                    return;
        }                           
            default->   
            System.out.println("Invalid Choice!!!");
            
        }
    }
        
}
}