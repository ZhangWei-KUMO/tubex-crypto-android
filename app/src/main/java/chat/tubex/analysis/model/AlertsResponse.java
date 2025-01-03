package chat.tubex.analysis.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AlertsResponse {

    @SerializedName("code")
    private String code; // 修改为 String 类型

    @SerializedName("message")
    private String message;

    @SerializedName("messageDetail")
    private String messageDetail;

    @SerializedName("data")
    private Data data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageDetail() {
        return messageDetail;
    }

    public void setMessageDetail(String messageDetail) {
        this.messageDetail = messageDetail;
    }


    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public static class Data {
        @SerializedName("catalogs")
        private List<Catalog> catalogs;

        public List<Catalog> getCatalogs() {
            return catalogs;
        }

        public void setCatalogs(List<Catalog> catalogs) {
            this.catalogs = catalogs;
        }
    }

    public static class Catalog {
        @SerializedName("catalogId")
        private int catalogId;

        @SerializedName("parentCatalogId")
        private Integer parentCatalogId;

        @SerializedName("icon")
        private String icon;

        @SerializedName("catalogName")
        private String catalogName;

        @SerializedName("description")
        private String description;

        @SerializedName("catalogType")
        private int catalogType;

        @SerializedName("total")
        private int total;

        @SerializedName("articles")
        private List<Article> articles;

        public int getCatalogId() {
            return catalogId;
        }

        public void setCatalogId(int catalogId) {
            this.catalogId = catalogId;
        }

        public Integer getParentCatalogId() {
            return parentCatalogId;
        }

        public void setParentCatalogId(Integer parentCatalogId) {
            this.parentCatalogId = parentCatalogId;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getCatalogName() {
            return catalogName;
        }

        public void setCatalogName(String catalogName) {
            this.catalogName = catalogName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getCatalogType() {
            return catalogType;
        }

        public void setCatalogType(int catalogType) {
            this.catalogType = catalogType;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<Article> getArticles() {
            return articles;
        }

        public void setArticles(List<Article> articles) {
            this.articles = articles;
        }
    }

    public static class Article {
        @SerializedName("id")
        private int id;

        @SerializedName("code")
        private String code;

        @SerializedName("title")
        private String title;

        @SerializedName("type")
        private int type;

        @SerializedName("releaseDate")
        private long releaseDate;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public long getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(long releaseDate) {
            this.releaseDate = releaseDate;
        }
    }
}