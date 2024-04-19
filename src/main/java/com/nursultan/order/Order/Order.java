package com.nursultan.order.Order;

import jakarta.persistence.*;

import java.lang.reflect.Array;

@Entity
@Table(name = "orders")
public class Order{
    @Id
    @SequenceGenerator(
            name = "order_sequence",
            sequenceName = "order_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String originLatitude;

    @Column(nullable = false)
    private String originLongitude;

    @Column(nullable = false)
    private String destinationLatitude;

    @Column(nullable = false)
    private String destinationLongitude;

    @Column(nullable = false)
    private Integer distance;

    @Column(nullable = false)
    private String status;  // "UNASSIGNED", "TAKEN"


    @Version
    private int version;  // Optimistic locking

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", originLatitude='" + originLatitude + '\'' +
                ", originLongitude='" + originLongitude + '\'' +
                ", destinationLatitude='" + destinationLatitude + '\'' +
                ", destinationLongitude='" + destinationLongitude + '\'' +
                ", distance=" + distance +
                ", status='" + status + '\'' +
                ", version=" + version +
                '}';
    }

    // Getters and setters
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setOriginLatitude(String originLatitude) {
        this.originLatitude = originLatitude;
    }

    public void setOriginLongitude(String originLongitude) {
        this.originLongitude = originLongitude;
    }

    public void setDestinationLatitude(String destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public void setDestinationLongitude(String destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public String getOriginLatitude() {
        return originLatitude;
    }

    public String getOriginLongitude() {
        return originLongitude;
    }

    public String getDestinationLatitude() {
        return destinationLatitude;
    }

    public String getDestinationLongitude() {
        return destinationLongitude;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}