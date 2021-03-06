package std.neomind.brainmanager.data;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;

// 구글의 권고사항에 따른 데이터 직접접근 (setter/getter 지양)
public class Keyword {
    public static final String EMPTY_STRINGS = "";
    public static final int NOT_REGISTERED = -1;
    public static final int ZERO_INIT = 0;
    public int id;                                          // primary key id
    public int cid;                                         // category id
    public String name;                                     // the name of keyword
    public String imagePath;                                // absolute path of contents image
    public int currentLevels;                               // automatically review levels
    public int reviewTimes;                                 // self review times
    public long registrationDate;                           // Date that long data type
    public double ef;                                       // 기억 용이성
    public int interval;                                    // 반복주기
    private CardView cardView;                              // cardView to be set
    @NonNull
    private ArrayList<Description> descriptions;   // contents
    @NonNull
    private ArrayList<Integer> relationIds;        // self-relation id
    private boolean selected;                               // 다중 삭제 등의 상태시 선택 여부

    private Keyword(CardView CardView, int id, int cid, String name, ArrayList<Description> descriptions,
                    String imagePath, int currentLevels, int reviewTimes, long registrationDate,
                    ArrayList<Integer> relationIds, double ef, int interval, boolean seledted) {
        this.cardView = CardView;
        this.id = id;
        this.cid = cid;
        this.name = name;
        this.descriptions = descriptions;
        this.imagePath = imagePath;
        this.currentLevels = currentLevels;
        this.reviewTimes = reviewTimes;
        this.registrationDate = registrationDate;
        this.relationIds = relationIds;
        this.ef = ef;
        this.interval = interval;
        this.selected = seledted;
    }

    public ArrayList<Description> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(@NonNull ArrayList<Description> descriptions) {
        this.descriptions = descriptions;
    }

    public ArrayList<Integer> getRelationIds() {
        return relationIds;
    }

    public void setRelationIds(@NonNull ArrayList<Integer> relationIds) {
        this.relationIds = relationIds;
    }

    public CardView getCardView() {
        return cardView;
    }

    public void setCardView(CardView CardView) {
        this.cardView = CardView;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return name;
    }

    public String toStringAbsolutely() {
        return "Keyword{" +
                "cardView=" + cardView +
                ", id=" + id +
                ", cid=" + cid +
                ", name='" + name + '\'' +
                ", descriptions=" + descriptions +
                ", imagePath='" + imagePath + '\'' +
                ", currentLevels=" + currentLevels +
                ", reviewTimes=" + reviewTimes +
                ", registrationDate=" + registrationDate +
                ", relationIds=" + relationIds +
                ", ef=" + ef +
                ", interval=" + interval +
                ", selected=" + selected +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static class Builder {
        private CardView cardView;                              // cardView to be set
        private int id;                                         // primary key id
        private int cid;                                        // category id
        private String name;                                    // the name of keyword
        @NonNull
        private ArrayList<Description> descriptions;   // contents
        private String imagePath;                               // absolute path of contents image
        private int currentLevels;                              // automatically review levels
        private int reviewTimes;                                // self review times
        private long registrationDate;                          // Date that long data type
        @NonNull
        private ArrayList<Integer> relationIds;        // self-relation id
        private double ef;                                      // 기억 용이성
        private int interval;                                   // 반복주기
        private boolean selected;                               // 선택 여부

        public Builder() {
            cardView = null;
            id = NOT_REGISTERED;
            cid = NOT_REGISTERED;
            name = EMPTY_STRINGS;
            descriptions = new ArrayList<>();
            imagePath = EMPTY_STRINGS;
            currentLevels = ZERO_INIT;
            reviewTimes = ZERO_INIT;
            registrationDate = System.currentTimeMillis();
            relationIds = new ArrayList<>();
            ef = ZERO_INIT;
            interval = ZERO_INIT;
            selected = false;
        }

        public Builder setCardView(CardView CardView) {
            this.cardView = CardView;
            return this;
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setCid(int cid) {
            this.cid = cid;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescriptions(@NonNull ArrayList<Description> descriptions) {
            this.descriptions = descriptions;
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

        public Builder setRegistrationDate(long registrationDate) {
            this.registrationDate = registrationDate;
            return this;
        }

        public Builder setRelationIds(@NonNull ArrayList<Integer> relationIds) {
            this.relationIds = relationIds;
            return this;
        }

        public Builder setEF(double ef) {
            this.ef = ef;
            return this;
        }

        public Builder setInterval(int interval) {
            this.interval = interval;
            return this;
        }

        public Builder setSelected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public Keyword build() {
            return new Keyword(cardView, id, cid, name, descriptions, imagePath, currentLevels,
                    reviewTimes, registrationDate, relationIds, ef, interval, selected);
        }
    }
}
