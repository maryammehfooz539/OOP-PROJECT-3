package com.jewellery.model;

// PriceQuote class: the Price Calculator. It holds what the admin typed (weight, metal, rate, charges)
// and works out the selling price of a piece of jewellery:
//   metal cost  = weight in tola x rate per tola x purity   (1 tola = 11.6638038 grams)
//   making      = a percent of the metal cost, OR rupees per gram, OR a flat amount
//   stones      = diamond carats x diamond rate per carat
//   total       = metal + making + stones + other charges - discount
public class PriceQuote {

    // Standard value (see the references in LiveRateFetcher): grams in one tola
    public static final double GRAMS_PER_TOLA = 11.6638038;

    // The three ways to charge for making
    public static final String MAKING_PERCENT = "percent";
    public static final String MAKING_PER_GRAM = "perGram";
    public static final String MAKING_FLAT = "flat";

    private String metal;            // like "Gold 22K"
    private double weight;           // grams
    private double purity;           // 1.0 for 24K gold, 22/24 for 22K gold ...
    private double ratePerTola;      // Rs. per tola of 24K gold (or of silver)
    private String makingType;       // percent, perGram or flat
    private double makingValue;      // the number that goes with makingType
    private double diamondCarats;    // weight of stones in carats (0 if none)
    private double diamondRate;      // Rs. per carat
    private double otherCharges;     // any extra charge (Rs.)
    private double discountPercent;  // 0 to 100

    public PriceQuote(String metal, double weight, double purity, double ratePerTola, String makingType,
                      double makingValue, double diamondCarats, double diamondRate,
                      double otherCharges, double discountPercent) {
        this.metal = metal;
        this.weight = weight;
        this.purity = purity;
        this.ratePerTola = ratePerTola;
        this.makingType = makingType;
        this.makingValue = makingValue;
        this.diamondCarats = diamondCarats;
        this.diamondRate = diamondRate;
        this.otherCharges = otherCharges;
        this.discountPercent = discountPercent;
    }

    // Returns the purity of a metal name: "Gold 22K" -> 22/24, "Gold 24K" -> 1, "Silver" -> 1 (-1 if unknown)
    public static double purityOf(String metal) {
        if ("Gold 24K".equals(metal) || "Silver".equals(metal)) {
            return 1.0;
        } else if ("Gold 22K".equals(metal)) {
            return 22.0 / 24.0;
        } else if ("Gold 21K".equals(metal)) {
            return 21.0 / 24.0;
        } else if ("Gold 18K".equals(metal)) {
            return 18.0 / 24.0;
        }
        return -1;
    }

    public String getMetal() { return metal; }
    public double getWeight() { return weight; }
    public double getPurity() { return purity; }
    public double getRatePerTola() { return ratePerTola; }
    public String getMakingType() { return makingType; }
    public double getMakingValue() { return makingValue; }
    public double getDiamondCarats() { return diamondCarats; }
    public double getDiamondRate() { return diamondRate; }
    public double getOtherCharges() { return otherCharges; }
    public double getDiscountPercent() { return discountPercent; }

    // Weight in tola
    public double getTola() {
        return weight / GRAMS_PER_TOLA;
    }

    // Price of the metal itself
    public double getMetalCost() {
        return getTola() * ratePerTola * purity;
    }

    // Making charges (workmanship)
    public double getMakingCharges() {
        if (MAKING_PERCENT.equals(makingType)) {
            return getMetalCost() * makingValue / 100.0;
        } else if (MAKING_PER_GRAM.equals(makingType)) {
            return weight * makingValue;
        }
        return makingValue;   // flat amount
    }

    // Price of the stones / diamonds
    public double getStoneCharges() {
        return diamondCarats * diamondRate;
    }

    // Everything added together, before the discount
    public double getSubtotal() {
        return getMetalCost() + getMakingCharges() + getStoneCharges() + otherCharges;
    }

    // Money taken off because of the discount
    public double getDiscountAmount() {
        return getSubtotal() * discountPercent / 100.0;
    }

    // The final selling price
    public double getTotal() {
        return getSubtotal() - getDiscountAmount();
    }
}
