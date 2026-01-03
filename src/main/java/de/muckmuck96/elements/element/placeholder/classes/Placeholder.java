package de.muckmuck96.elements.element.placeholder.classes;

import de.muckmuck96.elements.element.placeholder.functions.PlaceholderFunction;

import java.util.ArrayList;
import java.util.List;

public class Placeholder {
    private boolean cleanup;
    private List<String> keys = new ArrayList<>();
    private PlaceholderFunction<?> pf;

    public List<String> getKeys() {
        return keys != null ? keys : new ArrayList<>();
    }

    public String getKey() {
        List<String> k = getKeys();
        return !k.isEmpty() ? k.get(0) : "";
    }

    public void setKeys(List<String> keys) {
        this.keys = keys != null ? keys : new ArrayList<>();
    }

    public void addKey(String key) {
        if (this.keys == null) {
            this.keys = new ArrayList<>();
        }
        this.keys.add(key);
    }

    public void setCleanup(boolean cleanup) {
        this.cleanup = cleanup;
    }

    public boolean isCleanup() {
        return cleanup;
    }

    public PlaceholderFunction<?> getPf() {
        return pf;
    }

    public void setPf(PlaceholderFunction<?> pf) {
        this.pf = pf;
    }

    public static class Builder {
        private final Placeholder placeholder;

        public Builder() {
            this.placeholder = new Placeholder();
        }



        public Builder withKey(String key) {
            if(this.placeholder.getKeys() == null) {
                this.placeholder.setKeys(new ArrayList<>());
            }
            this.placeholder.addKey(key);
            return this;
        }

        public Builder withKeys(List<String> keys) {
            if(this.placeholder.getKeys() == null) {
                this.placeholder.setKeys(new ArrayList<>());
            }
            this.placeholder.setKeys(keys);
            return this;
        }

        public Builder withCleanup(boolean cleanup) {
            this.placeholder.setCleanup(cleanup);
            return this;
        }

        public Builder withPlaceholderFunction(PlaceholderFunction<?> pf) {
            this.placeholder.setPf(pf);
            return this;
        }

        public Placeholder build() {
            return placeholder;
        }
    }



}
