package Inventory.InventoryUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DateUtils {

    /**
     * Calculates how many days from today until the next supply day.
     *
     * @param supplyDaysInWeek Comma-separated days like "Sunday,Wednesday"
     * @return The number of days until the next supply day (1–7)
     */
    public static int calculateNextSupplyDayOffset(String supplyDaysInWeek) {
        List<String> dayNames = Arrays.asList(supplyDaysInWeek.split(","));
        List<DayOfWeek> supplyDays = new ArrayList<>();

        for (String name : dayNames) {
            try {
                supplyDays.add(DayOfWeek.valueOf(name.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Invalid day name in supply days: " + name);
            }
        }

        if (supplyDays.isEmpty()) return 7;

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        int todayValue = today.getValue(); // Monday = 1

        int minOffset = 7;
        for (DayOfWeek supplyDay : supplyDays) {
            int supplyValue = supplyDay.getValue();
            int offset = (supplyValue - todayValue + 7) % 7;
            if (offset == 0) offset = 7;
            minOffset = Math.min(minOffset, offset);
        }

        return minOffset;
    }
}
