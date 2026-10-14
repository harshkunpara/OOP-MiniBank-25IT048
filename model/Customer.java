package model;

public class Customer implements Cloneable {
    private String Name;
    private String Email;
    private String Mobile;
    private final String CustomerID;
    private Address address;
    private static long Customercounter = 101;

    private static String genratecustomerid() {
        return "CUST" + Customercounter++;
    }

    public static class Address {
        private String line;
        private String city;
        private String state;
        private String pincode;

        public Address(String city, String state) {
            this.city = city;
            this.state = state;
        }

        public Address(String line, String city, String pincode) {
            this.line = line;
            this.city = city;
            this.pincode = pincode;
        }

        public String getLine() {
            return line;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getPincode() {
            return pincode;
        }

        @Override
        public String toString() {
            if (line != null && pincode != null) {
                return line + ", " + city + " - " + pincode;
            }
            return city + (state != null ? ", " + state : "");
        }
    }

    public Customer(String Name, String Email, String Mobile) {
        this.Name = Name;
        this.Email = Email;
        this.Mobile = Mobile;
        this.CustomerID = genratecustomerid();
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public String getname() {
        return Name;
    }

    public String getName() {
        return Name;
    }

    public String getemail() {
        return Email;
    }

    public String getEmail() {
        return Email;
    }

    public String getmonile() {
        return Mobile;
    }

    public String getMobile() {
        return Mobile;
    }

    public String getcustomerid() {
        return CustomerID;
    }

    public String getCustomerId() {
        return CustomerID;
    }

    @Override
    public String toString() {
        return CustomerID + " | " + Name + " | " + Email + " | " + Mobile + " | " + address;
    }

    @Override
    public Customer clone() {
        try {
            return (Customer) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
