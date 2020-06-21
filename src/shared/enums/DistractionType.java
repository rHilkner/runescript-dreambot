package shared.enums;

import org.dreambot.api.methods.Calculations;
import shared.Util;

import java.util.Date;

public enum DistractionType {
    PhoneNotification(),
    TalkingToSomeone(),
    CoffeeBreak();
    
    private Date nextDistractionDate;

    DistractionType() { }

    public Date getNextDistractionDate() {
        return this.nextDistractionDate;
    }

    public void initialize() {
        this.resetNextDistractionDate();
    }

    public void resetNextDistractionDate() {

        nextDistractionDate = new Date();
        int millisToAdd;

        switch (this) {
            case PhoneNotification:
                // Happens with peak at u = 17 min and sigma = 6 min (99% of the probability are between (u - 2sigma) and (u + 2sigma), 69%  between (u - sigma) and (u + sigma))
                millisToAdd = (int) Calculations.nextGaussianRandom(17 * 60 * 1000, 6 * 60 * 60);
                this.nextDistractionDate = Util.dateAddMillis(nextDistractionDate, millisToAdd);
                break;
            case TalkingToSomeone:
                // Happens with peak at u = 17 min and sigma = 6 min
                millisToAdd = (int) Calculations.nextGaussianRandom(60 * 60 * 1000, 15 * 60 * 1000);
                this.nextDistractionDate = Util.dateAddMillis(nextDistractionDate, millisToAdd);
                break;
            case CoffeeBreak:
                // Happens with peak at u = 17 min and sigma = 6 min
                millisToAdd = (int) Calculations.nextGaussianRandom(60 * 60 * 1000, 30 * 60 * 1000);
                this.nextDistractionDate = Util.dateAddMillis(Util.getDate("15:30:00"), millisToAdd);
                break;
        }

    }

    public int getDistractionSleep(GameStyle gameStyle) {

        double engagementChance = getDistractionEngagement(gameStyle);

        // Obs: E(X) = shape * scale (expectation value of the distribution)
        // So if we want to have E(X) = 3000 as expectation of a phone distraction, and we have shape = 2, we should set scale = 1500
        // Use this website to try some values: https://homepage.divms.uiowa.edu/~mbognar/applets/gamma.html

        int engagedShape = Calculations.random(1, 3);
        int notEngagedShape = Calculations.random(2, 4);

        int engagedScale = Calculations.random(800, 1600);
        int notEngagedScale = Calculations.random(1000, 2000);

        double randomPercentage = Calculations.random(0.0, 1.0);
        boolean distractionEngaged = engagementChance < randomPercentage;

        switch (this) {
            case PhoneNotification:
                notEngagedScale = Calculations.random(1500, 2100); // P(X < 3s) = 0.5 - shape = 2; scale = 1.8k
                engagedScale = Calculations.random(10000, 14000);  // P(X < 20s) = 0.5 - shape = 2; scale = 12k
                break;
            case TalkingToSomeone:
                notEngagedScale = Calculations.random(4500, 7500); // P(X < 10s) = 0.5 - shape = 2; scale = 6k
                engagedScale = Calculations.random(30000, 42000);  // P(X < 60s) = 0.5 - shape = 2; scale = 36k
                break;
            case CoffeeBreak:
                notEngagedScale = Calculations.random(250000, 330000); // P(X < 8m) = 0.5 - shape = 2; scale = 290k
                engagedScale = Calculations.random(500000, 660000);    // P(X < 16m) = 0.5 - shape = 2; scale = 1.8k
                break;
        }

        return (int) (distractionEngaged ? Calculations.nextGammaRandom(engagedShape, engagedScale)
                        : Calculations.nextGammaRandom(notEngagedShape, notEngagedScale));
    }

    public double getDistractionEngagement(GameStyle gameStyle) {
        double engagementChance = 0.5;

        switch (this) {
            case PhoneNotification:
                switch (gameStyle) {
                    case HardCore:
                        engagementChance = 0.05;
                        break;
                    case Normal:
                        engagementChance = 0.10;
                        break;
                    case Lazy:
                        engagementChance = 0.15;
                        break;
                    case VeryLazy:
                        engagementChance = 0.20;
                        break;
                    case Afk:
                        engagementChance = 0.0;
                        break;
                }
            case TalkingToSomeone:
                switch (gameStyle) {
                    case HardCore:
                        engagementChance = 0.05;
                        break;
                    case Normal:
                        engagementChance = 0.15;
                        break;
                    case Lazy:
                        engagementChance = 0.20;
                        break;
                    case VeryLazy:
                        engagementChance = 0.20;
                        break;
                    case Afk:
                        engagementChance = 0.0;
                        break;
                }
                break;
            case CoffeeBreak:
                switch (gameStyle) {
                    case HardCore:
                        engagementChance = 0.8;
                        break;
                    case Normal:
                        engagementChance = 0.8;
                        break;
                    case Lazy:
                        engagementChance = 0.8;
                        break;
                    case VeryLazy:
                        engagementChance = 0.9;
                        break;
                    case Afk:
                        engagementChance = 0.0;
                        break;
                }
                break;
        }

        return engagementChance;

    }

}
