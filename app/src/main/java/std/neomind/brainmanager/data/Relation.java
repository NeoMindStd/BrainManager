package std.neomind.brainmanager.data;

import java.util.Objects;

// 구글의 권고사항에 따른 데이터 직접접근 (setter/getter 지양)
public class Relation {
    public static final int NOT_REGISTERED = -1;

    public int id;      // db id
    public int kid1;    // keyword id(smaller)
    public int kid2;    // keyword id(larger)

    private Relation(int id, int kid1, int kid2) {
        this.id = id;
        this.kid1 = kid1;
        this.kid2 = kid2;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "id=" + id +
                ", kid1=" + kid1 +
                ", kid2=" + kid2 +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relation)) return false;
        Relation relation = (Relation) o;
        return id == relation.id &&
                kid1 == relation.kid1 &&
                kid2 == relation.kid2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, kid1, kid2);
    }

    public class Builder{
        private int id;      // db id
        private int kid1;    // keyword id(smaller)
        private int kid2;    // keyword id(larger)

        public Builder() {
            this.id = NOT_REGISTERED;
            this.kid1 = NOT_REGISTERED;
            this.kid2 = NOT_REGISTERED;
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setKid1(int kid1) {
            this.kid1 = kid1;
            return this;
        }

        public Builder setKid2(int kid2) {
            this.kid2 = kid2;
            return this;
        }

        public Relation build() {
            return new Relation(id, kid1, kid2);
        }
    }
}
