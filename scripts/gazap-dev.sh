#!/bin/bash

# Define the root path to your workspace directory
PROJECT_ROOT="/workspace"
SESSION_NAME="gazap-dev"

# Ensure we start from the root workspace directory
cd "$PROJECT_ROOT" || { echo "❌ Failed to change directory to $PROJECT_ROOT"; exit 1; }

# Check if the tmux session already exists to avoid duplication
if tmux has-session -t "$SESSION_NAME" 2>/dev/null; then
    echo "🔄 Session '$SESSION_NAME' already exists. Attaching now..."
    tmux attach-session -t "$SESSION_NAME"
    exit 0
fi

echo "🚀 Spinning up the gaZap Development Grid inside tmux..."

# 1. Create the session and launch the first service (Top-Left Quad)
tmux new-session -d -s "$SESSION_NAME" -n 'Microservices' \
    "echo '📋 Starting zChecker...'; mvn -f ./gaZap/zChecker clean spring-boot:run"

# 2. Split vertically to create the Right side (Top-Right Quad)
tmux split-window -h -t "$SESSION_NAME:0.0" \
    "echo '📊 Starting neo4jStores...'; mvn -f ./gaZap/neo4jStores clean spring-boot:run"

# 3. Split the Left side horizontally to create the Bottom-Left Quad
tmux split-window -v -t "$SESSION_NAME:0.0" \
    "echo '⏳ Starting workflows (Temporal)...'; mvn -f ./gaZap/workflows clean spring-boot:run"

# 4. Split the Right side horizontally to create the Bottom-Right Quad
tmux split-window -v -t "$SESSION_NAME:0.1" \
    "echo '💻 Starting Frontend...'; npm --prefix ./gaZap/frontend install && npm --prefix ./gaZap/frontend run dev"

# Optional: Force all panes to be exactly equal in size
tmux select-layout -t "$SESSION_NAME" tiled

# Attach to the newly created session
tmux attach-session -t "$SESSION_NAME"
