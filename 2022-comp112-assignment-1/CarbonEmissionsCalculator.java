// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 1
 * Name: Jackson Rakena
 * Username: rakenajack
 * ID: 300609159
 */

import ecs100.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Program for calculating carbon emissions */

class CarbonProducingActivity{
    public String prompt;
    public Function<Double,Double> yearlyCalculator;
    public double coefficient;
    public String name;
    public String reductionTip;
    public CarbonProducingActivity(String name,
                                   String prompt,
                                   Function<Double,Double> yearlyCalculator,
                                   double coefficient,
                                   String reductionTip) {
        this.name = name;
        this.prompt = prompt;
        this.yearlyCalculator = yearlyCalculator;
        this.reductionTip = reductionTip;
        this.coefficient = coefficient;
    }
    public double requestAndCalculateEmissions(int current, int total) {
        double input = UI.askDouble("(" + current + "/" + total + ") " + this.prompt);
        return (coefficient * this.yearlyCalculator.apply(input));
    }
}

public class CarbonEmissionsCalculator{

    public static final double EMISSION_FACTOR_POWER = 0.0977;   // emissions factor of electricity per kWh
    public static final double EMISSION_FACTOR_WASTE = 0.299;    // emissions factor of food waste per kg

    public static final double ANNUAL_AVERAGE_POWER_EMISSION_NZ = 266.5;   //Annual average carbon emissions in NZ (kg) from electricity use
    public static final double ANNUAL_AVERAGE_FOOD_WASTE_EMISSION_NZ = 18; //Annual average carbon emissions in NZ (kg) from food waste

    public static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public static final double AVERAGE_DAYS_IN_MONTH = 30.437;
    public static final double AVERAGE_DAYS_IN_YEAR = AVERAGE_DAYS_IN_MONTH * 12;

    public static final ArrayList<CarbonProducingActivity> activities = new ArrayList<>() {
        {
            // Electricity
            add(new CarbonProducingActivity(
                    "Electricity", // Label
                    "What is your household's monthly consumption of electricity in kilowatt hours (kWh)?", // Prompt
                    (e) -> e*12, // Computes yearly unit
                    EMISSION_FACTOR_POWER, // Coefficient (convert unit -> kgCO2 usage)
                    "Try turning the lights off more often, and only running appliances when you need it."
            ));
            add(new CarbonProducingActivity(
                    "Food waste",
                    "How much food do you waste, per week, in kilogrammes (kg)?",
                    (e) -> (e/7)*AVERAGE_DAYS_IN_YEAR,
                    EMISSION_FACTOR_WASTE,
                    "Try wasting less food, such as using leftovers the next night, or buying less."
            ));

            add(new CarbonProducingActivity(
                    "Petrol vehicles",
                    "How many kilometres (km) does your household travel in a petrol-based vehicle each week?",
                    (e) -> (e/7)*AVERAGE_DAYS_IN_YEAR,
                    0.211,
                    "Try catching the bus, carpooling, or cycling."
            ));

            add(new CarbonProducingActivity(
                    "Diesel vehicles",
                    "How many kilometres (km) does your household travel in a diesel-based vehicle each week?",
                    (e) -> (e/7)*AVERAGE_DAYS_IN_YEAR,
                    0.202,
                    "Try catching the bus, carpooling, or cycling."
            ));

            add(new CarbonProducingActivity(
                    "Bus travel",
                    "How many kilometres (km) does your household travel by public bus each week?",
                    (e) -> (e/7)*AVERAGE_DAYS_IN_YEAR,
                    0.108,
                    "Using the bus is a great way to reduce carbon emissions."
                    // I catch the bus, so obviously I can't hate on my most preferred mode of transport
            ));

            add(new CarbonProducingActivity(
                    "Domestic air travel",
                    "How many times per year does your household travel by air between major New Zealand cities?",
                    (e) -> (e * 300), // in this case, the flying distance is roughly estimated at 300km
                    0.134,
                    "Try flying only for essential purposes, and using other ways to travel to your favourite destinations."
            ));

            // easy extensibility to add more
        }
    };

    /**
     * Calculates and prints carbon emissions
     */
    public double calculateEmissions(){
        UI.println();
        UI.println("I'm going to ask you about your household's energy usage and food wastage.");
        double monthlyElectricityUsageKwh = UI.askDouble("What is your household's monthly consumption of electricity in kilowatt hours (kWh)?");
        double weeklyFoodWasteKg = UI.askDouble("How much food do you waste, per week, in kilogrammes (kg)?");

        double monthlyElectricityCarbon = monthlyElectricityUsageKwh * EMISSION_FACTOR_POWER;
        double weeklyFoodCarbon = weeklyFoodWasteKg * EMISSION_FACTOR_WASTE;
        
        UI.println();
        UI.println("Your household's monthly carbon emissions from electricity: " + decimalFormat.format(monthlyElectricityCarbon) + " kg CO2");
        UI.println("Your household's weekly carbon emissions from food waste: " + decimalFormat.format(weeklyFoodCarbon) + " kg CO2");
        
        UI.println();
        double averageDailyEmissions = ((monthlyElectricityCarbon / AVERAGE_DAYS_IN_MONTH) + (weeklyFoodCarbon / 7));
        UI.println("Your household's average daily (combined) carbon emissions: " + decimalFormat.format(averageDailyEmissions) + " kg CO2");
        return averageDailyEmissions;
    }

    public void calculateEmissionsCompletion() {
        double averagePersonEmissions = ANNUAL_AVERAGE_POWER_EMISSION_NZ + ANNUAL_AVERAGE_FOOD_WASTE_EMISSION_NZ;
        double householdEmissionsDaily = calculateEmissions() * AVERAGE_DAYS_IN_YEAR;
        UI.println();
        UI.println("Now we're going to compare you and your household against the averages for New Zealand's households.");
        int householdMembers = UI.askInt("How many people live in your household?");

        double householdMemberEmissions = householdEmissionsDaily / householdMembers;
        UI.println();
        UI.println("The average Kiwi produces " + averagePersonEmissions + "kg/CO2 per year. You produce " + decimalFormat.format(householdMemberEmissions) + "kg/CO2 per year.");
        UI.println("That means your emissions are " + decimalFormat.format((householdMemberEmissions/averagePersonEmissions)*100) + "% of the New Zealand average.");
    }

    public void calculateEmissionsChallenge() {
        UI.println();
        UI.println("I'm going to ask you a few things about your household habits.");
        UI.println();
        HashMap<String, Double> yearlyHouseholdEmissions = new HashMap<String, Double>();

        for (int i = 0; i < activities.size(); i++) {
            CarbonProducingActivity activity = activities.get(i);
            yearlyHouseholdEmissions.put(activity.name, activity.requestAndCalculateEmissions(i+1, activities.size()));
        }

        UI.println();
        int householdMembers = UI.askInt("Finally, how many people live in your household?");

        double totalHouseholdEmissions = yearlyHouseholdEmissions.values().stream().mapToDouble(e ->  e).sum();

        UI.println("Thank you so much for answering.");
        UI.println();
        UI.println();
        UI.println("== YOUR RESULTS ==");

        UI.println();
        UI.println("Your " + householdMembers + "-person household produces " + decimalFormat.format(totalHouseholdEmissions) + " kg/CO2 per year.");
        UI.println("That's " + decimalFormat.format(totalHouseholdEmissions/householdMembers) + " kg of CO2 per person.");
        UI.println();
        UI.println("The average Kiwi household produces 7.5 tonnes (7,500 kg) of CO2 per year. ");
        UI.println("That means your household emissions are " + decimalFormat.format((totalHouseholdEmissions/7500)*100) + "% of the New Zealand average.");
        UI.println();
        UI.println();

        /**
         * Challenge:
         * Helpful tips for reducing your usage, key goals:
         *  - Identify biggest source
         *  - Rank emission sources by % of total
         */

        UI.println("Some tips for reducing your usage:");

        LinkedHashSet<Map.Entry<String, Double>> sortedEmissions = yearlyHouseholdEmissions.entrySet().stream().sorted((a, b) -> (int)(b.getValue() -a.getValue())).collect(Collectors.toCollection(LinkedHashSet::new));

        Map.Entry<String, Double> highestEmitter = sortedEmissions.stream().findFirst().get();
        UI.println(" - Your highest source of emissions is " + highestEmitter.getKey() + " (" + decimalFormat.format(highestEmitter.getValue()) + "kg of CO2). Try reducing this one, if you can.");
        UI.println();
        UI.println();
        UI.println("Here's a full breakdown of your emissions, by type:");
        for (Map.Entry<String, Double> emission : sortedEmissions) {
            if (emission.getValue() == 0) continue;
            UI.println(" - " + emission.getKey() + ": " + decimalFormat.format(emission.getValue()) + " kg of CO2 (" + decimalFormat.format((emission.getValue()/totalHouseholdEmissions)*100) + "% of your total)");
            UI.println("    - " + activities.stream().filter(e -> e.name.equals(emission.getKey())).findFirst().get().reductionTip);
        }
    }

    public void setupGUI(){
        UI.initialise();
        UI.addButton("Calculate Emissions (Core)", this::calculateEmissions);
        UI.addButton("Calculate Emissions (Completion)", this::calculateEmissionsCompletion);
        UI.addButton("Calculate Emissions (Challenge)", this::calculateEmissionsChallenge);
        UI.addButton("Quit", UI::quit);
        UI.setDivider(1);    // Expand the Text pane
        UI.println("Welcome to the Assignment 1 Carbon Emissions Calculator.");
        UI.println("We're going to calculate your carbon emissions,");
        UI.println("by asking you how much you do of each of these carbon-based activities.");
        UI.println();
        UI.println("Press any of the buttons on the left to begin, or press 'Quit' at any time to exit this program.");
        UI.printMessage("COMP 112 - Assignment 1 - Jackson Rakena (300609159)");
        UI.println();
    }

    public static void main(String[] args){
        CarbonEmissionsCalculator cec = new CarbonEmissionsCalculator();
        cec.setupGUI();
    }

}
