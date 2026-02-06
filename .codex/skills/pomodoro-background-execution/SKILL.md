---
name: pomodoro-background-execution
description: Background execution and notification reliability for the Pomodoro app. Use when planning or implementing AlarmManager, BroadcastReceiver, or Foreground Service scheduling under libs/notifications.
---

# Pomodoro Background Execution

## Overview

Implement reliable Pomodoro completion notifications that work with the screen locked while respecting battery and modern Android restrictions.

## Preferred approaches

- Use `AlarmManager` with a `BroadcastReceiver` for time-based completion.
- Use a Foreground Service only if strict reliability requires it.
- Use WorkManager only when the task can tolerate deferrable execution.

## Module boundaries

- All implementations live in `libs/notifications` with `api/impl` separation.
- Features depend on `libs/notifications/api` only.
- Avoid Android dependencies in domain.

## Planning checklist

- Confirm reliability requirements with screen locked.
- Choose the most battery-efficient option that meets reliability.
- Decide whether exact alarms or inexact scheduling is required.
- Define notification content and channels in `libs/notifications`.

## Implementation checklist

- Keep scheduling logic in `libs/notifications/impl`.
- Expose a scheduler interface in `libs/notifications/api`.
- Ensure alarms are rescheduled if needed after reboot.
- Keep all strings in resources.

## Testing guidance

- Unit test scheduling logic with fakes where possible.
- Avoid instrumented tests unless required.
