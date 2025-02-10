package com.mvilaboa.hogwarts_artifacts_online.artifact;

import java.io.Serializable;

import com.mvilaboa.hogwarts_artifacts_online.wizard.Wizard;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "artifacts")
public class Artifact implements Serializable {

    @Id
    private String id;

    private String name;

    private String description;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private Wizard owner;

    public Artifact() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Wizard getOwner() {
        return owner;
    }

    public void setOwner(Wizard owner) {
        this.owner = owner;
    }

}
