package model;

import java.io.Serializable;
import model.annotation.Id;

public class Customer implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private final String customerId;
    private String name;
    private String email;
    private String mobile;
    private Address address;

    private static long customerCounter = 101;

    private static synchronized String generateCustomerId() {
        return "CUST" + customerCounter++;
    }

    public static class Address implements Serializable {
        private static final long serialVersionUID = 1L;

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

    public Customer(String name, String email, String mobile) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.customerId = generateCustomerId();
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getCustomerId() {
        return customerId;
    }

    // Compatibility getters for legacy callers
    public String getname() {
        return name;
    }

    public String getemail() {
        return email;
    }

    public String getmonile() {
        return mobile;
    }

    public String getcustomerid() {
        return customerId;
    }

    @Override
    public String toString() {
        return customerId + " | " + name + " | " + email + " | " + mobile + " | " + address;
    }

    @Override
    public Customer clone() {
        try {
            Customer copy = (Customer) super.clone();
            if (this.address != null) {
                copy.address = new Address(this.address.getLine(), this.address.getCity(), this.address.getPincode());
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
