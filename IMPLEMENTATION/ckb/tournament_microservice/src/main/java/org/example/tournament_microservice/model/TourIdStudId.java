package org.example.tournament_microservice.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TourIdStudId implements Serializable {
    private String tourId;
    private String studId;

    public TourIdStudId(String tourId, String studId) {
        this.tourId = tourId;
        this.studId = studId;
    }

    public TourIdStudId() {

    }

    public String getStudId() {
        return studId;
    }
    public void setStudId(String studId) {
        this.studId = studId;
    }
    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TourIdStudId that = (TourIdStudId) o;

        return Objects.equals(tourId, that.tourId) &&
                Objects.equals(studId, that.studId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tourId, studId);
    }
}
