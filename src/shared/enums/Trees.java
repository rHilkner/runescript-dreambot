package shared.enums;

public enum  Trees {
    Tree("Tree", "Logs"),
    Willow("Willow", "Willow logs");

    private String treeName;
    private String logsName;

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
