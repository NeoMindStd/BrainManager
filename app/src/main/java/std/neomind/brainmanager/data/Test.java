package std.neomind.brainmanager.data;

import java.util.Objects;

// 구글의 권고사항에 따른 데이터 직접접근 (setter/getter 지양)
public class Test {
    public static final int NOT_REGISTERED = -1;

    public static final int TYPE_UNDEFINED = -1;

    public int id;              // db id
    public int cid;             // category id
    public int kid;             // keyword id
    public String name;         // test name
    public String description;  // test description
    private String testedDate;  // yy-mm-dd
    private boolean passed;     // test passing flag
    public int answerTime;      // answering time (s)
    public int type;            // test type (defined by constants. see above)

    private Test(int id, int cid, int kid, String name, String description, String testedDate, boolean passed, int answerTime, int type) {
        this.id = id;
        this.cid = cid;
        this.kid = kid;
        this.name = name;
        this.description = description;
        this.testedDate = testedDate;
        this.passed = passed;
        this.answerTime = answerTime;
        this.type = type;
    }

    // TODO 날짜 형식이 아니면 예외처리
    public String getTestedDate() { return testedDate; }
    public void setTestedDate(String testedDate) { this.testedDate = testedDate; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", cid=" + cid +
                ", kid=" + kid +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", testedDate='" + testedDate + '\'' +
                ", passed=" + passed +
                ", answerTime=" + answerTime +
                ", type=" + type +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Test)) return false;
        Test test = (Test) o;
        return id == test.id &&
                cid == test.cid &&
                kid == test.kid &&
                passed == test.passed &&
                answerTime == test.answerTime &&
                type == test.type &&
                Objects.equals(name, test.name) &&
                Objects.equals(description, test.description) &&
                Objects.equals(testedDate, test.testedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cid, kid, name, description, testedDate, passed, answerTime, type);
    }

    public class Builder {
        private int id;             // db id
        private int cid;            // category id
        private int kid;            // keyword id
        private String name;        // test name
        private String description; // test description
        private String testedDate;  // yy-mm-dd
        private boolean passed;     // test passing flag
        private int answerTime;     // answering time (s)
        private int type;           // test type (defined by constants. see above)

        public Builder() {
            id = NOT_REGISTERED;
            cid = NOT_REGISTERED;
            kid = NOT_REGISTERED;
            name = "";
            description = "";
            testedDate = "";
            passed = false;
            answerTime = NOT_REGISTERED;
            type = TYPE_UNDEFINED;
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setCid(int cid) {
            this.cid = cid;
            return this;
        }

        public Builder setKid(int kid) {
            this.kid = kid;
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

        public Builder setTestedDate(String testedDate) {
            this.testedDate = testedDate;
            return this;
        }

        public Builder setPassed(boolean passed) {
            this.passed = passed;
            return this;
        }

        public Builder setAnswerTime(int answerTime) {
            this.answerTime = answerTime;
            return this;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Test build() {
            return new Test(id, cid, kid, name, description, testedDate, passed, answerTime, type);
        }
    }
}
