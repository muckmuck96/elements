# Countdowns

Scheduled countdown tasks with lifecycle management.

## Setup

```java
CountdownRegistry countdowns = registry.enable(CountdownRegistry.class);
```

## Creating Countdowns

```java
Countdown countdown = countdowns.createCountdown(
    10,  // count from
    1,   // steps per tick
    () -> { /* each tick */ return null; },
    () -> { /* on complete */ return null; }
);
```

## Managing Countdowns

```java
countdown.cancel();
countdowns.cancelCountdown(countdown);
countdowns.getActiveCountdowns();
```

## Cleanup

```java
@Override
public void onDisable() {
    countdowns.cancelAllCountdowns();
}
```
