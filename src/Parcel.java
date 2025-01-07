class Parcel {
    private String id;
    private String dimensions;
    private double weight;
    private String status;

    public Parcel(String id, String dimensions, double weight, String status) {
        this.id = id;
        this.dimensions = dimensions;
        this.weight = weight;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Parcel[ID=" + id + ", Dimensions=" + dimensions + ", Weight=" + weight + ", Status=" + status + "]";
    }
}
