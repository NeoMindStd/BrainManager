package std.neomind.brainmanager.data;

// 구글의 권고사항에 따른 데이터 직접접근 (setter/getter 지양)
public class Test {
    public static final int NOT_REGISTERED = -1;

    public static final int TYPE_UNDEFINED = -1;

    public int id;              // db id
    public int cid;             // category id
    public int kid;             // keyword id
    public long testedDate;    // Date that long data type
    private boolean passed;     // test passing flag
    public int answerTime;      // answering time (s)
    public int type;            // test type (defined by constants. see above)

    private Test(int id, int cid, int kid, Long testedDate, boolean passed, int answerTime, int type) {
        this.id = id;
        this.cid = cid;
        this.kid = kid;
        this.testedDate = testedDate;
        this.passed = passed;
        this.answerTime = answerTime;
        this.type = type;
    }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    @Override
    public String toString() {
        return "Test{" +
                "textView=" + id +
                ", cid=" + cid +
                ", kid=" + kid +
                ", testedDate='" + testedDate + '\'' +
                ", passed=" + passed +
                ", answerTime=" + answerTime +
                ", type=" + type +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException { return super.clone(); }

    public static class Builder {
        private int id;                 // db id
        private int cid;                // category id
        private int kid;                // keyword id
        private long testedDate;        // Date that long data type
        private boolean passed;         // test passing flag
        private int answerTime;         // answering time (s)
        private int type;               // test type (defined by constants. see above)

        public Builder() {
            id = NOT_REGISTERED;
            cid = NOT_REGISTERED;
            kid = NOT_REGISTERED;
            testedDate = NOT_REGISTERED;
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

        public Builder setTestedDate(Long testedDate) {
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
            return new Test(id, cid, kid, testedDate, passed, answerTime, type);
        }
    }
}
