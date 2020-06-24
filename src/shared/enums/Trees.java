package shared.enums;

public enum  Trees {
    Tree("Tree", "Logs"),
    Willow("Willow", "Willow logs");

    private final String treeName;
    private final String logsName;

    Trees(String treeName, String logsName) {
        this.treeName = treeName;
        this.logsName = logsName;
    }

    public String getLogsName() {
        return logsName;
    }

    public String getTreeName() {
        return treeName;
    }
}
