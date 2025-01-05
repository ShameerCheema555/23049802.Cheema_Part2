class Parcel {
    private String id;
    private String dimensions; // Dimensions in lxwxh format
    private double weight;

    public Parcel(String id, String dimensions, double weight) {
        this.id = id;
        this.dimensions = dimensions;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public String getDimensions() {
        return dimensions;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Parcel[ID=" + id + ", Dimensions=" + dimensions + ", Weight=" + weight + "]";
    }
}
