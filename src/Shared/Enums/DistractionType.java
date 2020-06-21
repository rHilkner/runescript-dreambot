package Shared.Enums;

import org.dreambot.api.methods.Calculations;

public enum DistractionType {
    PhoneNotification,
    TalkingToSomeone,
    CoffeeBreak;

    double getDistractionSleep(double engagementChance) {

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

        return distractionEngaged ? Calculations.nextGammaRandom(engagedShape, engagedScale)
                : Calculations.nextGammaRandom(notEngagedShape, notEngagedScale);
    }

}
