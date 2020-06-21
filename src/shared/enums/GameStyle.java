package shared.enums;

public enum GameStyle {
    HardCore(600),
    Normal(1300),
    Lazy(8000),
    VeryLazy(16000),
    Afk(Integer.MAX_VALUE);

    double baseReactionTime; // in millis

    GameStyle(double baseReactionTime) {
        this.baseReactionTime = baseReactionTime;
    }

    public double getBaseReactionTime() {
        return baseReactionTime;
    }

}
