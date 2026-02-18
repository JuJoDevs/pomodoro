---
name: pomodoro-task-workflow-writer
description: Create or update task workflow markdown instructions for the Pomodoro Android project when another lower-capability LLM agent will execute the work. Use when a task brief must include explicit steps, acceptance criteria, project-aligned snippets, and expected tests.
---

# Pomodoro Task Workflow Writer

## Goal

Produce deterministic workflow instructions that minimize ambiguity for lower-capability LLM agents.

## Output Contract

Use the template in `references/workflow-template.md`.

Always include these sections in this exact order:

1. `# Task Workflow: <short-title>`
2. `## Objective`
3. `## Inputs`
4. `## Constraints`
5. `## Steps`
6. `## Acceptance Criteria`
7. `## Project Snippets`
8. `## Expected Tests`
9. `## Execution Commands`

## Authoring Rules

- Write everything in English.
- Keep steps atomic and actionable.
- Reference concrete file paths and module names.
- Use project architecture terms (`api/impl`, `data/domain/presentation`, `Route/Screen`, MVI contracts).
- Prefer module-scoped commands where possible.
- State assumptions explicitly when information is missing.
- Never omit `Acceptance Criteria`, `Project Snippets`, or `Expected Tests`.

## Section Rules

### Steps

- Write numbered implementation steps.
- Mention files to create or modify in each step.
- Include DI and navigation wiring steps when applicable.

### Acceptance Criteria

- Include compile, static analysis, unit tests, and screenshot verification.
- Keep criteria verifiable with Gradle commands.
- Mark emulator/device integration tests as out of scope unless explicitly requested.

### Project Snippets

- Include at least 2 snippets grounded in this codebase patterns.
- Prefer snippets for one domain contract/use case and one presentation/test artifact.
- Keep snippets short and directly reusable.

### Expected Tests

- Define required unit tests using GIVEN/WHEN/THEN naming.
- Define ViewModel tests when presentation logic changes.
- Define Roborazzi screenshot tests for changed screens/components.
- If screenshot tests are not applicable, add a one-line justification.

## Quality Checklist

- Workflow contains all required sections.
- Commands and snippets match project conventions.
- Test expectations map directly to changed behavior.
- Acceptance criteria are objective and pass/fail.
