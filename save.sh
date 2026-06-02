cat > ~/save-mcraft.sh << 'EOF'
#!/system/bin/sh

# MCraft Auto-Save Script for Termux
# Usage: ./save-mcraft.sh "Your commit message"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration - CHANGE THIS IF YOUR PATH IS DIFFERENT
MCRAFT_PATH="/storage/emulated/0/Projects/Main/Actual Projects/MCraft"
BRANCH="main"

# Check if commit message was provided
if [ -z "$1" ]; then
    echo -e "${RED}Error: No commit message provided!${NC}"
    echo -e "Usage: ./save-mcraft.sh \"Your commit message here\""
    exit 1
fi

COMMIT_MSG="$1"

# Check if MCraft directory exists
if [ ! -d "$MCRAFT_PATH" ]; then
    echo -e "${RED}Error: MCraft directory not found at $MCRAFT_PATH${NC}"
    exit 1
fi

# Navigate to MCraft directory
cd "$MCRAFT_PATH" || exit 1

echo -e "${GREEN}📁 MCraft Auto-Save Script${NC}"
echo -e "${YELLOW}Path: $MCRAFT_PATH${NC}"
echo -e "${YELLOW}Branch: $BRANCH${NC}"
echo ""

# Check git status
echo -e "${GREEN}📊 Checking changes...${NC}"
git status --short

# Count changes
CHANGES=$(git status --short | wc -l)

if [ "$CHANGES" -eq 0 ]; then
    echo -e "${YELLOW}No changes to commit.${NC}"
    exit 0
fi

echo ""
echo -e "${GREEN}📝 Changes detected: $CHANGES files${NC}"
echo ""

# Add all changes
echo -e "${GREEN}➕ Staging changes...${NC}"
git add .

# Commit with provided message
echo -e "${GREEN}💾 Committing: $COMMIT_MSG${NC}"
git commit -m "$COMMIT_MSG"

# Push to GitHub
echo -e "${GREEN}⬆️ Pushing to GitHub...${NC}"
git push origin "$BRANCH"

# Check if push succeeded
if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✅ SUCCESS! MCraft saved to GitHub!${NC}"
    echo -e "${GREEN}📦 Commit: $COMMIT_MSG${NC}"
    echo -e "${GREEN}🌐 https://github.com/Axl-Is-Coding/MCraft${NC}"
else
    echo -e "${RED}❌ Push failed! Check your network or authentication.${NC}"
    exit 1
fi
EOF

chmod +x ~/save-mcraft.sh