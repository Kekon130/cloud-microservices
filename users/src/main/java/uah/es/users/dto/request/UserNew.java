package uah.es.users.dto.request;

public class UserNew {
    private String name;
    private String surname;

    public UserNew() {
    }

    public UserNew(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
