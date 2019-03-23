package std.neomind.brainmanager.data;

import java.util.Objects;

// 구글의 권고사항에 따른 데이터 직접접근 (setter/getter 지양)
public class Category {
    public static final int NOT_REGISTERED = -1;

    public int id;
    public String name;
    public String description;

    private Category(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return id == category.id &&
                Objects.equals(name, category.name) &&
                Objects.equals(description, category.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }

    public static class Builder {
        private int id;
        private String name;
        private String description;

        public Builder() {
            id = NOT_REGISTERED;
            name = "";
            description = "";
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Category build() {
            return new Category(id, name, description);
        }
    }
}
