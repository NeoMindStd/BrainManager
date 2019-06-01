package std.neomind.brainmanager.data;

/**
 * The class of descriptions of keywords
 */
public class Description {
    public static final String EMPTY_STRINGS = "";

    public int id;
    public String description;

    public Description() {
        int id = -1;
        description = EMPTY_STRINGS;
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