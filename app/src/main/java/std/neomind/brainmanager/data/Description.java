package std.neomind.brainmanager.data;

/**
 * The class of descriptions of keywords
 */
public class Description {
    public int id;
    public String description;

    public Description() {
        int id = -1;
        description = "";
    }

    @Override
    public String toString() {
        return description;
    }

    public String toStringAbsolutely() {
        return "Description{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}