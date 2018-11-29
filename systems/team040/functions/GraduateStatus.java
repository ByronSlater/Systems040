package systems.team040.functions;

enum GraduateStatus {
    Undergraduate(120),
    PostGraduate(180);

    private final int creditRequirement;

    GraduateStatus(int creditRequirement) {
        this.creditRequirement = creditRequirement;
    }
}

