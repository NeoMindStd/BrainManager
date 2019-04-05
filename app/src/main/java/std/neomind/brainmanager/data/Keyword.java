package std.neomind.brainmanager.data;

import java.util.Objects;

// 구글의 권고사항에 따른 데이터 직접접근 (setter/getter 지양)
public class Keyword {
    public static final int NOT_REGISTERED = -1;

    public int id;                  // db item
    public int cid;                 // category item
    public String text;             // contents
    public String imagePath;        // absolute path of contents image
    public int currentLevels;       // automatically review levels
    public int reviewTimes;         // self review times
    private String registrationDate; // yy-mm-dd

    // TODO 날짜 형식이 아니면 예외처리
    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }

    private Keyword(int id, int cid, String text, String imagePath, int currentLevels, int reviewTimes, String registrationDate) {
        this.id = id;
        this.cid = cid;
        this.text = text;
        this.imagePath = imagePath;
        this.currentLevels = currentLevels;
        this.reviewTimes = reviewTimes;
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "Keyword{" +
                "item=" + id +
                ", cid=" + cid +
                ", text='" + text + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", currentLevels=" + currentLevels +
                ", reviewTimes=" + reviewTimes +
                ", registrationDate='" + registrationDate + '\'' +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Keyword)) return false;
        Keyword keyword = (Keyword) o;
        return id == keyword.id &&
                cid == keyword.cid &&
                currentLevels == keyword.currentLevels &&
                reviewTimes == keyword.reviewTimes &&
                Objects.equals(text, keyword.text) &&
                Objects.equals(imagePath, keyword.imagePath) &&
                Objects.equals(registrationDate, keyword.registrationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cid, text, imagePath, currentLevels, reviewTimes, registrationDate);
    }

    public static class Builder {
        private int id;                  // db item
        private int cid;                 // category item
        private String text;             // contents
        private String imagePath;        // absolute path of contents image
        private int currentLevels;       // automatically review levels
        private int reviewTimes;         // self review times
        private String registrationDate; // yy-mm-dd

        public Builder() {
            id = NOT_REGISTERED;
            cid = NOT_REGISTERED;
            text = "";
            imagePath = "";
            currentLevels = NOT_REGISTERED;
            reviewTimes = NOT_REGISTERED;
            registrationDate = "";
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setCid(int cid) {
            this.cid = cid;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder setCurrentLevels(int currentLevels) {
            this.currentLevels = currentLevels;
            return this;
        }

        public Builder setReviewTimes(int reviewTimes) {
            this.reviewTimes = reviewTimes;
            return this;
        }

        public Builder setRegistrationDate(String registrationDate) {
            this.registrationDate = registrationDate;
            return this;
        }

        public Keyword build() {
            return new Keyword(id, cid, text, imagePath, currentLevels, reviewTimes, registrationDate);
        }
    }
}
