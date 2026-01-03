package de.muckmuck96.elements.element.metadata;

final class MetadataInfo {
    final Class<?> clazz;
    final MetadataType type;
    final String path;
    final String nameTemplate; // e.g. "ForumConfig" or "{id}" or "profile-{id}"
    final boolean pattern;     // true if nameTemplate contains "{id}"
    final boolean editable;
    final boolean autoMigrate;
    final boolean config;

    MetadataInfo(Class<?> clazz, MetadataType type, String path, String nameTemplate,
                 boolean pattern, boolean editable, boolean autoMigrate, boolean config) {
        this.clazz = clazz;
        this.type = type;
        this.path = path;
        this.nameTemplate = nameTemplate;
        this.pattern = pattern;
        this.editable = editable;
        this.autoMigrate = autoMigrate;
        this.config = config;
    }
}
