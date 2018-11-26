package systems.team040.assigment;

enum GraduateStatus {
    Undergraduate(120),
    PostGraduate(120);

    private final int creditRequirement;

    GraduateStatus(int creditRequirement) {
        this.creditRequirement = creditRequirement;
    }
}

