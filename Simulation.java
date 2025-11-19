import java.util.*;
import java.util.regex.*;
import java.text.SimpleDateFormat;

public class Simulation {

    // --- MOCK DATA CLASSES ---
    static class ParsedTransaction {
        double amount;
        String currency;
        String merchantName;
        String cardIdentifier;

        public ParsedTransaction(double amount, String currency, String merchantName, String cardIdentifier) {
            this.amount = amount;
            this.currency = currency;
            this.merchantName = merchantName;
            this.cardIdentifier = cardIdentifier;
        }
    }

    static class Category {
        int id;
        String name;
        List<String> keywords;

        public Category(int id, String name, String... keywords) {
            this.id = id;
            this.name = name;
            this.keywords = Arrays.asList(keywords);
        }
    }

    // --- LOGIC CLASSES ---
    static class TransactionParser {
        private List<Pattern> patterns = new ArrayList<>();

        public TransactionParser() {
            // Pattern 1: Purchase of [CURRENCY] [AMOUNT] at [MERCHANT] with [CARD]
            patterns.add(Pattern.compile("Purchase of\\s+([A-Z]{3})\\s+(\\d+(?:\\.\\d{1,2})?)\\s+at\\s+(.+?)\\s+with\\s+(?:Card|Account)\\s+(.+)", Pattern.CASE_INSENSITIVE));
            
            // Pattern 2: [CURRENCY] [AMOUNT] spent on [CARD] at [MERCHANT]
            patterns.add(Pattern.compile("([A-Z]{3})\\s+(\\d+(?:\\.\\d{1,2})?)\\s+spent\\s+on\\s+(?:Card|Account)\\s+(.+?)\\s+at\\s+(.+)", Pattern.CASE_INSENSITIVE));
            
            // Pattern 3: Payment of [AMOUNT] [CURRENCY] to [MERCHANT]
            patterns.add(Pattern.compile("Payment of\\s+(\\d+(?:\\.\\d{1,2})?)\\s+([A-Z]{3})\\s+to\\s+(.+?)(?:\\s+on\\s+|$)", Pattern.CASE_INSENSITIVE));
        }

        public ParsedTransaction parse(String body) {
            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(body);
                if (matcher.find()) {
                    String p = pattern.pattern();
                    if (p.startsWith("Purchase of")) {
                        return new ParsedTransaction(
                            Double.parseDouble(matcher.group(2)),
                            matcher.group(1),
                            matcher.group(3).trim(),
                            matcher.group(4).trim()
                        );
                    } else if (p.startsWith("([A-Z]{3})")) {
                        return new ParsedTransaction(
                            Double.parseDouble(matcher.group(2)),
                            matcher.group(1),
                            matcher.group(4).trim(),
                            matcher.group(3).trim()
                        );
                    } else if (p.startsWith("Payment of")) {
                        return new ParsedTransaction(
                            Double.parseDouble(matcher.group(1)),
                            matcher.group(2),
                            matcher.group(3).trim(),
                            null
                        );
                    }
                }
            }
            return null;
        }
    }

    static class CategoryClassifier {
        public Category classify(String merchantName, List<Category> categories) {
            String normalized = merchantName.toLowerCase();
            for (Category cat : categories) {
                for (String keyword : cat.keywords) {
                    if (normalized.contains(keyword)) {
                        return cat;
                    }
                }
            }
            return categories.stream().filter(c -> c.name.equals("Other")).findFirst().orElse(null);
        }
    }

    // --- MAIN SIMULATION ---
    public static void main(String[] args) {
        System.out.println("=== SMS READER AI SIMULATION ===\n");

        // 1. Setup Categories
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Food", "mcdonalds", "kfc", "burger", "pizza", "starbucks", "talabat"));
        categories.add(new Category(2, "Transportation", "uber", "careem", "taxi", "fuel", "adnoc"));
        categories.add(new Category(3, "Groceries", "carrefour", "lulu", "spinneys", "mart"));
        categories.add(new Category(4, "Bills", "du", "etisalat", "dewa"));
        categories.add(new Category(5, "Other"));

        // 2. Setup Logic
        TransactionParser parser = new TransactionParser();
        CategoryClassifier classifier = new CategoryClassifier();

        // 3. Input Data (Sample SMS)
        String[] smsInbox = {
            "Purchase of AED 15.00 at STARBUCKS with Card 1234",
            "AED 45.50 spent on Card 8888 at UBER TRIP",
            "Payment of 120.00 AED to DEWA on 2023-10-01",
            "Purchase of USD 100.00 at AMAZON.COM with Card 1234", // Should be Other/Shopping (if added)
            "Hey, how are you?", // Ignored
            "OTP 123456 is your code" // Ignored
        };

        System.out.println("Scanning " + smsInbox.length + " messages...\n");

        double totalSpent = 0;
        Map<String, Double> categoryTotals = new HashMap<>();

        for (String sms : smsInbox) {
            System.out.println("Processing: \"" + sms + "\"");
            ParsedTransaction t = parser.parse(sms);
            
            if (t != null) {
                Category cat = classifier.classify(t.merchantName, categories);
                String catName = (cat != null) ? cat.name : "Unknown";
                
                System.out.println("  -> MATCH: " + t.currency + " " + t.amount + " | Merchant: " + t.merchantName + " | Category: " + catName);
                
                totalSpent += t.amount; // Assuming 1:1 currency for demo
                categoryTotals.put(catName, categoryTotals.getOrDefault(catName, 0.0) + t.amount);
            } else {
                System.out.println("  -> IGNORED (Not a transaction)");
            }
            System.out.println("");
        }

        // 4. Dashboard Output
        System.out.println("=== DASHBOARD ===");
        System.out.println("Total Spent: " + totalSpent);
        System.out.println("By Category:");
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            System.out.println("  - " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
