public class Driver {
    public static void main(String[] args) {

        // Mobile Validation
        System.out.println("===== MOBILE =====");
        System.out.println(Validator.isValidMobile("9876543210"));
        System.out.println(Validator.isValidMobile("12345"));

        // Email Validation
        System.out.println("\n===== EMAIL =====");
        System.out.println(Validator.isValidEmail("harsh@gmail.com"));
        System.out.println(Validator.isValidEmail("harshgmail.com"));

        // PAN Validation
        System.out.println("\n===== PAN =====");
        System.out.println(Validator.isValidPan("ABCDE1234F"));
        System.out.println(Validator.isValidPan("ABC123"));

        // IFSC Validation
        System.out.println("\n===== IFSC =====");
        System.out.println(Validator.isValidIfsc("SBIN0001234"));
        System.out.println(Validator.isValidIfsc("1234"));

        // Command Parsing
        System.out.println("\n===== COMMAND =====");

        Command cmd = CommandParser.parse("DEPOSIT AC0001 500");

        System.out.println("Type    : " + cmd.type());
        System.out.println("Account : " + cmd.accountNumber());
        System.out.println("Amount  : " + cmd.amount());

        // Statement Formatter
        System.out.println("\n===== STATEMENT =====");

        Account account = new Account("Harsh", 5000);

        System.out.println(
                StatementFormatter.buildStatement(account)
        );
    }
}
