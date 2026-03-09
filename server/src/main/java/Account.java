import java.util.Date;

public class Account {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String password; //Hashed
    private Date birthDate;

    public Account (int id, String email, String firstName, String lastName, String password, Date birthDate) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        String result = String.format("%d\t%s\t%s\t%s\t%s\t", id, email, firstName,
                lastName, password);
        result += birthDate;
        return result;
    }
}
