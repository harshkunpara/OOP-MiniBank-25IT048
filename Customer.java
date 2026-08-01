public class Customer {
private String Name;
private String Email;
private String Mobile;
private final String CustomerID;
   private Address address;//Practical 3
private static long Customercounter=101;
private static String genratecustomerid(){
    return "CUST"+Customercounter++;
}
//Practical 3
  public static class Address{

        private String city;
        private String state;

        public Address(String city, String state){
            this.city=city;
            this.state=state;
        }

        @Override
        public String toString() {
            return city+", "+state;
        }
    }
    //Practical 2
    public Customer(String Name,String Email,String Mobile){
     this.Name=Name;
     this.Email=Email;
     this.Mobile=Mobile;
     this.CustomerID=genratecustomerid();
    }
    //Practical 3

    public void setAddress(Address address){
        this.address=address;
    }

    public Address getAddress(){
        return address;
    }
    //Practical 2
    public String getname(){
        return Name;
    }
      public String getemail(){
        return Email;
    }
      public String getmonile(){
        return Mobile;
    }
      public String getcustomerid(){
        return CustomerID;
    }
    //Practical 3
     @Override
    public String toString() {
        return CustomerID+" | "+Name+" | "+Email+" | "+Mobile+" | "+address;
    }
    @Override
    public Customer clone() {
        try {
            return(Customer)super.clone();
        } catch(CloneNotSupportedException e){
            return null;
        }
    }
}