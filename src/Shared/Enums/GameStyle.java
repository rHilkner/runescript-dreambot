package Shared.Enums;

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

    public double getEngagementChance(DistractionType distractionType) {
        double engagementChance = 0.5;

        switch (this) {
            case HardCore:
                switch (distractionType) {
                    case PhoneNotification:
                        engagementChance = 0.05;
                        break;
                    case TalkingToSomeone:
                        engagementChance = 0.05;
                        break;
                    case CoffeeBreak:
                        engagementChance = 0.8;
                        break;
                }
                break;
            case Normal:
                switch (distractionType) {
                    case PhoneNotification:
                        engagementChance = 0.10;
                        break;
                    case TalkingToSomeone:
                        engagementChance = 0.15;
                        break;
                    case CoffeeBreak:
                        engagementChance = 0.8;
                        break;
                }
                break;
            case Lazy:
                switch (distractionType) {
                    case PhoneNotification:
                        engagementChance = 0.15;
                        break;
                    case TalkingToSomeone:
                        engagementChance = 0.20;
                        break;
                    case CoffeeBreak:
                        engagementChance = 0.8;
                        break;
                }
                break;
            case VeryLazy:
                switch (distractionType) {
                    case PhoneNotification:
                        engagementChance = 0.20;
                        break;
                    case TalkingToSomeone:
                        engagementChance = 0.20;
                        break;
                    case CoffeeBreak:
                        engagementChance = 0.9;
                        break;
                }
                break;
            case Afk:
                engagementChance = 0.0;
                break;
        }

        return engagementChance;

    }

}
