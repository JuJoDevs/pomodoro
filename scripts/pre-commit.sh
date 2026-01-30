#!/bin/bash

# Pre-commit hook for ktlint and detekt
# Only checks files that are staged for commit

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Get the project root directory
PROJECT_ROOT="$(git rev-parse --show-toplevel)"
cd "$PROJECT_ROOT"

# Get staged Kotlin files
STAGED_KOTLIN_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep -E '\.(kt|kts)$' || true)

# If no Kotlin files are staged, skip checks
if [ -z "$STAGED_KOTLIN_FILES" ]; then
    echo -e "${GREEN}No Kotlin files staged for commit. Skipping ktlint and detekt checks.${NC}"
    exit 0
fi

echo -e "${YELLOW}Running pre-commit checks on staged Kotlin files...${NC}"
echo "Files to check:"
echo "$STAGED_KOTLIN_FILES" | sed 's/^/  - /'

# Convert to array for processing
IFS=$'\n' read -rd '' -a FILES_ARRAY <<< "$STAGED_KOTLIN_FILES" || true

# Function to find modules containing files
find_modules() {
    local modules=""
    for file in "${FILES_ARRAY[@]}"; do
        if [ -f "$file" ]; then
            local dir=$(dirname "$file")
            local module=""
            
            # Check common module patterns
            if [[ "$dir" == app/* ]]; then
                module="app"
            elif [[ "$dir" == build-logic/* ]]; then
                # Only build-logic:convention has ktlint/detekt configured
                # Skip build-logic root as it's just a container
                if [[ "$dir" == build-logic/convention/* ]]; then
                    module="build-logic:convention"
                else
                    # Skip build-logic root module - it doesn't have ktlint/detekt
                    module=""
                fi
            else
                # Try to find module by looking for build.gradle.kts
                local current_dir="$dir"
                while [ "$current_dir" != "." ] && [ "$current_dir" != "/" ] && [ "$current_dir" != "$PROJECT_ROOT" ]; do
                    if [ -f "$current_dir/build.gradle.kts" ] || [ -f "$current_dir/build.gradle" ]; then
                        # Get relative path from project root
                        module=$(realpath --relative-to="$PROJECT_ROOT" "$current_dir" 2>/dev/null || echo "${current_dir#./}")
                        break
                    fi
                    current_dir=$(dirname "$current_dir")
                done
            fi
            
            if [ -n "$module" ] && [ -d "$module" ]; then
                # Explicitly exclude build-logic root (only build-logic:convention is valid)
                if [ "$module" != "build-logic" ]; then
                    modules="$modules $module"
                fi
            fi
        fi
    done
    echo "$modules" | tr ' ' '\n' | sort -u | grep -v '^$' | grep -v '^build-logic$'
}

# Get affected modules
MODULES=$(find_modules)

# Step 1: Run ktlintCheck on staged files
echo ""
echo -e "${YELLOW}Step 1: Running ktlintCheck...${NC}"

KTLINT_CHECK_FAILED=false

# Check if ktlint CLI is available
if command -v ktlint &> /dev/null; then
    # Use ktlint CLI directly for better file filtering
    for file in "${FILES_ARRAY[@]}"; do
        if [ -f "$file" ]; then
            if ! ktlint "$file" 2>&1; then
                KTLINT_CHECK_FAILED=true
            fi
        fi
    done
else
    # Fallback to Gradle task - run on modules containing the files
    if [ -n "$MODULES" ]; then
        while IFS= read -r module; do
            if [ -n "$module" ] && [ "$module" != "build-logic" ]; then
                # Convert module path to Gradle path (e.g., "build-logic:convention" stays as is, "app" -> "app")
                gradle_module=$(echo "$module" | tr '/' ':')
                # Skip build-logic root (only build-logic:convention should be checked)
                if [ "$gradle_module" != "build-logic" ]; then
                    if ! ./gradlew ":$gradle_module:ktlintCheck" --quiet 2>&1; then
                        KTLINT_CHECK_FAILED=true
                    fi
                fi
            fi
        done <<< "$MODULES"
    fi
    
    # Only run on root if we have modules, otherwise skip
    # build-logic doesn't have ktlint configured, so we skip root execution
    # to avoid errors. All files should be in specific modules anyway.
fi

if [ "$KTLINT_CHECK_FAILED" = true ]; then
    echo -e "${YELLOW}ktlintCheck found issues. Running ktlintFormat to auto-fix...${NC}"
    
    # Step 2: Run ktlintFormat on staged files
    KTLINT_FORMAT_FAILED=false
    
    if command -v ktlint &> /dev/null; then
        # Use ktlint CLI with --format flag
        for file in "${FILES_ARRAY[@]}"; do
            if [ -f "$file" ]; then
                if ! ktlint --format "$file" 2>&1; then
                    KTLINT_FORMAT_FAILED=true
                fi
            fi
        done
    else
        # Fallback to Gradle task
        if [ -n "$MODULES" ]; then
            while IFS= read -r module; do
                if [ -n "$module" ] && [ "$module" != "build-logic" ]; then
                    gradle_module=$(echo "$module" | tr '/' ':')
                    # Skip build-logic root (only build-logic:convention should be checked)
                    if [ "$gradle_module" != "build-logic" ]; then
                        if ! ./gradlew ":$gradle_module:ktlintFormat" --quiet 2>&1; then
                            KTLINT_FORMAT_FAILED=true
                        fi
                    fi
                fi
            done <<< "$MODULES"
        fi
        # Skip root execution - all files should be in specific modules
        # build-logic doesn't have ktlint configured
    fi
    
    if [ "$KTLINT_FORMAT_FAILED" = true ]; then
        echo -e "${RED}❌ ktlintFormat failed. Please fix the issues manually.${NC}"
        echo ""
        echo "Files with issues:"
        echo "$STAGED_KOTLIN_FILES" | sed 's/^/  - /'
        echo ""
        echo "Run './gradlew ktlintFormat' or 'ktlint --format <file>' to see detailed errors."
        exit 1
    fi
    
    # Re-stage the formatted files
    echo -e "${GREEN}✓ Files auto-formatted. Re-staging formatted files...${NC}"
    for file in "${FILES_ARRAY[@]}"; do
        if [ -f "$file" ]; then
            git add "$file"
        fi
    done
fi

echo -e "${GREEN}✓ ktlint checks passed${NC}"

# Step 3: Run detekt on staged files
echo ""
echo -e "${YELLOW}Step 2: Running detekt...${NC}"

DETEKT_FAILED=false

# Run detekt on affected modules
if [ -n "$MODULES" ]; then
    while IFS= read -r module; do
        if [ -n "$module" ] && [ "$module" != "build-logic" ]; then
            gradle_module=$(echo "$module" | tr '/' ':')
            # Skip build-logic root (only build-logic:convention should be checked)
            if [ "$gradle_module" != "build-logic" ]; then
                if ! ./gradlew ":$gradle_module:detekt" --quiet 2>&1; then
                    DETEKT_FAILED=true
                fi
            fi
        fi
    done <<< "$MODULES"
fi

# Skip root execution - all files should be in specific modules
# build-logic doesn't have detekt configured

if [ "$DETEKT_FAILED" = true ]; then
    echo -e "${RED}❌ detekt found issues. Please fix them before committing.${NC}"
    echo ""
    echo "Files with issues:"
    echo "$STAGED_KOTLIN_FILES" | sed 's/^/  - /'
    echo ""
    echo "Run './gradlew detekt' to see detailed errors."
    exit 1
fi

echo -e "${GREEN}✓ detekt checks passed${NC}"
echo ""
echo -e "${GREEN}✓ All pre-commit checks passed!${NC}"
exit 0
