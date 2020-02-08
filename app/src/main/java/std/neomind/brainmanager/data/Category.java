package std.neomind.brainmanager.data;

// 구글의 권고사항에 따른 데이터 직접접근 (setter/getter 지양)
public class Category {
    public static final int NOT_REGISTERED = -1;
    public static final int CATEGORY_ALL = 0;
    private static final String EMPTY_STRINGS = "";
    public int id;
    public String name;

    private Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String toStringAbsolutely() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static class Builder {
        private int id;
        private String name;

        public Builder() {
            id = NOT_REGISTERED;
            name = EMPTY_STRINGS;
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Category build() {
            return new Category(id, name);
        }
    }
}
