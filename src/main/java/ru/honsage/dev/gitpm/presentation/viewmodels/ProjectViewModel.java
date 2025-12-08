package ru.honsage.dev.gitpm.presentation.viewmodels;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.honsage.dev.gitpm.presentation.dto.ProjectDTO;

public class ProjectViewModel {
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty localPath = new SimpleStringProperty();
    private final StringProperty remoteURL = new SimpleStringProperty();
    private final StringProperty addedAt = new SimpleStringProperty();

    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public ProjectViewModel(ProjectDTO dto) {
        this.id.set(dto.id());
        this.title.set(dto.title());
        this.description.set(dto.description());
        this.localPath.set(dto.localPath());
        this.remoteURL.set(dto.remoteURL());
        this.addedAt.set(dto.addedAt());
    }

    public StringProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty localPathProperty() { return localPath; }
    public StringProperty remoteURLProperty() { return remoteURL; }
    public StringProperty addedAtProperty() { return addedAt; }
    public BooleanProperty selectedProperty() { return selected; }

    public String getId() { return this.id.get(); }
    public String getTitle() { return this.title.get(); }
    public String getLocalPath() { return this.localPath.get(); }

    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean isSelected) { this.selected.set(isSelected); }
}
