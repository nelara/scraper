import java.util.List;

public class Company {
    private String name = "";
    private List<String> emails;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
                ", emails=" + emails +
                '}';
    }
}
