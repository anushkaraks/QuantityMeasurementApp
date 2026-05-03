package com.apps.quantitymeasurement;

public class QuantityMeasurementApp {
    public static abstract class Quantity {
        protected final double value;

        protected Quantity(double value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) return true;

            if (obj == null || getClass() != obj.getClass()) return false;

            Quantity other = (Quantity) obj;

            return Double.compare(this.value, other.value) == 0;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(value);
        }
    }

    public static class Feet extends Quantity {
        public Feet(double value) {
            super(value);
        }
    }


    public static class Inches extends Quantity {
        public Inches(double value) {
            super(value);
        }
    }


    public static boolean areFeetEqual(double v1, double v2) {
        return new Feet(v1).equals(new Feet(v2));
    }

    public static boolean areInchesEqual(double v1, double v2) {
        return new Inches(v1).equals(new Inches(v2));
    }

    public static void main(String[] args) {

        Feet f1 = new Feet(1.0);
        Feet f2 = new Feet(1.0);

        System.out.println("Feet comparison (1.0 vs 1.0): " + f1.equals(f2));

        Inches i1 = new Inches(1.0);
        Inches i2 = new Inches(2.0);

        System.out.println("Inches comparison (1.0 vs 2.0): " + i1.equals(i2));

        System.out.println("Helper Feet check: " + areFeetEqual(2.0, 2.0));
        System.out.println("Helper Inches check: " + areInchesEqual(3.0, 4.0));
    }
}