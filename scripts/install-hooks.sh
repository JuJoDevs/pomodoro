#!/bin/bash

# Install git hooks script
# This script installs the pre-commit hook

set -e

PROJECT_ROOT="$(git rev-parse --show-toplevel)"
cd "$PROJECT_ROOT"

HOOKS_DIR=".git/hooks"
PRE_COMMIT_HOOK="$HOOKS_DIR/pre-commit"
SCRIPT_PATH="scripts/pre-commit.sh"

if [ ! -d "$HOOKS_DIR" ]; then
    echo "Error: .git/hooks directory not found. Are you in a git repository?"
    exit 1
fi

if [ ! -f "$SCRIPT_PATH" ]; then
    echo "Error: $SCRIPT_PATH not found."
    exit 1
fi

# Copy the pre-commit script to .git/hooks
cp "$SCRIPT_PATH" "$PRE_COMMIT_HOOK"
chmod +x "$PRE_COMMIT_HOOK"

echo "✓ Pre-commit hook installed successfully!"
echo ""
echo "The hook will now run ktlint and detekt checks on staged Kotlin files before each commit."
echo ""
echo "To skip the hook for a single commit, use:"
echo "  git commit --no-verify"
