package org.aksw.lucene.bean.parser;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SparqlQueryResult implements Serializable {

    @SerializedName("s")
    private TypeValue url;

    @SerializedName("streetLabel")
    private TypeValue streetLabel;

    @SerializedName("lat")
    private TypeValue latitude;

    @SerializedName("long")
    private TypeValue longitude;

    public TypeValue getUrl() {
        return url;
    }

    public void setUrl(TypeValue url) {
        this.url = url;
    }

    public TypeValue getStreetLabel() {
        return streetLabel;
    }

    public void setStreetLabel(TypeValue streetLabel) {
        this.streetLabel = streetLabel;
    }

    public TypeValue getLatitude() {
        return latitude;
    }

    public void setLatitude(TypeValue latitude) {
        this.latitude = latitude;
    }

    public TypeValue getLongitude() {
        return longitude;
    }

    public void setLongitude(TypeValue longitude) {
        this.longitude = longitude;
    }
}
