package systems.team040.functions;

public class Module {
    private String name, moduleID, title;
    private TimePeriod timePeriod;
    private Department department;


    public Module(String name, String moduleID, String title, TimePeriod timePeriod, Department department) {
        this.name = name;
        this.moduleID = moduleID;
        this.title = title;
        this.timePeriod = timePeriod;
        this.department = department;
    }
}
