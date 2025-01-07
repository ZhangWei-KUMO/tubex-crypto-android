package chat.tubex.analysis;

public class SubtitleItem {
    private String english;
    private String chinese;

    public SubtitleItem(String english, String chinese) {
        this.english = english;
        this.chinese = chinese;
    }

    public String getEnglish() {
        return english;
    }

    public String getChinese() {
        return chinese;
    }
}