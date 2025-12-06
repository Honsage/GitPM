package ru.honsage.dev.gitpm.domain.models;

public enum TaskPriority {
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    private final int value;

    TaskPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public boolean isHigherThan(TaskPriority other) {
        return this.value > other.value;
    }

    public static TaskPriority fromValue(int value) {
        return switch (value) {
            case 1 -> LOW;
            case 2 -> MEDIUM;
            case 3 -> HIGH;
            default -> throw new IllegalArgumentException(
                    String.format("Invalid value: %s. Value must be 1, 2 or 3",value)
            );
        };
    }
}
