import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

// Parent class: shared details for all records
abstract class Record {
    private final String date;
    private final String time;

    public Record(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public String getDate() { return date; }
    public String getTime() { return time; }

    public abstract void displayDetails();
}

// Stores one medicine
class Medicine {
    private final String name;
    private final String dosage;
    private final String frequency;
    private final int durationDays;

    public Medicine(String name, String dosage, String frequency, int durationDays) {
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.durationDays = durationDays;
    }

    public String getName() { return name; }

    public void displayDetails() {
        System.out.println("Name: " + name + " | Dosage: " + dosage
                + " | Frequency: " + frequency + " | Duration: " + durationDays + " days");
    }
}

// A medicine-taking event is a type of Record
class MedicineRecord extends Record {
    private final String medicineName;
    private final String status;

    public MedicineRecord(String date, String time, String medicineName, String status) {
        super(date, time);
        this.medicineName = medicineName;
        this.status = status;
    }

    @Override
    public void displayDetails() {
        System.out.println(getDate() + " " + getTime() + " | Medicine: "
                + medicineName + " | Status: " + status);
    }
}

// A health measurement is also a type of Record
class HealthRecord extends Record {
    private final String recordType;
    private final String value;
    private final String unit;

    public HealthRecord(String date, String time, String recordType, String value, String unit) {
        super(date, time);
        this.recordType = recordType;
        this.value = value;
        this.unit = unit;
    }

    @Override
    public void displayDetails() {
        System.out.println(getDate() + " " + getTime() + " | "
                + recordType + ": " + value + " " + unit);
    }
}

// Stores the reminder time and whether the dose is still pending
class Reminder {
    private final String medicineName;
    private final String time;
    private String status;

    public Reminder(String medicineName, String time) {
        this.medicineName = medicineName;
        this.time = time;
        this.status = "Pending";
    }

    public String getMedicineName() { return medicineName; }
    public String getTime() { return time; }
    public String getStatus() { return status; }

    public void updateStatus(String newStatus) { status = newStatus; }

    public void displayDetails() {
        System.out.println("Medicine: " + medicineName + " | Time: "
                + time + " | Status: " + status);
    }
}

// Main application
public class MediCare {
    private static final Scanner input = new Scanner(System.in);
    private static final ArrayList<Medicine> medicines = new ArrayList<>();
    private static final ArrayList<Reminder> reminders = new ArrayList<>();
    private static final ArrayList<Record> history = new ArrayList<>();

    // Dynamic user credentials set during registration
    private static String registeredUsername = "";
    private static String registeredPassword = "";

    private static String today() { return LocalDate.now().toString(); }
    private static String now() { return LocalTime.now().withSecond(0).withNano(0).toString(); }

    private static String readText(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            try {
                int number = Integer.parseInt(readText(prompt));
                if (number > 0) return number;
            } catch (NumberFormatException ignored) { }
            System.out.println("Please enter a whole number greater than zero.");
        }
    }

    private static void register() {
        System.out.println("\n========== MEDICARE REGISTRATION ==========");
        registeredUsername = readText("Enter your name / username: ");
        while (registeredUsername.isEmpty()) {
            System.out.println("Username cannot be empty.");
            registeredUsername = readText("Enter your name / username: ");
        }
        registeredPassword = readText("Set a new password: ");
        while (registeredPassword.isEmpty()) {
            System.out.println("Password cannot be empty.");
            registeredPassword = readText("Set a new password: ");
        }
        System.out.println("Registration successful! You can now log in.");
    }

    private static boolean login() {
        if (registeredUsername.isEmpty()) {
            System.out.println("\nNo registered account found. Please register first.");
            return false;
        }

        System.out.println("\n========== MEDICARE LOGIN ==========");
        String username = readText("Username: ");
        String password = readText("Password: ");
        
        if (registeredUsername.equalsIgnoreCase(username) && registeredPassword.equals(password)) {
            System.out.println("Login successful. Welcome, " + registeredUsername + "!");
            return true;
        }
        System.out.println("Incorrect username or password.");
        return false;
    }

    private static void addMedicine() {
        String name = readText("Medicine name: ");
        String dosage = readText("Dosage (example: 500 mg): ");
        String frequency = readText("Frequency (example: twice a day): ");
        int days = readPositiveInt("Duration in days: ");
        medicines.add(new Medicine(name, dosage, frequency, days));
        System.out.println("Medicine saved.");
    }

    private static void viewMedicines() {
        if (medicines.isEmpty()) {
            System.out.println("No medicines saved yet.");
            return;
        }
        System.out.println("\n--- MY MEDICINES ---");
        for (int i = 0; i < medicines.size(); i++) {
            System.out.print((i + 1) + ". ");
            medicines.get(i).displayDetails();
        }
    }

    private static void setReminder() {
        if (medicines.isEmpty()) {
            System.out.println("Add a medicine first, then create its reminder.");
            return;
        }
        viewMedicines();
        int index = readPositiveInt("Choose medicine number: ") - 1;
        if (index >= medicines.size()) {
            System.out.println("That medicine number does not exist.");
            return;
        }
        String time = readText("Reminder time (example: 08:00 AM): ");
        reminders.add(new Reminder(medicines.get(index).getName(), time));
        System.out.println("Reminder saved.");
    }

    private static void viewReminders() {
        if (reminders.isEmpty()) {
            System.out.println("No reminders saved yet.");
            return;
        }
        System.out.println("\n--- REMINDERS ---");
        for (int i = 0; i < reminders.size(); i++) {
            System.out.print((i + 1) + ". ");
            reminders.get(i).displayDetails();
        }
        String answer = readText("Update a reminder status? (y/n): ");
        if (!answer.equalsIgnoreCase("y")) return;

        int index = readPositiveInt("Reminder number: ") - 1;
        if (index < 0 || index >= reminders.size()) {
            System.out.println("Invalid reminder number.");
            return;
        }
        String status = readText("Enter Taken or Missed: ");
        if (!status.equalsIgnoreCase("Taken") && !status.equalsIgnoreCase("Missed")) {
            System.out.println("Status must be Taken or Missed.");
            return;
        }
        Reminder reminder = reminders.get(index);
        reminder.updateStatus(status.equalsIgnoreCase("Taken") ? "Taken" : "Missed");
        history.add(new MedicineRecord(today(), now(), reminder.getMedicineName(),
                reminder.getStatus()));
        System.out.println("Status updated and added to history.");
    }

    private static void addHealthRecord() {
        System.out.println("\n1. Blood pressure");
        System.out.println("2. Blood sugar");
        System.out.println("3. Weight");
        System.out.println("4. Temperature");
        int choice = readPositiveInt("Choose record type: ");

        String type;
        String unit;
        switch (choice) {
            case 1: type = "Blood pressure"; unit = "mmHg (enter as 120/80)"; break;
            case 2: type = "Blood sugar"; unit = "mg/dL"; break;
            case 3: type = "Weight"; unit = "kg"; break;
            case 4: type = "Temperature"; unit = "°F or °C"; break;
            default:
                System.out.println("Invalid record type.");
                return;
        }
        String value = readText("Enter value: ");
        String enteredUnit = readText("Enter unit (" + unit + "): ");
        history.add(new HealthRecord(today(), now(), type, value, enteredUnit));
        System.out.println("Health record saved.");
    }

    private static void viewHistory() {
        if (history.isEmpty()) {
            System.out.println("No history saved yet.");
            return;
        }
        System.out.println("\n--- HISTORY ---");
        for (Record record : history) {
            record.displayDetails();
        }
    }

    private static void dashboard() {
        while (true) {
            System.out.println("\n========== MEDICARE DASHBOARD ==========");
            System.out.println("1. Add medicine");
            System.out.println("2. View medicines");
            System.out.println("3. Set reminder");
            System.out.println("4. View/update reminders");
            System.out.println("5. Add health record");
            System.out.println("6. View history");
            System.out.println("7. Logout");
            int choice = readPositiveInt("Choose an option: ");

            switch (choice) {
                case 1: addMedicine(); break;
                case 2: viewMedicines(); break;
                case 3: setReminder(); break;
                case 4: viewReminders(); break;
                case 5: addHealthRecord(); break;
                case 6: viewHistory(); break;
                case 7:
                    System.out.println("Logged out. Thank you for using MediCare, " + registeredUsername + "!");
                    return;
                default:
                    System.out.println("Choose a number from 1 to 7.");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("MediCare - Medicine Reminder & Health Tracker");
        
        while (true) {
            System.out.println("\n1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            int authChoice = readPositiveInt("Choose an option: ");

            if (authChoice == 1) {
                register();
            } else if (authChoice == 2) {
                int attempts = 0;
                boolean loggedIn = false;
                while (attempts < 3 && !loggedIn) {
                    loggedIn = login();
                    if (!loggedIn && !registeredUsername.isEmpty()) {
                        attempts++;
                        if (attempts < 3) System.out.println("Please try again.");
                    } else if (registeredUsername.isEmpty()) {
                        break;
                    }
                }
                if (loggedIn) {
                    dashboard();
                } else if (attempts == 3) {
                    System.out.println("Too many unsuccessful attempts.");
                }
            } else if (authChoice == 3) {
                System.out.println("Exiting application.");
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
        input.close();
    }
}