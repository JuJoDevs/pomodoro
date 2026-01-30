# Git Hooks

This directory contains git hooks for the Pomodoro project.

## Pre-commit Hook

The pre-commit hook automatically runs code quality checks before each commit:

1. **ktlintCheck**: Checks code style on staged Kotlin files
2. **ktlintFormat**: Auto-formats files if ktlintCheck finds issues
3. **detekt**: Runs static analysis on staged Kotlin files

The hook only checks files that are staged for commit, making it fast and efficient.

### Installation

Run the installation script:

```bash
./scripts/install-hooks.sh
```

This will copy the pre-commit hook to `.git/hooks/pre-commit`.

### How It Works

1. When you commit, the hook identifies all staged `.kt` and `.kts` files
2. Runs `ktlintCheck` on those files
3. If issues are found, runs `ktlintFormat` to auto-fix them
4. If formatting fails, shows errors and aborts the commit
5. If ktlint passes, runs `detekt` on the staged files
6. If detekt finds issues, shows errors and aborts the commit
7. If all checks pass, the commit proceeds

### Skipping the Hook

To skip the hook for a single commit (not recommended):

```bash
git commit --no-verify
```

### Requirements

- The hook uses `./gradlew` to run Gradle tasks
- If `ktlint` CLI is installed, it will be used for better file filtering
- Otherwise, the hook falls back to Gradle tasks on affected modules

### Troubleshooting

If the hook fails:

1. Check the error messages - they will show which files have issues
2. Run the checks manually:
   ```bash
   ./gradlew ktlintFormat  # Auto-fix ktlint issues
   ./gradlew detekt        # Check detekt issues
   ```
3. Fix the issues and stage the files again
4. Try committing again
