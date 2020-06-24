package shared.enums;

import org.dreambot.api.methods.Calculations;
import shared.Util;

import java.util.Date;

import static scriptz.RunescriptAbstractContext.logScript;

public enum DistractionType {
    PhoneNotification( 3 * 60, 11 * 60, 3, 20),
    TalkingToSomeone(15 * 60, 75 * 60, 60, 180),
    LittleLogout(60 * 60, 90 * 60, 5 * 60, 10 * 60);

    private Date nextDistractionDate;
    private final int nextDistractionMin;
    private final int nextDistractionMax;
    private final int engagedDuration;
    private final int notEngagedDuration;

    DistractionType(int nextDistractionMin, int nextDistractionMax, int notEngagedDuration, int engagedDuration) {
        this.nextDistractionMin = nextDistractionMin;
        this.nextDistractionMax = nextDistractionMax;
        this.engagedDuration = engagedDuration;
        this.notEngagedDuration = notEngagedDuration;
        this.resetNextDistractionDate();
    }

    public Date getNextDistractionDate() {
        return this.nextDistractionDate;
    }

    public void resetNextDistractionDate() {

        nextDistractionDate = new Date();

        // 99% of the chances for a gaussian distribution happens between (peak - 2 sigma) and (peak + 2 sigma)
        double peak = (this.nextDistractionMin + this.nextDistractionMax) / 2.0;
        double sigma = (peak - this.nextDistractionMin) / 2;
        int millisToAdd = (int) Calculations.nextGaussianRandom(peak * 1000, sigma * 1000);
        this.nextDistractionDate = Util.dateAddMillis(nextDistractionDate, millisToAdd);

        logScript("nextDistractionDate " + this.name() + ": " + nextDistractionDate);
    }

    public int getDistractionSleep(GameStyle gameStyle) {

        // Obs: E(X) = shape * scale (expectation value of the distribution)
        // So if we want to have E(X) = 3000 as expectation of a phone distraction, and we have shape = 2, we should set scale = 1500
        // Use this website to try some values: https://homepage.divms.uiowa.edu/~mbognar/applets/gamma.html

        int shape;
        int scale;
        int baseScale;

        boolean isDistractionEngaged = Calculations.random(0.0, 1.0) <= getDistractionEngagement(gameStyle);

        if (isDistractionEngaged) {
            baseScale = engagedDuration * 500;
            shape = Calculations.random(2, 4);
        } else {
            baseScale = notEngagedDuration * 500;
            shape = Calculations.random(3, 5);
        }

        scale = (int) Calculations.random(baseScale * 0.8, baseScale * 1.2);
        return (int) Calculations.nextGammaRandom(shape, scale);
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
            case LittleLogout:
        }

        return engagementChance;

    }

}
