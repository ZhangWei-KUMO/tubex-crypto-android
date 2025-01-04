package chat.tubex.analysis.model;

import com.google.gson.annotations.SerializedName;

public class GoogleTranslatorResponse {

    @SerializedName("sentences")
    private Sentence[] sentences;

    @SerializedName("src")
    private String sourceLanguage;

    @SerializedName("spell")
    private Object spell; // 可以使用 LinkedTreeMap


    public Sentence[] getSentences() {
        return sentences;
    }

    public void setSentences(Sentence[] sentences) {
        this.sentences = sentences;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }


    public Object getSpell() {
        return spell;
    }

    public void setSpell(Object spell) {
        this.spell = spell;
    }

    public static class Sentence {
        @SerializedName("trans")
        private String translation;

        @SerializedName("orig")
        private String original;

        @SerializedName("backend")
        private int backend;


        public String getTranslation() {
            return translation;
        }

        public void setTranslation(String translation) {
            this.translation = translation;
        }


        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }

        public int getBackend() {
            return backend;
        }

        public void setBackend(int backend) {
            this.backend = backend;
        }
    }
}